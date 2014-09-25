package first.app.controlofbuy.activities;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import first.app.controlofbuy.R;
import first.app.controlofbuy.business.ProdutoBusiness;
import first.app.controlofbuy.business.ProdutoCompraBusiness;
import first.app.controlofbuy.business.exception.ControlOfBuyException;
import first.app.controlofbuy.dao.CompraDao;
import first.app.controlofbuy.dao.ProdutoCategoriaDao;
import first.app.controlofbuy.dao.ProdutoDao;
import first.app.controlofbuy.entities.Compra;
import first.app.controlofbuy.entities.Produto;
import first.app.controlofbuy.entities.ProdutoCategoria;
import first.app.controlofbuy.entities.ProdutoCompra;
import first.app.controlofbuy.entities.ProdutoCompra.UnidadeMedida;
import first.app.controlofbuy.helpers.DatabaseHelper;
import first.app.controlofbuy.helpers.MsgUtils;
import first.app.controlofbuy.helpers.StringUtils;
import first.app.controlofbuy.helpers.adapters.ProdutoAdapter;

/**
 * Activity utilizada nas funcionalidades de gerar, editar, excluir {@link Compra}.
 * 
 * @author Jonathas JosÈ da ConceiÁ„o.
 *
 */
public class ProdutoActivity extends Activity {
	
	//Lista que armazena todos os produtos.
	private List<Produto> produtos = new ArrayList<Produto>();
	//Lista que armazena os produtos selecionados.
	private List<Produto> produtosSelecionados = new ArrayList<Produto>();
	//Adaptador que ir· exibir os produtos.
	private ProdutoAdapter produtoAdapter;
	//Booleano que controla se o usu·rio esta vizualisando todos Produtos ou Produtos selecionados.
	private boolean isTodosProdutos = true;
	//Listview do XML.
	private ListView listViewProdutos;
	//EditText do XML.
	private EditText editFilter;
	//Compra
	private Compra compra;
	//Business Compra
	private ProdutoBusiness produtoBusiness;
	//Menu de opção visualizar 
	private MenuItem itemVisualizar;
	//Chave IDCOMPRA utilizado para passar o parametro pela Activity.
	public static final String COMPRA_ID = "COMPRA_ID"; 
	
	// Método é invocado ao criar a Activity.
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//Inicializa a Business 
		produtoBusiness = new ProdutoBusiness(this);
		
		//Acessa idCompra que vem por par‚metro da Activity, e carrega a Compra do banco.
		int idCompra = getIntent().getIntExtra(CompraActivity.COMPRA_ID, 0);
		CompraDao compraDao = new CompraDao(this);
		compra = compraDao.getById(idCompra);
		
		//Se a compra estiver concluída, envia para a tela de resumo.
		if(compra != null && compra.isCompraConcluida()) {
			Intent compraFinalizada = new Intent(getBaseContext(), FinalizarCompraActivity.class);
			compraFinalizada.putExtra(COMPRA_ID, compra.getId());
			startActivityForResult(compraFinalizada, RESULT_OK);
			finish();
		}
		
		setContentView(R.layout.produtos_lista);
		
		//Carrega todos os Produtos atravÈs do DAO, caso a compra ja exista, finculado dados da compra ao produto.
		produtos = produtoBusiness.consultarProdutosComCompra("", compra);
				
		//Atualiza a lista de produtos selecionados.
		atualizaProdutosSelecionados();
	
		//Acessa o listView do XML.
		listViewProdutos = (ListView) findViewById(R.id.listViewProdutos);
				
		//Cria o adaptador e passamos a lista para ele. 
		produtoAdapter = new ProdutoAdapter(this, produtos);

		//Seta o adaptador na ListView.
		listViewProdutos.setAdapter(produtoAdapter);
		
		//Acessa o widget EditText do XML.
		editFilter = (EditText) findViewById(R.id.editTextPesquisar);
		
