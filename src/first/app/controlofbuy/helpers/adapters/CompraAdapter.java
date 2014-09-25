package first.app.controlofbuy.helpers.adapters;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import first.app.controlofbuy.R;
import first.app.controlofbuy.entities.Compra;
import first.app.controlofbuy.entities.Mercado;
import first.app.controlofbuy.entities.Produto;
import first.app.controlofbuy.helpers.StringUtils;

/**
 * Adaptador para listar os {@link Produto}s.
 * 
 * @author pc Jonathas José da Conceição
 *
 */
public class CompraAdapter extends BaseAdapter {
	
	private List<Compra> compras;
	private Context cxt;
	
	//Classe utilizada para instanciar os objetos XML.
	private LayoutInflater inflater;
	
	public CompraAdapter(Context cxt, List<Compra> compras) {
		
		this.cxt = cxt;
		this.compras = compras;
		inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void addItem(Compra item) {
        
		this.compras.add(item);
        //Atualizar a lista caso seja adicionado algum item
        notifyDataSetChanged();
    }  
 
	@Override
	public int getCount() {
		
		return compras.size();
	}

	@Override
	public Object getItem(int position) {
		
		return compras.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//Acessa o registro da lista.
		Compra compra = compras.get(position);
		
		//Utiliza o layout produto_row para apresentar na tela.
		convertView = inflater.inflate(R.layout.compras_listview_linha, null);
		
		//Instância os objetos do XML
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.imageViewAguardando);
        TextView tvDataCompra = (TextView) convertView.findViewById(R.id.textViewDataCompra);
        TextView tvDescricao = (TextView) convertView.findViewById(R.id.textViewCompraDescricao);
        

		SimpleDateFormat formt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvDataCompra.setText(formt.format(compra.getDataCriacao()));
        
        if(compra.isCompraConcluida()) {
        	
        	Mercado mercado = compra.getMercado();
        	String local = mercado != null ? " / " + mercado.getNome() : "";
        	String valor = cxt.getString(R.string.rs) + " " + StringUtils.formatarMoeda(compra.getValorCompra());
        	tvDescricao.setText(valor + local);
        	ivIcon.setVisibility(ImageView.GONE);
        } else {
        	tvDescricao.setText(cxt.getString(R.string.compra_nao_finalizada));
        	ivIcon.setImageResource(R.drawable.ic_timer);
        }
         
        return convertView;
	}

}
