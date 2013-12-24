package compilo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LLVMGenerator {
	
	private String code = "";
	private boolean firstLabel;
	private int ifThenCount;
	private int ifElseCount;
	private int ifEndCount;
	private String localVar = "";
	
	/**Unnamed variable*/
	private int unamedVal = 0;
	private String val1 = null;
	private String val2 = null;

	public LLVMGenerator() {
		this.firstLabel = true;
	}
	
	public void declareVariable(String name, String value, String maxDigits) {
		String var = "@"+name+" = i";
		int maxNumber = maxNumbFromMaxDigits(Integer.parseInt(maxDigits));
		var += Integer.toString((int)(Math.ceil(Math.log10(maxNumber)/Math.log10(2))+1));
		if(!"".equals(value)) {
			var += " "+value;
		}
		this.code += var;
	}
	
	public void newLocalVariable(String var) {
		String str = "%temp = alloca i32\n";
		str += "%temp = load i32* @"+var+"\n";
		this.code += str;
	}
	
	public void compute() {
		String str = "store i32 +"+this.val1+", i32* %temp\n";
		this.code += str;
	}
	
	private int maxNumbFromMaxDigits(int maxDigits) {
		String numb = "";
		for(int i=0;i<maxDigits;i++) {
			numb += "9";
		}
		return Integer.parseInt(numb);
	}
	
	public void newProcedure(String proc) {
		String procedure = "define void @"+proc+"()";
		this.code += procedure;
	}
	
	public void newLabel(String lab) {
		if(firstLabel) {
			lab = "entry";
			firstLabel = false;
		}
		String label = "\t"+lab+":";
		this.code += label;
	}
	
	public void newLine() {
		this.code += "\n";
	}
	
	/**
	 * LLVM handles at most two variables at the same time.
	 * @param val name or value of the variable
	 */
	public void newVariable(String val) {
		if(this.val1 == null) {
			this.val1 = val;
			System.out.println("val1 now is '"+this.val1+"'");
		}
		else if(this.val1 != null) {
			this.val2 = val;
			System.out.println("val2 now is '"+this.val2+"'");
		}
	}

	public void resetVars() {
		this.val1 = null;
		this.val2 = null;
	}
	
	public void newComment(String comment) {
		comment = comment.replaceFirst("[/*]", ";");
		this.code += comment;
	}
	
	public void notOperation() {
		String op = "";
		if(this.val1.matches("^[a-zA-Z]")) {
			op = "%"+this.unamedVal+" = load i1* @"+this.val1;
			this.unamedVal++;
			op = "\n%"+this.unamedVal+" = xor i1 1, %"+Integer.toString(this.unamedVal-1);
			this.unamedVal++;
		}
		else if(this.val1.matches("^[1-9]") || "true".equals(this.val1)) {
			op = "%"+this.unamedVal+" = xor i1 1, 1";
			this.unamedVal++;
		}
		else if("0".equals(this.val1) || "false".equals(this.val1)) {
			op = "%"+this.unamedVal+" = xor i1 1, 0";
			this.unamedVal++;
		}
		else {
			op = "%"+this.unamedVal+" = xor i1 1, "+this.val1;
			this.unamedVal++;
		}
		this.val1 = "%"+Integer.toString(this.unamedVal-1);
		this.code += op;
		this.code += "\n";
	}
	
	public void oppositeOperation() {
		String op = "";
		if(this.val1.matches("^[a-zA-Z]")) {
			op = "%"+this.unamedVal+" = load i32* @"+this.val1;
			this.unamedVal++;
			op = "\n%"+this.unamedVal+" = sub i32 0, %"+Integer.toString(this.unamedVal-1);
			this.unamedVal++;
		}
		else if(this.val1.matches("^[0-9]")) {
			op = "%"+this.unamedVal+" = sub i32 0, "+this.val1;
			this.unamedVal++;
		}
		else {
			op = "%"+this.unamedVal+" = sub i32 0, "+this.val1;
			this.unamedVal++;
		}
		this.val1 = "%"+Integer.toString(this.unamedVal-1);
		this.code += op;
		this.code += "\n";
	}
	
	/**
	 * Performs one of the four basic arithmetic operations:
	 * addition, subtraction, multiplication or division.
	 * 
	 * @param opType
	 * 		<ul><li>multiplication</li>
	 * 			<li>subtraction</li>
	 * 			<li>addition</li>
	 * 			<li>subtraction</li>
	 * 		</ul>
	 */
	public void arthimOperation(String opType) {
		if("multiplication".equals(opType)) {
			opType = "mul";
		}
		else if("division".equals(opType)) {
			opType = "div";
		}
		else if("addition".equals(opType)) {
			opType = "add";
		}
		else if("subtraction".equals(opType)) {
			opType = "sub";
		}
		String op = "";
		//Both variables
		if(this.val1.matches("^[a-zA-Z]") && this.val2.matches("^[a-zA-Z]")) {
			op = "%"+this.unamedVal+" = load i32* @"+this.val1;
			this.unamedVal++;
			op += "\n%"+this.unamedVal+" = load i32* @"+this.val2;
			this.unamedVal++;
			op += "\n%"+this.unamedVal+" = "+opType+" i32 %"+
					Integer.toString(this.unamedVal-2)+
					", %"+Integer.toString(this.unamedVal-1);
			this.unamedVal++;
		}
		//val1 var && val2 value
		else if(this.val1.matches("^[a-zA-Z]") && this.val2.matches("^[0-9]")) {
			op = "%"+this.unamedVal+" = load i32* @"+this.val1;
			this.unamedVal++;
			op += "\n%"+this.unamedVal+" = "+opType+" i32 %"+
					Integer.toString(this.unamedVal-1)+", "+this.val2;
			this.unamedVal++;
		}
		//val1 value && val2 var
		else if(this.val1.matches("^[0-9]") && this.val2.matches("^[a-zA-Z]")) {
			op = "%"+this.unamedVal+" = load i32* @"+this.val2;
			this.unamedVal++;
			op += "\n%"+this.unamedVal+" = "+opType+" i32 %"+
					Integer.toString(this.unamedVal-1)+", "+this.val1;
			this.unamedVal++;
		}
		//both value
		else if(this.val1.matches("^[0-9]") && this.val2.matches("^[0-9]")) {
			op += "\n%"+this.unamedVal+" = "+opType+" i32 "+this.val2+", "+this.val1;
			this.unamedVal++;
		}
		else {
			op += "\n%"+this.unamedVal+" = "+opType+" i32 "+this.val2+", "+this.val1;
			this.unamedVal++;
		}
		this.val1 = "%"+Integer.toString(this.unamedVal-1);
		this.code += op;
		this.code += "\n";
	}
	
	public void subtract() {
		String op = "";
		//Both variables
		if(this.val1.matches("^[a-zA-Z]") && this.val2.matches("^[a-zA-Z]")) {
			op = "%"+this.unamedVal+" = load i32* @"+this.val1;
			this.unamedVal++;
			op += "\n%"+this.unamedVal+" = load i32* @"+this.val2;
			this.unamedVal++;
			op += "\n%"+this.unamedVal+" = sub i32 %"+
					Integer.toString(this.unamedVal-1)+
					", %"+Integer.toString(this.unamedVal-2);
			this.unamedVal++;
		}
		//val1 var && val2 value
		else if(this.val1.matches("^[a-zA-Z]") && this.val2.matches("^[0-9]")) {
			op = "%"+this.unamedVal+" = load i32* @"+this.val1;
			this.unamedVal++;
			op += "\n%"+this.unamedVal+" = sub i32 %"+
					this.val2+", "+Integer.toString(this.unamedVal-1);
			this.unamedVal++;
		}
		//val1 value && val2 var
		else if(this.val1.matches("^[0-9]") && this.val2.matches("^[a-zA-Z]")) {
			op = "%"+this.unamedVal+" = load i32* @"+this.val2;
			this.unamedVal++;
			op += "\n%"+this.unamedVal+" = sub i32 %"+
					this.val1+", "+Integer.toString(this.unamedVal-1);
			this.unamedVal++;
		}
		//both value
		else if(this.val1.matches("^[0-9]") && this.val2.matches("^[0-9]")) {
			op += "\n%"+this.unamedVal+" = sub i32 "+this.val1+", "+this.val2;
			this.unamedVal++;
		}
		else {
			op += "\n%"+this.unamedVal+" = sub i32 %"+this.val1+", "+this.val2;
			this.unamedVal++;
		}
		this.val1 = "%"+Integer.toString(this.unamedVal-1);
		this.code += op;
		this.code += "\n";
	}
	
	public void store() {
		String op = "";
		//Both variables
		if(this.val1.matches("^[a-zA-Z]") && this.val2.matches("^[a-zA-Z]")) {
			op = "%"+this.unamedVal+" = load i32* @"+this.val1;
			this.unamedVal++;
			op += "\n%"+this.unamedVal+" = load i32* @"+this.val2;
			this.unamedVal++;
			op += "\nstore i32 %"+Integer.toString(this.unamedVal-1)+
					", i32 %"+Integer.toString(this.unamedVal-2);
		}
		//val1 value && val2 var
		else if(this.val1.matches("^[0-9]") && this.val2.matches("^[a-zA-Z]")) {
			op = "%"+this.unamedVal+" = load i32* @"+this.val2;
			this.unamedVal++;
			op += "\nstore i32 %"+this.val1+", i32 "+
					Integer.toString(this.unamedVal-1);
		}
		else {
			op += "\nstore i32 %"+this.val1+", i32 "+this.val2;
			this.unamedVal++;
		}
		this.code += op;
		this.code += "\n";
	}
	
	public void condition(String condType, String left, String right) {
		String str = "";
		//Both variables
		if(left.matches("^[a-zA-Z]") && right.matches("^[a-zA-Z]")) {
			str += "%"+this.unamedVal+" = load i32* @"+left;
			this.unamedVal++;
			str += "\n%"+this.unamedVal+" = load i32* @"+right;
			this.unamedVal++;
			str = "\n%cond = icmp "+condType+" i32 %"+Integer.toString(this.unamedVal-2)+
					", %"+Integer.toString(this.unamedVal-1);
		}
		//val1 var && val2 value
		else if(left.matches("^[a-zA-Z]") && right.matches("^[0-9]")) {
			str += "%"+this.unamedVal+" = load i32* @"+left;
			this.unamedVal++;
			str = "\n%cond = icmp "+condType+" i32 %"+Integer.toString(this.unamedVal-1)+
					", "+right;
		}
		//val1 value && val2 var
		else if(left.matches("^[0-9]") && right.matches("^[a-zA-Z]")) {
			str += "\n%"+this.unamedVal+" = load i32* @"+right;
			this.unamedVal++;
			str = "\n%cond = icmp "+condType+" i32 "+
					", %"+Integer.toString(this.unamedVal-1);
		}
		//both value
		else if(left.matches("^[0-9]") && right.matches("^[0-9]")) {
			str += "\n%cond = icmp "+condType+" i32 "+this.val2+", "+this.val1;
		}
		else {
			str += "\n%cond = icmp "+condType+" i32 "+this.val2+", "+this.val1;
		}
		this.code += str;
		this.code += "\n";
	}
	
	public void toFile(String filename) {
		System.out.println("code llvm: \n"+this.code);
		FileOutputStream out;
		try {
			out = new FileOutputStream(filename);
			out.write(this.code.getBytes());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
