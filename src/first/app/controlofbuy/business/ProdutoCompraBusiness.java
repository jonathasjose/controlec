package first.app.controlofbuy.business;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import first.app.controlofbuy.R;
import first.app.controlofbuy.business.exception.ControlOfBuyException;
import first.app.controlofbuy.dao.CompraDao;
import first.app.controlofbuy.dao.MercadoDao;
import first.app.controlofbuy.dao.ProdutoCompraDao;
import first.app.controlofbuy.entities.Compra;
import first.app.controlofbuy.entities.Mercado;
import first.app.controlofbuy.entities.Produto;
import first.app.controlofbuy.entities.ProdutoCompra;

/**
 * Classe respons�vel pelas regras do {@link ProdutoCompra}.
 * 
 * @author Jonathas Jos� da Concei��o
 */
public class ProdutoCompraBusiness {
	
	//Contexto da Activity.
	private Context context;
	
	public ProdutoCompraBusiness(Context context) {
		this.context = context;
	}
	
	/**
	 * Salva um {@link Compra} e sua lista de {@link ProdutoCompra}s
	 * 
	 * @param produtosSelecionados Produtos selecionados que far�o parte da compra.
	 * @throws ControlOfBuyException Lan�a uma exception caso ocorra algum erro.
	 */
	public void salvarProdutoCompra(List<Produto> produtosSelecionados) throws ControlOfBuyException {
		
		validacaoProdutoCompra(produtosSelecionados);
		
		CompraDao compraDao = new CompraDao(context);
		ProdutoCompraDao produtoCompraDao = new ProdutoCompraDao(context);
		
		Compra compra = new Compra();
		compraDao.insert(compra);
		
		for(Produto produto: produtosSelecionados){
			
			ProdutoCompra produtoCompra = new ProdutoCompra(produto, compra);
			produtoCompra.setQtde(produto.getQtde());
			produtoCompra.setUnidadeMedida(produto.getUnidadeMedida());
			produtoCompraDao.insert(produtoCompra);
		}
	}


	/**
	 * Atualiza uma {@link Compra} e sua lista de {@link ProdutoCompra}s
	 * 
	 * @param produtosSelecionados Produtos selecionados que far�o parte da compra.
	 * @throws ControlOfBuyException Lan�a uma exception caso ocorra algum erro.
	 */
	public void editarProdutoCompra(List<Produto> produtosSelecionados, Compra compra) throws ControlOfBuyException {
		
		//Faz a valida��o nos Produtos antes de gerar os ProdutoCompra's.
		validacaoProdutoCompra(produtosSelecionados);
		
		//Instancia um dao.
		ProdutoCompraDao produtoCompraDao = new ProdutoCompraDao(context);
		
		//Varre os ProdutoCompra que ja est�o salvos no banco.
		for(ProdutoCompra pc: compra.getProdutosCompra()){
			
			//Se o produto selecionado ja estiver vinculado ao produto compra, atualiza o mesmo.
			if(produtosSelecionados.contains(pc.getProduto())) {
				
				for(int i = 0; i < produtosSelecionados.size(); i++){
					Produto ps = produtosSelecionados.get(i);
					if(ps.equals(pc.getProduto())){
						pc.setQtde(ps.getQtde());
						pc.setUnidadeMedida(ps.getUnidadeMedida());
						produtoCompraDao.update(pc);
						//Remove produto da lista, pois o mesmo ja foi salvo.
						produtosSelecionados.remove(ps);
						i--;
					}
				}
				//Os ProdutoCompra que n�o est�o nos produtos selecionados, s�o removidos.
			} else {
			
				//Se o produtoSelecionado n�o cont�m o produto, remove o produto.
				produtoCompraDao.delete(pc);
			}
		}
		
		//Os Produtos que sobraram s�o produtos novos, logo s�o criado os ProdutoCompra.
		for(Produto produto: produtosSelecionados) {

			ProdutoCompra produtoCompra = new ProdutoCompra(produto, compra);
			produtoCompra.setQtde(produto.getQtde());
			produtoCompra.setUnidadeMedida(produto.getUnidadeMedida());
			produtoCompraDao.insert(produtoCompra);
		}

	}
	
	/**
	 * Valida��o de uma lista de {@link ProdutoCompra}.
	 * 
	 * @param produtosSelecionados produtos que foram selecionados.
	 * @throws ControlOfBuyException Lan�a um exce��o caso ocorra algum erro.
	 */
	private void validacaoProdutoCompra(List<Produto> produtosSelecionados) throws ControlOfBuyException {
		
		//Faz a valida��o da lista, deve existir pelo menos um produto na lista.
		if(produtosSelecionados == null || produtosSelecionados.isEmpty()) {
			throw new ControlOfBuyException(first.app.controlofbuy.R.string.msg_validacao_necessario_pelo_menos_produto);
		}
	}
	
	/**
	 * M�todo utilizado para excluir uma {@link Compra} e todos os {@link ProdutoCompra} que est�o vinculados a ele. 
	 * 
	 * @param compra Compra a ser exclu�da.
	 */
	public void excluirCompra(Compra compra) {
		
		ProdutoCompraDao pcDao = new ProdutoCompraDao(context);
		CompraDao cDao = new CompraDao(context);
		
		Collection<ProdutoCompra> produtos = compra.getProdutosCompra();
		Iterator<ProdutoCompra> iterator = produtos.iterator();
		
		//Deleta todos os Produtos que s�o filhos da Compra.
		while(iterator.hasNext()) {
			
			ProdutoCompra produto = iterator.next();
			pcDao.delete(produto);
		}
		
		//Deleta a compra.
		cDao.delete(compra);
		
	}

	public void salvarMercado(Mercado mercado) throws ControlOfBuyException {
		
		//Valida o Local de compra (Mercado).
		validarMercado(mercado);
		
		MercadoDao mDao = new MercadoDao(context);
		mDao.insert(mercado);
		
	}

	private void validarMercado(Mercado mercado) throws ControlOfBuyException {

		if(mercado == null){
			throw new ControlOfBuyException(R.string.msg_validacao_nome_mercado_nulo);
		}
		
		String nomeMercado = mercado.getNome(); 
		if(nomeMercado == null || nomeMercado.length() == 0) {
			throw new ControlOfBuyException(R.string.msg_validacao_nome_mercado_nulo);
		}
		
		MercadoDao mDao = new MercadoDao(context);
		
		if(mDao.isMercadoByNome(nomeMercado)) {
			throw new ControlOfBuyException(R.string.msg_validacao_nome_duplicado);
		}
	}
	
	public void finalizarCompra(Compra compra) throws ControlOfBuyException {
		
		validarCompra(compra);
		
		compra.setCompraConcluida(true);
		compra.setDataCompra(new Date());
		
		CompraDao cDao = new CompraDao(context);
		cDao.update(compra);
	}

	private void validarCompra(Compra compra) throws ControlOfBuyException {

		if(compra.getValorCompra() == null || compra.getValorCompra().compareTo(BigDecimal.ZERO) == 0) {
			throw new ControlOfBuyException(R.string.msg_validacao_compra_sem_valor);
		}
	}
	
}
