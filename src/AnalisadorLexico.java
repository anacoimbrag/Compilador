import java.io.BufferedReader;

public class AnalisadorLexico {
	TabelaSimbolos simbolos = new TabelaSimbolos();
	String lexema = "";
	public boolean dev = false;
	Simbolo s;
	boolean id = false, constante = false;
	char c;
	public static int linha = 1;
	public boolean comentario = false;
	public boolean EOF = false;
	
	Simbolo analisar(boolean devolucao, BufferedReader arquivo) throws Exception{
		int satual = 0;
		int sfinal = 14;
		lexema = "";
				
		while(satual != sfinal){
			switch(satual){
			case 0:
				if(devolucao == false){
					c = (char)arquivo.read();
				}
				devolucao = false;
				
				if(c == '\n' || c == 11){
					linha++;
				}
				else if(c == '+' || c == '-' || c == '*' || c == '(' || c == ')' || c == ';' || c == ','){
					lexema += c;
					satual = sfinal;
					dev = false;
				}
				else if(c == 32 || c == 11 || c == 8 || c == 13 || c == 9){
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
				else if(c == 65535){
                    satual = sfinal;
                    lexema += c;
                    EOF = true;
                    dev = false;
                    arquivo.close();
                }else{
                	System.err.println(linha + ":Caractere inválido");
					System.exit(0);
                }
				break;
			case 1:
				c = (char)arquivo.read();
				if(c == '*'){
					lexema += c;
					comentario = true;
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
				}else if(c == 13){
					satual = 2;
					linha ++;
				}else if(c == -1 || c == 65535){
					EOF = true;
					System.err.println(linha + ":Fim de arquivo não esperado");
					System.exit(0);
				}else{
					satual = 2;
				}
				break;
			case 3:
				c = (char)arquivo.read();
				if(c == '/'){
					satual = 0;
					lexema = "";
					comentario = false;
				}else if(c == '*'){
					satual = 3;
				}else if(c == -1 || c == 65535){
					EOF = true;
					System.err.println(linha + ":Fim de arquivo não esperado");
					System.exit(0);
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
					System.err.println(linha + ":Caracter inválido.");
					System.exit(0);
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
				}else if(c == 'h'){
					lexema += c;
					satual = sfinal;
					dev = false;
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
						dev = false;
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
						dev = false;
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
					System.err.println(linha + ":Caracter inválido.");
					System.exit(0);
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
				//System.out.println(linha+"teste: "+(int)c);
				if(c == '"'){
					lexema += c;
					satual = sfinal;
					dev = false;
				}else if(c == -1 || c == 65535){
					EOF = true;
					System.err.println(linha + ":Fim de arquivo não esperado");
					System.exit(0);
				}else if(c == 13 || c == 10 || c == 11 || c == 8 && !digito(c) && !letra(c) && c != '+' && c != '-' && c != '*' && c != '(' && c != ')' && c != ';' && c != ',' && c != '/' && c != '>' && c != '<' && c != '=' && c != '"'){
					System.err.println(linha + ":Caracter inválido.");
					System.exit(0);
				}else{
					lexema += c;
					satual = 17;
				}
				break;
			}
			
		}
		
		if(!EOF){
			if(simbolos.tabela.get(lexema.toLowerCase()) != null){
				s = simbolos.tabela.get(lexema.toLowerCase());
			}
			else{
				if(lexema.toLowerCase().equals("true") || lexema.toLowerCase().equals("false") ){
					s = simbolos.inserirConst(lexema, "tipo_logico");
				}else if(lexema == "FFh"){
					s = simbolos.inserirConst(lexema, "tipo_logico");
				}else if(letra(lexema.charAt(0)) || lexema.charAt(0) == '_'){
					s = simbolos.inserirID(lexema);
				}
				else if(digito(lexema.charAt(0)) || lexema.charAt(0) == '"'){
					if(digito(lexema.charAt(0))){
						if(lexema.charAt(0) == '0'){
							//começa com 0 e tamanho maior que um, pode ser INTEIRO, BYTE ou LOGICO
							if(lexema.length() > 1){
								//0h é tipo LOGICO
								if(lexema.charAt(1) == 'h'){
									s = simbolos.inserirConst(lexema, "tipo_logico");
								}
								//0x garante ser hexadecimal
								else if(lexema.charAt(1) == 'x'){
									//hexadecimal tem que ser tamanho 4
									if(lexema.length() == 4){
										for(int i = 2; i < lexema.length(); i++){
											if((lexema.charAt(i)<'A' || lexema.charAt(i)>'F') && !(digito(lexema.charAt(i)))){
												System.err.println(linha + ":Lexema não esperado: " + lexema);
												System.exit(0);
											}
										}
										s = simbolos.inserirConst(lexema, "tipo_byte");
									//0x tamanho menor que 4 ERRO
									}else if(lexema.length() < 4){
										System.err.println(linha + ":Lexema não esperado: " + lexema);
										System.exit(0);
									}
																	
								}else if(lexema.length() <= 5){
									for(char l : lexema.toCharArray()){
										if(!digito(l)){
											System.err.println(linha + ":Lexema não esperado: " + lexema);
											System.exit(0);
										}
									}
									s = simbolos.inserirConst(lexema, "tipo_inteiro");
								}
							//começa com 0 e tem um digito só
							}else if(lexema.length() == 1){
								s = simbolos.inserirConst(lexema, "tipo_byte");
							}
						}else{
							if(lexema.length() <= 5){
								//começa com numero e tem algo diferente de digito - ERRO
								for(char l : lexema.toCharArray()){
									if(!digito(l)){
										System.err.println(linha + ":Caracter inválido.");
										System.exit(0);
									}
								}
								int lex = 0;
								lex = Integer.parseInt(lexema);
								//se for entre 0 e 255 = byte
								if(lex >= 0 && lex <= 255){
									s = simbolos.inserirConst(lexema, "tipo_byte");
								}else{
									s = simbolos.inserirConst(lexema, "tipo_inteiro");
								}
							//mais de 5 digitos = ERRO
							}else{
								System.err.println(linha + ":Lexema não identificado: " + lexema);
								System.exit(0);
							}
						}
					}else if(lexema.charAt(0) == '"' && lexema.charAt(lexema.length() - 1) == '"'){
						s = simbolos.inserirConst(lexema, "tipo_string");
					}else{
						System.err.println(linha + "::Lexema não identificado: " + lexema);
						System.exit(0);
					}
				}else{
					System.err.println(linha + ":Lexema não identificado: " + lexema);
					System.exit(0);
				}
			}
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