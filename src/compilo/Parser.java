package compilo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class of the parsing and the semantic analysis.
 *
 * The LLVM code generator is also invoked during the parsing.
 */
public class Parser {

	private LexicalAnalyzer lex;
	private LLVMGenerator llvm;
	private String input;
	private String token, terminal;
	private int lineNum;
	/** name of the program. Has to be consistent.*/
	private String programId;
	/** Tells whether we are on a new line or not.*/
	private boolean newLine;
	private SymbolsTable tos;
	/**Name, Initialization, Type, Digits, Current Value*/
	private String[] tosEntry = {"", "", "", "", ""};

	private VariableInteger varLeftMult=null;
	private VariableInteger varRightMult=null;
	private VariableInteger varLeftAdd;
	private VariableInteger varRightAdd;
	private VariableInteger varLeftEq;
	private VariableInteger varRightEq;
	//private VariableInteger varRightAnd;
	/**List of all the calls. @see Parser#call() */
	private List<String> callList;
	/** List of all the labels (ie methods). @see Parser#label()*/
	private List<String> performList;
	/** Usefull for the LLVM generator. */
	private boolean multiplying = false;
	/** Usefull for the LLVM generator. */
	private boolean dividing = false;

	public Parser(LexicalAnalyzer l, SymbolsTable tos, LLVMGenerator llvm) {
		this.tos = tos;
		this.llvm = llvm;
		this.programId = null;
		this.lex = l;
		this.lineNum = -1;
		this.newLine = true;
		this.callList = new ArrayList<String>();
		this.performList = new ArrayList<String>();
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
//		if("ERROR".equals(this.token)) {
//			new RaiseError("Error during the lexical analysis.");
//		}
	}
	
	/**
	 * Launches the parser, then check the call list, the asks
	 * the LLVM generator to finalize the code, then to write
	 * it into a file.
	 * @param input Whole code as one single String.
	 */
	public void parse(String input) {
		this.input = input;
		nextToken();
		program();
		checkCallList();
		
		this.llvm.finalizeCode();
		this.llvm.toFile("output.ll");
	}
	
	/**
	 * <p>Checks if all the procedure are called.</p>
	 * <p>Checks if all the procedure called exists.</p>
	 * 
	 * <p>The callList and performList are populated during
	 * the parsing.</p>
	 * 
	 * @see Parser#call()
	 * @see Parser#label()
	 */
	private void checkCallList() {
		if(!this.callList.isEmpty()) {
			for(int i=this.callList.size()-1;i>=0;i--) {
			//for(String str : this.callList) {
				String str = this.callList.get(i);
				if(this.performList.contains(str)) {
					this.performList.remove(str);
					this.callList.remove(str);
				}
			}
		}
		if(!this.callList.isEmpty()) {
			for(String str : this.callList) {
				new RaiseError("The '"+str+"' procedure has not been found.\n");
			}
		}
		this.performList.remove("start");//Never called.
		if(!this.performList.isEmpty()) {
			for(String str : this.performList) {
				new RaiseWarning("The '"+str+"' procedure has not been called.\n");
			}
		}
	}
	
	private void matchNextToken(String toMatch) {
		if(this.token.equals(toMatch)) {
			if("FINAL_SYMBOL".equals(toMatch)) {
				System.out.println("ACCEPT");
			}
			this.terminal = escapeChar(this.terminal);
			this.input = this.input.replaceFirst(this.terminal, "");
		}
		else {
			System.err.println("Error trying to match tos='"+toMatch+"' with '"+this.token+
					"' (which is '"+this.terminal+"')");
		}
		
		nextToken();
		//Comments are simply ignored by the parser
		if("COMMENT".equals(this.token)) {
			//this.llvm.newComment(this.terminal);//Not absolutely necessary
			matchNextToken("COMMENT");
		}
		this.newLine = false;
	}
	
