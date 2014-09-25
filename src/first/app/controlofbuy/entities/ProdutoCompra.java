package first.app.controlofbuy.entities;

import java.math.BigDecimal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class ProdutoCompra implements Comparable<ProdutoCompra> {
	
	public static enum UnidadeMedida {
		
		UN("UN"),
		KG("KG"),
		DZ("DZ"),
		LT("LT");
		
		private String valor;
		
		private UnidadeMedida(String valor) {
			this.valor = valor;
		}
		
		public String getValor() {
			return valor;
		}
		
		public static UnidadeMedida parse(String valor) {
            if (valor != null) {
                for (UnidadeMedida unidadeMedida : values()) {
                    if (unidadeMedida.valor.equals(valor)) {
                        return unidadeMedida;
                    }
                }
            }

            return null;
        }
	}
	
	public static final String ID = "id";
	public static final String PRODUTO_ID = "produto_id";
	public static final String COMPRA_ID = "compra_id";
	public static final String VALOR = "valor";
	public static final String QTDE = "qtde";
	public static final String UNIDADE_MEDIDA = "unidade_medida";
	public static final String VALOR_MANUAL = "valor_manual";
	public static final String OBSERVACAO = "observacao";
	public static final String CESTO = "cesto";
	
	@DatabaseField(generatedId = true, columnName = ID)
	private Integer id;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = PRODUTO_ID)
	private Produto produto;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = COMPRA_ID)
	private Compra compra;
	
	@DatabaseField(columnName = VALOR)
	private BigDecimal valor;
	
	@DatabaseField(columnName = QTDE)
	private BigDecimal qtde;
	
	@DatabaseField(columnName = UNIDADE_MEDIDA)
	private String unidadeMedida;
	
	@DatabaseField(columnName = VALOR_MANUAL)
	private boolean valorManual;
	
	@DatabaseField(columnName = OBSERVACAO)
	private String observacao;
	
	@DatabaseField(columnName = CESTO)
	private boolean cesto;
	
	public ProdutoCompra() {
		// TODO Auto-generated constructor stub
	}

	public ProdutoCompra(Produto produto) {
		this.produto = produto;
	}

	public ProdutoCompra(Produto produto, Compra compra) {
		this.produto = produto;
		this.compra = compra;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	public BigDecimal getValorTotal() {
		//Se for cálculo manual apresenta o valor do Produto.
		//Caso não seja valor manual, verifica se existe qtde, se sim calcula pela qtde.
		//Caso não, apresenta o valor do produto.
		if(valorManual) {
			return valor != null ? valor : BigDecimal.ZERO; 
		} else {
			if(qtde != null && qtde.compareTo(BigDecimal.ZERO) > 0 && valor != null) {
				return valor.multiply(qtde);
			} else {
				return valor != null ? valor : BigDecimal.ZERO; 
			}
		}
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public boolean isCesto() {
		return cesto;
	}

	public void setCesto(boolean cesto) {
		this.cesto = cesto;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public Compra getCompra() {
		return compra;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}

	public BigDecimal getQtde() {
		return qtde;
	}

	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida != null ? unidadeMedida.getValor() : null;
	}

	public UnidadeMedida getUnidadeMedida() {
		return UnidadeMedida.parse(unidadeMedida);
	}

	public void setValorManual(boolean valorManual) {
		this.valorManual = valorManual;
	}

	public boolean isValorManual() {
		return valorManual;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compra == null) ? 0 : compra.hashCode());
		result = prime * result + ((produto == null) ? 0 : produto.hashCode());
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
		ProdutoCompra other = (ProdutoCompra) obj;
		if (compra == null) {
			if (other.compra != null)
				return false;
		} else if (!compra.equals(other.compra))
			return false;
		if (produto == null) {
			if (other.produto != null)
				return false;
		} else if (!produto.equals(other.produto))
			return false;
		return true;
	}

	@Override
	public int compareTo(ProdutoCompra another) {
		
		String nome1 = this.getProduto().getNomeTemp();
		String nome2 = another.getProduto().getNomeTemp();
		
		return nome1.compareTo(nome2);
	}

}
