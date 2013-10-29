import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <b>Lexical analyzer implementing the DFA.</b>
 * <p>This analyzes the lexical content of the code entered
 * by the user. It also feed the table of symbols with
 * identifiers, images and number of line.
 * 
 * @author Quentin Delhaye
 *
 * @see SymbolsTable
 */
public class LexicalAnalyzer {
	/**
	 * <p>List of all the possible keywords.</p>
	 */
	private List<String> keywords;
	
	/**
	 * <p>List of all the possible lexical units corresponding
	 * to the equivalent keyword.</p>
	 * @see LexicalAnalyzer#keywords
	 */
	private List<String> units;
	
	/**
	 * <p>Array containing an identifier and its corresponding
	 * image. Used to feed the table of symbols.</p>
	 * @see SymbolsTable#addVariable
	 */
	private String[] variable = {"",""};
	
	/**
	 * <p>Array containing an identifier and the line number
	 * on which it occurred. Used to feed the table of symbols.</p>
	 * @see SymbolsTable#addLabel
	 */
	private String[] label = {"",""};
	
	/**
	 * <p>Table of symbols.</p>
	 * @see SymbolsTable
	 */
	private SymbolsTable symTab;
	
	/**
	 * <b>Constructor</b>
	 * <p>Initializes the keywords and lexical units ArrayLists.
	 * Creates the table of symbols.
	 * </p>
	 */
	public LexicalAnalyzer() {
		this.keywords = new ArrayList<String>(Arrays.asList("identification","division","program-id","author",".",
				"\n","date-written","environment","configuration","section","source-computer",
				"object-computer","data","working-storage","pic","value","procedure","end",
				"program","stop","run","move","to","compute","add","substract","multiply",
				"divide","giving",",","(",")","-","+","=","*","/","not","true","false",
				"<",">","<=",">=","and","or","if","else","end-if","until","accept","display"));

		this.units = new ArrayList<String>( Arrays.asList("IDENTIFICATION_KEYWORD","DIVISION_KEYWORD","PROGRAM-ID_KEYWORD",
				"AUTHOR_KEYWORD","DOT_KEYWORD","END_OF_LINE","DATE_WRITTEN_KEYWORD","ENVIRONMENT_KEYWORD",
				"CONFIGURATION_KEYWORD","SECTION_KEYWORD","SOURCE-COMPUTER_KEYWORD","OBJECT-COMPUTER_KEYWORD",
				"DATA_KEYWORD","WORKING-STORAGE_KEYWORD","PIC_KEYWORD","VALUE_KEYWORD","PROCEDURE_KEYWORD",
				"END_KEYWORD","PROGRAM_KEYWORD","STOP_KEYWORD","RUN_KEYWORD","MOVE_KEYWORD",
				"TO_KEYWORD","COMPUTE_KEYWORD","ADD_KEYWORD","SUBSTRACT_KEYWORD",
				"MULTIPLY_KEYWORD","DIVIDE_KEYWORD","GIVING_KEYWORD","COMMA","OPENING_PARENTHESIS",
				"CLOSING_PARENTHESIS","MINUS_SIGN","PLUS_SIGN","EQUALS_SIGN","MULTIPLICATION_SIGN",
				"DIVISION_SIGN","NOT_KEYWORD","TRUE_KEYWORD","FALSE_KEYWORD","LOWER_THAN",
				"GREATER_THAN","LOWER_OR_EQUAL","GREATER_OR_EQUAL","AND_KEYWORD","OR_KEYWORD",
				"IF_KEYWORD","ELSE_KEYWORD","ENDI-IF_KEYWORD","UNTIL_KEYWORD","ACCEPT_KEYWORD",
				"DISPLAY_KEYWORD"));

		this.symTab = new SymbolsTable();
		//this.label = {"",""};
		//this.variable = {"",""};
		/* On pourrait rencontrer un problème si on lit un simple '9', est-ce une image ou
		 * un réel ? Il va falloir prendre une décision et justifier ce choix dans le rapport
		 * On peut aussi penser à un nouvel identifiant qui peut être soit une image, soit
		 * un réel et décider du quel utiliser lors de l'analyse sémantique.
		 */
		
		//boolean bool = isIdentifier("1");
		//System.out.println(bool);
		
		//System.out.println(isImage("s9(5)v9(2)"));
		
		//System.out.println(isInteger("+9"));
		
		//System.out.println(isReal("-8093.0"));
		
		//System.out.println(isString("'fdFr5-?+*/:!'"));
		
		//System.out.println(isString("'This is SPARTA'"));
	}
	
	/**
	 * <p>Checks if the input String is an identifier.</p>
	 * 
	 * @param input
	 * 				Candidate to the identifier status.
	 * @return True if the input matches the regex.
	 */
	private boolean isIdentifier(String input) {
		return input.matches("^[a-zA-Z][\\w\\-]{0,14}");
	}

	/**
	 * <p>Checks if the input String is an image.</p>
	 * 
	 * @param input
	 * 				Candidate to the image status.
	 * @return True if the input matches the regex.
	 */
	private boolean isImage(String input) {
		return input.matches("s?9(\\([1-9]\\))?(v9(\\([1-9]\\))?)?");
	}

