package compilo;

/**
 * Prints error message on the error output.
 *
 */
public class RaiseError {
	
	public RaiseError(String message) {
		System.err.println("Error raised:\n\t"+message);
	}

}
