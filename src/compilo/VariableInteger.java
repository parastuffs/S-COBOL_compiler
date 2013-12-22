package compilo;

public class VariableInteger {
	
	private int value;
	private boolean signed;
	private int maxDigits;
	
	public VariableInteger(int v, boolean s, int mD) {
		this.value = v;
		this.signed = s;
		this.maxDigits = mD;
	}
	
	public boolean isSigned() {
		return this.signed;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public int getMaxDigits() {
		return this.maxDigits;
	}
}
