/*
 * ConvergenceTerminator.java
 *
 * Version:
 *     $Id: ConvergenceTerminator.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: ConvergenceTerminator.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.3  2009/10/27 23:45:58  kyle
 *      Now uses averages instead of min and max to see if
 *      termination should occur; things were running for
 *      nearly an hour without much change.
 *
 *      Revision 1.2  2009/10/27 21:58:24  kyle
 *      Fixed typo that always caused shouldTerminate() to return true.
 *
 *      Revision 1.1  2009/10/27 18:15:22  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;

/**
 * Terminates the GA when convergence is detected.
 * This has been defined as when the average fitness does
 * not improve over the course of a given number of generations.
 *
 * @author Kyle Dewey
 */
public class ConvergenceTerminator implements Terminator {
    // begin instance variables
    private long whenConverge; // how many generations until convergence
    private boolean lowGood;
    private LinkedList< Double > pastAverages;
    // end instance variables

    /**
     * Sets convergence to be after some user defined number
     * of generations where average hasn't improved.
     *
     * @param whenConverge The number of generations that must pass
     *        where the average doesn't improve
     *        before we say convergence has occurred
     * @param lowGood Whether or not low fitness is good
     */
    public ConvergenceTerminator( long whenConverge,
				  boolean lowGood ) {
        this.whenConverge = whenConverge;
	this.lowGood = lowGood;
        pastAverages = new LinkedList< Double >();
    }

    /**
     * Gets the average of all elements in a list of doubles.
     *
     * @param list The list of doubles
     *
     * @return The average of the list of doubles
     */
    public static double getAverage( List< Double > list ) {
        double retval = 0.0;

        for( Double current : list ) {
            retval += current.doubleValue();
        }
        retval /= list.size();

        return retval;
    }

    /**
     * Returns true if the number of user-defined generations
     * have passed without the average improving.
     *
     * @param population The population to check
     *
     * @return True if the population should terminate, else false
     */
    public boolean shouldTerminate( Population population ) {
        boolean retval = false;
        
        if ( population.getCurrentGeneration() < whenConverge ) {
            // not enough data yet
            pastAverages.add( population.getAverageFitness() );
        } else {
            double pastAverage = getAverage( pastAverages );
            double currentAverage = population.getAverageFitness();

            if ( ( lowGood && pastAverage < currentAverage ) ||
		 ( !lowGood && pastAverage > currentAverage ) ) {
                retval = true;
            } else {
                pastAverages.removeFirst();
                pastAverages.add( currentAverage );
            }
        }

        return retval;
    }
}
