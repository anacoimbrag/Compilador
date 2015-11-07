import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Parse {
	AnalisadorLexico lexico;
	TabelaSimbolos tabela;
	Simbolo s;
	BufferedReader arquivo;
	BufferedWriter codigo;
	Memoria memoria;
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
			codigo = new BufferedWriter(new FileWriter("codigo.asm"));
			if(s == null){ // comentario
				s = lexico.analisar(lexico.dev, arquivo);
			}
		}catch(Exception e){System.out.print(e.getMessage());}
	}
	
	void casaToken(byte token) throws Exception{
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
	}
	
	
	
	 void S() throws Exception{
		 if(lexico.EOF){
			 System.err.println(lexico.linha + ":Fim de arquivo não esperado.");
			 System.exit(0);
		 }
		 if(s != null){
			codigo.write("sseg SEGMENT STACK ;início seg. pilha");
			codigo.newLine();
			codigo.write("byte 4000h DUP(?) ;dimensiona pilha");
			codigo.newLine();
			codigo.write("sseg ENDS ;fim seg. pilha");
			codigo.newLine();
			codigo.newLine();
			codigo.write("dseg SEGMENT PUBLIC ;início seg. dados");
			codigo.newLine();
			codigo.write("byte 4000h DUP(?) ;temporários");
			codigo.newLine();
			memoria.alocarTemp();
	 		while(s.getToken() == tabela.FINAL || s.getToken() == tabela.INT || s.getToken() == tabela.BOOLEAN || s.getToken() == tabela.BYTE || s.getToken() == tabela.STRING){
				D();
			}
	 		codigo.write("dseg ENDS ;fim seg. dados");
	 		codigo.newLine();
	 		codigo.newLine();
	 		codigo.write("cseg SEGMENT PUBLIC ;início seg. código");
	 		codigo.newLine();
	 		codigo.write("ASSUME CS:cseg, DS:dseg");
	 		codigo.newLine();
	 		codigo.write("strt:");
	 		codigo.newLine();
	 		codigo.write("mov ax, dseg");
	 		codigo.newLine();
	 		codigo.write("mov ds, ax");
	 		codigo.newLine();
			B();
			codigo.write("cseg ENDS ;fim seg. código");
			codigo.newLine();
			codigo.write("END strt ;fim programa");
			if(!lexico.EOF){
				System.err.println(lexico.linha + ":Token não esperado: " + s.getLexema());
				System.exit(0);
			}
		 }
		 codigo.close();
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
				lexTemp = "ffh";
			}else if(s.getLexema().toLowerCase().equals("false")){
				lexTemp = "0h";
			}
			if(minus){
				codigo.write("byte -" + lexTemp + " ; valor negativo " + temp.getLexema());
				codigo.newLine();
			}else{
				codigo.write("byte " + lexTemp + "; valor positivo " + temp.getLexema());
				codigo.newLine();
			}
			
			temp.setEndereco(endereco);
			switch(temp.getTipo()){
				case "tipo_byte":
					endereco = memoria.alocarByte();
					break;
				case "tipo_logico":
					endereco = memoria.alocarLogico();
					break;
				case "tipo_inteiro":
					endereco = memoria.alocarInteiro();
					break;
				case "tipo_string":
					endereco = memoria.alocarString();
					break;
			}
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
					lexTemp = "ffh";
				}else if(s.getLexema().toLowerCase().equals("false")){
					lexTemp = "0h";
				}
				if(minus){
					codigo.write("byte -" + lexTemp + " ; valor negativo " + temp.getLexema());
					codigo.newLine();
				}
				else{
					codigo.write("byte " + lexTemp + " ; valor positivo " + temp.getLexema());
					codigo.newLine();
				}
				
					switch(temp.getTipo()){
						case "tipo_byte":
							endereco = memoria.alocarByte();
							break;
						case "tipo_logico":
							endereco = memoria.alocarLogico();
							break;
						case "tipo_inteiro":
							endereco = memoria.alocarInteiro();
							break;
						case "tipo_string":
							endereco = memoria.alocarString();
							break;
					}
					temp.setEndereco(endereco);
				
				casaToken(tabela.CONST);
			}else{
				switch(temp.getTipo()){
					case "tipo_byte":
						endereco = memoria.alocarByte();
						codigo.write("byte ? ;byte " + temp.getLexema());
						codigo.newLine();
						break;
					case "tipo_logico":
						endereco = memoria.alocarLogico();
						codigo.write("byte ? ;logico " + temp.getLexema());
						codigo.newLine();
						break;
					case "tipo_inteiro":
						endereco = memoria.alocarInteiro();
						System.out.println("contador: " + memoria.contador + "-endereco:" + endereco + "-lexema:" + temp.getLexema());
						codigo.write("sword ? ;inteiro " + temp.getLexema());
						codigo.newLine();
						break;
					case "tipo_string":
						endereco = memoria.alocarString();
						codigo.write("byte 100h DUP(?) ;string " + temp.getLexema());
						codigo.newLine();
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
						lexTemp = "ffh";
					}else if(s.getLexema().toLowerCase().equals("false")){
						lexTemp = "0h";
					}
					if(minus){
						codigo.write("byte -" + lexTemp + "; valor negativo " + temp.getLexema());
						codigo.newLine();
					}
					else{
						codigo.write("byte " + lexTemp + "; valor positivo " + temp.getLexema());
						codigo.newLine();
					}
					
					temp.setEndereco(endereco);
					switch(temp.getTipo()){
						case "tipo_byte":
							endereco = memoria.alocarByte();
							break;
						case "tipo_logico":
							endereco = memoria.alocarLogico();
							break;
						case "tipo_inteiro":
							endereco = memoria.alocarInteiro();
							break;
						case "tipo_string":
							endereco = memoria.alocarString();
							break;
					}
					
					casaToken(tabela.CONST);
				}else{
					temp.setEndereco(endereco);
					switch(temp.getTipo()){
						case "tipo_byte":
							endereco = memoria.alocarByte();
							codigo.write("byte ? ;byte " + temp.getLexema());
							codigo.newLine();
							break;
						case "tipo_logico":
							endereco = memoria.alocarLogico();
							codigo.write("byte ? ;logico " + temp.getLexema());
							codigo.newLine();
							break;
						case "tipo_inteiro":
							endereco = memoria.alocarInteiro();
							codigo.write("sword ? ;inteiro " + temp.getLexema());
							break;
						case "tipo_string":
							endereco = memoria.alocarString();
							codigo.write("byte 100h DUP(?) ; string " + temp.getLexema());
							codigo.newLine();
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
		if(s.getToken() == tabela.ID){
			/* Acao Semantica */
			if(s.getClasse() == ""){
				//erro
				System.err.println(lexico.linha + ":identificador ja declarado ["+s.getLexema()+"]");
				System.exit(0);
			}else if(s.getClass().equals("classe-const")){
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
			}
			casaToken(tabela.DOTCOMMA);
		}else if(s.getToken() == tabela.WHILE){
			casaToken(tabela.WHILE);
			/* Acao Semantica */
			Exp_tipo = exp();
			if(!Exp_tipo.equals("tipo_logico")){
				//erro
				System.err.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			if(s.getToken() == tabela.ID || s.getToken() == tabela.WHILE || s.getToken() == tabela.IF || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
				C();
			}else if(s.getToken() == tabela.BEGIN){
				B();
			}
		}else if(s.getToken() == tabela.IF){
			casaToken(tabela.IF);
			/* Acao Semantica */
			Exp_tipo = exp();
			if(!Exp_tipo.equals("tipo_logico")){
				//erro
				System.err.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			if(s.getToken() == tabela.ID || s.getToken() == tabela.WHILE || s.getToken() == tabela.IF || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
				C();
			}else if(s.getToken() == tabela.BEGIN){
				B();
			}
			if(s.getToken() == tabela.ELSE){
				casaToken(tabela.ELSE);
				if(s.getToken() == tabela.ID || s.getToken() == tabela.WHILE || s.getToken() == tabela.IF || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
					C();
				}else if(s.getToken() == tabela.BEGIN){
					B();
				}
			}
		}else if(s.getToken() == tabela.READLN){
			casaToken(tabela.READLN);
			casaToken(tabela.COMMA);
			if(!s.getTipo().equals("tipo_inteiro") && !s.getTipo().equals("tipo_string") && !s.getTipo().equals("tipo_byte")){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			casaToken(tabela.ID);
			casaToken(tabela.DOTCOMMA);
		}else if(s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
			if(s.getToken() == tabela.WRITE)
				casaToken(tabela.WRITE);
			else if(s.getToken() == tabela.WRITELN)
				casaToken(tabela.WRITELN);
			casaToken(tabela.COMMA);
			Exp_tipo = exp();
			if(!(Exp_tipo.equals("tipo_inteiro") || Exp_tipo.equals("tipo_string") || Exp_tipo.equals("tipo_byte"))){
				//erro
				System.err.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			while(s.getToken() == tabela.COMMA){
				casaToken(tabela.COMMA);
				Exp_tipo = exp();
				if(!(Exp_tipo.equals("tipo_inteiro") || Exp_tipo.equals("tipo_string") || Exp_tipo.equals("tipo_byte"))){
					//erro
					System.err.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
			}
			casaToken(tabela.DOTCOMMA);
		}else if(s.getToken() == tabela.DOTCOMMA){
			casaToken(tabela.DOTCOMMA);
		}
		
	}
	
	String exp() throws Exception{
		/* Acao Semantica */
		String exps_tipo = expS();
		String Exp_tipo = exps_tipo;
		//'>' '<' '>=' '<=' '=' '!='
		if(s.getToken() == tabela.MORETHAN || s.getToken() == tabela.LESSTHAN || s.getToken() == tabela.MOREEQUAL || s.getToken() == tabela.LESSEQUAL || s.getToken() == tabela.EQUAL || s.getToken() == tabela.DIFFERENT){
			/* Acao Semantica */
			Exp_tipo = "tipo_logico";

			if(!exps_tipo.equals("tipo_inteiro") && !exps_tipo.equals("tipo_byte") && !exps_tipo.equals("tipo_string")){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}else{
				if(s.getToken() == tabela.EQUAL){
					casaToken(tabela.EQUAL);
				}
				if(exps_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}else{
					if(s.getToken() == tabela.MORETHAN){
						casaToken(tabela.MORETHAN);
					}else if(s.getToken() == tabela.LESSTHAN){
						casaToken(tabela.LESSTHAN);
					}else if(s.getToken() == tabela.MOREEQUAL){
						casaToken(tabela.MOREEQUAL);
					}else if(s.getToken() == tabela.LESSEQUAL){
						casaToken(tabela.LESSEQUAL);
					}else if(s.getToken() == tabela.DIFFERENT){
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
			codigo.write("mov al, DS:[" + T_end + "] ;");
			codigo.newLine();
			codigo.write("not al");
			codigo.newLine();
			codigo.write("mov DS:[" + T_end + "], al");
			codigo.newLine();
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
			codigo.write("mov ax, DS:[" + Exps_end + "]");
			codigo.newLine();
			codigo.write("mov bx, DS:[" + T_end + "]");
			codigo.newLine();
			if(!Exps_tipo.equals(T1_tipo) && !(T1_tipo.equals("tipo_inteiro") && Exps_tipo.equals("tipo_byte") || Exps_tipo.equals("tipo_inteiro") && T1_tipo.equals("tipo_byte"))){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			
			switch(op){
				case 1:
					codigo.write("sub ax, bx ; minus");
					codigo.newLine();
					break;
				case 2:
					codigo.write("add ax, bx ; plus");
					codigo.newLine();
					break;
				case 3:
					codigo.write("or ax, bx ; and");
					codigo.newLine();
					break;
			}
			
			Exps_end = memoria.novoTemp();
			codigo.write("cwd ; converter pra inteiro");
			codigo.newLine();
			codigo.write("mov DS:[" + T_end + "], ax");
			codigo.newLine();
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
			
			codigo.write("mov al, DS:[" + T_end + "]");
			codigo.newLine();
			codigo.write("mov bx, DS:[" + F_end + "]");
			codigo.newLine();
			if(op == 2){
				if(!F_tipo.equals("tipo_inteiro")){
					//converter para inteiro
					codigo.write("cwd ; conversao para inteiro");
					codigo.newLine();
				}
				if(!F1_tipo.equals("tipo_inteiro")){
					codigo.write("mov cx, DS:[al] ; salvar o que tinha em al");
					codigo.newLine();
					codigo.write("mov al, DS:[" + F_end + "] ; mover F1.end para al");
					codigo.newLine();
					codigo.write("cwd ; conversao para inteiro");
					codigo.newLine();
					codigo.write("mov bx, DS:[ax] ; voltar F1.end para bx");
					codigo.newLine();
					codigo.write("mov ax, DS:[cx] voltar valor anterior de ax");
					codigo.newLine();
				}
			}
			
			switch(op){
				case 1:
					codigo.write("imul bx ; multiplicacao");
					codigo.newLine();
					break;
				case 2:
					codigo.write("idiv bx ; divisao");
					codigo.newLine();
					break;
				case 3:
					codigo.write("and ax, bx ; and");
					codigo.newLine();
					break;
			}
			
			T_end = memoria.novoTemp();
			codigo.write("cwd ; converter pra inteiro");
			codigo.newLine();
			codigo.write("mov DS:[" + T_end + "], ax");
			codigo.newLine();
			
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
			codigo.write("mov al, DS:[" + F_end + "] ;");
			codigo.newLine();
			codigo.write("not al");
			codigo.write("mov DS:[" + Fend + "], al");
			F_end = Fend;
		}else if(s.getToken() == tabela.CONST){
			/* Acao Semantica */
			F_tipo = s.getTipo();
			if(s.getTipo().equals("tipo_string")){
				//declarar constante na área de dados:
				codigo.newLine();
				codigo.write("dseg SEGMENT PUBLIC");
				codigo.newLine();
				codigo.write("byte " + s.getLexema().substring(0, s.getLexema().length() - 2) + "$" + s.getLexema().charAt(s.getLexema().length() - 1));
				codigo.newLine();
				codigo.write("dseg ENDS");
				codigo.newLine();
				codigo.newLine();
				F_end = endereco;
				memoria.alocarString();
			}else{
				String lexTemp = s.getLexema();
				if(s.getLexema().toLowerCase().equals("true"))
					lexTemp = "ffh";
				else if(s.getLexema().toLowerCase().equals("false"))
					lexTemp = "0h";
				F_end = memoria.novoTemp();
				codigo.write("mov al, " + lexTemp + " ; const " + s.getLexema());
				codigo.newLine();
				codigo.write("mov DS:[" + F_end + "], al");
				codigo.newLine();
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
			System.out.println(F_end + " - " + s.getLexema());
			casaToken(tabela.ID);
		}
		
		return F_tipo;
	}
}
