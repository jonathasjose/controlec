package first.app.controlofbuy.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import first.app.controlofbuy.R;
import first.app.controlofbuy.business.ProdutoCompraBusiness;
import first.app.controlofbuy.dao.CompraDao;
import first.app.controlofbuy.entities.Compra;
import first.app.controlofbuy.helpers.adapters.CompraAdapter;

/**
 * Activity utilizado na tela principal de compra.
 * 
 * @author Jonathas J. C.
 *
 */
public class CompraActivity extends Activity {
	
	private List<Compra> compras = new ArrayList<Compra>();
	private ListView listViewCompras;
	private CompraAdapter comprasAdapter;
	public static final String COMPRA_ID = "COMPRA_ID";
	
	//Primeiro método executado pela Activity.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_lista);
		
		CompraDao compraDao = new CompraDao(getApplicationContext());
		compras = compraDao.getAllOrderByData();
		
		//Exibe o ListView na tela.
		listViewCompras = (ListView) findViewById(R.id.listViewCompras);
		comprasAdapter = new CompraAdapter(this, compras);
		listViewCompras.setAdapter(comprasAdapter);
		
	
		listViewCompras.setOnItemClickListener(new OnItemClickListener() {
			
			//Inicializa a Actitivity produtoCompra e passa o id_compra como par‚metro.
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Compra compra = compras.get(position);
				Intent produtoCompraIntent = new Intent(getBaseContext(), ProdutoCompraActivity.class);
				produtoCompraIntent.putExtra(COMPRA_ID, compra.getId());
				startActivityForResult(produtoCompraIntent, RESULT_OK);
				finish();
			}
		});
		
		listViewCompras.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				registerForContextMenu(listViewCompras);
				return false;
			}
		});
	}
	
	/**
	 * Inicializa uma nova Activity e finaliza a atual.
	 * 
	 * @param view Componente que vem do XML.
	 */
	public void loadProdutos(View view){

		//Cria uma Intent a partir da classe Activity e inicializa 'startActivity'.
		Intent produtoActivity = new Intent(this, ProdutoActivity.class);
		startActivity(produtoActivity);
		//Finaliza a atual.
		finish();
	}
	
	//Cria menu de contexto para a activity.
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, view, menuInfo);
		//Seta um título para o Menu de Contexto e infla o XML.
		menu.setHeaderTitle(R.string.opcoes);
		getMenuInflater().inflate(R.menu.menu_opcoes_compra, menu);
	}
	
	//Quando um Item do Menu é selecionado.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final int pos = info.position;
		final Compra compra = compras.get(pos);
		
		switch (item.getItemId()) {
		
		case R.id.excluir_compra:
			
			int sim = R.string.sim;
			int cancel = R.string.cancel;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.info_deseja_realmente_excluir)
			.setPositiveButton(sim, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					excluirCompra(compra, pos);
				}
			})
			.setNegativeButton(cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			.create()
			.show();
			break;
		
		case R.id.editar_compra:
			
			Intent produtoActivity = new Intent(this, ProdutoActivity.class);
			produtoActivity.putExtra(COMPRA_ID, compra.getId());
			startActivityForResult(produtoActivity, RESULT_OK);
			finish();
			break;
		}
		
		return true;
	}
	
	/**
	 * Exclui um Compra e todos os seus filhos.
	 * 
	 * @param compra {@link Compra} a ser excluída. 
	 * @param pos Posição utilizada para remover o item da lista.
	 */
	private void excluirCompra(Compra compra, int pos) {
		
		if(compra != null) {
		
			ProdutoCompraBusiness pcBusiness = new ProdutoCompraBusiness(this);
			//Remove a compra.
			pcBusiness.excluirCompra(compra);
			//Remove item do adaptador e lista;
			//comprasAdapter.remove(comprasAdapter.getItem(pos));
			compras.remove(pos);
			//Atualiza o adaptador.
			comprasAdapter.notifyDataSetChanged();
		}
	}
	
}
