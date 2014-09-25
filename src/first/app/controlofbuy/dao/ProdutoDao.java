package first.app.controlofbuy.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import first.app.controlofbuy.entities.Produto;
import first.app.controlofbuy.helpers.DatabaseHelper;
import first.app.controlofbuy.helpers.GenericDao;
import first.app.controlofbuy.helpers.StringUtils;

public class ProdutoDao extends GenericDao<Produto>{

	public ProdutoDao(Context context) {
		super(context, Produto.class);
	}
	
	public List<Produto> getAllOrdemNome(String filter) {
		
		List<Produto> produtos = new ArrayList<Produto>();
		try {
			String buscaSemAcento = StringUtils.removerAcentos(filter); 
			QueryBuilder<Produto, Integer> builder = dao.queryBuilder();
			builder.where().like(Produto.NOME_TEMP,"%" + buscaSemAcento + "%");
			builder.orderBy(Produto.NOME_TEMP, true);
			PreparedQuery<Produto> prepared = builder.prepare();
		
			produtos =  dao.query(prepared);
		
		} catch (SQLException e) {
			Log.d(DatabaseHelper.TAG, "Não foi possível realizar a consulta " + e.getLocalizedMessage());
		}
		
		return produtos;
	}
}
