
public class DFA implements IDFA{
	private IState root;
	public DFA(IState r) {
		this.root = r;
	}
	
	public IState getRootNode() {
		return this.root;
	}
}
