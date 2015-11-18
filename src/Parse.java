import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Parse {
	AnalisadorLexico lexico;
	TabelaSimbolos tabela;
	Simbolo s;
	BufferedReader arquivo;
	Memoria memoria;
	Rotulo rotulo;
	Buffer b;
	int endereco = memoria.contador;
	
	int F_end = 0;
	int T_end = 0;
	int Exps_end = 0;
	int Exp_end = 0;
	
	Parse(BufferedReader arquivo){
		try{
			this.arquivo = arquivo;
			lexico = new AnalisadorLexico();
			tabela = new TabelaSimbolos();
			s = lexico.analisar(lexico.dev, arquivo);
			memoria = new Memoria();
			rotulo = new Rotulo();
			b = new Buffer();
			if(s == null){ // comentario
				s = lexico.analisar(lexico.dev, arquivo);
			}
		}catch(Exception e){System.out.print(e.getMessage());}
	}
	
	void casaToken(byte token){
		try{
			if(s != null){
				//System.out.println(s.getTipo());
				//System.out.println(s.getClasse());
				if(s.getToken() == token){
					s = lexico.analisar(lexico.dev, arquivo);
				}else{
					if(lexico.EOF){
						System.err.println(lexico.linha + ":Fim de Arquivo não esperado.");
						System.exit(0);
					}else{
						System.err.println(lexico.linha + ":Token não esperado: " + s.getLexema());
						System.exit(0);
					}	
				}
			}
		}catch(Exception e){
			System.err.println("casaT" + e.toString());
		}
	}
	
	
	
	 void S(){
		 try{
			 if(lexico.EOF){
				 System.err.println(lexico.linha + ":Fim de arquivo não esperado.");
				 System.exit(0);
			 }
			 if(s != null){
				b.buffer.add("sseg SEGMENT STACK ;início seg. pilha");
				b.buffer.add("byte 4000h DUP(?) ;dimensiona pilha");
				b.buffer.add("sseg ENDS ;fim seg. pilha");
				b.buffer.add("dseg SEGMENT PUBLIC ;início seg. dados");
				b.buffer.add("byte 4000h DUP(?) ;temporários");
				endereco = memoria.alocarTemp();
		 		while(s.getToken() == tabela.FINAL || s.getToken() == tabela.INT || s.getToken() == tabela.BOOLEAN || s.getToken() == tabela.BYTE || s.getToken() == tabela.STRING){
					D();
				}
		 		b.buffer.add("dseg ENDS ;fim seg. dados");
		 		b.buffer.add("cseg SEGMENT PUBLIC ;início seg. código");
		 		b.buffer.add("ASSUME CS:cseg, DS:dseg");
		 		b.buffer.add("strt:");
		 		b.buffer.add("mov ax, dseg");
		 		b.buffer.add("mov ds, ax");
				B();
				b.buffer.add("mov ah, 4Ch");
				b.buffer.add("int 21h");
				b.buffer.add("cseg ENDS ;fim seg. código");
				b.buffer.add("END strt ;fim programa");
				
				b.otimizar();
				b.criarArquivo();
				if(!lexico.EOF){
					System.err.println(lexico.linha + ":Token não esperado: " + s.getLexema());
					System.exit(0);
				}
			 }
		 }catch(Exception e){
			System.err.println(e.toString());
		}
	}
	
	void D() throws Exception{
		String D_classe = "", D_tipo = "";
		Simbolo temp = s;
		boolean minus = false;
		if(s.getToken() == tabela.FINAL){
			casaToken(tabela.FINAL);
			/* Acao Semantica 1 */
			temp = s;
			if(!s.getClasse().equals("")){
				//erro
				System.out.println(lexico.linha+":identificador ja declarado ["+s.getLexema()+"]");
				System.exit(0);
			}else{
				s.setClasse("classe_const");
			}
			casaToken(tabela.ID);
			casaToken(tabela.RECIEVE);
			if(s.getToken() == tabela.MINUS){
				minus = true;
				casaToken(tabela.MINUS);
				if(s.getTipo().equals("tipo_string")){
					System.err.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
			}else{
				minus = false;
			}
			
			/* Acao Semantica */
			temp.setTipo(s.getTipo());
			String lexTemp = s.getLexema();
			if(s.getLexema().toLowerCase().equals("true")){
				lexTemp = "0FFh";
			}else if(s.getLexema().toLowerCase().equals("false")){
				lexTemp = "0h";
			}
			if(minus){
				endereco = memoria.alocarInteiro();
				b.buffer.add("sword -" + lexTemp + " ; valor negativo " + temp.getLexema());
				temp.setTipo("tipo_inteiro");
			}else{
				switch(temp.getTipo()){
					case "tipo_byte":
						endereco = memoria.alocarByte();
						b.buffer.add("byte " + lexTemp + " ; byte " + temp.getLexema());
						break;
					case "tipo_logico":
						endereco = memoria.alocarLogico();
						b.buffer.add("byte " + lexTemp + " ; boolean " + temp.getLexema());
						break;
					case "tipo_inteiro":
						endereco = memoria.alocarInteiro();
						b.buffer.add("sword " + lexTemp + " ; inteiro " + temp.getLexema());
						break;
					case "tipo_string":
						endereco = memoria.alocarString();
						b.buffer.add("byte " + s.getLexema().substring(0, s.getLexema().length() - 1) + "$" + s.getLexema().charAt(s.getLexema().length() - 1));					
						break;
				}
			}
			temp.setEndereco(endereco);
			casaToken(tabela.CONST);
		}else if(s.getToken() == tabela.INT || s.getToken() == tabela.BOOLEAN || s.getToken() == tabela.BYTE || s.getToken() == tabela.STRING){
			D_tipo = tipo();
			temp = s;
			if(!s.getClasse().equals("")){
				//erro
				System.out.println(lexico.linha+":identificador ja declarado ["+s.getLexema()+"]");
				System.exit(0);
			}else{
				/* Ação semântica */
				s.setClasse("classe_var");
				s.setTipo(D_tipo);
			}
			casaToken(tabela.ID);
			if(s.getToken() == tabela.RECIEVE){
				if(temp.getClass().equals("classe_const")){
					//erro
					System.out.println(lexico.linha+"classe de identificador incompativel ["+s.getLexema()+"]");
					System.exit(0);
				}
				casaToken(tabela.RECIEVE);
				if(s.getToken() == tabela.MINUS){
					minus = true;
					casaToken(tabela.MINUS);
					if(s.getTipo().equals("tipo_string")){
						System.err.println(lexico.linha + ":tipos incompativeis.");
						System.exit(0);
					}
				}else{
					minus = false;
				}
				/* Acao Semantica */
				if(!temp.getTipo().equals(s.getTipo()) && !(temp.getTipo().equals("tipo_inteiro") && s.getTipo().equals("tipo_byte"))){
					//erro
					System.out.println(lexico.linha+"tipos incompativeis.");
					System.exit(0);
				}
				String lexTemp = s.getLexema();
				if(s.getLexema().toLowerCase().equals("true")){
					lexTemp = "0FFh";
				}else if(s.getLexema().toLowerCase().equals("false")){
					lexTemp = "0h";
				}
				
				if(minus){
					endereco = memoria.alocarInteiro();
					b.buffer.add("sword -" + lexTemp + " ; valor negativo " + temp.getLexema());
					temp.setTipo("tipo_inteiro");
				}else{
				
					switch(temp.getTipo()){
						case "tipo_byte":
							b.buffer.add("byte " + lexTemp + " ; byte " + temp.getLexema());
							endereco = memoria.alocarByte();
							break;
						case "tipo_logico":
							b.buffer.add("byte " + lexTemp + " ; byte " + temp.getLexema());
							endereco = memoria.alocarLogico();
							break;
						case "tipo_inteiro":
							b.buffer.add("sword " + lexTemp + " ; byte " + temp.getLexema());
							endereco = memoria.alocarInteiro();
							break;
						case "tipo_string":
							b.buffer.add("byte " + s.getLexema().substring(0, s.getLexema().length() - 1) + "$" + s.getLexema().charAt(s.getLexema().length() - 1));
							endereco = memoria.alocarString();
							break;
					}
				}
				temp.setEndereco(endereco);
				System.out.println(temp.getLexema() + "-" + temp.getEndereco());
				
				casaToken(tabela.CONST);
			}else{
				switch(temp.getTipo()){
					case "tipo_byte":
						endereco = memoria.alocarByte();
						b.buffer.add("byte ? ;byte " + temp.getLexema());
						
						break;
					case "tipo_logico":
						endereco = memoria.alocarLogico();
						b.buffer.add("byte ? ;logico " + temp.getLexema());
						
						break;
					case "tipo_inteiro":
						endereco = memoria.alocarInteiro();
						b.buffer.add("sword ? ;inteiro " + temp.getLexema());
						
						break;
					case "tipo_string":
						endereco = memoria.alocarString();
						b.buffer.add("byte 100h DUP(?) ;string " + temp.getLexema());
						
						break;
				}
				temp.setEndereco(endereco);
			}
			while(s.getToken() == tabela.COMMA){
				casaToken(tabela.COMMA);
				temp = s;
				if(!s.getClasse().equals("")){
					//erro
					System.out.println(lexico.linha+":identificador ja declarado ["+s.getLexema()+"]");
					System.exit(0);
				}else{
					/* Ação semântica */
					s.setClasse("classe_var");
					s.setTipo(D_tipo);
				}
				casaToken(tabela.ID);
				if(s.getToken() == tabela.RECIEVE){
					casaToken(tabela.RECIEVE);
					if(s.getToken() == tabela.MINUS){
						casaToken(tabela.MINUS);
					}
					/* Acao Semantica */
					if(!temp.getTipo().equals(s.getTipo()) || !(temp.getTipo().equals("tipo_inteiro") && s.getTipo().equals("tipo_byte"))){
						//erro
						System.out.println(lexico.linha+"tipos incompativeis.");
						System.exit(0);
					}
					
					String lexTemp = s.getLexema();
					if(s.getLexema().toLowerCase().equals("true")){
						lexTemp = "0FFh";
					}else if(s.getLexema().toLowerCase().equals("false")){
						lexTemp = "0h";
					}
					if(minus){
						endereco = memoria.alocarInteiro();
						b.buffer.add("sword -" + lexTemp + "; valor negativo " + temp.getLexema());
						temp.setTipo("tipo_inteiro");
					}else{					
						switch(temp.getTipo()){
							case "tipo_byte":
								b.buffer.add("byte " + lexTemp + "; valor positivo " + temp.getLexema());
								endereco = memoria.alocarByte();
								break;
							case "tipo_logico":
								b.buffer.add("byte " + lexTemp + "; valor positivo " + temp.getLexema());
								endereco = memoria.alocarLogico();
								break;
							case "tipo_inteiro":
								b.buffer.add("sword " + lexTemp + "; valor positivo " + temp.getLexema());
								endereco = memoria.alocarInteiro();
								break;
							case "tipo_string":
								b.buffer.add("byte " + s.getLexema().substring(0, s.getLexema().length() - 1) + "$" + s.getLexema().charAt(s.getLexema().length() - 1));
								endereco = memoria.alocarString();
								break;
						}
					}
					temp.setEndereco(endereco);
					
					casaToken(tabela.CONST);
				}else{
					temp.setEndereco(endereco);
					switch(temp.getTipo()){
						case "tipo_byte":
							endereco = memoria.alocarByte();
							b.buffer.add("byte ? ;byte " + temp.getLexema());
							
							break;
						case "tipo_logico":
							endereco = memoria.alocarLogico();
							b.buffer.add("byte ? ;logico " + temp.getLexema());
							
							break;
						case "tipo_inteiro":
							endereco = memoria.alocarInteiro();
							b.buffer.add("sword ? ;inteiro " + temp.getLexema());
							break;
						case "tipo_string":
							endereco = memoria.alocarString();
							b.buffer.add("byte 100h DUP(?) ; string " + temp.getLexema());
							
							break;
					}
				}
			}
		}
		casaToken(tabela.DOTCOMMA);		
	}
	
	String tipo() throws Exception{
		if(s.getToken() == tabela.INT){
			casaToken(tabela.INT);
			/* Acao semantica */
			return "tipo_inteiro";
		}else if(s.getToken() == tabela.BOOLEAN){
			casaToken(tabela.BOOLEAN);
			/* Acao semantica */
			return "tipo_logico";
		}else if(s.getToken() == tabela.BYTE){
			casaToken(tabela.BYTE);
			/* Acao semantica */
			return "tipo_byte";
		}else if(s.getToken() == tabela.STRING){
			casaToken(tabela.STRING);
			/* Acao semantica */
			return "tipo_string";
		}else{
			System.err.println(lexico.linha + ":Token não esperado.");
			System.exit(0);
			return null;
		}
	}
	
	void B() throws Exception{
		casaToken(tabela.BEGIN);
		while(s.getToken() == tabela.ID || s.getToken() == tabela.WHILE || s.getToken() == tabela.IF || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN || s.getToken() == tabela.DOTCOMMA){
			C();
		}
		casaToken(tabela.END);
	}
	
	void C() throws Exception{
		String C_tipo = "";
		String Exp_tipo = "";
		Simbolo tmp;
		boolean conv = false;
		if(s.getToken() == tabela.ID){
			/* Acao Semantica */
			if(s.getClasse() == ""){
				//erro
				System.err.println(lexico.linha + ":identificador nao declarado ["+s.getLexema()+"]");
				System.exit(0);
			}else if(s.getClasse().equals("classe-const")){
				//erro
				System.err.println(lexico.linha + ":classe de identificador incompatível ["+s.getLexema()+"]");
				System.exit(0);
			}
			tmp = s;
			casaToken(tabela.ID);
			casaToken(tabela.RECIEVE);
			Exp_tipo = exp();
			if(!tmp.getTipo().equals(Exp_tipo) && !(tmp.getTipo().equals("tipo_inteiro") && Exp_tipo.equals("tipo_byte"))){
				//erro
				System.err.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			if((s.getTipo().equals("tipo_inteiro") && Exp_tipo.equals("tipo_byte")) || (Exp_tipo.equals("tipo_inteiro") && s.getTipo().equals("tipo_byte"))){
				C_tipo = "tipo_inteiro";
				conv = true;
			}else{
				conv = false;
			}
			b.buffer.add("mov al, DS:[" + Exp_end + "]");
			
			if(Exp_tipo.equals("tipo_byte")){
				b.buffer.add("mov ah, 0");
				
			}
			b.buffer.add("mov DS:[" + tmp.getEnd() + "], ax");
			
			casaToken(tabela.DOTCOMMA);
		}else if(s.getToken() == tabela.WHILE){
			casaToken(tabela.WHILE);
			String RotuloInicio = rotulo.novoRotulo();
			String RotuloFim = rotulo.novoRotulo();
			
			b.buffer.add(RotuloInicio + ":");
			
			
			/* Acao Semantica */
			Exp_tipo = exp();
			
			if(!Exp_tipo.equals("tipo_logico")){
				//erro
				System.err.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			
			b.buffer.add("mov ax, DS:[" + Exp_end + "]");
			b.buffer.add("cmp ax, 0");	
			b.buffer.add("je " + RotuloFim);
			
			
			if(s.getToken() == tabela.ID || s.getToken() == tabela.WHILE || s.getToken() == tabela.IF || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
				C();
			}else if(s.getToken() == tabela.BEGIN){
				B();
			}
			
			b.buffer.add("jmp " + RotuloInicio);
			
			b.buffer.add(RotuloFim + ":");
			
		}else if(s.getToken() == tabela.IF){
			casaToken(tabela.IF);
			String RotuloFalso = rotulo.novoRotulo();
			String RotuloFim = rotulo.novoRotulo();
			
			/* Acao Semantica */
			Exp_tipo = exp();
			
			if(!Exp_tipo.equals("tipo_logico")){
				//erro
				System.err.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			
			b.buffer.add("mov ax, DS:[" + Exp_end + "]");
			
			b.buffer.add("cmp ax, 0");
			
			b.buffer.add("je " + RotuloFalso);
			
			
			if(s.getToken() == tabela.ID || s.getToken() == tabela.WHILE || s.getToken() == tabela.IF || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
				C();
			}else if(s.getToken() == tabela.BEGIN){
				B();
			}
			if(s.getToken() == tabela.ELSE){
				casaToken(tabela.ELSE);
				b.buffer.add("jmp " + RotuloFim);
				
				b.buffer.add(RotuloFalso + ":");
				
				if(s.getToken() == tabela.ID || s.getToken() == tabela.WHILE || s.getToken() == tabela.IF || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
					C();
				}else if(s.getToken() == tabela.BEGIN){
					B();
				}
				
				b.buffer.add(RotuloFim + ":");
			}
		}else if(s.getToken() == tabela.READLN){
			casaToken(tabela.READLN);
			casaToken(tabela.COMMA);
			if(!s.getTipo().equals("tipo_inteiro") && !s.getTipo().equals("tipo_string") && !s.getTipo().equals("tipo_byte")){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			tmp = s;
			casaToken(tabela.ID);
			casaToken(tabela.DOTCOMMA);
			
			int bufferEnd = memoria.alocarTempString();
			memoria.contTemp += 3; //Deve ser alocado 259 bytes
			b.buffer.add("mov dx, " + bufferEnd);
			
			b.buffer.add("mov al, 0FFh");
			
			b.buffer.add("mov ds:[" + bufferEnd + "], al");
			
			b.buffer.add("mov ah, 0Ah");
			
			b.buffer.add("int 21h");
			
			
			
			b.buffer.add("mov ah, 02h");
			
			b.buffer.add("mov dl, 0Dh");
			
			b.buffer.add("int 21h");
			
			b.buffer.add("mov DL, 0Ah");
			
			b.buffer.add("int 21h");
			
			
			b.buffer.add("mov di, " + bufferEnd+2 + ";posição do string");
			if(!tmp.getTipo().equals("tipo_string")){
				b.buffer.add("mov ax, 0");
				b.buffer.add("mov cx, 10");
				b.buffer.add("mov dx, 1");
				b.buffer.add("mov bh, 0");
				b.buffer.add("mov bl, ds:[di]");
				b.buffer.add("cmp bx, 2Dh");
				String rot = rotulo.novoRotulo();
				b.buffer.add("jne " + rot);
				b.buffer.add("mov dx, -1");
				b.buffer.add("add di, 1");
				b.buffer.add("mov bl, ds:[di]");
				b.buffer.add(rot + ":");
				b.buffer.add("push dx");
				b.buffer.add("mov dx, 0");
				String rot1 = rotulo.novoRotulo();
				b.buffer.add(rot1 + ":");
				b.buffer.add("cmp bx, 0Dh");
				String rot2 = rotulo.novoRotulo();
				b.buffer.add("je " + rot2);
				b.buffer.add("imul cx");
				b.buffer.add("add bx, -48");
				b.buffer.add("add ax, bx");
				b.buffer.add("add di, 1");
				b.buffer.add("mov bh, 0");
				b.buffer.add("mov bl, ds:[di]");
				b.buffer.add("jmp " + rot1);
				b.buffer.add(rot2 + ":");
				b.buffer.add("pop cx");
				b.buffer.add("imul cx");
				
				b.buffer.add("mov DS:[" + tmp.getEnd() + "], ax");
			}else{			
				b.buffer.add("mov si, " + tmp.getEnd());
				
				
				String rotString = rotulo.novoRotulo();
				b.buffer.add(rotString + ":");
				
				b.buffer.add("mov al, ds:[di]");
				
				b.buffer.add("cmp al, 0dh ;verifica fim string");
				
				String rot2 = rotulo.novoRotulo();
	 			b.buffer.add("je " + rot2 + " ;salta se fim string");
	 			
	 			b.buffer.add("mov ds:[si], al ;próximo caractere");
				
				b.buffer.add("add di, 1 ;incrementa base");
				
				b.buffer.add("add si, 1");
				
				b.buffer.add("jmp " + rotString + " ;loop");
				
				b.buffer.add(rot2 + ":");
				
				b.buffer.add("mov al, 024h ;fim de string");
				
				b.buffer.add("mov ds:[si], al ;grava '$'");
			
			}


		}else if(s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
			int stringEnd = memoria.novoTemp();
			boolean ln = false;
			if(s.getToken() == tabela.WRITE){
				casaToken(tabela.WRITE);
				ln = false;
			}
			else if(s.getToken() == tabela.WRITELN){
				casaToken(tabela.WRITELN);
				ln = true;
			}
			casaToken(tabela.COMMA);
			Exp_tipo = exp();
//			b.buffer.add("mov dx, " + Exp_end);
//			
//			b.buffer.add("mov ah, 09h");
//			
//			b.buffer.add("int 21h");
			
			if(Exp_tipo.equals("tipo_string")){
				b.buffer.add("mov dx, " + Exp_end);
				
				b.buffer.add("mov ah, 09h");
				
				b.buffer.add("int 21h");
				
				
			}else{
				b.buffer.add("mov ax, DS:[" + Exp_end + "]");
				b.buffer.add("mov di, " + stringEnd + " ;end. string temp.");
				
				b.buffer.add("mov cx, 0 ;contador");
				
				b.buffer.add("cmp ax,0 ;verifica sinal");
				
				String rot = rotulo.novoRotulo();
				b.buffer.add("jge " + rot + " ;salta se numero positivo");
				
				b.buffer.add("mov bl, 2Dh ;senao, escreve sinal ");
				
				b.buffer.add("mov ds:[di], bl");
				
				b.buffer.add("add di, 1 ;incrementa indice");
				
				b.buffer.add("neg ax ;toma modulo do numero");
				
				b.buffer.add(rot + ":");
				
				b.buffer.add("mov bx, 10 ;divisor");
				
				String rot1 = rotulo.novoRotulo();
				b.buffer.add(rot1 + ":");
				
				b.buffer.add("add cx, 1 ;incrementa contador");
				
				b.buffer.add("mov dx, 0 ;estende 32bits p/ div.");
				
				b.buffer.add("idiv bx ;divide DXAX por BX");
				
				b.buffer.add("push dx ;empilha valor do resto");
				
				b.buffer.add("cmp ax, 0 ;verifica se quoc.  0");
				
				b.buffer.add("jne " + rot1 + " ;se nao  0, continua");
								
				String rot2 = rotulo.novoRotulo();
				b.buffer.add(rot2 + ":");
				
				b.buffer.add("pop dx ;desempilha valor");
				
				b.buffer.add("add dx, 30h ;transforma em caractere");
				
				b.buffer.add("mov ds:[di],dl ;escreve caractere");
				
				b.buffer.add("add di, 1 ;incrementa base");
				
				b.buffer.add("add cx, -1 ;decrementa contador");
				
				b.buffer.add("cmp cx, 0 ;verifica pilha vazia");
				
				b.buffer.add("jne " + rot2 + " ;se nao pilha vazia, loop");
				
				b.buffer.add("mov dl, 024h ;fim de string");
				b.buffer.add("mov ds:[di], dl ;grava '$'");
				
				b.buffer.add("mov dx, " + stringEnd);
				
				b.buffer.add("mov ah, 09h");
				
				b.buffer.add("int 21h");
			}
			
			if(!(Exp_tipo.equals("tipo_inteiro") || Exp_tipo.equals("tipo_string") || Exp_tipo.equals("tipo_byte"))){
				//erro
				System.err.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			while(s.getToken() == tabela.COMMA){
				casaToken(tabela.COMMA);
				
				Exp_tipo = exp();
				stringEnd = memoria.novoTemp();
				
				if(Exp_tipo.equals("tipo_string")){
					b.buffer.add("mov dx, " + Exp_end);
					
					b.buffer.add("mov ah, 09h");
					
					b.buffer.add("int 21h");
					
					
				}else{
					b.buffer.add("mov ax, DS:[" + Exp_end + "]");
					b.buffer.add("mov di, " + stringEnd + " ;end. string temp.");
					
					b.buffer.add("mov cx, 0 ;contador");
					
					b.buffer.add("cmp ax,0 ;verifica sinal");
					
					String rot = rotulo.novoRotulo();
					b.buffer.add("jge " + rot + " ;salta se numero positivo");
					
					b.buffer.add("mov bl, 2Dh ;senao, escreve sinal ");
					
					b.buffer.add("mov ds:[di], bl");
					
					b.buffer.add("add di, 1 ;incrementa indice");
					
					b.buffer.add("neg ax ;toma modulo do numero");
					
					b.buffer.add(rot + ":");
					
					b.buffer.add("mov bx, 10 ;divisor");
					
					String rot1 = rotulo.novoRotulo();
					b.buffer.add(rot1 + ":");
					
					b.buffer.add("add cx, 1 ;incrementa contador");
					
					b.buffer.add("mov dx, 0 ;estende 32bits p/ div.");
					
					b.buffer.add("idiv bx ;divide DXAX por BX");
					
					b.buffer.add("push dx ;empilha valor do resto");
					
					b.buffer.add("cmp ax, 0 ;verifica se quoc.  0");
					
					b.buffer.add("jne " + rot1 + " ;se nao  0, continua");
									
					String rot2 = rotulo.novoRotulo();
					b.buffer.add(rot2 + ":");
					
					b.buffer.add("pop dx ;desempilha valor");
					
					b.buffer.add("add dx, 30h ;transforma em caractere");
					
					b.buffer.add("mov ds:[di],dl ;escreve caractere");
					
					b.buffer.add("add di, 1 ;incrementa base");
					
					b.buffer.add("add cx, -1 ;decrementa contador");
					
					b.buffer.add("cmp cx, 0 ;verifica pilha vazia");
					
					b.buffer.add("jne " + rot2 + " ;se nao pilha vazia, loop");
					
					b.buffer.add("mov dl, 024h ;fim de string");
					b.buffer.add("mov ds:[di], dl ;grava '$'");
					
					b.buffer.add("mov dx, " + stringEnd);
					
					b.buffer.add("mov ah, 09h");
					
					b.buffer.add("int 21h");
					
				}
				
				
					
				if(!(Exp_tipo.equals("tipo_inteiro") || Exp_tipo.equals("tipo_string") || Exp_tipo.equals("tipo_byte"))){
					//erro
					System.err.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
			}
			
			if(ln){
				b.buffer.add("mov ah, 02h");
				
				b.buffer.add("mov dl, 0Dh");
				
				b.buffer.add("int 21h");
				
				b.buffer.add("mov DL, 0Ah");
				
				b.buffer.add("int 21h");
				
				
			}
			
			casaToken(tabela.DOTCOMMA);
		}else if(s.getToken() == tabela.DOTCOMMA){
			casaToken(tabela.DOTCOMMA);
		}
		memoria.restetTemp();
	}
	
	String exp() throws Exception{
		/* Acao Semantica */
		String exps_tipo = expS();
		String Exp_tipo = exps_tipo;
		//'>' '<' '>=' '<=' '=' '!='
		Exp_end = Exps_end;
		/**
		 * 
		 * Operacao op
		 * 1 - >
		 * 2 - <
		 * 3 - >=
		 * 4 - <=
		 * 5 - ==
		 * 6 - !=
		 * 
		 */
		int op = 0;
		if(s.getToken() == tabela.MORETHAN || s.getToken() == tabela.LESSTHAN || s.getToken() == tabela.MOREEQUAL || s.getToken() == tabela.LESSEQUAL || s.getToken() == tabela.EQUAL || s.getToken() == tabela.DIFFERENT){

			if(!exps_tipo.equals("tipo_inteiro") && !exps_tipo.equals("tipo_byte") && !exps_tipo.equals("tipo_string")){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}else{
				if(s.getToken() == tabela.EQUAL){
					op = 5;
					casaToken(tabela.EQUAL);
				}
				if(exps_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}else{
					if(s.getToken() == tabela.MORETHAN){
						op = 1;
						casaToken(tabela.MORETHAN);
					}else if(s.getToken() == tabela.LESSTHAN){
						op = 2;
						casaToken(tabela.LESSTHAN);
					}else if(s.getToken() == tabela.MOREEQUAL){
						op = 3;
						casaToken(tabela.MOREEQUAL);
					}else if(s.getToken() == tabela.LESSEQUAL){
						op = 5;
						casaToken(tabela.LESSEQUAL);
					}else if(s.getToken() == tabela.DIFFERENT){
						op = 6;
						casaToken(tabela.DIFFERENT);
					}
				}
			}
			
			String exps1_tipo = expS();
			if(!exps1_tipo.equals("tipo_inteiro") && !exps1_tipo.equals("tipo_byte")){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			
			b.buffer.add("mov ax, DS:[" + Exp_end + "]");
			
			if(exps1_tipo.equals("tipo_byte") || exps1_tipo.equals("tipo_logico")){
				b.buffer.add("mov cx, ax");
				
				b.buffer.add("mov bl, DS:[" + Exps_end + "]");
				
				b.buffer.add("mov al, bl");
				
				b.buffer.add("mov ah, 0");
				
				b.buffer.add("mov bx, ax");
				
				b.buffer.add("mov ax, cx");
				
			}else{
				b.buffer.add("mov bx, DS:[" + Exps_end + "]");
			}
			
			
			b.buffer.add("cmp ax, bx");
			

			String RotuloVerdadeiro = rotulo.novoRotulo();

			switch(op){
				case 1:
					b.buffer.add("jg " + RotuloVerdadeiro);
					
					break;
				case 2:
					b.buffer.add("jl " + RotuloVerdadeiro);
					
					break;
				case 3:
					b.buffer.add("jge " + RotuloVerdadeiro);
					
					break;
				case 4:
					b.buffer.add("jle " + RotuloVerdadeiro);
					
					break;
				case 5:
					b.buffer.add("je " + RotuloVerdadeiro);
					
					break;
				case 6:
					b.buffer.add("jne " + RotuloVerdadeiro);
					
					break;
			}
			
			b.buffer.add("mov AL, 0");
			
			
			String RotuloFalso = rotulo.novoRotulo();
			b.buffer.add("jmp " + RotuloFalso);
			
			
			b.buffer.add(RotuloVerdadeiro + ":");
			
			b.buffer.add("mov AL, 0FFh");
			
			
			b.buffer.add(RotuloFalso + ":");
			
			Exp_end = memoria.novoTemp();
			/* Acao Semantica */
			Exp_tipo = "tipo_logico";
			b.buffer.add("mov DS:[" + Exp_end + "], AL");
			
		}
		
		return Exp_tipo;
	}
	
	String expS() throws Exception{
		String Exps_tipo = "";
		boolean minus = false;
		if(s.getToken() == tabela.MINUS || s.getToken() == tabela.PLUS){
			if(s.getToken() == tabela.MINUS){
				minus = true;
				casaToken(tabela.MINUS);
			}else if(s.getToken() == tabela.PLUS){
				minus = false;
				casaToken(tabela.PLUS);
			}else{
				minus = false;
			}
		}
		/* Acao Semantica */
		Exps_tipo = T();
		if(minus){
			Exps_end = memoria.novoTemp();
			b.buffer.add("mov al, DS:[" + T_end + "] ;");
			
			b.buffer.add("not al");
			
			b.buffer.add("mov DS:[" + T_end + "], al");
			
		}
		Exps_end = T_end;
		
		/**
		 * Operador op
		 * 1 - Minus
		 * 2 - Plus
		 * 3 - Or
		 */
		int op = 0;
		while(s.getToken() == tabela.MINUS || s.getToken() == tabela.PLUS || s.getToken() == tabela.OR){
			if(s.getToken() == tabela.MINUS){
				if(Exps_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				op = 1;
				casaToken(tabela.MINUS);
			}else if(s.getToken() == tabela.PLUS){
				op = 2;
				casaToken(tabela.PLUS);
			}else if(s.getToken() == tabela.OR){
				if(Exps_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				op = 3;
				casaToken(tabela.OR);
			}
			int Tend = T_end;
			/* Acao Semantica */
			String T1_tipo = T();
			b.buffer.add("mov ax, DS:[" + Exps_end + "]");
			
			b.buffer.add("mov bx, DS:[" + T_end + "]");
			
			if(!Exps_tipo.equals(T1_tipo) && !(T1_tipo.equals("tipo_inteiro") && Exps_tipo.equals("tipo_byte") || Exps_tipo.equals("tipo_inteiro") && T1_tipo.equals("tipo_byte"))){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			
			switch(op){
				case 1:
					b.buffer.add("sub ax, bx ; minus");
					break;
				case 2:
					b.buffer.add("add ax, bx ; plus");
					break;
				case 3:
					b.buffer.add("or ax, bx ; and");					
					break;
			}
			
			Exps_end = memoria.novoTemp();
			b.buffer.add("cwd ; converter pra inteiro");
			
			b.buffer.add("mov DS:[" + Exps_end + "], ax");
			
		}
		
		return Exps_tipo;
	}
	
	String T() throws Exception{
		/* Acao Semantica */
		String F_tipo = F();
		String T_tipo = F_tipo;
		T_end = F_end;
		/**
		 * op -> operacao
		 * 1 - multiplicacao
		 * 2 - divisao
		 * 3 - and
		 * 
		 */
		int op = 0;
		while(s.getToken() == tabela.MULT || s.getToken() == tabela.DIVIDE || s.getToken() == tabela.AND){
			if(s.getToken() == tabela.MULT){
				if(F_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				op = 1;
				casaToken(tabela.MULT);
			}else if(s.getToken() == tabela.DIVIDE){
				if(F_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				op = 2;
				casaToken(tabela.DIVIDE);
			}else if(s.getToken() == tabela.AND){
				if(F_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				op = 3;
				casaToken(tabela.AND);
			}
			String F1_tipo = F();
			
			b.buffer.add("mov al, DS:[" + T_end + "]");
			
			b.buffer.add("mov bx, DS:[" + F_end + "]");
			
			if(op == 2){
				if(!F_tipo.equals("tipo_inteiro")){
					//converter para inteiro
					b.buffer.add("mov ah, 0 ; conversao para inteiro");
					
				}
				if(!F1_tipo.equals("tipo_inteiro")){
					b.buffer.add("mov cx, DS:[ax] ; salvar o que tinha em al");
					
					b.buffer.add("mov al, DS:[" + F_end + "] ; mover F1.end para al");
					
					b.buffer.add("mov ah, 0 ; conversao para inteiro");
					
					b.buffer.add("mov bx, ax ; voltar F1.end para bx");
					
					b.buffer.add("mov ax, cx voltar valor anterior de ax");
					
				}
			}
			
			switch(op){
				case 1:
					b.buffer.add("imul bx ; multiplicacao");
					
					break;
				case 2:
					b.buffer.add("idiv bx ; divisao");
					
					break;
				case 3:
					b.buffer.add("and ax, bx ; and");
					
					break;
			}
			
			T_end = memoria.novoTemp();
			
			b.buffer.add("mov DS:[" + T_end + "], ax");
			
			
			/* Acao Semantica */
			if(!T_tipo.equals(F1_tipo) || !(T_tipo.equals("tipo_inteiro") && F1_tipo.equals("tipo_byte") || F1_tipo.equals("tipo_inteiro") && T_tipo.equals("tipo_byte"))){
				//erro
				System.err.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			
			if((T_tipo.equals("tipo_inteiro") && F1_tipo.equals("tipo_byte")) || (F1_tipo.equals("tipo_inteiro") && T_tipo.equals("tipo_byte"))){
				T_tipo = "tipo_inteiro";
			}
		}
		
		return T_tipo;
	}
	
	String F() throws Exception{
		String F_tipo = "";
		if(s.getToken() == tabela.OPPAR){
			casaToken(tabela.OPPAR);
			F_tipo = exp();
			F_end = Exp_end;
			casaToken(tabela.CLPAR);
		}else if(s.getToken() == tabela.NOT){
			if(F_tipo.equals("tipo_string") || F_tipo.equals("tipo_logico")){
				//erro
				System.err.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			casaToken(tabela.NOT);
			/* Acao Semantica */
			F_tipo = "tipo_inteiro"; 
			int Fend = F_end;
			F();
			Fend = memoria.novoTemp();
			b.buffer.add("mov al, DS:[" + F_end + "] ;");
			
			b.buffer.add("not al");
			b.buffer.add("mov DS:[" + Fend + "], al");
			F_end = Fend;
		}else if(s.getToken() == tabela.CONST){
			/* Acao Semantica */
			F_tipo = s.getTipo();
			if(s.getTipo().equals("tipo_string")){
				//declarar constante na área de dados:
				
				b.buffer.add("dseg SEGMENT PUBLIC");
				
				b.buffer.add("byte " + s.getLexema().substring(0, s.getLexema().length() - 1) + "$" + s.getLexema().charAt(s.getLexema().length() - 1));
				
				b.buffer.add("dseg ENDS");
				
				
				F_end = memoria.contador;
				memoria.alocarString(s.getLexema().length() - 1);
			}else{
				String lexTemp = s.getLexema();
				if(s.getLexema().toLowerCase().equals("true"))
					lexTemp = "0FFh";
				else if(s.getLexema().toLowerCase().equals("false"))
					lexTemp = "0h";
				F_end = memoria.novoTemp();
				b.buffer.add("mov ax, " + lexTemp + " ; const " + s.getLexema());
				
				b.buffer.add("mov DS:[" + F_end + "], al");
				
				if(s.getTipo().equals("tipo_byte")){
					memoria.alocarTempByte();
				}else if(s.getTipo().equals("tipo_logico")){
					memoria.alocarTempLogico();
				}else if(s.getTipo().equals("tipo_inteiro")){
					memoria.alocarTempInteiro();
				}
			}
			casaToken(tabela.CONST);
		}else if(s.getToken() == tabela.ID){
			/* Acao Semantica */
			if(s.getClass().equals("")){
				//erro
				System.err.println(lexico.linha + ":identificador ja declarado[" + s.getLexema() + "]");
				System.exit(0);
			}else{
				F_tipo = s.getTipo();
			}
			F_end = s.getEndereco();
			casaToken(tabela.ID);
		}
		
		return F_tipo;
	}
}
