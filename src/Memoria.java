
public class Memoria {
	public static int contador;
	public static int contTemp;
	
	public Memoria(){
		contador = 0;
		contTemp = 0;
	}
	
	public void alocarTemp(){
		contador += 16384;
	}
	
	public int alocarByte(){
		int tmp = contador;
		contador++;
		return tmp;
	}
	
	public int alocarLogico(){
		int tmp = contador;
		contador++;
		return tmp;
	}
	
	public int alocarInteiro(){
		int tmp = contador;
		contador += 2;
		return tmp;
	}
	
	public int alocarString(){
		int tmp = contador;
		contador += 256;
		return tmp;
	}
	
	public int novoTemp(){
		return contTemp;
	}
	
	public int alocarTempByte(){
		int tmp = contTemp;
		contTemp++;
		return tmp;
	}
	
	public int alocarTempLogico(){
		int tmp = contTemp;
		contTemp++;
		return tmp;
	}
	
	public int alocarTempInteiro(){
		int tmp = contTemp;
		contTemp += 2;
		return tmp;
	}
	
	public int alocarTempString(){
		int tmp = contTemp;
		contTemp += 256;
		return tmp;
	}
}
