/**
 * Exception thrown when an argument is invalid
 * @author Kyle Dewey
 */
public class ArgsException extends Exception {
    /**
     * Creates a new args exception.
     * @param message A message to show the user
     */
    public ArgsException( String message ) {
	super( message );
    }
}
