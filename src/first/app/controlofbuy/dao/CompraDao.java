package first.app.controlofbuy.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import android.content.Context;
import android.util.Log;
import first.app.controlofbuy.entities.Compra;
import first.app.controlofbuy.helpers.DatabaseHelper;
import first.app.controlofbuy.helpers.GenericDao;

public class CompraDao extends GenericDao<Compra>{

	public CompraDao(Context context) {
		super(context, Compra.class);
	}
	
	/**
	 * @return Consulta todas as {@link Compra} e ordena pela data de criação.
	 */
	public List<Compra> getAllOrderByData() {
		
		List<Compra> compras = new ArrayList<Compra>();
		
		try {
			QueryBuilder<Compra, Integer> builder = dao.queryBuilder();
			builder.orderBy(Compra.DATA_CRICAO, false);
			PreparedQuery<Compra> prepared = builder.prepare();
			
			compras = dao.query(prepared);
					
		} catch (SQLException e) {
			Log.d(DatabaseHelper.TAG, "Não foi possível realizar a consulta " + e.getLocalizedMessage());
		}
		
		return compras;
	}
}
