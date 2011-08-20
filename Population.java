/*
 * Population.java
 *
 * Version:
 *     $Id: Population.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: Population.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.9  2009/10/31 14:36:34  kyle
 *      Now caches min, avg, and max fitnesses, and prints out
 *      amount of time needed per generation.
 *
 *      Revision 1.8  2009/10/27 21:42:10  kyle
 *      Implemented proper handling for the rates of crossover
 *      and mutation.
 *
 *      Revision 1.7  2009/10/27 18:15:22  kyle
 *      Now uses the selection mechanism interface.
 *
 *      Revision 1.6  2009/10/20 19:36:29  kyle
 *      Added getAverageFitness() method;
 *      toString() now also prints average fitness.
 *
 *      Revision 1.5  2009/10/09 02:28:41  kyle
 *      Added the getBestIndividual() method.
 *
 *      Revision 1.4  2009/10/03 21:20:44  kyle
 *      Modified mutate() to mutate a copy, instead of the
 *      object upon which mutate was invoked.
 *
 *      Revision 1.3  2009/09/29 19:23:11  kyle
 *      Put initialization of currentGeneration in the constructor.
 *
 *      Revision 1.2  2009/09/27 23:39:50  kyle
 *      Made toString() method's output to be easier to read.
 *
 *      Revision 1.1  2009/09/27 22:54:59  kyle
 *      Initial revision
 *
 *
 */

import java.util.*; // for lists

/**
 * Holds a collection of individuals.
 * A population may shink, expand, or remain constant,
 * depending on the given parameters.  Note that
 * a population consists of individuals of the same
 * type.
 *
 * @author Kyle Dewey
 */
public class Population< T extends Individual > {
    // begin constants
    protected static final Random random = new Random();
    // end constants

    // begin instance variables
    private long whenCalculated; // when average, min, max were calculated
    private double minFitness; // min fitness in the population
    private double maxFitness; // max fitness in the population
    private double avgFitness; // average fitness in the population

    private long startTime; // when the generation started
    private long endTime; // when the generation finished

    private List< T > population; // the individuals in the population
    private Selection< T > parentSelection; // parent selection mechanism
    private Selection< T > survivalSelection; // survival selection mechanism
    private double mutationRate; // rate of mutation, between 0-1
    private double crossoverRate; // rate of crossover, between 0-1
    private double elitism; // parent elitism, between 0-1
    private int maxPopulationSize;
    private long currentGeneration; // which generation we are on
    // end instance variables

    /**
     * Creates a new population, with a number of
     * parameters.  The population is empty.
     *
     * @param crossoverRate Rate of crossover
     * @param mutationRate Rate of mutation
     * @param elitism Amount of elitism
     * @param maxPopulationSize Maximum population size; < 0 means infinite
     * @param parentSelection Parent selection mechanism
     * @param survivalSelection Survivor selection mechanism
     */
    public Population( double crossoverRate, 
                       double mutationRate,
                       double elitism,
                       int maxPopulationSize,
                       Selection< T > parentSelection,
                       Selection< T > survivalSelection ) {
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;
        this.maxPopulationSize = maxPopulationSize;
        this.parentSelection = parentSelection;
        this.survivalSelection = survivalSelection;
        currentGeneration = 0;
        whenCalculated = -1;
        startTime = -1;
        endTime = -1;
        population = new ArrayList< T >();
    }

    /**
     * Gets the maximum size of the population.
     * Negative numbers mean infinite population size
     *
     * @return The maximum size of the population
     */
    public int getMaxPopulationSize() {
        return maxPopulationSize;
    }

    /**
     * Gets the current size of the population.
     *
     * @return The current size of the population
     */
    public int getPopulationSize() {
        return population.size();
    }

    /**
     * Adds an individual to this population.
     * This ignores the size of the population and
     * any limits on the population size
     *
     * @param individual The individual to add to this population
     */
    public void addIndividual( T individual ) {
        population.add( individual );
    }

    /**
     * Gets the current generation of the population.
     *
     * @return The current generation of the population
     */
    public long getCurrentGeneration() {
        return currentGeneration;
    }

    /**
     * Does all the operations involved in the crossover phase.
     *
     * @param parents The pool of parents
     *
     * @return The new individuals that resulted from crossover
     */
    private List< T > doCrossover( List< T > parents ) {
        List< T > retval; // what will be returned

        retval = new ArrayList< T >( parents.size() );

        // breed each parent with another random parent
        for( int x = 0; x < parents.size(); x++ ) {
            try {
                int otherParent;
                T child;
                
                do {
                    otherParent = random.nextInt( parents.size() );
                } while( otherParent == x );
                child = (T)parents.get( x ).breed( parents.get( otherParent ) );
                retval.add( child );
            } catch( GenotypeCastException e ) {
                // impossible
                System.err.println( e );
                e.printStackTrace();
                System.exit( 1 );
            }
        } // for each parent

        return retval;
    }

    /**
     * Does all the operations involved in the mutation phase.
     *
     * @param pool The pool of individuals to mutate
     *
     * @return The new individuals resulting from mutation
     */
    private List< T > doMutation( List< T > pool ) {
        List< T > retval;

        retval = new ArrayList< T >();
        for( T individual : pool ) {
            T newIndividual;

            newIndividual = (T)individual.mutate( mutationRate );
            if ( newIndividual != null ) {
                retval.add( newIndividual );
            }
        }

        return retval;
    }

