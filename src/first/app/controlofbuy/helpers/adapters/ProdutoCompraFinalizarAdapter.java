package first.app.controlofbuy.helpers.adapters;

import java.math.BigDecimal;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import first.app.controlofbuy.R;
import first.app.controlofbuy.entities.Produto;
import first.app.controlofbuy.entities.ProdutoCompra;
import first.app.controlofbuy.helpers.StringUtils;

/**
 * Adaptador para listar os {@link Produto}s.
 * 
 * @author pc Jonathas José da Conceição
 *
 */
public class ProdutoCompraFinalizarAdapter extends BaseAdapter {
	
	private List<ProdutoCompra> produtosCompra;
	private Context cxt;
	//Classe utilizada para instanciar os objetos XML.
	private LayoutInflater inflater;
	
	public ProdutoCompraFinalizarAdapter(Context cxt, List<ProdutoCompra> produtos) {
		this.cxt = cxt;
		this.produtosCompra = produtos;
		inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void addItem(ProdutoCompra item) {
        
		this.produtosCompra.add(item);
        //Atualizar a lista caso seja adicionado algum item
        notifyDataSetChanged();
    }  
 
	@Override
	public int getCount() {
		
		return produtosCompra.size();
	}

	@Override
	public Object getItem(int position) {
		
		return produtosCompra.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//Acessa o registro da lista.
		ProdutoCompra pc = produtosCompra.get(position);
		
		//Utiliza o layout produto_row para apresentar na tela.
		convertView = inflater.inflate(R.layout.carrinho_finalizar_listview_linha, null);
		
        
        TextView tvCompraNome = (TextView) convertView.findViewById(R.id.textViewProdutoCompraNome);
        
		Produto produto = pc.getProduto();
		
		String nome = produto.getNome();
		String unidade = pc.getUnidadeMedida() != null ? pc.getUnidadeMedida().getValor() : null;
		String qtde = pc.getQtde() != null ? pc.getQtde().toString() : null;
		BigDecimal valorTotal = pc.getValorTotal() != null && pc.getValorTotal().compareTo(BigDecimal.ZERO) > 0 ? pc.getValorTotal() : null;
		
		String barra = " / ";
		StringBuilder descricao = new StringBuilder();
		descricao.append(nome);
		if(qtde != null) {
			descricao.append(barra + qtde);
		}
		if(unidade != null) {
			if(qtde == null) descricao.append(barra + unidade); else descricao.append(unidade);
		}
		if(valorTotal != null) {
			descricao.append(barra + cxt.getString(R.string.rs) + " " + StringUtils.formatarMoeda(valorTotal));
		}
             
        
        //Insere o nome do Produto no TextView do ListView.
        tvCompraNome.setText(descricao.toString());
         
        return convertView;
	}

}
