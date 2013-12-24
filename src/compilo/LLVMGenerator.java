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

	public LLVMGenerator() {
		this.firstLabel = true;
	}
	
	public void newVariable(String name, String value, String maxDigits) {
		String var = "@"+name+" = i";
		int maxNumber = maxNumbFromMaxDigits(Integer.parseInt(maxDigits));
		var += Integer.toString((int)(Math.ceil(Math.log10(maxNumber)/Math.log10(2))+1));
		if(!"".equals(value)) {
			var += " "+value;
		}
		var+="\n";
		this.code += var;
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
		procedure+="\n";
		this.code += procedure;
	}
	
	public void newLabel(String lab) {
		if(firstLabel) {
			lab = "entry";
			firstLabel = false;
		}
		String label = "\t"+lab+":";
		label+="\n";
		this.code += label;
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
