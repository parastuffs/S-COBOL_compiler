package compilo;

public class VariableInteger {
	
	private String value;
	private boolean signed;
	private int maxDigits;
	
	public VariableInteger(String v, boolean s, int mD) {
		this.value = v;
		this.signed = s;
		this.maxDigits = mD;
	}
	
	public boolean isSigned() {
		return this.signed;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String v) {
		this.value = v;
	}
	
	public int getMaxDigits() {
		return this.maxDigits;
	}
	
	public void setMaxDigits(int m) {
		this.maxDigits = m;
	}
}
