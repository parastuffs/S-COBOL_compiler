package compilo;

import java.util.Stack;

public class Parser {

	private LexicalAnalyzer lex;
	private Stack<String> stack;
	private String input;
	private String token, terminal;
	private int lineNum;
	
	/** Tells whether we are on a new line or not.*/
	private boolean newLine;
	
	public Parser(LexicalAnalyzer l) {
		this.lex = l;
		this.stack = new Stack<String>();
		this.lineNum = 0;
		this.newLine = true;
	}
	
	/**
	 * Determine the next token on the input using the lexical analyzer.
	 */
	public void nextToken() {
		String[] lexCouple = {"", ""};
		this.input = this.input.replaceAll("^\\ *", "");//Removes all the preceding white spaces.
		if(this.newLine) {
			this.lineNum ++;
		}
		lexCouple = this.lex.nextToken(this.input, this.newLine, this.lineNum);
		this.token = lexCouple[1];//We know consider that the token is the lexical unit...
		this.terminal = lexCouple[0];//... and the token becomes the terminal.
		//TODO check if token == ERROR
		this.newLine = false;
		
		//TESTS ###
		
		//###
	}
	
	public void parse(String input) {
		this.input = input;
		nextToken();
		program();
	}
	
	public void matchNextToken() {
		String tos = this.stack.peek();//Top os stack
		if(tos.equals(this.token)) {
			if("FINAL_SYMBOL".equals(this.token)) {
				System.out.println("ACCEPT");
			}
			this.stack.pop();
			this.terminal = escapeChar(this.terminal);
			this.input = this.input.replaceFirst(this.terminal, "");
			//System.out.println("Match successful of '"+this.token+"'");
		}
		else {
			System.err.println("Error trying to match tos='"+tos+"' with '"+this.token+
					"' (which is '"+this.terminal+"')");
		}
		nextToken();
	}
	
	public void program() {
		this.stack.push("FINAL_SYMBOL");
		this.stack.push("PROC");
		this.stack.push("DATA");
		this.stack.push("ENV");
		this.stack.push("IDENT");
		ident();
		env();
		data();
		proc();
		matchNextToken();//Match the final Symbol
	}
	
	public void ident() {
		this.stack.pop();//Pops the previous variable from which we came from.
		this.stack.push("END_INST");
		this.stack.push("WORDS");
		this.stack.push("DOT_KEYWORD");
		this.stack.push("DATE_WRITTEN_KEYWORD");
		this.stack.push("END_INST");
		this.stack.push("WORDS");
		this.stack.push("DOT_KEYWORD");
		this.stack.push("AUTHOR_KEYWORD");
		this.stack.push("END_INST");
		this.stack.push("IDENTIFIER");
		this.stack.push("DOT_KEYWORD");
		this.stack.push("PROGRAM-ID_KEYWORD");
		this.stack.push("END_INST");
		this.stack.push("DIVISION_KEYWORD");
		this.stack.push("IDENTIFICATION_KEYWORD");
		
		matchNextToken();//identification
		matchNextToken();//division
		endInst();
		matchNextToken();//program-id
		matchNextToken();//dot
		matchNextToken();//IDENTIFIER
		endInst();
		matchNextToken();//author
		matchNextToken();//dot
		words();
		endInst();
		matchNextToken();//date-written
		matchNextToken();//dot
		words();
		endInst();
		
		
		
	}
	
	public void endInst() {
		this.stack.pop();
		this.stack.push("END_OF_INSTRUCTION");
		this.newLine = true;//We just ended a line, thus begining a new one.
		matchNextToken();
	}
	
	public void words() {
		this.stack.pop();
		if("IDENTIFIER".equals(this.token)) {
			this.stack.push("WORDS_LR");
			this.stack.push("IDENTIFIER");
			matchNextToken();//IDENTIFIER
			wordsLR();
		}
		else if("INTEGER".equals(this.token)) {
			this.stack.push("WORDS_LR");
			this.stack.push("INTEGER");
			matchNextToken();//INTEGER
			wordsLR();
		}
		
		//TODO PROBLEM: word (300 BNC) is first recognized as an integer.	
		
	}
	
	public void wordsLR() {
		this.stack.pop();
		if("IDENTIFIER".equals(this.token)) {
			this.stack.push("WORDS_LR");
			this.stack.push("IDENTIFIER");
			matchNextToken();//IDENTIFIER
			wordsLR();
		}
		else if("INTEGER".equals(this.token)) {
			this.stack.push("WORDS_LR");
			this.stack.push("INTEGER");
			matchNextToken();//INTEGER
			wordsLR();
		}
		else if("DOT_KEYWORD".equals(this.token)) {
			//Nothing to do, this is the \varespilon clause.
		}
		
	}
	
	public void env() {
		this.stack.pop();
	}
	
	public void data() {
		this.stack.pop();
		
	}
	
	public void proc() {
		this.stack.pop();
		
	}
	
	/**
	 * <p>Un-escape the special characters</p>
	 * <p>Removes the brackets around the special characters
	 * so that the working String can be subtracted to the main
	 * String being analyzed.
	 * </p>
	 * @param input
	 * 				String containing special characters previously escaped.
	 * @return The same String with the special characters not escaped anymore.
	 */
	private static String unEscpaceChar(String input) {
		input = input.replaceAll("\\[", "");
		input = input.replaceAll("\\]", "");
		return input;
	}
	
	/**
	 * <p>Escapes special characters by placing them inside
	 * rackets.</p>
	 * <p>This is to avoid confusion the string is used in regular
	 * expressions.
	 * </p>
	 * @param input
	 * 				String containing the characters to escape.
	 * @return The escaped String.
	 */
	private static String escapeChar(String input) {
		input = input.replaceAll("\\(", "[(]");
		input = input.replaceAll("\\)", "[)]");
		input = input.replaceAll("\\*", "[*]");
		input = input.replaceAll("\\?", "[?]");
		input = input.replaceAll("\\+", "[+]");
		input = input.replaceAll("\\-", "[-]");
		return input;
	}
}
