/*
 * InverseFitnessPopulation.java
 *
 * Version:
 *     $Id: InverseFitnessPopulation.java,v 1.3 2009/10/27 21:42:10 kyle Exp $
 *
 * Revisions:
 *      $Log: InverseFitnessPopulation.java,v $
 *      Revision 1.3  2009/10/27 21:42:10  kyle
 *      Changed constructor to take selection mechanism.
 *
 *      Revision 1.2  2009/10/27 18:15:22  kyle
 *      Changed constructor to use TruncationSelection with
 *      low fitness values being good.
 *
 *      Revision 1.1  2009/10/20 01:49:09  kyle
 *      Initial revision
 *
 *
 */

/**
 * Holds a collection of individuals.
 * This is for cases where lower fitness values are
 * better than higher ones, as in the Large Parsimony
 * Problem.
 *
 * @author Kyle Dewey
 */
public class InverseFitnessPopulation< T extends Individual >
    extends Population< T > {
    
    /**
     * Creates a new population, with the given parameters.
     * NOTE: for this to work properly, then the selection
     * mechanisms must be set so that low fitness is good.
     *
     * @param crossoverRate Rate of crossover
     * @param mutationRate Rate of mutation
     * @param elitism Amount of elitism
     * @param maxPopulationSize Maximum population size; < 0 means infinite
     * @param parentSelection Parent selection mechanism
     * @param survivalSelection Survivor selection mechanism
     */
    public InverseFitnessPopulation( double crossoverRate, 
                                     double mutationRate,
                                     double elitism,
                                     int maxPopulationSize,
                                     Selection< T > parentSelection,
                                     Selection< T > survivalSelection ) {
        super( crossoverRate,
               mutationRate,
               elitism,
               maxPopulationSize,
               parentSelection,
               survivalSelection );
    }

    /**
     * Gets the index of the individual with the maximum fitness
     * in the population.  Note that for this, the maximum
     * fitness is actually the LOWEST fitness in the population.
     *
     * @return The index of the individual with maximal (lowest) fitness
     */
    protected int getIndividualMaxFitness() {
        return super.getIndividualMinFitness();
    }

    /**
     * Gets the index of the individual with the minimal fitness
     * in the population.  Note that for this, the minimal fitness
     * is actually the HIGHEST fitness in the population.
     *
     * @return The index of the individual with minimal (highest) fitness
     */
    protected int getIndividualMinFitness() {
        return super.getIndividualMaxFitness();
    }
}

