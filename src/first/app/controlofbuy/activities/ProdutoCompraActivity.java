package first.app.controlofbuy.activities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import first.app.controlofbuy.R;
import first.app.controlofbuy.dao.CompraDao;
import first.app.controlofbuy.dao.ProdutoCompraDao;
import first.app.controlofbuy.entities.Compra;
import first.app.controlofbuy.entities.ProdutoCompra;
import first.app.controlofbuy.entities.ProdutoCompra.UnidadeMedida;
import first.app.controlofbuy.helpers.DatabaseHelper;
import first.app.controlofbuy.helpers.MsgUtils;
import first.app.controlofbuy.helpers.StringUtils;
import first.app.controlofbuy.helpers.adapters.ProdutoCompraAdapter;

/**
 * Activity utilizado o carrinho de compras {@link ProdutoCompra}.
 * 
 * @author Jonathas JosÈ da ConceiÁ„o.
 *
 */
public class ProdutoCompraActivity extends Activity {
	
	private List<ProdutoCompra> produtosCompras;
	private ProdutoCompraAdapter produtoCompraAdapter;
	private Compra compra;
	public static final String COMPRA_ID = "COMPRA_ID";
	public static final String PRODUTO_COMPRA = "PRODUTO_COMPRA";

	//Primeiro mÈtodo executado pela Actitivity.
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		//Acessa o ID da compra que veio por parametro.
		int idCompra = getIntent().getIntExtra(CompraActivity.COMPRA_ID, 0);
		CompraDao CDAo = new CompraDao(this);
		//Carrega a compra pelo ID.
		compra = CDAo.getById(idCompra);
		
		//Se a compra estiver concluída, envia para a tela de resumo.
		if(compra != null && compra.isCompraConcluida()) {
			Intent compraFinalizada = new Intent(getBaseContext(), FinalizarCompraActivity.class);
			compraFinalizada.putExtra(COMPRA_ID, compra.getId());
			startActivityForResult(compraFinalizada, RESULT_OK);
			finish();
		}
		
		setContentView(R.layout.carrinho_lista);
		
		// Atualiza o valor do cabeÁalho da compra.
		atualizarCabecalhoProdutoCompra();
		
		//Acessa o ListView do XML.
		ListView listViewProdutoCompra = (ListView) findViewById(R.id.listViewProdutoCompra);
		//Carrega todos Produtos que foram selecionados para esta compra.
		produtosCompras = new ArrayList<ProdutoCompra>(compra.getProdutosCompra());
		//Ordenação
		Collections.sort(produtosCompras);
		//Cria o adaptador com os ProdutoCompra.
		produtoCompraAdapter = new ProdutoCompraAdapter(this, produtosCompras);
		//Alimenta o ListView com o adaptador de compra.
		listViewProdutoCompra.setAdapter(produtoCompraAdapter);
		
		//LongClick, utilizado para remover um produto do carrinho.
		listViewProdutoCompra.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
				
				ProdutoCompra produtoCompra = produtosCompras.get(pos);
				confirmaExclusao(produtoCompra);
				