    /**
     * Causes a population to go through a generation.
     * The details are set through the class instantiation
     * parameters.
     */
    public void undergoGeneration() {
        List< T > originalPool = population; // the original pool
        List< T > parentPool; // pool where parents go
        List< T > newPool; // where newly created individuals go
        List< T > finalPool; // the pool that will become the population pool

        startTime = System.currentTimeMillis();
        // prep the pools
        newPool = new ArrayList< T >();

        // get the parents ready
        parentPool = parentSelection.performSelection( originalPool,
                                                       (int)( crossoverRate * originalPool.size() ) );
        // do the crossovers and mutations
        // note that for most cases, we will have too many
        // offspring.  However, with lots of parameter tweaking,
        // we can have too few
        while( newPool.size() < maxPopulationSize ) {
            newPool.addAll( doCrossover( parentPool ) );
            newPool.addAll( doMutation( originalPool ) );
        }

        // transfer some of the parents over to the new pool
        newPool.addAll( parentSelection.performSelection( parentPool,
                                                          (int)( elitism * parentPool.size() ) ) );

        // now reduce the size of the pool down to
        // the maximum population size
        finalPool = survivalSelection.performSelection( newPool,
                                                        maxPopulationSize );
        population = finalPool;

        currentGeneration++;
        endTime = System.currentTimeMillis();
    }

    /**
     * Gets the individual with the best fitness in the current
     * generation.
     *
     * @return The inividial with the best fitness in the population
     */
    public T getBestIndividual() {
        return population.get( getIndividualMaxFitness() );
    }

    /**
     * Traverses over the population looking at fitness.
     * Gets the index of an individual matching criteria.
     * @param max Whether or not to get the max.  If false, then min.
     * @return The index of the individual
     */
    protected int getIndividualMaxMinFitness( boolean max ) {
	int retval = 0;
	int size = population.size();
	double fitness = population.get( 0 ).getFitness();

	for( int x = 1; x < size; x++ ) {
	    double currentFitness = population.get( x ).getFitness();
	    if ( ( max && currentFitness > fitness ) ||
		 ( !max && currentFitness < fitness ) ) {
		fitness = currentFitness;
		retval = x;
	    }
	}

	return retval;
    }
		
    /**
     * Gets the index of the individual with the maximum
     * fitness in the population.
     *
     * @return The index of the individual with maximal fitness
     */
    protected int getIndividualMaxFitness() {
        return getIndividualMaxMinFitness( true );
    }

    /**
     * Gets the index of the individual with the minimal
     * fitness in the population.
     *
     * @return The index of the individual with minimal fitness
     */
    protected int getIndividualMinFitness() {
	return getIndividualMaxMinFitness( false );
    }

    /**
     * Gets the selection mechanism used for survival.
     *
     * @return The selection mechanism for survival
     */
    public Selection< T > getSurvivalSelection() {
        return survivalSelection;
    }

    /**
     * Gets the selection mechanism used for parents.
     *
     * @return The selection mechanism for parents
     */
    public Selection< T > getParentSelection() {
        return parentSelection;
    }

    /**
     * Sets the internal min, average, and max fitness values.
     */
    private void doStats() {
        if ( whenCalculated != currentGeneration ) {
            maxFitness = population.get( getIndividualMaxFitness() ).getFitness();
            minFitness = population.get( getIndividualMinFitness() ).getFitness();
            avgFitness = averageFitness( population );
            whenCalculated = currentGeneration;
        }
    }

    /**
     * Gets the maximal fitness of the population.
     *
     * @return The maximal fitness of the population.
     */
    public double getMaxFitness() {
        doStats();
        return maxFitness;
    }

    /**
     * Gets the minimal fitness of the population.
     *
     * @return the minimal fitness of the population.
     */
    public double getMinFitness() {
        doStats();
        return minFitness;
    }

    /**
     * Gets the average fitness of the population.
     *
     * @return The average fitness of the population
     */
    public double getAverageFitness() {
        doStats();
        return avgFitness;
    }

    /**
     * Represents this population as a string.
     * The format is as follows:
     * Generation: <gen number>
     * Min fitness: <min fitness>
     * Max fitness: <max fitness>
     * <Data for each individual>
     *
     * @return A string in the above format
     */
    public String toString() {
        String retval = ""; // what will be returned

        if ( startTime != -1 &&
             endTime != -1 ) {
            retval += "Generation #" + Long.toString( currentGeneration - 1 ) +
                " runtime(ms): " + Long.toString( endTime - startTime ) + "\n";
        }

        retval += "Generation #: " + Long.toString( currentGeneration ) + "\n" +
            "Min fitness: " + Double.toString( getMinFitness() ) + "\n" +
            "Avg fitness: " + Double.toString( getAverageFitness() ) + "\n" +
            "Max fitness: " + Double.toString( getMaxFitness() ) + "\n" + 
            "Individuals (" + Long.toString( population.size() ) + " total):\n";

        for( T current : population ) {
            retval += current.toString();
        }

        return retval;
    }

    /**
     * Gets the total fitness for the given individuals.
     * @param indiv The individuals to get the total fitness of
     * @return The total fitness of the individuals
     */
    public static <T extends Individual> double totalFitness( List< T > indiv ) {
	double retval = 0.0;

	for( T current : indiv ) {
	    retval += current.getFitness();
	}
	return retval;
    }

    /**
     * Gets the average fitness for the given individuals.
     * @param indiv the individuals
     * @return The average fitness
     */
    public static <T extends Individual> double averageFitness( List< T > indiv ) {
	return totalFitness( indiv ) / indiv.size();
    }
}