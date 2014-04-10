/*
 * FitnessThread.java
 *
 * Version:
 *     $Id: FitnessThread.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: FitnessThread.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.1  2009/11/03 04:24:11  kyle
 *      Initial revision
 *
 *
 */

/**
 * A threadthat will calculate the fitness of an individual.
 *
 * @author Kyle Dewey
 */
public class FitnessThread implements Runnable {
    // begin instance variables
    private Individual individual; // the individual to calculate the fitnes of
    // end instance variables

    /**
     * Creates a new fitness job.
     *
     * @param individual The individual to calculate the fitness of
     */
    public FitnessThread( Individual individual ) {
        this.individual = individual;
    }

    /**
     * Calculates the fitness of the individual.
     */
    public void run() {
        individual.setFitness( individual.calculateFitness() );
    }

    /**
     * Gets the hash code.
     *
     * @return the hash code - the ID of the individual
     */
    public int hashCode() {
        return (int)individual.getId();
    }
}
