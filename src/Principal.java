import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.InputStreamReader;

public class Principal {
	static Parse p;
	static BufferedReader arquivo;
	
	public static void main(String[] args) throws Exception{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));  
		String file = "";
		
		do{
			System.out.print("Digite o nome do arquivo: ");
			file = in.readLine();
			if(file.length() > 0){
				if(file.charAt(file.length()-2) != '.' && file.charAt(file.length() - 1) != 'l' && file.charAt(file.length() - 1) != 'L'){
					System.err.println("Arquivo não compatível!");
					System.out.print("Digite o nome do arquivo: ");
					file = in.readLine();
				}
			}
			
		}while(file.length() == 0);

		arquivo = new BufferedReader(new FileReader(file));
		p = new Parse(arquivo);
		p.S();
	}
}
