package first.app.controlofbuy.activities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import first.app.controlofbuy.R;
import first.app.controlofbuy.business.ProdutoCompraBusiness;
import first.app.controlofbuy.business.exception.ControlOfBuyException;
import first.app.controlofbuy.dao.CompraDao;
import first.app.controlofbuy.dao.MercadoDao;
import first.app.controlofbuy.entities.Compra;
import first.app.controlofbuy.entities.Mercado;
import first.app.controlofbuy.entities.ProdutoCompra;
import first.app.controlofbuy.helpers.MsgUtils;
import first.app.controlofbuy.helpers.StringUtils;
import first.app.controlofbuy.helpers.adapters.ProdutoCompraFinalizarAdapter;

/**
 * Activity utilizada para finalizar uma compra.
 * 
 * @author Jonathas J. C.
 *
 */
public class FinalizarCompraActivity extends Activity {
	
	private Activity activity;
	private List<Mercado> mercados = new ArrayList<Mercado>();
	private Compra compra;
	private Dialog dialog = null;
	public static final String COMPRA_ID = "COMPRA_ID";
	
	//Primeiro m�todo executado pela Activity.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carrinho_finalizar_compra);
		activity = this;
		
		int idCompra = getIntent().getIntExtra(ProdutoCompraActivity.COMPRA_ID, 0);
		CompraDao CDAo = new CompraDao(this);
		//Carrega a compra pelo ID.
		compra = CDAo.getById(idCompra);
		
		//Carrega os produtos da compra.
		ArrayList<ProdutoCompra> produtosCompra = new ArrayList<ProdutoCompra>(compra.getProdutosCompra());
		Collections.sort(produtosCompra);
		ListView lvProdutos = (ListView) findViewById(R.id.listViewProdutos);
		ProdutoCompraFinalizarAdapter pcAdapter = new ProdutoCompraFinalizarAdapter(this, produtosCompra);
		lvProdutos.setAdapter(pcAdapter);
		