	private void program() {
		ident();
		env();
		data();
		proc();
		matchNextToken("FINAL_SYMBOL");//Match the final Symbol
		//this.lex.printSymbolsTable();
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
		this.llvm.newLine();
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
			varDecl();
			varList();
		}
		else if("PROCEDURE_KEYWORD".equals(this.token)) {
			//espilon
		}
	}
	
	private void varDecl() {
		level();
		
		this.tosEntry[0] = this.terminal;
		matchNextToken("IDENTIFIER");
		
		matchNextToken("PIC_KEYWORD");
		
		if(this.terminal.matches("^s9.*")) {
			this.tosEntry[2] = "signed int";
		}
		else if(this.token.matches("^9.*")) {
			this.tosEntry[2] = "unsigned int";
		}
		
		//Extracting the digits
		Pattern pat = Pattern.compile("^s?9(\\(([1-9])\\))?");
		Matcher m = pat.matcher(this.terminal);
		if(m.matches()) {
			if(m.group(2) != null) {
				this.tosEntry[3] = m.group(2);
			}
			else {
				this.tosEntry[3] = "1";
			}
		}
		matchNextToken("IMAGE");
		varDeclTail();
		
		this.tos.newEntry(this.tosEntry);
		this.llvm.declareVariable(this.tosEntry[0], this.tosEntry[1], this.tosEntry[3]);
		
		this.tosEntry[0] = "";
		this.tosEntry[1] = "";
		this.tosEntry[2] = "";
		this.tosEntry[3] = "";
		this.tosEntry[4] = "";
		
	}
	
	private void level() {
		matchNextToken("INTEGER");
	}
	
	private void varDeclTail() {
		if("VALUE_KEYWORD".equals(this.token)) {
			//Initialization of the variable
			matchNextToken("VALUE_KEYWORD");
			
			int maxLength = Integer.parseInt(this.tosEntry[3]);
			if(this.tosEntry[2].matches("^.*int") && this.terminal.length() > maxLength) {
				new RaiseError("Bad integer initialization for variable "+this.tosEntry[0]+
						"\n\tExpecting: length = "+maxLength+
						"\n\tEncountered: length = "+this.terminal.length()+"\n");
			}
			this.tosEntry[1] = this.terminal;
			this.tosEntry[4] = this.terminal;
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
		this.llvm.newProcedure(this.terminal);
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
		//Gives the name of a method, like 'start' or 'find'
		if(!this.performList.contains(this.terminal)) {
			this.performList.add(this.terminal);
		}
		this.llvm.newLabel(this.terminal);
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
			VariableInteger moveFrom = expression();
			matchNextToken("TO_KEYWORD");
			
			//Need to check the number of digits
			int maxLengthTo = this.tos.getMaxLengthOf(this.terminal);
			if(maxLengthTo == -1) {
				new RaiseError(this.terminal+" has not been declared properly.");
			}
			if(maxLengthTo < moveFrom.getMaxDigits()) {
				new RaiseWarning("Warning: "+this.terminal+" will contain "+
						moveFrom.getMaxDigits()+" instead of maximum "+maxLengthTo);
			}
			this.llvm.newVariable(this.terminal);
			matchNextToken("IDENTIFIER");
			this.llvm.store();
			endInst();
		}
		else if("COMPUTE_KEYWORD".equals(this.token)) {
			VariableInteger computedExp = null;
			matchNextToken("COMPUTE_KEYWORD");
			
			int maxLengthTo = this.tos.getMaxLengthOf(this.terminal);
			if(maxLengthTo == -1) {
				new RaiseError(this.terminal+" has not been declared properly.");
			}
			this.llvm.newLocalVariable(this.terminal);
			matchNextToken("IDENTIFIER");
			matchNextToken("EQUALS_SIGN");
			computedExp = expression();
			if(maxLengthTo < computedExp.getMaxDigits()) {
				new RaiseWarning("Warning: "+this.terminal+" will contain "+
						computedExp.getMaxDigits()+" instead of maximum "+maxLengthTo);
			}
			this.llvm.compute();
			endInst();
		}
		else if("ADD_KEYWORD".equals(this.token)) {
			//In an addition, we will have at most the highest number number of digits
			//of one of the operand +1.
			VariableInteger addThis = null;
			
			matchNextToken("ADD_KEYWORD");
			addThis = expression();
			matchNextToken("TO_KEYWORD");

			if(!"".equals(this.tos.getValueOf(this.terminal)) && !"".equals(addThis.getValue())) {
				int maxLengthTo = this.tos.getMaxLengthOf(this.terminal);		
				if(maxLengthTo == -1) {
					new RaiseError(this.terminal+" has not been declared properly.");
				}
				//Maximum length between the two operands
				int maxLengthOp = (this.tos.getValueOf(this.terminal).length() >= addThis.getValue().length())?
						this.tos.getValueOf(this.terminal).length():
							addThis.getValue().length();
				if(maxLengthOp > this.tos.getMaxLengthOf(this.terminal)) {
					new RaiseWarning("Warning: "+this.terminal+" may contain a number composed of " +
							maxLengthOp+" digits, its limit being "+this.tos.getMaxLengthOf(this.terminal));
				}
			}

			this.llvm.newVariable(this.terminal);
			matchNextToken("IDENTIFIER");
			this.llvm.arthimOperation("addition");
			endInst();
		}
		else if("SUBSTRACT_KEYWORD".equals(this.token)) {
			matchNextToken("SUBSTRACT_KEYWORD");
			expression();
			matchNextToken("FROM_KEYWORD");

			this.llvm.newVariable(this.terminal);
			this.llvm.subtract();
			
			matchNextToken("IDENTIFIER");
			endInst();
		}
		else if("MULTIPLY_KEYWORD".equals(this.token)) {
			matchNextToken("MULTIPLY_KEYWORD");
			this.multiplying = true;
			assignEnd();
			endInst();
		}
		else if("DIVIDE_KEYWORD".equals(this.token)) {
			matchNextToken("DIVIDE_KEYWORD");
			this.dividing = true;
			assignEnd();
			endInst();
		}
		
		this.llvm.resetVars();
	}
	
	private void assignEnd() {
		expression();
		matchNextToken("COMMA");
		expression();
		
		if(this.multiplying) {
			this.llvm.arthimOperation("multiplication");
		}
		else if(this.dividing) {
			this.llvm.arthimOperation("division");
		}
			
		matchNextToken("GIVING_KEYWORD");

		this.llvm.newLocalVariable(this.terminal);
		
		matchNextToken("IDENTIFIER");
		
		this.llvm.compute();
		this.dividing = false;
		this.multiplying = false;
	}
	
	private VariableInteger expression() {
		VariableInteger varInt = expAnd();
		expressionLR();
		return varInt;
	}
	
	private void expressionLR() {
		if("OR_KEYWORD".equals(this.token)) {
			//TODO LLVM
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
	
	private VariableInteger expAnd() {
		//TODO LLVM
		VariableInteger var = expEqual();
		expAndLR();
		return var;
	}
	
	private void expAndLR() {
		//TODO LLVM
		if("AND_KEYWORD".equals(this.token)) {
			matchNextToken("AND_KEYWORD");
			//this.varRightAnd = expEqual();
			expEqual();
			expAndLR();
		}
		else if("OR_KEYWORD".equals(this.token)) {
			//epsilon
		}
	}
	
	private VariableInteger expEqual() {
		this.varLeftEq = expAdd();
		return expEqualLR();
	}
	
	private VariableInteger expEqualLR() {
		if("EQUALS_SIGN".equals(this.token)) {
			matchNextToken("EQUALS_SIGN");
			this.varRightEq = expAdd();
			if(this.varLeftEq != null && this.varRightEq != null) {
				if(!"".equals(this.varLeftEq.getValue()) && !"".equals(this.varRightEq.getValue())) {
					this.varLeftEq.setValue(Integer.toString(
							(Integer.parseInt(this.varLeftEq.getValue()))));
					this.varLeftEq.setMaxDigits(this.varLeftEq.getValue().length());
				}
				else {
					int maxDigits = (this.varLeftEq.getMaxDigits()>=this.varRightEq.getMaxDigits())?
							this.varLeftEq.getMaxDigits():this.varRightEq.getMaxDigits();
					this.varLeftEq.setMaxDigits(maxDigits);
				}
				
				this.llvm.condition("eq", this.varLeftEq.getValue(), this.varRightEq.getValue());
			}
		}
		else if("LOWER_THAN".equals(this.token)) {
			matchNextToken("LOWER_THAN");
			this.varRightEq = expAdd();
			this.llvm.condition("slt", this.varLeftEq.getValue(), this.varRightEq.getValue());
		}
		else if("GREATER_THAN".equals(this.token)) {
			matchNextToken("GREATER_THAN");
			this.varRightEq = expAdd();
			this.llvm.condition("sgt", this.varLeftEq.getValue(), this.varRightEq.getValue());
		}
		else if("LOWER_OR_EQUAL".equals(this.token)) {
			matchNextToken("LOWER_OR_EQUAL");
			this.varRightEq = expAdd();
			this.llvm.condition("sle", this.varLeftEq.getValue(), this.varRightEq.getValue());
		}
		else if("GREATER_OR_EQUAL".equals(this.token)) {
			matchNextToken("GREATER_OR_EQUAL");
			this.varRightEq = expAdd();
			this.llvm.condition("sge", this.varLeftEq.getValue(), this.varRightEq.getValue());
		}
		else if("AND_KEYWORD".equals(this.token)) {
			//epsilon
		}
		
		return this.varLeftEq;
	}
	
	private VariableInteger expAdd() {
		this.varLeftAdd = expMult();
		return expAddLR();
	}
	
	private VariableInteger expAddLR() {
		if("PLUS_SIGN".equals(this.token)) {
			matchNextToken("PLUS_SIGN");
			this.varRightAdd = expMult();
			
			this.llvm.arthimOperation("addition");
			
			if(this.varLeftAdd != null && this.varRightAdd != null) {
				if(!"".equals(this.varLeftAdd.getValue()) && !"".equals(this.varRightAdd.getValue())) {
					this.varLeftAdd.setValue(Integer.toString(
							(Integer.parseInt(this.varLeftAdd.getValue())
							+Integer.parseInt(this.varRightAdd.getValue()))));
					this.varLeftAdd.setMaxDigits(this.varLeftAdd.getValue().length());
				}
				else {
					int maxDigits = (this.varLeftAdd.getMaxDigits()>=this.varRightAdd.getMaxDigits())?
							this.varLeftAdd.getMaxDigits():this.varRightAdd.getMaxDigits();
					this.varLeftAdd.setMaxDigits(maxDigits);
				}
			}
			expAddLR();
		}
		else if("MINUS_SIGN".equals(this.token)) {
			matchNextToken("MINUS_SIGN");
			expMult();
			
			this.llvm.arthimOperation("subtraction");
			
			if(this.varLeftAdd != null && this.varRightAdd != null) {
				if(!"".equals(this.varLeftAdd.getValue()) && !"".equals(this.varRightAdd.getValue())) {
					this.varLeftAdd.setValue(Integer.toString(
							(Integer.parseInt(this.varLeftAdd.getValue())
							-Integer.parseInt(this.varRightAdd.getValue()))));
					this.varLeftAdd.setMaxDigits(this.varLeftAdd.getValue().length());
				}
				else {
					int maxDigits = (this.varLeftAdd.getMaxDigits()>=this.varRightAdd.getMaxDigits())?
							this.varLeftAdd.getMaxDigits():this.varRightAdd.getMaxDigits();
					this.varLeftAdd.setMaxDigits(maxDigits);
				}
			}
			
			expAddLR();
		}
		else if("EQUALS_SIGN".equals(this.token) || "LOWER_THAN".equals(this.token) ||
				"GREATER_THAN".equals(this.token) || "LOWER_OR_EQUAL".equals(this.token) ||
				"GREATER_OR_EQUAL".equals(this.token)) {
			//epsilon
		}
		
		return this.varLeftAdd;
	}
	
	private VariableInteger expMult() {
		this.varLeftMult = expNot();//
		return expMultLR();
	}
	
	private VariableInteger expMultLR() {
		if("MULTIPLICATION_SIGN".equals(this.token)) {
			matchNextToken("MULTIPLICATION_SIGN");
			this.varRightMult = expNot();//Now it should be time to do left * right
			
			//Now that we have the two operands, we can ask llvm to do the operation:
			this.llvm.arthimOperation("multiplication");
			if(this.varLeftMult != null && this.varRightMult != null) {
				if(!"".equals(this.varLeftMult.getValue()) && !"".equals(this.varRightMult.getValue())) {
					this.varLeftMult.setValue(Integer.toString(
							(Integer.parseInt(this.varLeftMult.getValue())
							*Integer.parseInt(this.varRightMult.getValue()))));
					this.varLeftMult.setMaxDigits(this.varLeftMult.getValue().length());
				}
				else {
					int maxDigits = (this.varLeftMult.getMaxDigits()>=this.varRightMult.getMaxDigits())?
							this.varLeftMult.getMaxDigits():this.varRightMult.getMaxDigits();
					this.varLeftMult.setMaxDigits(maxDigits);
				}
			}
			
			expMultLR();
		}
		else if("DIVISION_SIGN".equals(this.token)) {
			matchNextToken("DIVISION_SIGN");
			this.varRightMult = expNot();
			
			this.llvm.arthimOperation("division");
			
			if(this.varLeftMult != null && this.varRightMult != null) {
				if(!"".equals(this.varLeftMult.getValue()) && !"".equals(this.varRightMult.getValue())) {
					this.varLeftMult.setValue(Integer.toString(
							(Integer.parseInt(this.varLeftMult.getValue())
							/Integer.parseInt(this.varRightMult.getValue()))));
					this.varLeftMult.setMaxDigits(this.varLeftMult.getValue().length());
				}
				else {
					int maxDigits = (this.varLeftMult.getMaxDigits()>=this.varRightMult.getMaxDigits())?
							this.varLeftMult.getMaxDigits():this.varRightMult.getMaxDigits();
					this.varLeftMult.setMaxDigits(maxDigits);
				}
			}
			
			expMultLR();
		}
		else if("PLUS_SIGN".equals(this.token) || "MINUS_SIGN".equals(this.token)) {
			//espilon
		}
		return this.varLeftMult;
	}
	
	private VariableInteger expNot() {
		VariableInteger var = null;
		if("MINUS_SIGN".equals(this.token)) {
			matchNextToken("MINUS_SIGN");
			if("FALSE_KEYWORD".equals(this.token) || "TRUE_KEYWORD".equals(this.token)) {
				new RaiseError("Trying to do '- "+this.terminal+"', this makes no sense.");
			}
			expNot();
			this.llvm.oppositeOperation();
		}
		else if("NOT_KEYWORD".equals(this.token)) {
			matchNextToken("NOT_KEYWORD");
			if("MINUS_SIGN".equals(this.token)) {
				new RaiseError("Trying to do 'not "+this.terminal+"', this makes no sense.");
			}
			expNot();
			this.llvm.notOperation();
		}
		else if("OPENING_PARENTHESIS".equals(this.token) || "IDENTIFIER".equals(this.token) ||
				"INTEGER".equals(this.token) || "TRUE_KEYWORD".equals(this.token) ||
				"FALSE_KEYWORD".equals(this.token)) {
			var = expressionParenthesis();
		}
		return var;
	}
	
	private VariableInteger expressionParenthesis() {
		//Parenthesis are not explicitly written in llvm.
		VariableInteger var=null;
		if("OPENING_PARENTHESIS".equals(this.token)) {
			matchNextToken("OPENING_PARENTHESIS");
			var = expression();
			matchNextToken("CLOSING_PARENTHESIS");
		}
		else if("IDENTIFIER".equals(this.token) ||
				"INTEGER".equals(this.token) || "TRUE_KEYWORD".equals(this.token) ||
				"FALSE_KEYWORD".equals(this.token)) {
			var = expTerm();
		}
		return var;
	}
	
	private VariableInteger expTerm() {
		VariableInteger var=null;
		if("IDENTIFIER".equals(this.token)) {
			var =  new VariableInteger(this.tos.getValueOf(this.terminal),
					this.tos.isIdSigned(this.terminal),
					this.tos.getMaxLengthOf(this.terminal));
			this.llvm.newVariable(this.terminal);
			matchNextToken("IDENTIFIER");
		}
		else if("INTEGER".equals(this.token)) {
			var = new VariableInteger(this.terminal,
					this.terminal.matches("^-.*"),
					this.terminal.length());
			this.llvm.newVariable(this.terminal);
			matchNextToken("INTEGER");
		}
		else if("TRUE_KEYWORD".equals(this.token)) {
			this.llvm.newVariable(this.terminal);
			matchNextToken("TRUE_KEYWORD");
		}
		else if("FALSE_KEYWORD".equals(this.token)) {
			this.llvm.newVariable(this.terminal);
			matchNextToken("FALSE_KEYWORD");
		}
		return var;
	}
	
	private void ifRule() {
		matchNextToken("IF_KEYWORD");
		expression();
		this.llvm.ifStatement();
		matchNextToken("THEN_KEYWORD");
		instructionList();
		ifEnd();
	}
	
	private void ifEnd() {
		if("ELSE_KEYWORD".equals(this.token)) {
			matchNextToken("ELSE_KEYWORD");
			this.llvm.elseStatement();
			instructionList();
			this.llvm.endIfStatement();
			matchNextToken("ENDI-IF_KEYWORD");
		}
		else if("ENDI-IF_KEYWORD".equals(this.token)) {
			this.llvm.endIfStatement();
			matchNextToken("ENDI-IF_KEYWORD");
		}
	}
	
	private void call() {
		matchNextToken("PERFORM_KEYWORD");
		if(!this.callList.contains(this.terminal)) {
			this.callList.add(this.terminal);
		}
		this.llvm.jumpTo(this.terminal);
		matchNextToken("IDENTIFIER");
		callTail();
	}
	
	private void callTail() {
		if("UNTIL_KEYWORD".equals(this.token)) {
			matchNextToken("UNTIL_KEYWORD");
			expression();
			this.llvm.untilCondition();
			endInst();
		}
		else if("END_OF_INSTRUCTION".equals(this.token)) {
			endInst();
		}
	}
	
	private void read() {
		matchNextToken("ACCEPT_KEYWORD");
		this.llvm.readInput(this.terminal);
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
			this.llvm.display(this.varLeftEq.getValue());
			endInst();
		}
		else if("STRING".equals(this.token)) {
			this.llvm.display(this.terminal);
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
