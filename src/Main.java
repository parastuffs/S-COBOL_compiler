import java.util.Scanner;

/**
 * <b>Main class handling the interaction with the user.</b>
 * <p>This class creates a lexical analyzer and feeds him with
 * the entries of the user.
 * </p>
 * @author Quentin Delhaye
 * 
 * @see LexicalAnalyzer
 *
 */
public class Main {

	/**
	 * <p>Executed method.</p>
	 * <p>This method creates the lexical analyzer, then ask the
	 * user to enter S-COBOL code lines. It feeds them to the
	 * analyzer until the user enters an empty String (by simply
	 * hitting the return key without entering anything).
	 * After each line of code, the method prints the tokens
	 * found in the line by the analyzer or notify the user
	 * of the failure of the analysis.
	 * 
	 * When the user stops entering code, the method asks the 
	 * analyzer to print the table of symbols on the standard output.
	 * 
	 * @param args
	 * 
	 * @see LexicalAnalyzer#nextToken(String, boolean, int)
	 * @see LexicalAnalyzer#printSymbolsTable()
	 */
	public static void main(String[] args) {
	
		LexicalAnalyzer lex = new LexicalAnalyzer();
		
		Scanner sc = new Scanner(System.in);
		String str;
		boolean newString;
		int lineNum = 1;
		System.out.println(">Please enter a line of S-COBOL code:");
		str = sc.nextLine();
		while (!str.isEmpty()){
			str = str+"\n";
			String[] lexCouple={"",""};
			newString = true;
			lexCouple[1] = "";
			while(!str.isEmpty() && lexCouple[1]!="ERROR") {
				lexCouple = lex.nextToken(str,newString,lineNum);
				//System.out.println("Couple returned: {"+lexCouple[0]+","+lexCouple[1]+"}");
				//System.out.println("Before escaping: '"+lexCouple[0]+"'");
				lexCouple[0] = espaceChar(lexCouple[0]);
				//System.out.println("After escaping: '"+lexCouple[0]+"'");
				str = str.replaceFirst(lexCouple[0], "");
				str = str.replaceFirst("^\\ ", "");
				lexCouple[0] = unEscpaceChar(lexCouple[0]);
				if(lexCouple[1]=="ERROR") {
					System.out.println("The line was rejected.");
				}
				else {
					System.out.println("token: '"+lexCouple[0]+"'	lexical unit: '"
							+lexCouple[1]+"'");
				}
				//System.out.println("What's left: '"+str+"'");
				newString = false;
			}
			lineNum++;
			System.out.println(">Please enter a line of S-COBOL code:");
			str = sc.nextLine();
		}
		
		lex.printSymbolsTable();
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
	private static String espaceChar(String input) {
		input = input.replaceAll("\\(", "[(]");
		input = input.replaceAll("\\)", "[)]");
		input = input.replaceAll("\\*", "[*]");
		input = input.replaceAll("\\?", "[?]");
		return input;
	}
}
