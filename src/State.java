import java.util.List;


public class State implements IState{
	private boolean finalState;
	private String lexicalUnit;
	private int id;
	private IState nextState;
	private String stateTokens;
	
	public State(boolean f, String s, int i, IState n, String t) {
		this.finalState = f;
		this.lexicalUnit = s;
		this.id = i;
		this.nextState = n;
		this.stateTokens = t;
	}
	
	public boolean isFinal() {
		return this.finalState;
	}
	
	public String toString() {
		return this.lexicalUnit;
	}

	public IState getNextState() {
		return this.nextState;
	}
	
	public boolean stateValidated(String input) {
		return this.stateTokens.contains(input);
	}
	
}
