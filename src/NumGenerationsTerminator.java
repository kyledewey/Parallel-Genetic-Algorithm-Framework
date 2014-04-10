/*
 * NumGenerationsTerminator.java
 *
 * Version:
 *     $Id: NumGenerationsTerminator.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: NumGenerationsTerminator.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.1  2009/10/27 18:15:22  kyle
 *      Initial revision
 *
 *
 */

/**
 * Terminates the GA after a given number of generations have 
 * passed.
 *
 * @author Kyle Dewey
 */
public class NumGenerationsTerminator implements Terminator {
    // begin instance varibles
    private long numGenerations; // number of generations to run for
    // end instance variables
    
    /**
     * Creates a new NumGenerationsTerminator.
     * @param numGenerations The number of generations to run for
     * @param lowGood Whether or not low fitness is good (ignored)
     */
    public NumGenerationsTerminator( long numGenerations,
				     boolean lowGood ) {
        this.numGenerations = numGenerations;
    }

    /**
     * Alternate constructor.  Takes only what is used.
     * @param numGenerations The number of generations to run for
     */
    public NumGenerationsTerminator( long numGenerations ) {
	this( numGenerations,
	      true );
    }

    /**
     * If the given population has run for >= the given
     * number of generations, then it gives it the ok to 
     * terminate.
     *
     * @param population The population to check
     *
     * @return true if the population should terminate, else false
     */
    public boolean shouldTerminate( Population population ) {
	return population.getCurrentGeneration() >= numGenerations;
    }
}

      