package first.app.controlofbuy.dao;

import java.sql.SQLException;

import android.content.Context;
import first.app.controlofbuy.entities.Mercado;
import first.app.controlofbuy.helpers.GenericDao;

public class MercadoDao extends GenericDao<Mercado>{

	public MercadoDao(Context context) {
		super(context, Mercado.class);
	}

	public boolean isMercadoByNome(String nomeMercado) {

		try {
			int count = (int) dao.countOf(dao.queryBuilder()
					.setCountOf(true)
					.where()
					.eq(Mercado.NOME, nomeMercado)
					.prepare());
			
			return count > 0;
			
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return false;
	}

}
