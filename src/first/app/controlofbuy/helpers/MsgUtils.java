package first.app.controlofbuy.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import first.app.controlofbuy.R;

/**
 * Classe utilizada para armazenar as telas de mensagens.
 * 
 * @author Jonathas José da Conceição
 *
 */
public class MsgUtils extends Activity {
	
	/**
	 * Método cria um AlertDialog com apenas um botão OK.
	 * 
	 * @param msg Mensagem a ser exibida no Dialog.
	 * @param activity Activity utilizada.
	 * @return AlertDialog.
	 */
	public static AlertDialog  msgValidacao(String msg, Activity activity){
		
		//TODO melhorar o design deste Dialog, inserir title, icon, etc.
		AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
		//alertDialog.setTitle("Erro de validação");
		alertDialog.setMessage(msg);
		alertDialog.setButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which) {
		      dialog.cancel();
		   }
		});
		//alertDialog.setIcon(R.drawable.ic_check);
		alertDialog.show();
		
		return alertDialog;
	}
	
}
