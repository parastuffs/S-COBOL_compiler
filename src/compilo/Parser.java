package compilo;

import java.util.Stack;

public class Parser {

	private LexicalAnalyzer lex;
	private String input;
	private String token, terminal;
	private int lineNum;
	
	/** Tells whether we are on a new line or not.*/
	private boolean newLine;
	
	public Parser(LexicalAnalyzer l) {
		this.lex = l;
		this.lineNum = 0;
		this.newLine = true;
	}
	
	/**
	 * Determine the next token on the input using the lexical analyzer.
	 */
	private void nextToken() {
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
	
	private void matchNextToken(String toMatch) {
		if(this.token.equals(toMatch)) {
			if("FINAL_SYMBOL".equals(toMatch)) {
				System.out.println("ACCEPT");
			}
			this.terminal = escapeChar(this.terminal);
			this.input = this.input.replaceFirst(this.terminal, "");
			//System.out.println("Match successful of '"+this.token+"'");
		}
		else {
			System.err.println("Error trying to match tos='"+toMatch+"' with '"+this.token+
					"' (which is '"+this.terminal+"')");
		}
		nextToken();
	}
	
	private void program() {
		ident();
		env();
		data();
		proc();
		matchNextToken("FINAL_SYMBOL");//Match the final Symbol
	}
	
	private void ident() {		
		matchNextToken("IDENTIFICATION_KEYWORD");//identification
		matchNextToken("DIVISION_KEYWORD");//division
		endInst();
		matchNextToken("PROGRAM-ID_KEYWORD");//program-id
		matchNextToken("DOT_KEYWORD");//dot
		matchNextToken("IDENTIFIER");//IDENTIFIER
		endInst();
		matchNextToken("AUTHOR_KEYWORD");//author
		matchNextToken("DOT_KEYWORD");//dot
		words();
		endInst();
		matchNextToken("DATE_WRITTEN_KEYWORD");//date-written
		matchNextToken("DOT_KEYWORD");//dot
		words();
		endInst();
	}
	
	private void endInst() {
		this.newLine = true;//We just ended a line, thus begining a new one.
		matchNextToken("END_OF_INSTRUCTION");
	}
	
	private void words() {
		if("IDENTIFIER".equals(this.token)) {
			matchNextToken("IDENTIFIER");//IDENTIFIER
			wordsLR();
		}
		else if("INTEGER".equals(this.token)) {
			matchNextToken("INTEGER");//INTEGER
			wordsLR();
		}
		
		//TODO PROBLEM: word (300 BNC) is first recognized as an integer.	
		
	}
	
	private void wordsLR() {
		if("IDENTIFIER".equals(this.token)) {
			matchNextToken("IDENTIFIER");//IDENTIFIER
			wordsLR();
		}
		else if("INTEGER".equals(this.token)) {
			matchNextToken("INTEGER");//INTEGER
			wordsLR();
		}
		else if("DOT_KEYWORD".equals(this.token)) {
			//Nothing to do, this is the \varespilon clause.
		}
		
	}
	
	private void env() {
		matchNextToken("ENVIRONMENT_KEYWORD");
		matchNextToken("DIVISION_KEYWORD");
		endInst();
		matchNextToken("CONFIGURATION_KEYWORD");
		matchNextToken("SECTION_KEYWORD");
		endInst();
		matchNextToken("SOURCE-COMPUTER_KEYWORD");
		matchNextToken("DOT_KEYWORD");
		words();
		endInst();
		matchNextToken("OBJECT-COMPUTER_KEYWORD");
		matchNextToken("DOT_KEYWORD");
		words();
		endInst();		
	}
	
	private void data() {
		
	}
	
	private void proc() {
		
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