		//M�todo utilizado para atuzaliar os campos no XML.
		atualizarCampos(compra);
	}
	
	//M�todo utilizado para atuzaliar os campos no XML.
	private void atualizarCampos(Compra compra) {
		
		EditText etValor = (EditText) findViewById(R.id.editTextValorProduto);
		TextView tvCabecalho = (TextView) findViewById(R.id.textViewCabecalhoFinalizarCompra);
		TextView tvLocalCompra = (TextView) findViewById(R.id.textViewLocalCompra);
		EditText etObservacoes = (EditText) findViewById(R.id.editTextObservacoesMercado);
		Button btFinalizar = (Button) findViewById(R.id.btFinalizarCompra);
		RadioGroup rgMercado = (RadioGroup) findViewById(R.id.radioGMercado);
		
		
		//Se a compra estiver conclu�da, atualiza os atributos da tela.
		if(compra.isCompraConcluida()) {
			
			//Atualiza o cabe�alho
			tvCabecalho.setText(getString(R.string.resumo_compra));
			//Remove o bot�o e radia group.
			btFinalizar.setVisibility(View.GONE);
			rgMercado.setVisibility(View.GONE);
			
			//Define um local de compra.
			tvLocalCompra.setVisibility(View.VISIBLE);
			String localCompra;
			if(compra.getMercado() != null) {
				localCompra = getString(R.string.local_compra) + ": " + compra.getMercado().getNome();
			} else {
				localCompra = getString(R.string.local_compra) + ": " + getString(R.string.nao_definido);
			}
			tvLocalCompra.setText(localCompra);
			
			if(compra.getObservacao() != null && compra.getObservacao().length() > 0) {
				etObservacoes.setEnabled(false);
				etObservacoes.setText(getString(R.string.observacoes) + ": " + compra.getObservacao());
			} else {
				etObservacoes.setVisibility(View.GONE);
				View viewValorCompra = findViewById(R.id.viewLocalCompra);
				TableLayout trObs = (TableLayout) findViewById(R.id.tableRowObservacoes);
				trObs.setVisibility(View.GONE);
				viewValorCompra.setVisibility(View.GONE);
			}
		}
		
		//Se a compra ja estiver valor, atualizar o campo.
		BigDecimal totalCompraProdutos = compra.getCalculoTotalCompra();
		BigDecimal totalCompra = compra.getValorCompra();
		
		//Se a compra ja tem valor apresenta o mesmo.
		if(totalCompra != null) {
			
			etValor.setText(StringUtils.formatarMoeda(totalCompra));
			
		//Caso contr�rio, faz o c�lculo com base no valor dos produtos.	
		} else if(totalCompraProdutos.compareTo(BigDecimal.ZERO) > 0) {
			
			etValor.setText(StringUtils.formatarMoeda(totalCompraProdutos));
		}
	}
	
	/**
	 * Utilizado para escolher um local de compra.
	 * 
	 * @param view XML que cont�m os componentes.
	 */
	public void definirMercado(View view) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		builder.setTitle(R.string.definir_mercado);
		
		//Infla o XML utilizado no dialog.
		final View viewInflado = inflater.inflate(R.layout.carrinho_mercado_lista, null);
		
		builder.setView(viewInflado)
			.setPositiveButton(getString(R.string.salvar), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog2, int which) {
					
					EditText etNomeLocal = (EditText) dialog.findViewById(R.id.editTextMercadoNome);
					String nomeLocal = etNomeLocal.getText().toString();
					if(nomeLocal != null && nomeLocal.length() > 0){
						

						ProdutoCompraBusiness pcBusiness = new ProdutoCompraBusiness(getBaseContext());
						Mercado mercado = new Mercado();
						mercado.setNome(StringUtils.formatarNomeProduto(nomeLocal));
						
						try {
							
							pcBusiness.salvarMercado(mercado);
							mercados = atualizarListaMercado(dialog);
							compra.setMercado(mercado);
							
							RadioButton rb = (RadioButton) findViewById(R.id.radioBMercado);
							rb.setText(mercado.getNome());
							rb.setChecked(true);
						
						} catch (ControlOfBuyException e) {
							
							String msg = getBaseContext().getString(e.getMensagem());						
							MsgUtils.msgValidacao(msg, activity);
						}
					} else {
					
						ListView l = (ListView) dialog.findViewById(R.id.listViewOpcoesMercado);
						int pos = l.getCheckedItemPosition();
						//Verifica se algum item foi selecionado.
						if(pos >= 0) {
							//Acessa o item pela posicao encontrada.
							Mercado mercado = mercados.get(pos); 
							compra.setMercado(mercado);
							//Atualiza a tela com o Local encontrado.
							RadioButton rb = (RadioButton) findViewById(R.id.radioBMercado);
							rb.setText(mercado.getNome());
							rb.setChecked(true);
						} 
					}
				}
			})
			.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.cancel();
				}
			});
		
		dialog = builder.create();
		dialog.show();
		
		//Carrega todos os locais (mercados) que foram adicionados.
		final MercadoDao mDao = new MercadoDao(this);
		mercados = mDao.getAll();
		Collections.sort(mercados);
		final List<String> descricao = getDescricaoMercados(mercados);
		
		//Atualiza o adaptador e o listview.
		ListView lvMercado = (ListView) dialog.findViewById(R.id.listViewOpcoesMercado);
		final ArrayAdapter<String> adapterMercado = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, descricao);
		lvMercado.setAdapter(adapterMercado);
		lvMercado.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		
		//Listener quando � clicado na imagem pra adicionar um novo mercado.
		ImageView ivAddLocal = (ImageView) dialog.findViewById(R.id.imageViewAdicionarLocal);
		ivAddLocal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final TextView tvMercado = (TextView) dialog.findViewById(R.id.editTextMercadoNome);
				String nomeMercado = tvMercado.getText() != null ? tvMercado.getText().toString() : null;

				ProdutoCompraBusiness pcBusiness = new ProdutoCompraBusiness(getBaseContext());
				Mercado mercado = new Mercado();
				mercado.setNome(StringUtils.formatarNomeProduto(nomeMercado));
				
				try {
					
					pcBusiness.salvarMercado(mercado);
					tvMercado.setText(null);
					mercados = atualizarListaMercado(dialog);
				
				} catch (ControlOfBuyException e) {
					
					String msg = getBaseContext().getString(e.getMensagem());						
					MsgUtils.msgValidacao(msg, activity);
				}
			}
		});
		
		//Long click para remover um local.
		lvMercado.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
				
				final Mercado mercado = mercados.get(pos);
				if(mercado != null){
					
					int sim = R.string.sim;
					int cancel = R.string.cancel;
					
					AlertDialog.Builder alert = new AlertDialog.Builder(activity);
					alert.setMessage(R.string.info_deseja_realmente_excluir);
					alert.setPositiveButton(sim, new android.content.DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog2, int which) {
							
							mDao.delete(mercado);
							mercados = atualizarListaMercado(dialog);
						}
					})
					.setNegativeButton(cancel, new android.content.DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.cancel();
						}
					})
					.create()
					.show();
				}
				
				return false;
			}
		});
		

	}

	//Retorna um lista de descri�ao dos mercados.
	private List<String> getDescricaoMercados(List<Mercado> mercados) {
		
		ArrayList<String> descricao = new ArrayList<String>();
		for(Mercado mercado: mercados) {
			descricao.add(mercado.getNome());
		}
		//Ordena a lista.
		Collections.sort(descricao);
		
		return descricao;
	}
	
	//Refaz a consulta e atualiza a lista de mercado.
	private List<Mercado> atualizarListaMercado(Dialog dialog) {
		
		final MercadoDao mDao = new MercadoDao(this);
		final List<Mercado> mercados = mDao.getAll();
		
		Collections.sort(mercados);
		
		ListView lvMercado = (ListView) dialog.findViewById(R.id.listViewOpcoesMercado);
		final ArrayAdapter<String> adapterMercado = 
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, getDescricaoMercados(mercados));
		lvMercado.setAdapter(adapterMercado);
		lvMercado.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		
		return mercados;
	}
	
	//Utilizado para finalizar um compra.
	public void finalizarCompra(View view) {
		
		int sim = R.string.sim;
		int cancel = R.string.cancel;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.info_deseja_realmente_finalizar);
		builder.setPositiveButton(sim, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

				
				EditText etValor = (EditText) findViewById(R.id.editTextValorProduto);
				String valor = etValor.getText() != null ? etValor.getText().toString() : null;

				EditText etObservacoes = (EditText) findViewById(R.id.editTextObservacoesMercado);
				String observacoes = etObservacoes.getText() != null ? etObservacoes.getText().toString() : null;
				
				if(valor != null && valor.length() > 0) {
					valor = valor.replace(",", ".");
					compra.setValorCompra(new BigDecimal(valor));
				}
				if(observacoes != null) {
					compra.setObservacao(observacoes);
				}
				
				ProdutoCompraBusiness pcBusiness = new ProdutoCompraBusiness(getBaseContext());
				try {
					
					pcBusiness.finalizarCompra(compra);
					Intent compraActivity = new Intent(activity, CompraActivity.class);
					startActivity(compraActivity);
					finish();
				
				} catch (ControlOfBuyException e) {
					
					MsgUtils.msgValidacao(getString(e.getMensagem()), activity);
				}
			}
		});
		builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		});
		builder.create();
		builder.show();

	}
	
	@Override
	public void onBackPressed() {
		
		Intent compraActivity = new Intent(this, CompraActivity.class);
		startActivity(compraActivity);
		finish();
	}
}
