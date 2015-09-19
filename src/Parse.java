import java.io.BufferedReader;
import java.io.FileReader;

public class Parse {
	AnalisadorLexico lexico;
	TabelaSimbolos tabela;
	Simbolo s;
	BufferedReader arquivo;
	
	Parse(BufferedReader arquivo){
		try{
			this.arquivo = arquivo;
			lexico = new AnalisadorLexico();
			tabela = new TabelaSimbolos();
			s = lexico.analisar(lexico.dev, arquivo);
		}catch(Exception e){System.out.print(e.getMessage());}
	}
	
	void casaToken(byte token) throws Exception{
		if(s != null){
			if(s.getToken() == token){
				System.out.print(s.getToken());
				s = lexico.analisar(lexico.dev, arquivo);
			}else{
				//erro
			}
		}
	}
	
	 void S() throws Exception{
		while(s.getToken() == tabela.FINAL || s.getToken() == tabela.INT || s.getToken() == tabela.BOOLEAN || s.getToken() == tabela.BYTE || s.getToken() == tabela.STRING){
			D();
		}
		B();
	}
	
	void D() throws Exception{
		System.out.print("d {");
		if(s.getToken() == tabela.FINAL){
			casaToken(tabela.FINAL);
			casaToken(tabela.ID);
			casaToken(tabela.RECIEVE);
			if(s.getToken() == tabela.MINUS){
				casaToken(tabela.MINUS);
			}
			casaToken(tabela.CONST);
		}else if(s.getToken() == tabela.INT || s.getToken() == tabela.BOOLEAN || s.getToken() == tabela.BYTE || s.getToken() == tabela.STRING){
			tipo();
			casaToken(tabela.ID);
			if(s.getToken() == tabela.RECIEVE){
				casaToken(tabela.RECIEVE);
				if(s.getToken() == tabela.MINUS){
					casaToken(tabela.MINUS);
				}
				casaToken(tabela.CONST);
			}
			while(s.getToken() == tabela.COMMA){
				casaToken(tabela.COMMA);
				casaToken(tabela.ID);
				if(s.getToken() == tabela.RECIEVE){
					casaToken(tabela.RECIEVE);
					if(s.getToken() == tabela.MINUS){
						casaToken(tabela.MINUS);
					}
					casaToken(tabela.CONST);
				}
			}
		}
		casaToken(tabela.DOTCOMMA);
		System.out.print("}\n");;
	}
	
	void tipo() throws Exception{
		System.out.print("TIPO (");
		if(s.getToken() == tabela.INT){
			casaToken(tabela.INT);
		}else if(s.getToken() == tabela.BOOLEAN){
			casaToken(tabela.BOOLEAN);
		}else if(s.getToken() == tabela.BYTE){
			casaToken(tabela.BYTE);
		}else if(s.getToken() == tabela.STRING){
			casaToken(tabela.STRING);
		}else{
			//erro
		}
		System.out.print(")");
	}
	
	void B() throws Exception{
		System.out.print("B {");
		casaToken(tabela.BEGIN);
		while(s.getToken() == tabela.ID || s.getToken() == tabela.WHILE || s.getToken() == tabela.IF || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
			C();
		}
		casaToken(tabela.END);
		System.out.print("}\n");
	}
	
	void C() throws Exception{
		System.out.print("C {");
		if(s.getToken() == tabela.ID){
			casaToken(tabela.ID);
			casaToken(tabela.RECIEVE);
			exp();
		}else if(s.getToken() == tabela.WHILE){
			casaToken(tabela.WHILE);
			exp();
			if(s.getToken() == tabela.ID || s.getToken() == tabela.WHILE || s.getToken() == tabela.IF || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
				C();
			}else if(s.getToken() == tabela.BEGIN){
				B();
			}
		}else if(s.getToken() == tabela.IF){
			casaToken(tabela.IF);
			exp();
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
			casaToken(tabela.ID);
		}else if(s.getToken() == tabela.WRITE || s.getToken() == tabela.WRITELN){
			if(s.getToken() == tabela.WRITE)
				casaToken(tabela.WRITE);
			else if(s.getToken() == tabela.WRITELN)
				casaToken(tabela.WRITELN);
			casaToken(tabela.COMMA);
			exp();
			while(s.getToken() == tabela.COMMA){
				casaToken(tabela.COMMA);
				exp();
			}
		}
		
		casaToken(tabela.DOTCOMMA);
		System.out.print("}\n");
	}
	
	void exp() throws Exception{
		System.out.print("EXP {");
		expS();
		if(s.getToken() == tabela.MORETHAN || s.getToken() == tabela.LESSTHAN || s.getToken() == tabela.MOREEQUAL || s.getToken() == tabela.LESSEQUAL || s.getToken() == tabela.EQUAL || s.getToken() == tabela.DIFFERENT){
			if(s.getToken() == tabela.MORETHAN){
				casaToken(tabela.MORETHAN);
			}else if(s.getToken() == tabela.LESSTHAN){
				casaToken(tabela.LESSTHAN);
			}else if(s.getToken() == tabela.MOREEQUAL){
				casaToken(tabela.MOREEQUAL);
			}else if(s.getToken() == tabela.LESSEQUAL){
				casaToken(tabela.LESSEQUAL);
			}else if(s.getToken() == tabela.EQUAL){
				casaToken(tabela.EQUAL);
			}else if(s.getToken() == tabela.DIFFERENT){
				casaToken(tabela.DIFFERENT);
			}
			expS();
		}
		System.out.print("}\n");
	}
	
	void expS() throws Exception{
		if(s.getToken() == tabela.MINUS || s.getToken() == tabela.PLUS){
			if(s.getToken() == tabela.MINUS){
				casaToken(tabela.MINUS);
			}else if(s.getToken() == tabela.PLUS){
				casaToken(tabela.PLUS);
			}
		}
		T();
		while(s.getToken() == tabela.MINUS || s.getToken() == tabela.PLUS || s.getToken() == tabela.OR){
			if(s.getToken() == tabela.MINUS){
				casaToken(tabela.MINUS);
			}else if(s.getToken() == tabela.PLUS){
				casaToken(tabela.PLUS);
			}else if(s.getToken() == tabela.OR){
				casaToken(tabela.OR);
			}
			T();
		}
	}
	
	void T() throws Exception{
		F();
		while(s.getToken() == tabela.MULT || s.getToken() == tabela.DIVIDE || s.getToken() == tabela.AND){
			if(s.getToken() == tabela.MULT){
				casaToken(tabela.MULT);
			}else if(s.getToken() == tabela.DIVIDE){
				casaToken(tabela.DIVIDE);
			}else if(s.getToken() == tabela.AND){
				casaToken(tabela.AND);
			}
			F();
		}
	}
	
	void F() throws Exception{
		if(s.getToken() == tabela.OPPAR){
			casaToken(tabela.OPPAR);
			exp();
			casaToken(tabela.CLPAR);
		}else if(s.getToken() == tabela.NOT){
			casaToken(tabela.NOT);
			F();
		}else if(s.getToken() == tabela.CONST){
			casaToken(tabela.CONST);
		}else if(s.getToken() == tabela.ID){
			casaToken(tabela.ID);
		}
	}
}
