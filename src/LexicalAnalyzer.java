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
		
		boolean bool = isIdentifier("1");
		//System.out.println(bool);
		
		//System.out.println(isImage("s9(5)v9(2)"));
		
		//System.out.println(isInteger("+9"));
		
		//System.out.println(isReal("-8093.0"));
		
		System.out.println(isString("'fdFr5-?+*/:!'"));
	}
	
	public boolean isIdentifier(String input) {
		this.pattern = Pattern.compile("^[a-zA-Z][\\w\\-]{0,14}");
		this.match = this.pattern.matcher(input);
		return this.match.matches();
	}
	
	public boolean isImage(String input) {
		this.pattern = Pattern.compile("s?9(\\([1-9]\\))?(v9(\\([1-9]\\))?)?");
		this.match = this.pattern.matcher(input);
		return this.match.matches();
	}
	
	public boolean isInteger(String input) {
		this.pattern = Pattern.compile("[\\+\\-]?[1-9][0-9]*");
		this.match = this.pattern.matcher(input);
		return this.match.matches();
	}
	
	public boolean isReal(String input) {
		this.pattern = Pattern.compile("[\\+\\-]?[1-9][0-9]*(\\.[0-9]+)?");
		this.match = this.pattern.matcher(input);
		return this.match.matches();
	}
	
	public boolean isString(String input) {
		this.pattern = Pattern.compile("^'[\\w\\-\\+\\*/:!\\?]*'$");
		this.match = this.pattern.matcher(input);
		return this.match.matches();
	}
	
	
	public String readToken(String input) {
		
		
		return "";
	}
	
	
	public String nextToken() {
		String token="";
		
		return token;
	}
}
