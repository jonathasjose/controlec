package first.app.controlofbuy.entities;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Compra  {
	
	public static final String ID = "id";
	public static final String MERCADO_ID = "mercado_id";
	public static final String OBSERVACAO = "observacao";
	public static final String DATA_CRICAO = "data_criacao";
	public static final String DATA_COMPRA = "data_compra";
	public static final String VALOR_COMPRA = "valor_compra";
	public static final String COMPRA_CONCLUIDA = "compra_concluida";
	public static final String PRODUTOS_COMPRA = "produtos_compra";
	
	@DatabaseField(generatedId = true, columnName = ID)
	private Integer id;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = MERCADO_ID)
	private Mercado mercado;
	
	@DatabaseField(columnName = OBSERVACAO)
	private String observacao;
	
	@DatabaseField(columnName = DATA_CRICAO)
	private Date dataCriacao;

	@DatabaseField(columnName = DATA_COMPRA)
	private Date dataCompra;
	
	@DatabaseField(columnName = VALOR_COMPRA)
	private BigDecimal valorCompra;
	
	@DatabaseField(columnName = COMPRA_CONCLUIDA)
	private boolean compraConcluida;
	
	@ForeignCollectionField(eager = true, columnName = PRODUTOS_COMPRA)
	private Collection<ProdutoCompra> produtosCompra;
	
	public Compra() {
		dataCriacao = new Date();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isCompraConcluida() {
		return compraConcluida;
	}

	public void setCompraConcluida(boolean compraConcluida) {
		this.compraConcluida = compraConcluida;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCompra(Date dataCompra) {
		this.dataCompra = dataCompra;
	}

	public Date getDataCompra() {
		return dataCompra;
	}

	public void setMercado(Mercado mercado) {
		this.mercado = mercado;
	}

	public Mercado getMercado() {
		return mercado;
	}

	public void setProdutosCompra(Collection<ProdutoCompra> produtosCompra) {
		this.produtosCompra = produtosCompra;
	}

	public Collection<ProdutoCompra> getProdutosCompra() {
		return produtosCompra;
	}
	

	public BigDecimal getValorCompra() {
		return valorCompra;
	}

	public void setValorCompra(BigDecimal valorCompra) {
		this.valorCompra = valorCompra;
	}
	
	
	//Métodos auxiliares
	public BigDecimal getCalculoTotalCompra() {
		
		BigDecimal total = BigDecimal.ZERO;
		
		for(ProdutoCompra pc: produtosCompra) {
			total = total.add(pc.getValorTotal());
		}
		
		return total;
	}
}
