package compilo;

public class Parser {

	private LexicalAnalyzer lex;
	private String input;
	private String token, terminal;
	private int lineNum;
	private String programId;
	
	/** Tells whether we are on a new line or not.*/
	private boolean newLine;
	
	public Parser(LexicalAnalyzer l) {
		this.programId = null;
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
			//System.out.println("New line. input='"+this.input+"'");
		}
		lexCouple = this.lex.nextToken(this.input, this.newLine, this.lineNum);
		this.token = lexCouple[1];//We know consider that the token is the lexical unit...
		this.terminal = lexCouple[0];//... and the token becomes the terminal.
		//TODO check if token == ERROR
		
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
			//System.out.println("Match successful of '"+this.token+"' (terminal='"+this.terminal+"'");
			//System.out.println("Left on the input: '"+this.input+"'");
		}
		else {
			System.err.println("Error trying to match tos='"+toMatch+"' with '"+this.token+
					"' (which is '"+this.terminal+"')");
		}
		
		//do {
			nextToken();
			//Comments are simply ignored by the parser
			if("COMMENT".equals(this.token)) {
				matchNextToken("COMMENT");
			}
		//} while("COMMENT".equals(this.token));
		this.newLine = false;
	}
	
	private void program() {
		ident();
		env();
		data();
		proc();
		matchNextToken("FINAL_SYMBOL");//Match the final Symbol
		
		this.lex.printSymbolsTable();
	}
	
	private void ident() {		
		matchNextToken("IDENTIFICATION_KEYWORD");//identification
		matchNextToken("DIVISION_KEYWORD");//division
		endInst();
		matchNextToken("PROGRAM-ID_KEYWORD");//program-id
		matchNextToken("DOT_KEYWORD");//dot
		checkProgramId();
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
		matchNextToken("DATA_KEYWORD");
		matchNextToken("DIVISION_KEYWORD");
		endInst();
		matchNextToken("WORKING-STORAGE_KEYWORD");
		matchNextToken("SECTION_KEYWORD");
		endInst();
		varList();
	}
	
	private void varList() {
		if("INTEGER".equals(this.token)) {
			//System.out.println("Inside varList.INTEGER");
			varDecl();
			varList();
		}
		else if("PROCEDURE_KEYWORD".equals(this.token)) {
			//espilon
		}
	}
	
	private void varDecl() {
		level();
		matchNextToken("IDENTIFIER");
		matchNextToken("PIC_KEYWORD");
		matchNextToken("IMAGE");
		varDeclTail();
		
	}
	
	private void level() {
		matchNextToken("INTEGER");
	}
	
	private void varDeclTail() {
		if("VALUE_KEYWORD".equals(this.token)) {
			matchNextToken("VALUE_KEYWORD");
			matchNextToken("INTEGER");
			endInst();
		}
		else if("END_OF_INSTRUCTION".equals(this.token)) {
			//WARNING, the follow table indicates a DOT ('.'), but it
			//actually is the DOT of the END_OF_INSTRUCTION ('.\n').
			endInst();
		}
	}
	
	private void proc() {
		matchNextToken("PROCEDURE_KEYWORD");
		matchNextToken("DIVISION_KEYWORD");
		endInst();
		matchNextToken("IDENTIFIER");
		matchNextToken("SECTION_KEYWORD");
		endInst();
		labels();
		matchNextToken("END_KEYWORD");
		matchNextToken("PROGRAM_KEYWORD");
		checkProgramId();
		matchNextToken("IDENTIFIER");
		matchNextToken("DOT_KEYWORD");
	}
	
	private void labels() {
		label();
		endInst();
		instructionList();
		labelsLR();
	}
	
	private void labelsLR() {
		if("IDENTIFIER".equals(this.token)) {
			label();
			endInst();
			instructionList();
			labelsLR();
		}
		else if("END_KEYWORD".equals(this.token)) {
			//espilon
		}
	}
	
	private void label() {
		matchNextToken("IDENTIFIER");
	}
	
	private void instructionList() {
		if("MOVE_KEYWORD".equals(this.token) || "COMPUTE_KEYWORD".equals(this.token) ||
				"ADD_KEYWORD".equals(this.token) || "SUBSTRACT_KEYWORD".equals(this.token) ||
				"MULTIPLY_KEYWORD".equals(this.token) || "DIVIDE_KEYWORD".equals(this.token) ||
				"IF_KEYWORD".equals(this.token) || "ACCEPT_KEYWORD".equals(this.token) ||
				"DISPLAY_KEYWORD".equals(this.token) || "STOP_KEYWORD".equals(this.token) ||
				"PERFORM_KEYWORD".equals(this.token)) {
			instruction();
			instructionList();
		}
		else if("IDENTIFIER".equals(this.token)) {
			//epsilon
		}
	}
	
	private void instruction() {
		if("MOVE_KEYWORD".equals(this.token) || "COMPUTE_KEYWORD".equals(this.token) ||
				"ADD_KEYWORD".equals(this.token) || "SUBSTRACT_KEYWORD".equals(this.token) ||
				"MULTIPLY_KEYWORD".equals(this.token) || "DIVIDE_KEYWORD".equals(this.token)) {	
			assignation();
		}
		else if("IF_KEYWORD".equals(this.token)) {
			ifRule();
		}
		else if("PERFORM_KEYWORD".equals(this.token)) {
			call();
		}
		else if("ACCEPT_KEYWORD".equals(this.token)) {
			read();
		}
		else if("DISPLAY_KEYWORD".equals(this.token)) {
			write();
		}
		else if("STOP_KEYWORD".equals(this.token)) {
			matchNextToken("STOP_KEYWORD");
			matchNextToken("RUN_KEYWORD");
			endInst();
		}
	}
	
	private void assignation() {
		if("MOVE_KEYWORD".equals(this.token)) {
			matchNextToken("MOVE_KEYWORD");
			expression();
			matchNextToken("TO_KEYWORD");
			matchNextToken("IDENTIFIER");
			endInst();
		}
		else if("COMPUTE_KEYWORD".equals(this.token)) {
			matchNextToken("COMPUTE_KEYWORD");
			matchNextToken("IDENTIFIER");
			matchNextToken("EQUALS_SIGN");
			expression();
			endInst();
		}
		else if("ADD_KEYWORD".equals(this.token)) {
			matchNextToken("ADD_KEYWORD");
			expression();
			matchNextToken("TO_KEYWORD");
			matchNextToken("IDENTIFIER");
			endInst();
		}
		else if("SUBSTRACT_KEYWORD".equals(this.token)) {
			matchNextToken("SUBSTRACT_KEYWORD");
			expression();
			matchNextToken("FROM_KEYWORD");
			matchNextToken("IDENTIFIER");
			endInst();
		}
		else if("MULTIPLY_KEYWORD".equals(this.token)) {
			matchNextToken("MULTIPLY_KEYWORD");
			assignEnd();
			endInst();
		}
		else if("DIVIDE_KEYWORD".equals(this.token)) {
			matchNextToken("DIVIDE_KEYWORD");
			assignEnd();
			endInst();
		}
	}
	
	private void assignEnd() {
		expression();
		matchNextToken("COMMA");
		expression();
		matchNextToken("GIVING_KEYWORD");
		matchNextToken("IDENTIFIER");
	}
	
	private void expression() {
		expAnd();
		expressionLR();
	}
	
	private void expressionLR() {
		if("OR_KEYWORD".equals(this.token)) {
			matchNextToken("OR_KEYWORD");
			expAnd();
			expressionLR();
		}
		else if("TO_KEYWORD".equals(this.token) || "DOT_KEYWORD".equals(this.token) ||
				"FROM_KEYWORD".equals(this.token) || "COMMA".equals(this.token) ||
				"GIVING_KEYWORD".equals(this.token) ||
				"CLOSING_PARENTHESIS_KEYWORD".equals(this.token) ||
				"THEN_KEYWORD".equals(this.token)) {
			//epsilon
		}
	}
	
	private void expAnd() {
		expEqual();
		expAndLR();
	}
	
	private void expAndLR() {
		if("AND_KEYWORD".equals(this.token)) {
			matchNextToken("AND_KEYWORD");
			expEqual();
			expAndLR();
		}
		else if("OR_KEYWORD".equals(this.token)) {
			//epsilon
		}
	}
	
	private void expEqual() {
		expAdd();
		expEqualLR();
	}
	
	private void expEqualLR() {
		if("EQUALS_SIGN".equals(this.token)) {
			matchNextToken("EQUALS_SIGN");
			expAdd();
		}
		else if("LOWER_THAN".equals(this.token)) {
			matchNextToken("LOWER_THAN");
			expAdd();
		}
		else if("GREATER_THAN".equals(this.token)) {
			matchNextToken("GREATER_THAN");
			expAdd();
		}
		else if("LOWER_OR_EQUAL".equals(this.token)) {
			matchNextToken("LOWER_OR_EQUAL");
			expAdd();
		}
		else if("GREATER_OR_EQUAL".equals(this.token)) {
			matchNextToken("GREATER_OR_EQUAL");
			expAdd();
		}
		else if("AND_KEYWORD".equals(this.token)) {
			//epsilon
		}
	}
	
	private void expAdd() {
		expMult();
		expAddLR();
	}
	
	private void expAddLR() {
		if("PLUS_SIGN".equals(this.token)) {
			matchNextToken("PLUS_SIGN");
			expMult();
			expAddLR();
		}
		else if("MINUS_SIGN".equals(this.token)) {
			matchNextToken("MINUS_SIGN");
			expMult();
			expAddLR();
		}
		else if("EQUALS_SIGN".equals(this.token) || "LOWER_THAN".equals(this.token) ||
				"GREATER_THAN".equals(this.token) || "LOWER_OR_EQUAL".equals(this.token) ||
				"GREATER_OR_EQUAL".equals(this.token)) {
			//epsilon
		}
	}
	
	private void expMult() {
		expNot();
		expMultLR();
	}
	
	private void expMultLR() {
		if("MULTIPLICATION_SIGN".equals(this.token)) {
			matchNextToken("MULTIPLICATION_SIGN");
			expNot();
			expMultLR();
		}
		else if("DIVISION_SIGN".equals(this.token)) {
			matchNextToken("DIVISION_SIGN");
			expNot();
			expMultLR();
		}
		else if("PLUS_SIGN".equals(this.token) || "MINUS_SIGN".equals(this.token)) {
			//espilon
		}
	}
	
	private void expNot() {
		if("MINUS_SIGN".equals(this.token)) {
			matchNextToken("MINUS_SIGN");
			expNot();
		}
		else if("NOT_KEYWORD".equals(this.token)) {
			matchNextToken("NOT_KEYWORD");
			expNot();
		}
		else if("OPENING_PARENTHESIS".equals(this.token) || "IDENTIFIER".equals(this.token) ||
				"INTEGER".equals(this.token) || "TRUE_KEYWORD".equals(this.token) ||
				"FALSE_KEYWORD".equals(this.token)) {
			expressionParenthesis();
		}
	}
	
	private void expressionParenthesis() {
		if("OPENING_PARENTHESIS".equals(this.token)) {
			matchNextToken("OPENING_PARENTHESIS");
			expression();
			matchNextToken("CLOSING_PARENTHESIS");
		}
		else if("IDENTIFIER".equals(this.token) ||
				"INTEGER".equals(this.token) || "TRUE_KEYWORD".equals(this.token) ||
				"FALSE_KEYWORD".equals(this.token)) {
			expTerm();
		}
	}
	
	private void expTerm() {
		if("IDENTIFIER".equals(this.token)) {
			matchNextToken("IDENTIFIER");
		}
		else if("INTEGER".equals(this.token)) {
			matchNextToken("INTEGER");
		}
		else if("TRUE_KEYWORD".equals(this.token)) {
			matchNextToken("TRUE_KEYWORD");
		}
		else if("FALSE_KEYWORD".equals(this.token)) {
			matchNextToken("FALSE_KEYWORD");
		}
	}
	
	private void ifRule() {
		matchNextToken("IF_KEYWORD");
		expression();
		matchNextToken("THEN_KEYWORD");
		instructionList();
		ifEnd();
	}
	
	private void ifEnd() {
		if("ELSE_KEYWORD".equals(this.token)) {
			matchNextToken("ELSE_KEYWORD");
			instructionList();
			matchNextToken("ENDI-IF_KEYWORD");
		}
		else if("ENDI-IF_KEYWORD".equals(this.token)) {
			matchNextToken("ENDI-IF_KEYWORD");
		}
	}
	
	private void call() {
		matchNextToken("PERFORM_KEYWORD");
		matchNextToken("IDENTIFIER");
		callTail();
	}
	
	private void callTail() {
		if("UNTIL_KEYWORD".equals(this.token)) {
			matchNextToken("UNTIL_KEYWORD");
			expression();
			System.out.println("callTrail>UNTIL>juste before endInst();");
			endInst();
		}
		else if("END_OF_INSTRUCTION".equals(this.token)) {
			endInst();
		}
	}
	
	private void read() {
		matchNextToken("ACCEPT_KEYWORD");
		matchNextToken("IDENTIFIER");
		endInst();
	}
	
	private void write() {
		matchNextToken("DISPLAY_KEYWORD");
		writeTail();
	}
	
	private void writeTail() {
		if("OPENING_PARENTHESIS".equals(this.token) || "IDENTIFIER".equals(this.token) ||
				"INTEGER".equals(this.token) || "TRUE_KEYWORD".equals(this.token) ||
				"FALSE_KEYWORD".equals(this.token) || "MINUS_SIGN".equals(this.token)||
				"NOT_KEYWORD".equals(this.token)) {
			expression();
			endInst();
		}
		else if("STRING".equals(this.token)) {
			System.out.println("writeTail>else if\"STRING\", just before matchNextToken(STRING)");
			matchNextToken("STRING");
			endInst();
		}
	}
	
	/**
	 * Check if the program id the same at the beginning and
	 * the end of the code.
	 * If the program as not been initialized yet, it means
	 * we are at the beginning of the source code and we initialize
	 * it with the value of the current terminal - supposed to be
	 * an identifier.
	 * Else, it means we are at the end of the source, thus we check
	 * the program id is correct.
	 * 
	 * In case of non correspondence, an error is raised.
	 */
	private void checkProgramId() {
		if("IDENTIFIER".equals(this.token)) {
			if(this.programId == null) {
				this.programId = this.terminal;
			}
			else {
				if(!this.programId.equals(this.terminal)) {
					new RaiseError("The program-id does not match.\n" +
							"\tExpected: '"+this.programId+"'\n"+
							"\tEncountered: '"+this.terminal+"\'n");
				}
			}
		}
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
