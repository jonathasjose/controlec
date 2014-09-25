package first.app.controlofbuy.helpers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Classe utilit·ria para formataÁao de campos String.
 * 
 * @author Jonathas JosÈ da ConceiÁ„o
 */
public class StringUtils {
	
	/**
	 * Remove todos os caracteres especiais.
	 * 
	 * @param s Palavra ou frase de entrada.
	 * @return Palavra ou frase formatados sem os caracteres especiais.
	 */
	public static String removerAcentos(String passa) {
	
		  passa = passa.replaceAll("[ÂÀÁÄÃ]","A");  
	      passa = passa.replaceAll("[âãàáä]","a");  
	      passa = passa.replaceAll("[ÊÈÉË]","E");  
	      passa = passa.replaceAll("[êèéë]","e");  
	      passa = passa.replaceAll("ÎÍÌÏ","I");  
	      passa = passa.replaceAll("îíìï","i");  
	      passa = passa.replaceAll("[ÔÕÒÓÖ]","O");  
	      passa = passa.replaceAll("[ôõòóö]","o");  
	      passa = passa.replaceAll("[ÛÙÚÜ]","U");  
	      passa = passa.replaceAll("[ûúùü]","u");  
	      passa = passa.replaceAll("Ç","C");  
	      passa = passa.replaceAll("ç","c");   
	      passa = passa.replaceAll("[ýÿ]","y");  
	      passa = passa.replaceAll("Ý","Y");  
	      passa = passa.replaceAll("ñ","n");  
	      passa = passa.replaceAll("Ñ","N");  

		return passa;
	}
	
	/**
	 * Tratamento para frases ou palavras onde se deseja transformar apenas as primeias letras em mai˙sculas.
	 * 
	 * @param nome Palavra ou frase a serem formatadas;
	 * @return Palavra ou frase j· formatadas.
	 */
	public static String formatarNomeProduto(String nome) {
		
		if(nome != null && nome.length() > 0) {
			//Deixa tudo em min˙sculo
			nome = nome.toLowerCase();
			//Quebra a frase pelo caracter de espaÁo.
			String[] palavras = nome.split(" ");
			//Palavra ou frase final.
			StringBuffer palavraFinal = new StringBuffer();
			
			//Itera as palavras que foram saparadas.
			for(int i = 0; i < palavras.length; i++){
				//Remove os espaÁos.
				String palavra = palavras[i].trim(); 
				//Atualiza se a palavra tiver mais que 3 letras ou for a primeira.
				if(palavra.length() > 3 || i == 0) {
					//Quebra a palavra em letras.
					char[] letra = palavra.trim().toCharArray();
					char[] letraRetorno = new char[letra.length];
					//A primeira letra, transforma em mai˙scula.
					letraRetorno[0] = Character.toUpperCase(letra[0]);
					//Concate o resto das letras.
					for(int z = 1; z < letra.length; z++){
						letraRetorno[z] = letra[z];
					}
					//Se n„o for a primeira palavra, adiciona um espaÁo na frente.
					if(i == 0) {
						palavraFinal.append(new String(letraRetorno));
					} else {
						palavraFinal.append(" " + new String(letraRetorno));
					}
				//Se n„o for a primeira palavra adiciona um espaÁo na frente da palavra.	
				} else if(palavra.length() > 0){

					palavraFinal.append(" " + palavra);
				}
			}
			
			//Palavra formatada.
			return palavraFinal.toString();
		}
			
	
		return "";
	}
	
	public static String formatarMoeda(BigDecimal valor) {
		
		//Formata para moeda com 2 dÌgitos.
		DecimalFormat format = new DecimalFormat("##,###,###,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));
		format.setMinimumFractionDigits(2);
		format.setParseBigDecimal(true);
		
		return format.format(valor);
	}

}
