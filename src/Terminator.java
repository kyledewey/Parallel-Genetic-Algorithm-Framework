/*
 * Terminator.java
 * 
 * Version:
 *     $Id: Terminator.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: Terminator.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.1  2009/10/27 18:15:22  kyle
 *      Initial revision
 *
 *
 */

/**
 * A termination condition for the given population.
 * Can be based on anything that the population can
 * tell you.
 *
 * @author Kyle Dewey
 */
public interface Terminator {
    /**
     * Returns true if the given population is done, else false.
     *
     * @param population The population to check
     *
     * @return true if the given population is done, else false
     */
    public boolean shouldTerminate( Population population );
}
