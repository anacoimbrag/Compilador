import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.InputStreamReader;

public class Principal {
	static Parse p;
	static BufferedReader arquivo;
	
	static void lerCaminho(){
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));  	
		String file = "";
		try{
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
		}catch (Exception e) {
			System.err.println("Arquivo não encontrado");
			lerCaminho();
		}
	}
	
	public static void main(String[] args) throws Exception{
		try{
			lerCaminho();
			p = new Parse(arquivo);
			p.S();
			System.out.println("Finalizado - sem erros.");
		}catch (Exception e) {
			System.err.println("Erro");
			
		}
	}
}