	/**
	 * <p>Checks if the input String is an integer.</p>
	 * 
	 * @param input
	 * 				Candidate to the integer status.
	 * @return True if the input matches the regex.
	 */
	private boolean isInteger(String input) {
		return input.matches("[\\+\\-]?([0-9])|([1-9][0-9]*)");
	}

	/**
	 * <p>Checks if the input String is a real number.</p>
	 * 
	 * @param input
	 * 				Candidate to the real status.
	 * @return True if the input matches the regex.
	 */
	private boolean isReal(String input) {
		return input.matches("[\\+\\-]?[1-9][0-9]*(\\.[0-9]+)?");
	}

	/**
	 * <p>Checks if the input String is a string.</p>
	 * 
	 * @param input
	 * 				Candidate to the string status.
	 * @return True if the input matches the regex.
	 */
	private boolean isString(String input) {
		return input.matches("^'[\\w\\ \\-\\+\\*/:!\\?]*'$");
	}

	/**
	 * <p>Checks if the input String is a comment.</p>
	 * 
	 * @param input
	 * 				Candidate to the comment status.
	 * @return True if the input matches the regex.
	 */
	private boolean isComment(String input, boolean newLine) {
		//System.out.println("In 'iscomment()': input='"+input+"'");
		return (newLine && (input.matches("\\*.*\n$") || input.matches("/.*\n$")));
	}
	
	
	/**
	 * <p>Analyzes the String it is feed with.</p>
	 * <p>It begins by checking either the input is a comment or not.
	 * If not, it splits it and begins the analysis. As soon as a token
	 * is recognized, the method gives it back to the caller along 
	 * the corresponding lexical unit.
	 * </p>
	 * <p>It will also feed the table of symbols with various couple
	 * of identifier/image or identifier/line number.
	 * </p>
	 * 
	 * @param input
	 * 				String to analyze.
	 * @param newLine
	 * 				Boolean telling if the input String is
	 * 				the beginning of a new line or not.
	 * 				Useful for the isComment() method.
	 * @param lineNum
	 * 				Line of the current String being analyzed.
	 * 				Useful for the table of symbols.
	 * 				
	 * @return Array containing the result of the analysis:
	 * 			<ul>
	 * 				<li>[0]: token.</li>
	 * 				<li>[1]: lexical unit.</li>
	 * 			</ul>
	 * 
	 * @see SymbolsTable#addLabel
	 * @see SymbolsTable#addVariable
	 */
	public String[] nextToken(String input, boolean newLine, int lineNum) {
		//System.out.println("Entry: input='"+input+"', newLine="+newLine);
		String[] couple = {"",""};
		int n;
		
		//Comment line
		if(isComment(input,newLine)) {
			//System.out.println("Comment");
			couple[0] = input;
			couple[1] = "COMMENT";
		}
		
		else {
			String sub[] = input.split(" ");
			//System.out.println("sub length="+sub.length);
			boolean found = false;
			for(int i=0;i<sub.length && !found;i++) {
				couple[0] = "";
				//Successive concatenation
				for(int j=0;j<=i;j++) {
					//System.out.println("sub[j]="+sub[j]);
					couple[0] += sub[j];
					if(j<i) {//If it's not the last piece
						couple[0] += " ";
					}
				}
				
				//The identifier may end with a dot or dot+\n:
				if(couple[0].matches(".+\\.$") || couple[0].matches(".+\\.\\n$")) {
					couple[0] = couple[0].substring(0, couple[0].indexOf("."));
				}
				//Dot + \n -> end_of_instruction
				if(input.matches("^\\.\n$")) {
					found=true;
					couple[0] = couple[0].replaceAll("\n$", "\\\\n");
					couple[1] = "END_OF_INSTRUCTION";
					this.variable[0] = null;
					this.label[0] = null;
				}
				else if((n=this.keywords.indexOf(couple[0])) > 0) {
					couple[1] = this.units.get(n);
					found = true;
					this.variable[0] = null;
					this.label[0] = null;
				}
				else if(isString(couple[0])) {
					found = true;
					couple[1] = "STRING";
					this.variable[0] = null;
					this.label[0] = null;
				}
				else if(isInteger(couple[0])) {
					found = true;
					couple[1] = "INTEGER";
					this.variable[0] = null;
					this.label[0] = null;
				}
				else if(isReal(couple[0])) {
					found = true;
					couple[1] = "REAL";
					this.variable[0] = null;
					this.label[0] = null;
				}
				else if(isImage(couple[0])) {
					if(this.variable[0]!=null) {
						this.variable[1] = couple[0];
						this.symTab.addVariable(this.variable);
					}
					found = true;
					couple[1] = "IMAGE";
					this.variable[0] = null;
					this.label[0] = null;
				}
				else if(isIdentifier(couple[0])) {
					this.variable[0] = couple[0];
					this.label[0] = couple[0];
					this.label[1] = Integer.toString(lineNum);
					this.symTab.addLabel(this.label);
					found = true;
					couple[1] = "IDENTIFIER";
				}
				else {
					couple[1] = "ERROR";//ERROR state
				}
				
			}
		}
		return couple;
	}

	/**
	 * <p>Ask the table of symbols to the SymbolsTable
	 * object and print it on the standard output.</p>
	 * @see SymbolsTable#toString
	 */
	public void printSymbolsTable() {
		System.out.println(this.symTab.toString());
		
	}
}
