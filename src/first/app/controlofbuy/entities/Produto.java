package first.app.controlofbuy.entities;


import java.math.BigDecimal;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import first.app.controlofbuy.entities.ProdutoCompra.UnidadeMedida;
import first.app.controlofbuy.helpers.StringUtils;

@DatabaseTable
public class Produto implements Comparable<Produto>{
	
	public static final String ID = "id";
	public static final String NOME = "nome";
	public static final String NOME_TEMP = "nome_temp";
	public static final String DESCRICAO = "descricao";
	public static final String DATA_CRICAO = "data_criacao";
	public static final String CATEGORIA_ID = "categoria_id";
	
	@DatabaseField(generatedId = true, columnName = ID)
	private Integer id;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = CATEGORIA_ID)
	private ProdutoCategoria categoria;
	
	@DatabaseField(columnDefinition = "COLLATE NOCASE", columnName = NOME)
	private String nome;
	
	//Atributo utilizado nas consulta, SQLite possui sérios problemas com Charset.
	@DatabaseField(columnDefinition = "COLLATE NOCASE", columnName = NOME_TEMP)
	private String nomeTemp;
	
	@DatabaseField(columnName = DESCRICAO)
	private String descricao;
	
	@DatabaseField(columnName = DATA_CRICAO)
	private Date criacao;
	
	//Transiente.
	private boolean selecionado;
	
	//Transiente.
	private BigDecimal qtde;
	
	//Transiente.
	private String unidadeMedida;
	
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
		nomeTemp = StringUtils.removerAcentos(nome);
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
	
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isSelecionado() {
		return selecionado;
	}
	
	public void setCategoria(ProdutoCategoria categoria) {
		this.categoria = categoria;
	}

	public ProdutoCategoria getCategoria() {
		return categoria;
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
		Produto other = (Produto) obj;
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
	public int compareTo(Produto another) {
		return this.getNome().compareTo(another.getNome());
	}

	public BigDecimal getQtde() {
		return qtde;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}

	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida != null ? unidadeMedida.getValor() : null;
	}

	public UnidadeMedida getUnidadeMedida() {
		return UnidadeMedida.parse(unidadeMedida);
	}
	
	public String getNomeTemp() {
		return nomeTemp;
	}

}
