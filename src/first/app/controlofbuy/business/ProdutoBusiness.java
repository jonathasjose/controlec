package first.app.controlofbuy.business;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import first.app.controlofbuy.R;
import first.app.controlofbuy.business.exception.ControlOfBuyException;
import first.app.controlofbuy.dao.ProdutoCompraDao;
import first.app.controlofbuy.dao.ProdutoDao;
import first.app.controlofbuy.entities.Compra;
import first.app.controlofbuy.entities.Produto;
import first.app.controlofbuy.entities.ProdutoCompra;

/**
 * Classe responsável pelas regras de uma {@link Produto}.
 * 
 * @author Jonathas José da Conceição
 */
public class ProdutoBusiness {
	
	//Contexto da Activity.
	private Context context;
	
	/**
	 * Construtor utilizado para inicializar o Contexto.
	 * 
	 * @param context Contexto de persistencia.
	 */
	public ProdutoBusiness(Context context) {
		this.context = context;
	}
	
	/**
	 * Consulta os {@link Produto}s utilizando um filtro.
	 * 
	 * @param string Filtro utilizado para consulta.
	 * @param compra Compra, caso seja uma edição de uma Compra.
	 * @return Lista de {@link Produto}s encontrados.
	 */
	public List<Produto> consultarProdutosComCompra(String consulta, Compra compra) {
		
		List<Produto> produtos = consultarProdutos(consulta);
		
		if(compra != null){
			for(Produto p: produtos){
				for(ProdutoCompra pc: compra.getProdutosCompra()){
					if(p.equals(pc.getProduto())) {
						p.setQtde(pc.getQtde());
						p.setUnidadeMedida(pc.getUnidadeMedida() != null ? pc.getUnidadeMedida() : null);
						p.setSelecionado(true);
					}
				}
			}
		}
		
		return produtos;
	}

	public List<Produto> consultarProdutos(String consulta) {
		
		ProdutoDao produtoDao = new ProdutoDao(context);
		return produtoDao.getAllOrdemNome(consulta);
	}
	
	/**
	 * Valida e Salva um {@link Produto}.
	 * 
	 * @param produto produto a ser salvo.
	 */
	public void salvarProduto(Produto produto) throws ControlOfBuyException{
		
		//Validação do Produto.
		validacaoProduto(produto);
		
		ProdutoDao produtoDao = new ProdutoDao(context);
		
		//Atualiza ou salva um Produto.
		if(produto.getId() != null) produtoDao.update(produto); else produtoDao.insert(produto);
	}
	
	/**
	 * Validação de um {@link Produto}.
	 * 
	 * @param produtosSelecionados produtos que foram selecionados.
	 * @throws ControlOfBuyException Lança um exceção caso ocorra algum erro.
	 */
	private void validacaoProduto(Produto produto) throws ControlOfBuyException {
		
		if(produto.getNome() == null || produto.getNome().length() < 2) {
			throw new ControlOfBuyException(first.app.controlofbuy.R.string.msg_validacao_nome_produto_invalido);
		}
		
		if(produto.getCategoria() == null) {
			throw new ControlOfBuyException(first.app.controlofbuy.R.string.msg_validacao_e_nessario_escolher_categoria);
		}
		
	}

	/**
	 * Valida e Exclui um {@link Produto}.
	 * 
	 * @param produto {@link Produto} a ser excluído.
	 * @throws ControlOfBuyException Lança uma exceção caso ocorra algum erro. 
	 * @throws SQLException Lança uma exceção caso ocorra algum erro.
	 */
	public void excluirProduto(Produto produto) throws ControlOfBuyException, SQLException {
		
		ProdutoCompraDao pcDao = new ProdutoCompraDao(context); 		
		ProdutoDao pDao = new ProdutoDao(context);
		
		//Verifica se o Produto ja esta vinculado a uma Compra.
		if(pcDao.hasProdutoInProdutoCompra(produto)) {
			throw new ControlOfBuyException(R.string.msg_validacao_produto_ja_esta_vinculado_compra);
		}
		
	    pDao.delete(produto);	
	}

}
