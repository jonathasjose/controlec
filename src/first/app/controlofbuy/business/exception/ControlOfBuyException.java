package first.app.controlofbuy.business.exception;

public class ControlOfBuyException extends Exception{
	
	static final long serialVersionUID = 1L;
	private int msg;
	
	public ControlOfBuyException(int msg) {
		this.msg = msg;
	}
	
	public int getMensagem(){
		return msg;
	}
}
