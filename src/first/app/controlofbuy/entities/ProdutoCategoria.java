package first.app.controlofbuy.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class ProdutoCategoria {
	
	public static final String ID = "id";
	public static final String NOME = "nome";
	
	@DatabaseField(generatedId = true, columnName = ID)
	private int id;
	
	@DatabaseField(columnName = NOME)
	private String nome;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
