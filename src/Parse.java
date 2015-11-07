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
	
	Parse(BufferedReader arquivo){
		try{
			this.arquivo = arquivo;
			lexico = new AnalisadorLexico();
			tabela = new TabelaSimbolos();
			s = lexico.analisar(lexico.dev, arquivo);
			memoria = new Memoria();
			codigo = new BufferedWriter(new FileWriter("codigo.asm"));
			codigo.write("dseg SEGMENT PUBLIC ;início seg. dados\n" + 
					"byte 4000h DUP(?) ;temporários\n" +
					"dseg ENDS ;fim seg. dados\n");
			memoria.alocarTemp();
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
	 		while(s.getToken() == tabela.FINAL || s.getToken() == tabela.INT || s.getToken() == tabela.BOOLEAN || s.getToken() == tabela.BYTE || s.getToken() == tabela.STRING){
				D();
			}
			B();
			//System.out.println("Finalizou.>>" + lexico.EOF);
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
				if(s.getTipo().equals("tipo-string")){
					System.err.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
			}
			
			/* Acao Semantica */
			temp.setTipo(s.getTipo());
			String lexTemp = s.getLexema();
			if(s.getLexema().toLowerCase().equals("true")){
				lexTemp = "0h";
			}else if(s.getLexema().toLowerCase().equals("false")){
				lexTemp = "FFh";
			}
			if(minus){
				codigo.write("dseg SEGMENT PUBLIC ;início seg. dados\n" + 
						"byte -" + lexTemp + ";temporários\n" +
						"dseg ENDS ;fim seg. dados\n");
			}else{
				codigo.write("dseg SEGMENT PUBLIC ;início seg. dados\n" + 
						"byte " + lexTemp + ";temporários\n" +
						"dseg ENDS ;fim seg. dados\n");
			}
			
			temp.setEndereco(endereco);
			switch(temp.getTipo()){
				case "tipo-byte":
					endereco = memoria.alocarByte();
					break;
				case "tipo-logico":
					endereco = memoria.alocarLogico();
					break;
				case "tipo-inteiro":
					endereco = memoria.alocarInteiro();
					break;
				case "tipo-string":
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
					
					casaToken(tabela.MINUS);
				}
				/* Acao Semantica */
				if(!temp.getTipo().equals(s.getTipo()) && !(temp.getTipo().equals("tipo_inteiro") && s.getTipo().equals("tipo_byte"))){
					//erro
					System.out.println(lexico.linha+"tipos incompativeis.");
					System.exit(0);
				}
				String lexTemp = s.getLexema();
				if(s.getLexema().toLowerCase().equals("true")){
					lexTemp = "0h";
				}else if(s.getLexema().toLowerCase().equals("false")){
					lexTemp = "FFh";
				}
				codigo.write("dseg SEGMENT PUBLIC ;início seg. dados\n" + 
						"byte " + lexTemp + ";temporários\n" +
						"dseg ENDS ;fim seg. dados\n");
				
				
					temp.setEndereco(endereco);
					switch(temp.getTipo()){
						case "tipo-byte":
							endereco = memoria.alocarByte();
							break;
						case "tipo-logico":
							endereco = memoria.alocarLogico();
							break;
						case "tipo-inteiro":
							endereco = memoria.alocarInteiro();
							break;
						case "tipo-string":
							endereco = memoria.alocarString();
							break;
				}
				
				casaToken(tabela.CONST);
			}else{
				temp.setEndereco(endereco);
				switch(temp.getTipo()){
					case "tipo-byte":
						endereco = memoria.alocarByte();
						codigo.write("dseg SEGMENT PUBLIC ;início seg. dados\n" + 
								"byte 1h ?;temporários\n" +
								"dseg ENDS ;fim seg. dados\n");
						break;
					case "tipo-logico":
						endereco = memoria.alocarLogico();
						codigo.write("dseg SEGMENT PUBLIC ;início seg. dados\n" + 
								"byte 1h ?;temporários\n" +
								"dseg ENDS ;fim seg. dados\n");
						break;
					case "tipo-inteiro":
						endereco = memoria.alocarInteiro();
						codigo.write("dseg SEGMENT PUBLIC ;início seg. dados\n" + 
								"sword ? ;temporários\n" +
								"dseg ENDS ;fim seg. dados\n");
						break;
					case "tipo-string":
						endereco = memoria.alocarString();
						codigo.write("dseg SEGMENT PUBLIC ;início seg. dados\n" + 
								"byte 100h DUP(?);temporários\n" +
								"dseg ENDS ;fim seg. dados\n");
						break;
				}
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
					casaToken(tabela.CONST);
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
				System.out.println(lexico.linha + ":identificador ja declarado ["+s.getLexema()+"]");
				System.exit(0);
			}else if(s.getClass().equals("classe-const")){
				//erro
				System.out.println(lexico.linha + ":classe de identificador incompatível ["+s.getLexema()+"]");
				System.exit(0);
			}
			tmp = s;
			casaToken(tabela.ID);
			casaToken(tabela.RECIEVE);
			Exp_tipo = exp();
			if(!tmp.getTipo().equals(Exp_tipo) && !(tmp.getTipo().equals("tipo_inteiro") && Exp_tipo.equals("tipo_byte"))){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
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
				System.out.println(lexico.linha + ":tipos incompativeis.");
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
				System.out.println(lexico.linha + ":tipos incompativeis.");
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
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			while(s.getToken() == tabela.COMMA){
				casaToken(tabela.COMMA);
				Exp_tipo = exp();
				if(!(Exp_tipo.equals("tipo_inteiro") || Exp_tipo.equals("tipo_string") || Exp_tipo.equals("tipo_byte"))){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
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
		if(s.getToken() == tabela.MORETHAN || s.getToken() == tabela.LESSTHAN || s.getToken() == tabela.MOREEQUAL || s.getToken() == tabela.LESSEQUAL || s.getToken() == tabela.EQUAL || s.getToken() == tabela.DIFFERENT){
			/* Acao Semantica */
			Exp_tipo = "tipo_logico";
			if(s.getToken() == tabela.MORETHAN){
				if(!exps_tipo.equals("tipo_inteiro") && !exps_tipo.equals("tipo_byte")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.MORETHAN);
			}else if(s.getToken() == tabela.LESSTHAN){
				if(!exps_tipo.equals("tipo_inteiro") && !exps_tipo.equals("tipo_byte")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.LESSTHAN);
			}else if(s.getToken() == tabela.MOREEQUAL){
				if(!exps_tipo.equals("tipo_inteiro") && !exps_tipo.equals("tipo_byte")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.MOREEQUAL);
			}else if(s.getToken() == tabela.LESSEQUAL){
				if(!exps_tipo.equals("tipo_inteiro") && !exps_tipo.equals("tipo_byte")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.LESSEQUAL);
			}else if(s.getToken() == tabela.EQUAL){
				if(!exps_tipo.equals("tipo_inteiro") && !exps_tipo.equals("tipo_byte") && !exps_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.EQUAL);
			}else if(s.getToken() == tabela.DIFFERENT){
				if(!exps_tipo.equals("tipo_inteiro") && !exps_tipo.equals("tipo_byte")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.DIFFERENT);
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
		if(s.getToken() == tabela.MINUS || s.getToken() == tabela.PLUS){
			if(s.getToken() == tabela.MINUS){
				casaToken(tabela.MINUS);
			}else if(s.getToken() == tabela.PLUS){
				casaToken(tabela.PLUS);
			}
		}
		/* Acao Semantica */
		Exps_tipo = T();
		while(s.getToken() == tabela.MINUS || s.getToken() == tabela.PLUS || s.getToken() == tabela.OR){
			if(s.getToken() == tabela.MINUS){
				if(Exps_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.MINUS);
			}else if(s.getToken() == tabela.PLUS){
				casaToken(tabela.PLUS);
			}else if(s.getToken() == tabela.OR){
				if(Exps_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.OR);
			}
			/* Acao Semantica */
			String T1_tipo = T();
			if(!Exps_tipo.equals(T1_tipo) && !(T1_tipo.equals("tipo_inteiro") && Exps_tipo.equals("tipo_byte") || Exps_tipo.equals("tipo_inteiro") && T1_tipo.equals("tipo_byte"))){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
		}
		
		return Exps_tipo;
	}
	
	String T() throws Exception{
		/* Acao Semantica */
		String F_tipo = F();
		String T_tipo = F_tipo;
		while(s.getToken() == tabela.MULT || s.getToken() == tabela.DIVIDE || s.getToken() == tabela.AND){
			if(s.getToken() == tabela.MULT){
				if(F_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.MULT);
			}else if(s.getToken() == tabela.DIVIDE){
				if(F_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				if(!F_tipo.equals("tipo_inteiro")){
					//converter para inteiro
				}
				casaToken(tabela.DIVIDE);
			}else if(s.getToken() == tabela.AND){
				if(F_tipo.equals("tipo_string")){
					//erro
					System.out.println(lexico.linha + ":tipos incompativeis.");
					System.exit(0);
				}
				casaToken(tabela.AND);
			}
			String F1_tipo = F();
			/* Acao Semantica */
			if(!T_tipo.equals(F1_tipo) || !(T_tipo.equals("tipo_inteiro") && F1_tipo.equals("tipo_byte") || F1_tipo.equals("tipo_inteiro") && T_tipo.equals("tipo_byte"))){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
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
			casaToken(tabela.CLPAR);
		}else if(s.getToken() == tabela.NOT){
			if(F_tipo.equals("tipo_string")){
				//erro
				System.out.println(lexico.linha + ":tipos incompativeis.");
				System.exit(0);
			}
			if(!F_tipo.equals("tipo_inteiro")){
				//converter para inteiro
			}
			casaToken(tabela.NOT);
			/* Acao Semantica */
			F_tipo = "tipo_inteiro"; 
			F();
		}else if(s.getToken() == tabela.CONST){
			/* Acao Semantica */
			F_tipo = s.getTipo();
			casaToken(tabela.CONST);
		}else if(s.getToken() == tabela.ID){
			/* Acao Semantica */
			if(s.getClass().equals("")){
				//erro
				System.out.println(lexico.linha + ":identificador ja declarado[" + s.getLexema() + "]");
				System.exit(0);
			}else{
				F_tipo = s.getTipo();
			}
			casaToken(tabela.ID);
		}
		
		return F_tipo;
	}
}
