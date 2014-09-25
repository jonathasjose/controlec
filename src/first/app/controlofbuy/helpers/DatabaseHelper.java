package first.app.controlofbuy.helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import first.app.controlofbuy.entities.Compra;
import first.app.controlofbuy.entities.Mercado;
import first.app.controlofbuy.entities.Produto;
import first.app.controlofbuy.entities.ProdutoCategoria;
import first.app.controlofbuy.entities.ProdutoCompra;

/**
 * Database auxiliar para criacao das primeiras tabelas e inserção de dados.
 * 
 * @author Jonathas J. C.
 *
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	
	public static final String DATABASE = "controlofbuy.db"; 
	public static int VERSAO = 6;
	public static final String TAG = "ControlOfBuy";
	public Context context;
	
	
	public DatabaseHelper(Context context) {
		
		super(context, DATABASE, null, VERSAO);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource cnn) {

		try {
			//Cria as tabelas.
			TableUtils.createTable(cnn, ProdutoCategoria.class);
			TableUtils.createTable(cnn, Produto.class);
			TableUtils.createTable(cnn, Mercado.class);
			TableUtils.createTable(cnn, ProdutoCompra.class);
			TableUtils.createTable(cnn, Compra.class);
			
			try {
				
				//Acessa os arquivos cvs com os dados iniciais para alimentar as tabelas.
				AssetManager assetManager = context.getAssets();
				InputStream inCategorias = assetManager.open("categoria.csv");
				InputStream inProdutos = assetManager.open("produtos.csv");
				
				BufferedReader categorias = new BufferedReader(new InputStreamReader(inCategorias, "ISO-8859-1"));
				BufferedReader produtos = new BufferedReader(new InputStreamReader(inProdutos, "ISO-8859-1"));

				String linha;
				
				Dao daoCategoria = DaoManager.createDao(getConnectionSource(), ProdutoCategoria.class);
				Dao daoProduto = DaoManager.createDao(getConnectionSource(), Produto.class);
				
				while((linha = categorias.readLine()) != null) {
					
					String conteudo[] = linha.split(";");
					Integer id = new Integer(conteudo[1]);
					String nome = conteudo[0];
					ProdutoCategoria pc = new ProdutoCategoria();
					pc.setId(id);
					pc.setNome(nome);
					
					daoCategoria.create(pc);
				}
				while((linha = produtos.readLine()) != null) {
					
					String conteudo[] = linha.split(";");
					Integer idCategoria = new Integer(conteudo[1]);
					String nome = conteudo[0];
					Produto p = new Produto();		
					
					ProdutoCategoria pc = (ProdutoCategoria) daoCategoria.queryForId(idCategoria);
					p.setNome(nome);
					p.setCategoria(pc);
					
					daoProduto.create(p);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			Log.d(TAG, "Erro ao criar tabelas: " + e.getLocalizedMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource cnn, int oldVersion, int newVersion) {
		
		try {
			TableUtils.dropTable(cnn, Compra.class, true);
			TableUtils.dropTable(cnn, ProdutoCompra.class, true);
			TableUtils.dropTable(cnn, Mercado.class, true);
			TableUtils.dropTable(cnn, Produto.class, true);
			TableUtils.dropTable(cnn, ProdutoCategoria.class, true);
			
			onCreate(database, cnn);
			
		} catch (SQLException e) {
			Log.d(TAG, "Erro ao criar uma nova verão do banco: " + e.getLocalizedMessage());
		}
	}
	
	@Override
	public void close() {

		super.close();
	}

}
