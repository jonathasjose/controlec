package first.app.controlofbuy.helpers.adapters;

import java.math.BigDecimal;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import first.app.controlofbuy.R;
import first.app.controlofbuy.entities.ProdutoCompra;
import first.app.controlofbuy.entities.ProdutoCompra.UnidadeMedida;
import first.app.controlofbuy.helpers.StringUtils;

public class ProdutoCompraAdapter extends BaseAdapter {
	
	private List<ProdutoCompra> produtos;
	
	//Classe utilizada para instanciar os objetos XML.
	private LayoutInflater inflater;
	
	private Context context;
	
	public ProdutoCompraAdapter(Context cxt, List<ProdutoCompra> produtos) {
		
		context = cxt;
		this.produtos = produtos;
		inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void addItem(ProdutoCompra item) {
        
		this.produtos.add(item);
        //Atualizar a lista caso seja adicionado algum item.
        notifyDataSetChanged();
    }  
 
	@Override
	public int getCount() {
		
		return produtos.size();
	}

	@Override
	public Object getItem(int position) {
		
		return produtos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//Acessa o registro da lista.
		ProdutoCompra produto = produtos.get(position);
		
		//Utiliza o layout produto_row para apresentar na tela.
		convertView = inflater.inflate(R.layout.carrinho_listview_linha, null);
		
		//Instância os objetos do XML
        ImageView icon = (ImageView) convertView.findViewById(R.id.imageViewProdutoCompraCheck);
        TextView compraNome = (TextView) convertView.findViewById(R.id.textViewProdutoCompraNome);
        TextView tvInformacoes = (TextView) convertView.findViewById(R.id.textViewProdutoInformacoes);
        
        BigDecimal qtde = produto.getQtde();
        UnidadeMedida unidade = produto.getUnidadeMedida();
        BigDecimal valor = produto.getValor();
        
        StringBuffer informacoes = new StringBuffer();
        String rs = context.getString(R.string.rs);
        
        if(qtde != null) {
        	informacoes.append(qtde + " " + unidade);
        }
        
        String barra = informacoes.length() > 0 ? " | " : "";
        
        if(valor != null) {
    		
        	informacoes.append(barra + rs + " " + StringUtils.formatarMoeda(produto.getValorTotal()));
        
        }
        	
        tvInformacoes.setText(informacoes.length() > 0 ? informacoes.toString() : context.getString(R.string.info_toque_mais_opcoes));
        
        icon.setImageResource(R.drawable.ic_launcher);
        //Renderiz o ícone de check caso o produto esteja marcado com seleção.
        if(produto.isCesto()) {
        	icon.setImageResource(R.drawable.ic_check);
        } else {
        	icon.setImageResource(R.drawable.ic_check_alpha);
        }
        
        compraNome.setText(produto.getProduto().getNome());
         
        return convertView;
	}
	
}
