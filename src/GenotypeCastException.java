/*
 * GenotypeCastException.java
 *
 * Version:
 *     $Id: GenotypeCastException.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: GenotypeCastException.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.1  2009/09/27 22:54:59  kyle
 *      Initial revision
 *
 *
 */

/**
 * Exception thrown when two incompatible genotypes
 * are attempted to be crossed over.
 *
 * @author Kyle Dewey
 */
public class GenotypeCastException extends Exception {
    /**
     * Default constructor.
     */
    public GenotypeCastException() {
        super();
    }

    /**
     * A constructor, with an informative message attached.
     *
     * @param message The message to use
     */
    public GenotypeCastException( String message ) {
        super( message );
    }
}