		//Invocado quando um item da ListView È clicado.
		listViewProdutos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				//Verifica em qual lista o usu·rio esta, e acessa o produto.
				Produto produto = isTodosProdutos ? produtos.get(position) : produtosSelecionados.get(position);
				//Se o produto ja estiver selecionado, abre Dialog para ediÁ„o da qtde.
				if(produto.isSelecionado()) editarQtdeProduto(produto); else addRemoverProduto(produto);
				//Atualiza o adaptador.
				produtoAdapter.notifyDataSetChanged();
			}
		});
		
		listViewProdutos.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				//Registra o Menu de contexto.
				registerForContextMenu(listViewProdutos);
				return false;
			}
		});
		
		//Listener invocado quando uma tecla È executada.
		editFilter.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			//MÈtodo È invocado após a tecla ser precionada.
			@Override
			public void afterTextChanged(Editable s) {
				
				//Filtro È feito apenas na view de todos os produtos. 
				if(isTodosProdutos){
					
					//Faz a consulta pelo filtro.
					produtos = produtoBusiness.consultarProdutos(s.toString());
					//Atualiza a nova lista de produto com os produtos que ja foram selecionados.
					atualizarProdutos();
					//Atualiza o adaptador e notifica.
					produtoAdapter = new ProdutoAdapter(getBaseContext(), produtos);
					listViewProdutos.setAdapter(produtoAdapter);
					produtoAdapter.notifyDataSetChanged();
				}
			}
			
		});
	}
	

	/**
	 * Cria Menu de OpÁıes do Android.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		return createMenu(menu);
	}
	
	/**
	 * Invocado quando um Item do Menu de OpÁıes È clicado.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		menuChoice(item);
		return true;
	}
	
	// Cria um Menu de Opção para a Activity.
	private boolean createMenu(Menu menu) {
		
		try {
			menu.add(0,0,0,R.string.menuoptions_vis_sel);
			//Se compra for != nulll È uma ediÁ„o.
			if(compra != null) {
				//Se estiver editando, mostra menu com texto de atualizar.
				menu.add(0,1,1,R.string.menuoptions_atualizar_lista_compra);
			} else {
				//Se n„o estiver editando, mostra menu com texto de gerar compra.
				menu.add(0,1,1,R.string.menuoptions_gerar_compra);
			}
			
			return true;
			
		} catch (Exception e) {
			Log.d(DatabaseHelper.TAG, "Erro ao criar menu");
			return false;
		}
	}
	
	/**
	 * Invocado quando um Item do Menu de OpÁ„o È selecionado.
	 * 
	 * @param item Item a ser selecionado.
	 */
	public void menuChoice(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case 0:
			//Armazena o Menu visualizar em uma variável de instância.
			itemVisualizar = item;
			//Conforme a posição da chave, visualiza todos produtos ou selecionados. 
			if(isTodosProdutos) setVerProdutosSelecionados(); else setVerProdutosTodos();
			break;

		case 1:
			//Gerar lista de compra.
			gerarListaCompra(true);
			break;
		}
	}
	
	//Evento È invocado quando o bot„o voltar do celular È pressionado.
	@Override
	public void onBackPressed() {
		
		//Se possui dados no filtro, limpa e encerra o mÈtodo.
		if(editFilter.getText().toString().length() > 0){
			editFilter.setText(null);
			return;
		}
		
		//Se n„o estiver visualizando todos produtos, manda pra lista de todos produtos e encerra o mÈtodo.
		if(!isTodosProdutos){
			setVerProdutosTodos();
			return;
		}
		
		//Gera uma lista de compra, o false significa que n„o È pra ter mensagem de validação.
		gerarListaCompra(false);
	}
	
	/**
	 * MÈtodo para cadastrar um novo {@link Produto}.
	 * 
	 * @param view Componente do XML.
	 */
	public void cadastrarProduto(View view) {

		String nomeProduto = editFilter.getText().toString();
		Produto produto = new Produto();
		produto.setNome(nomeProduto);
		
		// Cria um Dialog para editar/salvar um produto.
		createDialogSalvarEditarProduto(produto);
	}
	
	//Menu de contexto do Produto.
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, view, menuInfo);
		menu.setHeaderTitle(R.string.opcoes);
		getMenuInflater().inflate(R.menu.menu_opcoes_compra, menu);
	}
	
	//Quando um item do Produto È pressionado o menu de contexto È invocado.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final int pos = info.position;
		
		switch (item.getItemId()) {
		
		case R.id.excluir_compra:
			
			int sim = R.string.sim;
			int cancel = R.string.cancel;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.info_deseja_realmente_excluir)
			.setPositiveButton(sim, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					//Verifica de qual lista È o Produto. 
					Produto produto = isTodosProdutos ? produtos.get(pos) : produtosSelecionados.get(pos);
					//Exclui o produto.
					excluirProduto(produto);
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
			
			Produto produto = isTodosProdutos ? produtos.get(pos) : produtosSelecionados.get(pos);
			//Carrega o Dialog para salvar ou editar o Produto.
			createDialogSalvarEditarProduto(produto);
			break;
		}
		
		return super.onContextItemSelected(item);
	}
	
	//Excluir um produto.
	private void excluirProduto(Produto produto) {
		try {
			produtoBusiness.excluirProduto(produto);
			produtos.remove(produto);
			produtosSelecionados.remove(produto);
			visualizarProdutos();
			
		} catch (ControlOfBuyException e) {
			MsgUtils.msgValidacao(getString(e.getMensagem()), this);
		} catch (SQLException e) {
			MsgUtils.msgValidacao(e.getMessage(), this);
		}
	}
	
	//Adiciona ou remove um Produto que foi selecionado.
	private void addRemoverProduto(Produto produtoSelecionado) {
		
		Produto produto = null;
		//Encontra o produto da lista principal.
		for(Produto p: produtos){
			if(p.equals(produtoSelecionado)) {
				produto = p;
				break;
			}
		}
		
		//Se o produtoSelecionado estiver na lista.
		if(produtosSelecionados.contains(produtoSelecionado)) {
			//Se o produto como n„o selecionado.
			produto.setSelecionado(false);
			//Remove ele da lista.
			produtosSelecionados.remove(produtoSelecionado);
		} else {
			//Se o produto n„o estiver na lista de produto selecionado adiciona o mesmo.
			produto.setSelecionado(true);
			//Adiciona o produto na lista de selecionados. 
			produtosSelecionados.add(produtoSelecionado);
			//Ordena a lista de produtos selecionados.
			Collections.sort(produtosSelecionados);
		}
	}
	
	//Atualiza a situaÁ„o dos Produtos de acordo com os Produtos que ja foram selecionados.
	private void atualizarProdutos() {
		
		for(Produto produto: produtos){
			for(Produto produtoSelecionado: produtosSelecionados) {
				//Verifica se o Produto esta na lista de produto selecionado.
				if(produtoSelecionado.equals(produto)) {
					//Atualiza o produto com o que esta salvo na lista de produto selecionado.
					produto.setSelecionado(true);
					produto.setQtde(produtoSelecionado.getQtde());
					produto.setUnidadeMedida(produtoSelecionado.getUnidadeMedida());
				}
			}
		}
	}
	
	
	//Atualiza a lista de produtos selecionados atravÈs da lista produto.
	private void atualizaProdutosSelecionados() {

		for(Produto produto: produtos){
			if(produto.isSelecionado()) {
				produtosSelecionados.add(produto);
			}
		}
	}
	
	//Remove dados que foram adicionados ao {@link Produto}.
	private void limparDadosProduto(Produto produto){
		
		produto.setSelecionado(false);
		produto.setQtde(null);
		produto.setUnidadeMedida(null);
		produtosSelecionados.remove(produto);
		//Atualiza o adaptador.
		produtoAdapter.notifyDataSetChanged();
	}
	
	//MÈtodo para inserir Quantidade e Unidade de Medida no {@link Produto}.
	private void editarQtdeProduto(final Produto produto) {
		
		BigDecimal qtde = produto.getQtde();
		UnidadeMedida uMProduto = produto.getUnidadeMedida();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		
		builder.setTitle(getString(R.string.atualizar_produto));
		//Infla o XML que ser· o Dialog.
		final View view = inflater.inflate(R.layout.produtos_edit_dialog, null);
		
		//Alimenta os componentes do XML com valores que o produto ja possui.
		final CheckBox cbSelecao = (CheckBox) view.findViewById(R.id.checkBoxMaisQtde);
		RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGOptionUNProduto);
		TextView tvNomeProduto = (TextView) view.findViewById(R.id.textViewProdutoNome);
		EditText etQtde = (EditText) view.findViewById(R.id.editTextQtdeProduto);
		tvNomeProduto.setText(produto.getNome());
		
		//Se o produto já possuir qtde atualiza a tela.
		if(qtde != null && qtde.compareTo(BigDecimal.ZERO) > 0) {
			etQtde.setText(qtde.toString());
		}
		
		int i = 0;
		for(ProdutoCompra.UnidadeMedida unidadeMedida: ProdutoCompra.UnidadeMedida.values()){
			
			RadioButton rb = new RadioButton(this);
			rb.setText(unidadeMedida.getValor());
			rb.setTextColor(Color.BLACK);
			
			if(unidadeMedida.equals(uMProduto)) {
				rb.setChecked(true);
			}
			
			rg.addView(rb);
			
			if(i > 0) {
				LayoutParams params = (LayoutParams) rb.getLayoutParams();
				params.leftMargin = 15;
				rb.setLayoutParams(params);
			}
			
			i++;
		}
		
		builder.setView(view)
			.setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
										
					//Se o produto j· estiver checado, abre pra inserir qtde, caso contr·rio desmarca o mesmo.
					if(cbSelecao.isChecked()) atualizarProdutoQtde(view, produto); else limparDadosProduto(produto);
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.cancel();
				}
			});
		
		final Dialog dialog = builder.create();
		dialog.show();
		
		//Invocado quando o checkbox È alterado. 
		cbSelecao.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				// Limpa o produto (desvincula de um produto selecionado) e cancela o Dialog.
				limparDadosProduto(produto);
				dialog.cancel();
			}
		});
		
	}
	
	//MÈtodo È utilizado para atualizar qtde e unidade de medida de um produto.
	private void atualizarProdutoQtde(View view, Produto produto) {
		
		CheckBox cbSelecao = (CheckBox) view.findViewById(R.id.checkBoxMaisQtde);
		
		//Se o checkbox estiver marcado, atualiza o Produto.
		if(cbSelecao.isChecked()){
			
			EditText etQtde = (EditText) view.findViewById(R.id.editTextQtdeProduto);
			RadioGroup rgUnidade = (RadioGroup) view.findViewById(R.id.radioGOptionUNProduto);
			int selected = rgUnidade.getCheckedRadioButtonId();
			
			String qtde = etQtde != null ? etQtde.getText().toString() : null;
			
			//Se o campo quantidade for preenchido, atualiza o produto. 
			if(qtde != null && qtde.length() > 0){
				
				//Atualiza o Produto na lista produtos.
				for(Produto p: produtos){
					if(p.equals(produto)) {
						p.setQtde(new BigDecimal(qtde));

						if(selected >= 0) {
							RadioButton b = (RadioButton) view.findViewById(selected);
							p.setUnidadeMedida(UnidadeMedida.parse(b.getText().toString()));
						}
					}
				}
				
				//Atualiza o Produto na lista produtosSelecionados.
				for(Produto p: produtosSelecionados){
					if(p.equals(produto)) {
						p.setQtde(new BigDecimal(qtde));
						
						if(selected >= 0) {
							RadioButton b = (RadioButton) view.findViewById(selected);
							p.setUnidadeMedida(UnidadeMedida.parse(b.getText().toString()));
						}
					}
				}
			}
			
			//Notifica o adaptador.
			produtoAdapter.notifyDataSetChanged();
		} 
	}
	
	//Atualiza e notifica o Adaptador com os Produtos ou os Produtos Selecionados.
	private void visualizarProdutos() {
		
		//Atualiza o Adaptador e Notifica com a lista de Produto (Selecionados ou Todos) que ser· exibida na View.
		produtoAdapter = !isTodosProdutos ? new ProdutoAdapter(this, produtosSelecionados) : new ProdutoAdapter(this, produtos);
		listViewProdutos.setAdapter(produtoAdapter);
		produtoAdapter.notifyDataSetChanged();
	}
	
	
	// Gera a Lista de compra com os produtos selecionados.
	private void gerarListaCompra(boolean verMsg){
		
		try {
			//Instancia a Business de ProdutoCompra.
			ProdutoCompraBusiness pcBusiness = new ProdutoCompraBusiness(this);

			if(compra != null) {
				//Atualiza a compra.
				pcBusiness.editarProdutoCompra(produtosSelecionados, compra);
			} else {
				//Salva uma nova compra.
				pcBusiness.salvarProdutoCompra(produtosSelecionados);
			}
			
		} catch (ControlOfBuyException e) {
			if(verMsg){
				//Msg de validaÁ„o caso a business lance alguma exceÁ„o.
				MsgUtils.msgValidacao(getString(e.getMensagem()), this);
			}
		}
		
		// Se estiver editando a compra, inicia a Activity de Compra, caso contr·rio envia para a tela principal
		int isProdutoCompra = getIntent().getIntExtra(ProdutoCompraActivity.PRODUTO_COMPRA, 0);
		if(isProdutoCompra > 0) startActivityProdutoCompra(); else startActivityPrincipal();
	}
	
	
	//Visualiza todos os produtos.
	private void setVerProdutosTodos() {
		
		//Altera a chave para todos os produtos.
		isTodosProdutos = true;
		//Limpa o filter.
		editFilter.setText(null);
		//Acessa o Menu de OpÁ„o e altera o tÌtulo.
		itemVisualizar.setTitle(R.string.menuoptions_vis_sel);
		//Visualiza os produtos.
		visualizarProdutos();
		//Acessa o bot„o do filtro.
		ImageView btAddProduto = (ImageView) findViewById(R.id.botaoAdicionarProduto);
		//Exibe o bot„o e filtro.
		editFilter.setVisibility(TextView.VISIBLE);
		btAddProduto.setVisibility(ImageView.VISIBLE);
		
		TableLayout tlCabecalho = (TableLayout) findViewById(R.id.tableLCabecalhoProduto);
		tlCabecalho.setVisibility(TableLayout.GONE);
	}
	
	//Visualiza todos os produtos que foram selecionados.
	private void setVerProdutosSelecionados() {
		
		//Altera a chave para todos os produtos selecionados.
		isTodosProdutos = false;
		//Acessa o Menu de OpÁ„o e altera o tÌtulo.
		itemVisualizar.setTitle(R.string.menuoptions_vis_all_produtos);
		//Visualiza os produtos.
		visualizarProdutos();
		//Acessa o bot„o do filtro.
		ImageView btAddProduto = (ImageView) findViewById(R.id.botaoAdicionarProduto);
		//Exibe o bot„o e filtro.
		editFilter.setVisibility(TextView.GONE);
		btAddProduto.setVisibility(ImageView.GONE);
		
		TableLayout tlCabecalho = (TableLayout) findViewById(R.id.tableLCabecalhoProduto);
		tlCabecalho.setVisibility(TableLayout.VISIBLE);
	}
	
	// Inicial a activity princial e finaliza a atual.
	private void startActivityPrincipal() {
		
		Intent compraActivity = new Intent(this, CompraActivity.class);
		startActivity(compraActivity);
		finish();
	}

	// Inicializa a activity ProdutoCompra e finaliza a atual.
	private void startActivityProdutoCompra() {
		
		Intent produtoCompraActivity = new Intent(this, ProdutoCompraActivity.class);
		produtoCompraActivity.putExtra(COMPRA_ID, compra.getId());
		startActivityForResult(produtoCompraActivity, RESULT_OK);
		finish();
	}
	
	// Cria um Dialog para editar/salvar um produto.
	private Dialog createDialogSalvarEditarProduto(final Produto produto) {
		
		ProdutoCategoriaDao categoriaDao = new ProdutoCategoriaDao(this);
		//Carrega as categorias.
		final List<ProdutoCategoria> categorias = categoriaDao.getAll();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		
		builder.setTitle(R.string.cadastrar_produto);
		final View view = inflater.inflate(R.layout.produtos_cadastro_dialog, null);
		
		
		builder.setView(view)
			.setPositiveButton(getString(R.string.salvar), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					//Salva um produto.
					salvarProduto(view, categorias, produto);
					
					//Recarrega a lista utilizando o que foi digitado no filtro.
					ProdutoDao dao = new ProdutoDao(getBaseContext());
					EditText filter = (EditText) findViewById(R.id.editTextPesquisar);
					produtos = dao.getAllOrdemNome(filter.getText().toString());
					//Atualiza a nova lista de produto com os produtos que ja foram selecionados.
					atualizarProdutos();
					//Atualiza o adaptador e notifica.
					produtoAdapter = new ProdutoAdapter(getBaseContext(), produtos);
					listViewProdutos.setAdapter(produtoAdapter);
					produtoAdapter.notifyDataSetChanged();
				}

			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.cancel();
				}
			});
		
		Dialog dialog = builder.create();
		dialog.show();
		
		
		//Carrega o ListView de categorias
		ListView listViewCategoria = (ListView) dialog.findViewById(R.id.listViewCategorias);
		List<String> descricoes = getDescricao(categorias);
		//Acessa a categoria que esta setada no produto.
		ProdutoCategoria pc = produto.getCategoria() != null ? produto.getCategoria() : null;
		String categoria = pc != null ? pc.getNome() : null;
		
		//Seta o listView modo escolha.
		listViewCategoria.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		//Atualiza o adaptador de categoria.
		ArrayAdapter<String> adaptarCategoria = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, descricoes);
		listViewCategoria.setAdapter(adaptarCategoria);
		
		//Utilizado para setar a categoria deu um Produto no XML.
		for(int i = 0; i < descricoes.size(); i++){
			
			String dCategoria = descricoes.get(i);
			if(dCategoria.equals(categoria)) {
				listViewCategoria.setItemChecked(i, true);
			}
		}
		
		//Seta o nome do produto no input no XML.
		EditText editTextNome = (EditText) dialog.findViewById(R.id.editTextNomeProduto);
		editTextNome.setText(StringUtils.formatarNomeProduto(produto.getNome()));
		
		return dialog;
	}
	
	//Cria uma lista de nome de categoria.
	private List<String> getDescricao(List<ProdutoCategoria> categorias) {
		
		List<String> nomes = new ArrayList<String>();
		for(ProdutoCategoria categoria: categorias){
			nomes.add(categoria.getNome());
		}
		
		return nomes;
	}
	
	//Salvar um Produto e sua Categoria.
	private void salvarProduto(View view, List<ProdutoCategoria> categorias, final Produto produto) {
		
		EditText editTextNome = (EditText) view.findViewById(R.id.editTextNomeProduto);
		ListView listCategoria = (ListView) view.findViewById(R.id.listViewCategorias);
		
		//Seta a categoria que foi marcada no Produto.
		if(listCategoria.getCheckedItemPosition() >= 0) {
			ProdutoCategoria categoria = categorias.get(listCategoria.getCheckedItemPosition());
			produto.setCategoria(categoria);
		}
		
		//Trata o nome do produto.
		final String nomeProduto = StringUtils.formatarNomeProduto(editTextNome.getText().toString());
		produto.setNome(nomeProduto);
		
		try {
			//Salva o Produto.
			produtoBusiness.salvarProduto(produto);
		} catch (ControlOfBuyException e) {
			
			// Mensagem de validaÁ„o.
			AlertDialog d = MsgUtils.msgValidacao(this.getString(e.getMensagem()), this);
			d.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					//Abre novamente o dialog para os ajustes apÛs a msg de validaÁ„o.
					createDialogSalvarEditarProduto(produto);
				}
			});
		}
	}
}
