
public class Rotulo {
	static int contador;
	
	public Rotulo(){
		contador = 0;
	}
	
	public String novoRotulo(){
		return "R" + contador++;
	}
}
