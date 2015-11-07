
public class Memoria {
	public static int contador;
	
	public Memoria(){
		contador = 0;
	}
	
	public void alocarTemp(){
		contador += 4000;
	}
	
	public int alocarByte(){
		return contador++;
	}
	
	public int alocarLogico(){
		return contador++;
	}
	
	public int alocarInteiro(){
		return contador += 2;
	}
	
	public int alocarString(){
		return contador += 100;
	}
}
