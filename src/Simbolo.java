
public class Simbolo {
	private String lexema = "";
	private byte token;
	private String classe = "";
	private String tipo = "";
	private int endereco;
	
	public Simbolo(){
		
	}
	
	public Simbolo(byte token, String lexema, int endereco){
		this.lexema = lexema;
		this.token = token;
		this.endereco = endereco;
	}
	
	public Simbolo(byte token, String lexema, String tipo, int endereco){
		this.lexema = lexema;
		this.token = token;
		this.tipo = tipo;
		this.endereco = endereco;
	}
	
	public Simbolo(String lexema, byte token, String classe, String tipo, int endereco) {
		super();
		this.lexema = lexema;
		this.token = token;
		this.classe = classe;
		this.tipo = tipo;
		this.endereco = endereco;
	}
	
	public byte getToken(){
		return token;
	}
	
	public int getEnd(){
		return endereco;
	}
	
	public String getLexema(){
		return lexema;
	}
}
