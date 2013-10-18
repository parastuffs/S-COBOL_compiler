
public interface IState {
	
	/**
	 * @return true if the node is an accepting state.
	 */
	public String toString();
	
	/**
	 * @return the lexicalUnit string.
	 */
	public boolean isFinal();
	
	/**
	 * @return the subsequent state in the DFA.
	 */
	public IState getNextState();
	
	/**
	 * @param input(input character to test)
	 * @return True if the transition from the previous state to the
	 * current is validated by the DFA, that is if the input character (String typed)
	 * is in the set of tokens specified in the state.
	 */
	public boolean stateValidated(String input);
}
