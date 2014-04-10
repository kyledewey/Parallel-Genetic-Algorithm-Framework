/*
 * LargeParsimonyArgumentException.java
 *
 * Version:
 *     $Id: LargeParsimonyArgumentException.java,v 1.1 2009/10/27 21:42:10 kyle Exp $
 *
 * Revisions:
 *      $Log: LargeParsimonyArgumentException.java,v $
 *      Revision 1.1  2009/10/27 21:42:10  kyle
 *      Initial revision
 *
 *
 */

/**
 * Exception thrown when command line arguments for
 * the large parsimony problem are malformed.
 *
 * @author Kyle Dewey
 */
public class LargeParsimonyArgumentException extends Exception {
    /**
     * Contains an informative message to show the user
     * on error.
     *
     * @param message The message to show the user
     */
    public LargeParsimonyArgumentException( String message ) {
        super( message );
    }
}
