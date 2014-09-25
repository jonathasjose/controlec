package first.app.controlofbuy.entities;


import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Mercado implements Comparable<Mercado>{
	
	public static final String ID = "id";
	public static final String NOME = "nome";
	public static final String DESCRICAO = "descricao";
	public static final String DATA_CRICAO = "data_criacao";

	
	@DatabaseField(generatedId = true, columnName = ID)
	private Integer id;
	
	@DatabaseField(columnName = NOME)
	private String nome;
	
	@DatabaseField(columnName = DESCRICAO)
	private String descricao;
	
	@DatabaseField(columnName = DATA_CRICAO)
	private Date criacao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getCriacao() {
		return criacao;
	}

	public void setCriacao(Date criacao) {
		this.criacao = criacao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mercado other = (Mercado) obj;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public int compareTo(Mercado another) {
		return nome.compareTo(another.getNome());
	}
	
	
	
}
