package first.app.controlofbuy.dao;

import java.sql.SQLException;

import android.content.Context;
import first.app.controlofbuy.entities.Produto;
import first.app.controlofbuy.entities.ProdutoCompra;
import first.app.controlofbuy.helpers.GenericDao;

public class ProdutoCompraDao extends GenericDao<ProdutoCompra>{

	public ProdutoCompraDao(Context context) {
		super(context, ProdutoCompra.class);
	}
	
	//Verifica se o Produto ja esta vinculado a um ProdutoCompra.
	public boolean hasProdutoInProdutoCompra(Produto produto) throws SQLException {
		
		int count = (int) dao.countOf(dao.queryBuilder()
				.setCountOf(true)
				.where()
				.eq(ProdutoCompra.PRODUTO_ID, produto.getId())
				.prepare());
		
		return count > 0;
	}
}
