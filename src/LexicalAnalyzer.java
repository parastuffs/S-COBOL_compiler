import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LexicalAnalyzer {

	private String[] keywords;
	Matcher match;
	Pattern pattern;
	
	
	public LexicalAnalyzer() {
		this.keywords = new String[]{"identification","division","program-id","author",".",
				"\n","date-written","environment","configuration","section","source-computer",
				"object-computer","data","working-storage","pic","value","procedure","end",
				"program","stop","run","move","to","compute","add","substract","multiply",
				"divide","giving",",","(",")","-","+","=","*","/","not","true","false",
				"<",">","<=",">=","and","or","if","else","end-if","until","accept","display"};
				
		System.out.println(this.keywords[0]);
		
		/* On pourrati rencontrer un problème si on lit un simple '9', est-ce une image ou
		 * un réel ? Il va falloir prendre une décision et justifier ce choix dans le rapport
		 * On peut aussi penser à un nouvel identifiant qui peut être soit une image, soit
		 * un réel et décider du quel utiliser lors de l'analyse sémantique.
		 */
		
		/* Implémenter le DFA sous forme d'un arbre. Chaque noeud sera un objet qui aura un attribut
		 * 'isFinalState' à tester à la fin de l'input. Un autre attribut déterminera l'unité
		 * lexicale. Construire la table des symboles en parallèle.
		 */
		
		boolean bool = isIdentifier("1");
		//System.out.println(bool);
		
		//System.out.println(isImage("s9(5)v9(2)"));
		
		//System.out.println(isInteger("+9"));
		
		//System.out.println(isReal("-8093.0"));
		
		System.out.println(isString("'fdFr5-?+*/:!'"));
	}
	
	public boolean isIdentifier(String input) {
		return input.matches("^[a-zA-Z][\\w\\-]{0,14}");
	}
	
	public boolean isImage(String input) {
		return input.matches("s?9(\\([1-9]\\))?(v9(\\([1-9]\\))?)?");
	}
	
	public boolean isInteger(String input) {
		return input.matches("[\\+\\-]?[1-9][0-9]*");
	}
	
	public boolean isReal(String input) {
		return input.matches("[\\+\\-]?[1-9][0-9]*(\\.[0-9]+)?");
	}
	
	public boolean isString(String input) {
		return input.matches("^'[\\w\\-\\+\\*/:!\\?]*'$");
	}
	
	
	public String readToken(String input) {
		
		
		return "";
	}
	
	
	public String nextToken() {
		String token="";
		
		return token;
	}
}
