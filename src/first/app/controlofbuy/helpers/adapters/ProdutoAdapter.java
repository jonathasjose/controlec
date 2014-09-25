package first.app.controlofbuy.helpers.adapters;

import java.math.BigDecimal;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import first.app.controlofbuy.R;
import first.app.controlofbuy.entities.Produto;
import first.app.controlofbuy.entities.ProdutoCompra.UnidadeMedida;

/**
 * Adaptador para listar os {@link Produto}s.
 * 
 * @author pc Jonathas José da Conceição
 *
 */
public class ProdutoAdapter extends BaseAdapter {
	
	private List<Produto> produtos;
	
	//Classe utilizada para instanciar os objetos XML.
	private LayoutInflater inflater;
	
	public ProdutoAdapter(Context cxt, List<Produto> produtos) {
		
		this.produtos = produtos;
		inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void addItem(Produto item) {
        
		this.produtos.add(item);
        //Atualizar a lista caso seja adicionado algum item
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//Acessa o registro da lista.
		Produto produto = produtos.get(position);
		
		//Utiliza o layout produto_row para apresentar na tela.
		convertView = inflater.inflate(R.layout.produtos_listview_linha, null);
		
		//Instância os objetos do XML
        ImageView icon = (ImageView) convertView.findViewById(R.id.imageViewProdutoCheck);
        icon.setImageResource(R.drawable.ic_launcher);
        
        TextView tvCompraNome = (TextView) convertView.findViewById(R.id.textViewProdutoNome);
        TextView tvQtde = (TextView) convertView.findViewById(R.id.textViewProdutoQtde);
        
        BigDecimal qtde = produto.getQtde();
        UnidadeMedida unidade = produto.getUnidadeMedida();
        
        if(qtde != null) {
        	tvQtde.setText(unidade != null ?  qtde + " " + unidade.getValor() : qtde + ""); 
        // Caso o produto esteja selecionado, mas não tenha quantidade, uma msg de auxílio ao usuário é passada.
        } else if(produto.isSelecionado()){
        	tvQtde.setText(R.string.info_toque_para_inserir_qtde);
        }
             
        //Renderiz o ícone de check caso o produto esteja marcado com seleção.
        if(produto.isSelecionado()) {
        	icon.setImageResource(R.drawable.ic_check);
        } else {
        	icon.setImageResource(R.drawable.ic_check_alpha);
        }
        
        //Insere o nome do Produto no TextView do ListView.
        tvCompraNome.setText(produto.getNome());
         
        return convertView;
	}

}
