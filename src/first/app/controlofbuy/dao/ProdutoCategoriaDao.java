package first.app.controlofbuy.dao;

import android.content.Context;
import first.app.controlofbuy.entities.ProdutoCategoria;
import first.app.controlofbuy.helpers.GenericDao;

public class ProdutoCategoriaDao extends GenericDao<ProdutoCategoria>{

	public ProdutoCategoriaDao(Context context) {
		super(context, ProdutoCategoria.class);
	}
}
