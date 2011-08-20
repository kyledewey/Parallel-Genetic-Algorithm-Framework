/*
 * Individual.java
 *
 * Version:
 *     $Id: Individual.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: Individual.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.8  2009/11/03 04:24:11  kyle
 *      Made it so fitness is calculated in parallel.
 *
 *      Revision 1.7  2009/10/27 21:42:10  kyle
 *      Now there is a probability of mutation occurring.
 *
 *      Revision 1.6  2009/10/27 16:05:04  kyle
 *      Added a source for individuals.
 *
 *      Revision 1.5  2009/10/25 15:31:27  kyle
 *      Added caching for fitness values;
 *      made getFitness() non abstract and added
 *      abstract method calculateFitness()
 *
 *      Revision 1.4  2009/10/03 21:20:44  kyle
 *      Modified mutate() to mutate a copy, instead of the
 *      object upon which mutate was invoked.
 *
 *      Revision 1.3  2009/09/29 19:20:21  kyle
 *      Added comment stating that child classes must have
 *      a constructor that takes a genotype as a param.
 *
 *      Revision 1.2  2009/09/27 23:39:50  kyle
 *      Now uses reflection to do breeding.
 *      Before, the correct fitness function would not be used.
 *
 *      Revision 1.1  2009/09/27 22:54:59  kyle
 *      Initial revision
 *
 *
 */

// for breeding to work properly, reflection 
// is required.
import java.lang.reflect.Constructor;

/**
 * Represents an individual in the simulation.
 * Note that the child class MUST have a constructor
 * that takes a Genotype as the param.  Also note
 * that this class should be treated as immutable.
 * Although it is possible to mutate it after creation,
 * DO NOT DO THIS!!  The rest of the code assumes this won't
 * happen, and fitness values are cached such that any
 * changes will not be reflected in the fitness.
 *
 * @author Kyle Dewey
 */
public abstract class Individual {
    // begin constants
    public static final double FITNESS_WAITING = -1;
    // end constants

    // begin global variables
    private static WorkerPool pool = new WorkerPool();
    // end global variables

    // begin instance variables
    private static long nextId = 0; // ID the next ID to assign
    private long id; // the ID of this individual
    private Genotype phenotype; // the phenotype of this individual
    private double fitness; // the fitness of this individual
    private FitnessThread fitnessCalculator;
    // end instance variables

    /**
     * Creates a new individual.
     *
     * @param phenotype The phenotype of the individual
     */
    public Individual( Genotype phenotype ) {
        this.phenotype = phenotype;
        id = nextId;
        nextId++;
        fitness = FITNESS_WAITING;
        fitnessCalculator = new FitnessThread( this );
        pool.addJob( fitnessCalculator );
    }

    /**
     * Gets the ID of this individual.
     *
     * @return The ID of this individual
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the phenotype of this individual
     *
     * @return The phenotype of this individual
     */
    public Genotype getPhenotype() {
        return phenotype;
    }

    /**
     * Breeds this individual with other individual.
     * Returns the new individual
     *
     * @param other The other individual to breed with
     *
     * @return The new individual
     *
     * @exception GenotypeCastException If the genotypes of the two
     *            individuals are physically incompatible with each
     *            other
     */
    public Individual breed( Individual other ) 
    throws GenotypeCastException {
        Genotype newPhenotype; // phenotype of the new individual
        Individual retval; // what will be returned
        Class toMake; // the class of the thing to make

        newPhenotype = phenotype.crossover( other.getPhenotype() );
        
        // this cannot be done; the base class is not
        // going to take any fitness functions into account
        // we need the functionality of the child class, NOT
        // the base class
        //return new Individual( newPhenotype );
        
        // make the new individual
        toMake = this.getClass();
        if ( !toMake.equals( other.getClass() ) ) {
            throw new GenotypeCastException( "Different individuals with " +
                                             "compatible genotypes tried " +
                                             "to breed." );
        } else {
            Constructor con;
            try {
                con = toMake.getConstructor( new Class[]{ Genotype.class } );
                retval = (Individual)con.newInstance( new Object[]{ newPhenotype } );
            } catch ( Exception e ) {
                e.printStackTrace();
                throw new GenotypeCastException( e.getMessage() );
            }

            return retval;
        }
    }

    /**
     * Causes a copy of this individual to undergo a mutation.
     * The returned individual is of the same class that called
     * the method.
     *
     * @param prob The probability of mutation occurring for any given gene
     *
     * @return The mutated copy of this individual, or null if no
     *         mutation occurred
     */
    public Individual mutate( double prob ) {
        Constructor con;
        Individual retval = null; // what will be returned

        try {
            con = this.getClass().getConstructor( new Class[]{ Genotype.class } );
            Genotype newGenotype;
            newGenotype = phenotype.mutate( prob );
            if ( newGenotype != null ) {
                retval = (Individual)con.newInstance( new Object[]{ newGenotype } );
            }
        } catch( Exception e ) {
            //impossible
        }

        return retval;
    }

    /**
     * Sets the fitness of this individual.
     *
     * @param fitness The fitness to set it to
     */
    public void setFitness( double fitness ) {
        this.fitness = fitness;
    }

    /**
     * Gets the fitness of this individual.
     * Note that if the fitness has not yet been
     * calculated, it will block.
     *
     * @return The fitness of this individual
     */
    public double getFitness() {
        double retval = fitness; // what will be returned

        if ( retval == FITNESS_WAITING ) {
            pool.waitForJob( fitnessCalculator );
            retval = fitness;
        }

        return retval;
    }

    /**
     * Calculates the fitness of this individual.
     * Calculated only once, at object initialization.
     * 
     * @return The calculated fitness of this individual
     */
    protected abstract double calculateFitness();

    /**
     * Returns a string representation of this individual.
     * The format is as follows:
     * Individual #<id>:
     *     Phenotype: <genotype>
     *     Fitness: <fitness>
     *
     * @return A string representing this individual in the above
     *         format
     */
    public String toString() {
        String retval; // what will be returned

        retval = "Individual ID: " + Long.toString( id ) + "\n" +
            "\t Phenotype: " + phenotype.toString() + "\n" +
            "\t Fitness: " + Double.toString( getFitness() ) + "\n" +
            "\t Source: " + phenotype.getSource().toString() + "\n";

        return retval;
    }

    /**
     * Performs internal cleanup.  To be called at GA end.
     */
    public static void cleanup() {
        pool.cleanup();
    }
}
