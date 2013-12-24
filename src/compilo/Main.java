package compilo;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
		
		SymbolsTable tos = new SymbolsTable();
		LexicalAnalyzer lex = new LexicalAnalyzer(tos);
		LLVMGenerator llvm = new LLVMGenerator();
		Parser parser = new Parser(lex, tos, llvm);
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new DataInputStream(new FileInputStream(args[0]))));
			String input = "";
			String line;
			while((line = br.readLine()) != null) {
				input += line + "\n";
			}
			input = input.substring(0, input.length()-1);//Removes the final '\n'
			input += "#";
			input = input.replaceAll("\t", "");
			
			parser.parse(input);
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