				return false;
			}

		});
		
		//Listener invocado pelo click no ListView.
		listViewProdutoCompra.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				
				//Adiciona ou remove o Produto do cesto de compras.
				ProdutoCompra produtoCompra = produtosCompras.get(pos);
				
				//Se o produto ja esta no carrinho, abre para ediÁ„o do mesmo.
				if(produtoCompra.isCesto()){

					final Dialog dialog = createDialog(produtoCompra);
					
					//Acessa os componentes do XML.
					EditText etValorProduto = (EditText) dialog.findViewById(R.id.editTextValorProduto);
					EditText etQtde = (EditText) dialog.findViewById(R.id.editTextQtdeProduto);
					CheckBox calcQtde = (CheckBox) dialog.findViewById(R.id.checkBoxMaisQtde);
					
					//Listener escuta quando uma tecla È pressionada.
					etValorProduto.addTextChangedListener(new TextWatcher() {
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
						
						@Override
						public void afterTextChanged(Editable s) {
							//Atualiza o valor total do produto compra.
							atualizarValorProdutoQtde(dialog);
						}
					});
					
					//Listener escuta quando uma tecla È pressionada.
					etQtde.addTextChangedListener(new TextWatcher() {
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
						
						@Override
						public void afterTextChanged(Editable s) {
							//Atualiza o valor total do produto compra.
							atualizarValorProdutoQtde(dialog);
						}
					});
					
					//Listener escuta quando o checkbox È clicado.
					calcQtde.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View view) {
							//Atualiza o valor total do produto compra.
							atualizarValorProdutoQtde(dialog);
						}
					});
				} else {
					produtoCompra.setCesto(true);
				}
				
				//Atualiza o adaptador.
				produtoCompraAdapter.notifyDataSetChanged();
			}
		});
		
	}
	
	//Cria um Menu de opÁ„o.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return createMenu(menu);
	}
	
	//Acessa um item do Menu de opÁ„o.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return menuChoice(item);
	}
	
	// Cria um Menu de OpÁ„o para a Activity.
	private boolean createMenu(Menu menu) {
		
		try {
			menu.add(0,0,0,R.string.adicionar_produto);
			menu.add(0,1,1,R.string.finalizar_compra);
			
		} catch (Exception e) {
			Log.d(DatabaseHelper.TAG, "Erro ao criar menu");
		}
		
		return true;
	}
	
	//Acessa um item do Menu.
	private boolean menuChoice(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case 0:
			//Adiciona um novo produto
			Intent produtoActivity = new Intent(this, ProdutoActivity.class);
			produtoActivity.putExtra(COMPRA_ID, compra.getId());
			produtoActivity.putExtra(PRODUTO_COMPRA, 1);
			startActivityForResult(produtoActivity, RESULT_OK);
			break;

		case 1:
			//Verificar se existe itens no carrinho.
			if(hasAllItensCarrinho()) {
				
				//Finaliza uma compra
				Intent compraActivity = new Intent(this, FinalizarCompraActivity.class);
				compraActivity.putExtra(COMPRA_ID, compra.getId());
				startActivityForResult(compraActivity, RESULT_OK);
				break;
			} else {
				//Caso n„o, obriga o usu·rio a atualizar o carrinho.
				MsgUtils.msgValidacao(getString(R.string.msg_validacao_existem_itens_pendentes_carrinho), this);
			}
		}
		
		return true;
	}
	
	//Verificar se existe itens no carrinho.
	private boolean hasAllItensCarrinho() {

		for(ProdutoCompra pc: compra.getProdutosCompra()){
			
			if(!pc.isCesto()) {
				return false;
			}
		}
		
		return true;
	}
	

	//Show Dialog confirmando se o usu·rio quer mesmo excluir o Produto do carrinho.
	private void confirmaExclusao(final ProdutoCompra produtoCompra) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.info_deseja_realmente_excluir_produto_carrinho);
		builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				//Remove o Produto do carrinho e atualiza o adaptador.
				ProdutoCompraDao pcDao = new ProdutoCompraDao(getBaseContext());
				pcDao.delete(produtoCompra);
				produtosCompras.remove(produtoCompra);
				compra.getProdutosCompra().remove(produtoCompra);
				produtoCompraAdapter.notifyDataSetChanged();
				//Atualiza o topo da activity.
				atualizarCabecalhoProdutoCompra();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});
		builder.create();
		builder.show();
		
	}

	//Cria um AlertDialog com informaÁıes do Produto.
	private Dialog createDialog(final ProdutoCompra produtoCompra) {
		
		//Dados do produto
		String produtoNome = produtoCompra.getProduto().getNome();
		BigDecimal qtde = produtoCompra.getQtde();
		ProdutoCompra.UnidadeMedida unMedida = produtoCompra.getUnidadeMedida(); 
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		
		builder.setTitle(R.string.atualizar_produto);
		//Infla o XML utilizado no dialog.
		final View view = inflater.inflate(R.layout.carrinho_edit_dialog, null);
		RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGOptionUN);
		
		//Cria o RadioGroup com informaÁıes do Enum que ser· exibido na view (XML).
		int i = 0;
		for(ProdutoCompra.UnidadeMedida unidadeMedida: ProdutoCompra.UnidadeMedida.values()){
			
			RadioButton rb = new RadioButton(this);
			rb.setText(unidadeMedida.getValor());
			rb.setChecked(unidadeMedida.equals(unMedida));
			rb.setTextColor(Color.BLACK);
			rb.setId(i);
			rg.addView(rb);
			
			if(i > 0) {
				LayoutParams params = (LayoutParams) rb.getLayoutParams();
				params.leftMargin = 15;
				rb.setLayoutParams(params);
			}
			
			i++;
		}
		
		builder.setView(view)
			.setPositiveButton(getString(R.string.salvar), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					atualizaProdutoCarrinho(view, produtoCompra);
				}
			})
			.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.cancel();
				}
			});
		
		final Dialog dialog = builder.create();
		dialog.show();
		
		CheckBox cbCalcQtde = (CheckBox) dialog.findViewById(R.id.checkBoxMaisQtde);
		cbCalcQtde.setChecked(produtoCompra.isValorManual());
		
		TextView tvQtde = (TextView) dialog.findViewById(R.id.editTextQtdeProduto);
		tvQtde.setText(qtde!= null ? qtde.toString() : null);

		TextView tvProdutoNome = (TextView) dialog.findViewById(R.id.textViewProdutoNome);
		tvProdutoNome.setText(produtoNome);
		
		EditText etValor = (EditText) dialog.findViewById(R.id.editTextValorProduto);
		etValor.setText(produtoCompra.getValor() != null ? produtoCompra.getValor().toString() : null);
		
		CheckBox cbChecked = (CheckBox) dialog.findViewById(R.id.checkBoxProdutoCarrinho);
		cbChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				removerProdutoCesto(produtoCompra);
				dialog.cancel();
			}
		});
		
		atualizarValorProdutoQtde(dialog);
		
		return dialog;
	}
	
	//Remove um produto do cesto
	private void removerProdutoCesto(ProdutoCompra produtoCompra){
		
		produtoCompra.setCesto(false);
		produtoCompra.setValor(null);
		produtoCompraAdapter.notifyDataSetChanged();
		atualizarCabecalhoProdutoCompra();
	}
	
	
	//Atualiza um produto que esta no carrinho de compras.
	private void atualizaProdutoCarrinho(View view, ProdutoCompra produtoCompra) {
		
		EditText etValor = (EditText) view.findViewById(R.id.editTextValorProduto);
		EditText etQtde = (EditText) view.findViewById(R.id.editTextQtdeProduto);
		CheckBox cbCalcularQtde = (CheckBox) view.findViewById(R.id.checkBoxMaisQtde);
		RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGOptionUN);
		int selected = rg.getCheckedRadioButtonId();
		RadioButton rbUnidade = (RadioButton) view.findViewById(selected);
		
		BigDecimal qtde = etQtde != null && etQtde.length() > 0 ? new BigDecimal(etQtde.getText().toString()) : null;
		BigDecimal valor = etValor != null && etValor.length() > 0 ? new BigDecimal(etValor.getText().toString()) : null;
		UnidadeMedida unidadeMedidade = rbUnidade != null ? UnidadeMedida.parse(rbUnidade.getText().toString()) : null; 
		
		produtoCompra.setQtde(qtde);
		produtoCompra.setValor(valor);
		produtoCompra.setUnidadeMedida(unidadeMedidade);
		produtoCompra.setValorManual(cbCalcularQtde.isChecked());
		
		ProdutoCompraDao pcDao = new ProdutoCompraDao(this);
		pcDao.update(produtoCompra);
		produtoCompraAdapter.notifyDataSetChanged();
		atualizarCabecalhoProdutoCompra();
		
	}
	
	//Atualiza o valor total do produto.
	private void atualizarValorProdutoQtde(Dialog dialog) {
		
		//Acessa componentes do XML.
		EditText etValorProduto = (EditText) dialog.findViewById(R.id.editTextValorProduto);
		EditText etQtde = (EditText) dialog.findViewById(R.id.editTextQtdeProduto);
		TextView tvValorTotal = (TextView) dialog.findViewById(R.id.textViewValorTotal);
		CheckBox valorManual = (CheckBox) dialog.findViewById(R.id.checkBoxMaisQtde);
	
		String sValorProduto = etValorProduto.getText() != null ? etValorProduto.getText().toString() : null;
		String sQtde = etQtde.getText() != null ? etQtde.getText().toString() : null;
		
		
		//Se o checkBox calcular qtde estiver checado e tiver valor no produto e qtde, faz o c·lculo.
		if(!valorManual.isChecked() && sValorProduto.length() > 0){
			
			BigDecimal qtde = BigDecimal.ZERO;
			
			if(sQtde != null && sQtde.length() > 0) {
				qtde = new BigDecimal(sQtde);
			}
			
			BigDecimal valorProduto = new BigDecimal(sValorProduto);
			BigDecimal valorTotal = sQtde.length() > 0 ? valorProduto.multiply(qtde) : valorProduto;
			
			tvValorTotal.setText(getString(R.string.rs) + " " + StringUtils.formatarMoeda(valorTotal));
			
		} 
		//Se o produto tiver valor e o checkbox n„o estiver checado, È setado apenas o valor do produto.
		else if(valorManual.isChecked() && sValorProduto.length() > 0){
			
			BigDecimal valorProduto = new BigDecimal(sValorProduto);
			tvValorTotal.setText(getString(R.string.rs) + " " + StringUtils.formatarMoeda(valorProduto));
			
		} 
		//Caso nenhuma das condiÁıes acima estejam satisfeitas, seta o valor total como zero.
		else {
			tvValorTotal.setText(getString(R.string.rs) + " " + 0);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//Cada que o Evento onPause for chamado (sair da Activity), È feito um backup 
		//do estado dos produtos ja inseridos no carrinho.
		ProdutoCompraDao produtoCompraDao = new ProdutoCompraDao(this);
		for(ProdutoCompra pc: produtosCompras){
			produtoCompraDao.update(pc);
		}
		
		finish();
	}
	
	private void atualizarCabecalhoProdutoCompra() {
		
		TextView tvCabecalho = (TextView) findViewById(R.id.textViewCabecalhoProdutoCompra);
		TextView tvValor = (TextView) findViewById(R.id.textViewCabecalhoProdutoValor);
		
		tvCabecalho.setText(getString(R.string.carrinho) + ": " + getString(R.string.rs));
		tvValor.setText(StringUtils.formatarMoeda(compra.getCalculoTotalCompra()));
	}
	
	@Override
	public void onBackPressed() {

		Intent compraActivity = new Intent(this, CompraActivity.class);
		startActivity(compraActivity);
		finish();
	}
}
