package first.app.controlofbuy.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import first.app.controlofbuy.R;
import first.app.controlofbuy.helpers.DatabaseHelper;

/**
 * Activity utilizado na tela principal de compra.
 * 
 * @author Jonathas J. C.
 *
 */
public class HomeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
	}
	
	/**
	 * Inicializa uma nova Activity e finaliza a atual.
	 * 
	 * @param view Componente que vem do XML.
	 */
	public void novaListaCompras(View view){

		//Cria uma Intent a partir da classe Activity e inicializa 'startActivity'.
		Intent produtoActivity = new Intent(this, ProdutoActivity.class);
		startActivity(produtoActivity);
		//Finaliza a atual.
		finish();
	}
	
	public void visualizarListaCompras(View view){

		//Cria uma Intent a partir da classe Activity e inicializa 'startActivity'.
		Intent compraActivity = new Intent(this, CompraActivity.class);
		startActivity(compraActivity);
		//Finaliza a atual.
		finish();
	}
	
	public void visualizarRelatorios(View view){

		Context context = getApplicationContext();
		CharSequence text = "Módulo Relatório não disponível!";
		int duration = Toast.LENGTH_SHORT;
		
		Toast.makeText(context, text, duration).show();
	}
	
	//Cria um Menu de opção.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return createMenu(menu);
	}
	
	
	//Acessa um item do Menu de opção.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return menuChoice(item);
	}
	
	// Cria um Menu de Opção para a Activity.
	private boolean createMenu(Menu menu) {
		try {
			menu.add(0,0,0,R.string.btn_nova_compra);
			menu.add(0,1,1,R.string.btn_visualizar_listas);
			menu.add(0,3,3,R.string.btn_sobre);
			menu.add(0,2,2,R.string.btn_visualizar_relatorios);
			
		} catch (Exception e) {
			Log.d(DatabaseHelper.TAG, "Erro ao criar menu");
		}
		
		return true;
	}
	
	//Acessa um item do Menu.
	private boolean menuChoice(MenuItem item) {
		
		switch (item.getItemId()) {
		
			case 0:
				novaListaCompras(null);
				break;
	
			case 1:
				visualizarListaCompras(null);
				break;
				
			case 3:
				dialogInformacoes();
				break;
			case 2:
				visualizarRelatorios(null);
				break;
	
		}
		
		return true;
	}
	
	private Dialog dialogInformacoes() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.btn_sobre);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.informacoes_edit_dialog, null);
		
		builder.setView(view)
			.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.cancel();
				}
			});
		
		Dialog dialog = builder.create();
		dialog.show();
		
		return dialog;
		
	}
	
}
