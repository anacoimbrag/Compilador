import java.io.BufferedReader;

public class AnalisadorLexico {
	TabelaSimbolos simbolos = new TabelaSimbolos();
	String lexema = "";
	public boolean dev = false;
	Simbolo s;
	boolean id = false, constante = false;
	char c;
	public static int linha = 0;
	
	Simbolo analisar(boolean devolucao, BufferedReader arquivo) throws Exception{
		int satual = 0;
		int sfinal = 14;
		lexema = "";
				
		if(c == -1){
			//erro
		}
		
		while(satual != sfinal){
			switch(satual){
			case 0:
				if(devolucao == false){
					c = (char)arquivo.read();
				}
				devolucao = false;
				
				if(c == '+' || c == '-' || c == '*' || c == '(' || c == ')' || c == ';' || c == ','){
					lexema += c;
					satual = sfinal;
					dev = false;
				}
				if(c == 32 || c == 11 || c == 8){
					satual = 0;
				}
				else if(c == '/'){
					lexema += c;
					satual = 1;
				}
				else if(c == '<' || c == '>' || c == '='){
					lexema += c;
					satual = 4;
				}
				else if(c == '!'){
					lexema += c;
					satual = 5;
				}
				else if(letra(c) || c == '_'){
					lexema += c;
					satual = 6;
				}
				else if(digito(c)){
					if(c == '0'){
						lexema += c;
						satual = 7;
					}
					else{
						lexema += c;
						satual = 8;
					}
				}else if(c == '"'){
					lexema += c;
					satual = 17;
				}
				break;
			case 1:
				c = (char)arquivo.read();
				if(c == '*'){
					lexema += c;
					satual = 2;
				}else{
					satual = sfinal;
					devolucao = true;
					this.dev = true;
				}
				break;
			case 2:
				c = (char)arquivo.read();
				if(c == '*'){
					satual = 3;
				}else{
					satual = 2;
				}
				break;
			case 3:
				c = (char)arquivo.read();
				if(c == '/'){
					satual = 0;
					lexema = "";
				}else if(c == '*'){
					satual = 3;
				}else{
					satual = 2;
				}
				break;
			case 4:
				c = (char)arquivo.read();
				if(c == '='){
					lexema += c;
					satual = sfinal;
					dev = false;
				}else{
					satual = sfinal;
					devolucao = true;
					dev = true;
				}
				break;
			case 5:
				c = (char)arquivo.read();
				if(c == '='){
					lexema += c;
					satual = sfinal;
					dev = false;
				}else {
					//erro
				}
				break;
			case 6:
				c = (char)arquivo.read();
				if(digito(c) || letra(c) || c == '_'){
					lexema += c;
					satual = 6;
				}else{
					satual = sfinal;
					devolucao = true;
					dev = true;
				}
				break;
			case 7:
				c = (char)arquivo.read();
				if(c == 'x'){
					lexema += c;
					satual = 10;
				}else if(digito(c)){
					lexema += c;
					satual = 9;
				}
				else{
					satual = sfinal;
					devolucao = true;
					dev = true;
				}
				break;
			case 8:
				c = (char)arquivo.read();
				if(digito(c)){
					lexema += c;
					satual = 9;
				}else {
					satual = sfinal;
					devolucao = true;
					dev = true;
				}
				break;
			case 9:
				c = (char)arquivo.read();
				if(digito(c)){
					lexema += c;
					satual = 15;
				}else {
					satual = sfinal;
					devolucao = true;
					dev = true;
				}
				break;
			case 10:
				c = (char)arquivo.read();
				if(digito(c)){
					lexema += c;
					satual = 11;
				}else if(letra(c)){
					lexema += c;
					satual = 12;
				}else{
					satual = sfinal;
					devolucao = true;
					dev = true;
				}
				break;
			case 11:
				c = (char)arquivo.read();
				if(digito(c)){
					lexema += c;
					satual = 13;
				}else{
					if(letra(c)){
						lexema += c;
						satual = sfinal;
					}else{
						satual = sfinal;
						devolucao = true;
						dev = true;
					}
				}
				break;
			case 12:
				c = (char)arquivo.read();
				if(digito(c)){
					lexema += c;
					satual = 13;
				}else{
					if(letra(c)){
						lexema += c;
						satual = sfinal;
					}else{
						satual = sfinal;
						devolucao = true;
						dev = true;
					}
				}
				break;
			case 13:
				c = (char)arquivo.read();
				if(!digito(c)){
					satual = sfinal;
					devolucao = true;
					dev = true;
				}else{
					//erro
				}
				break;
			case 15:
				c = (char)arquivo.read();
				if(digito(c)){
					lexema += c;
					satual = 16;
				}else{
					satual = sfinal;
					devolucao = true;
					dev = true;
				}
				break;
			case 16:
				c = (char)arquivo.read();
				if(digito(c)){
					lexema += c;
					satual = 13;
				}else{
					satual = sfinal;
					devolucao = true;
					dev = true;
				}
				break;
				
			case 17:
				c = (char)arquivo.read();
				if(c == '"'){
					lexema += c;
					satual = sfinal;
					dev = false;
				}else if(c == 11 || c == 8){
					//erro
				}else{
					lexema += c;
					satual = 17;
				}
			}
			
		}
		
		if(simbolos.tabela.get(lexema) != null){
			s = simbolos.tabela.get(lexema);
		}
		else{
			if(letra(lexema.charAt(0)) || lexema.charAt(0) == '_'){
				s = simbolos.inserirID(lexema);
			}
			else if(digito(lexema.charAt(0)) || lexema.charAt(0) == '"' || lexema.charAt(0) == 'F'){
				if(digito(lexema.charAt(0))){
					if(lexema.charAt(0) == '0'){
						if(lexema.length() > 1){
							if(lexema.charAt(1) == 'h'){
								s = simbolos.inserirConst(lexema, "boolean");
							}
							else if(lexema.charAt(1) == 'x'){
								if(lexema.length() >= 3 && lexema.length() <= 4){
									for(int i = 2; i < lexema.length(); i++){
										if(!digito(lexema.charAt(i)) || !letra(lexema.charAt(i))){
											//erro
										}
									}
									s = simbolos.inserirConst(lexema, "byte");
								}
							}else if(lexema.length() <= 5){
								for(char l : lexema.toCharArray()){
									if(!digito(l)){
										//erro
									}
								}
								s = simbolos.inserirConst(lexema, "int");
							}
						}else{
							s = simbolos.inserirConst(lexema, "int");
						}
					}else{
						if(lexema.length() <= 5){
							for(char l : lexema.toCharArray()){
								if(!digito(l)){
									//erro
								}
							}
							s = simbolos.inserirConst(lexema, "int");
						}
					}
				}else if(lexema.charAt(0) == '"' && lexema.charAt(lexema.length() - 1) == '"'){
					s = simbolos.inserirConst(lexema, "string");
				}else if(lexema == "FFh"){
					s = simbolos.inserirConst(lexema, "boolean");
				}
			}else{
				//erro
			}
		}
		
		if(c == '\n' || c == 11){
			linha++;
		}
		
		return s;
	}
	
	public static boolean letra(char c){
		boolean isLetra = false;
		if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
			isLetra = true;
		return isLetra;
	}
	
	public static boolean digito(char c){
		boolean idDigito = false;
		if(c >= '0' && c <= '9')
			idDigito = true;
		return idDigito;
	}
}