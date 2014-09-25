package first.app.controlofbuy.helpers;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * Classe genérica para utilizar o DAO.
 * 
 * @author Jonathas José da Conceição;
 * @param <T> Classe DAO que será utilizada.
 */
public class GenericDao<T> extends DatabaseHelper {
	
	protected Dao<T, Integer> dao;
	
	private Class<T> type;
	
	public GenericDao(Context context, Class<T> type) {

		super(context);
		this.type =  type;
		setDao();
	}

	private void setDao() {
		
		try {
			dao = DaoManager.createDao(getConnectionSource(), type);
		} catch (SQLException e) {
			Log.d(DatabaseHelper.TAG, "Erro ao criar Dao: " + e.getLocalizedMessage());
		}
	}
	
	public List<T> getAll() {
		
		List<T> result = null;
		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			Log.d(DatabaseHelper.TAG, "Erro ao carregar a lista : " + e.getLocalizedMessage());
		}
		
		return result;
	}

	
	 public T getById(int id) {
		 
		 T obj = null;
		 try{
			 obj = dao.queryForId(id);
		 }catch(Exception e){
			 Log.d(DatabaseHelper.TAG, "Erro ao carregar objeto : " + e.getLocalizedMessage());
		 }
		 
		 return obj;
	 }

	 public void insert(T obj) {
		 
		 try{
			 dao.create(obj);
		 }catch(Exception e){
			 Log.d(DatabaseHelper.TAG, "Erro ao inserir um objeto : " + e.getLocalizedMessage());
		 }
	 }

	 public void delete(T obj) {
		
		 try{
			 dao.delete(obj);
		 }catch(Exception e){
			 Log.d(DatabaseHelper.TAG, "Erro ao deletar objeto : " + e.getLocalizedMessage());
		 }
	 }

	 public void update(T obj) {
		 
		 try{
			 dao.update(obj); 
		 }catch(Exception e){
			 Log.d(DatabaseHelper.TAG, "Erro ao atualizar objeto : " + e.getLocalizedMessage());
		 }
	 }
	 
	 public QueryBuilder<T, Integer> queryBuilder(){
		 return dao.queryBuilder();
	 }
}
