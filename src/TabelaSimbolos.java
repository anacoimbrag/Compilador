import java.util.HashMap;

public class TabelaSimbolos {
	public HashMap<String, Simbolo> tabela = new HashMap<>();
	public static int end = -1;
	
	public final byte FINAL = 0;
	public final byte INT = 1;
	public final byte BYTE = 2;
	public final byte STRING = 3;
	public final byte WHILE = 4;
	public final byte IF = 5;
	public final byte ELSE = 6;
	public final byte AND = 7;
	public final byte OR = 8;
	public final byte NOT = 9;
	public final byte EQUAL = 10;
	public final byte RECIEVE = 11;
	public final byte OPPAR = 12;
	public final byte CLPAR = 13;
	public final byte MORETHAN = 14;
	public final byte LESSTHAN = 15;
	public final byte DIFFERENT = 16;
	public final byte MOREEQUAL = 17;
	public final byte LESSEQUAL = 18;
	public final byte COMMA = 19;
	public final byte PLUS = 20;
	public final byte MINUS = 21;
	public final byte MULT = 22;
	public final byte DIVIDE = 23;
	public final byte DOTCOMMA = 24;
	public final byte BEGIN = 25;
	public final byte END = 26;
	public final byte READLN = 27;
	public final byte WRITE = 28;
	public final byte WRITELN = 29;
	public final byte TRUE = 30;
	public final byte FALSE = 31;
	public final byte BOOLEAN = 32;
	
	public final byte ID = 33;
	public final byte CONST = 34; 
	
	public TabelaSimbolos() {
		tabela.put("final", new Simbolo(FINAL,"final", ++end));
		tabela.put("int", new Simbolo(INT,"int", ++end));
		tabela.put("byte", new Simbolo(BYTE,"byte", ++end));
		tabela.put("string", new Simbolo(STRING,"string", ++end));
		tabela.put("while", new Simbolo(WHILE,"while", ++end));
		tabela.put("if", new Simbolo(IF,"if", ++end));
		tabela.put("else", new Simbolo(ELSE,"else", ++end));
		tabela.put("and", new Simbolo(AND,"and", ++end));
		tabela.put("or", new Simbolo(OR,"or", ++end));
		tabela.put("not", new Simbolo(NOT,"not", ++end));
		tabela.put("==", new Simbolo(EQUAL,"==", ++end));
		tabela.put("=", new Simbolo(RECIEVE,"=", ++end));
		tabela.put("(", new Simbolo(OPPAR,"(", ++end));
		tabela.put(")", new Simbolo(CLPAR,")", ++end));
		tabela.put(">", new Simbolo(MORETHAN,">", ++end));
		tabela.put("<", new Simbolo(LESSTHAN,"<", ++end));
		tabela.put("!=", new Simbolo(DIFFERENT,"!=", ++end));
		tabela.put(">=", new Simbolo(MOREEQUAL,">=", ++end));
		tabela.put("<=", new Simbolo(LESSEQUAL,"<=", ++end));
		tabela.put(",", new Simbolo(COMMA,",", ++end));
		tabela.put("+", new Simbolo(PLUS,"+", ++end));
		tabela.put("-", new Simbolo(MINUS,"-", ++end));
		tabela.put("*", new Simbolo(MULT,"*", ++end));
		tabela.put("/", new Simbolo(DIVIDE,"/", ++end));
		tabela.put(";", new Simbolo(DOTCOMMA,";", ++end));
		tabela.put("begin", new Simbolo(BEGIN,"begin", ++end));
		tabela.put("end", new Simbolo(END,"end", ++end));
		tabela.put("readln", new Simbolo(READLN,"readln", ++end));
		tabela.put("write", new Simbolo(WRITE,"write", ++end));
		tabela.put("writeln", new Simbolo(WRITELN,"writeln", ++end));
		tabela.put("boolean", new Simbolo(BOOLEAN,"boolean", ++end));
		
	}
	
	public int pesquisa(String lexema){
		lexema = lexema.toLowerCase();
		Simbolo aux = tabela.get(lexema);
		return aux.getEnd();
	}
	
	public Simbolo buscaSimbolo(String lexema){
		lexema = lexema.toLowerCase();
		return tabela.get(lexema);
	}
	
	public Simbolo inserirID(String lexema){
		lexema = lexema.toLowerCase();
		Simbolo simbolo = new Simbolo(ID,lexema, ++end);
		tabela.put(lexema, simbolo);
		return tabela.get(lexema);
	}
	
	public Simbolo inserirConst(String lexema, String tipo){
		lexema = lexema.toLowerCase();
		Simbolo simbolo = new Simbolo(CONST, lexema, tipo, ++end);
		tabela.put(lexema, simbolo);
		return tabela.get(lexema);
	}
}
