/*
 * Genotype.java
 *
 * Version:
 *     $Id: Genotype.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: Genotype.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.4  2009/10/27 21:42:10  kyle
 *      Now there is a probability of mutation occurring.
 *
 *      Revision 1.3  2009/10/27 16:05:04  kyle
 *      Added a source for individuals.
 *
 *      Revision 1.2  2009/10/03 21:20:44  kyle
 *      Modified mutate() to mutate a copy, instead of the
 *      object upon which mutate was invoked.
 *
 *      Revision 1.1  2009/09/27 22:54:59  kyle
 *      Initial revision
 *
 *
 */

import java.util.Random; // for making random numbers

/**
 * Represents a genotype in a GA.
 * Note that this is an abstract base class, since there
 * exist different genotypes.  Also, note that there are
 * no phenotype classes; an instance of a genotype class
 * is understood to be a phenotype.
 *
 * @author Kyle Dewey
 */
public abstract class Genotype {
    // begin contants
    // defines how this genotype came to be
    public enum Source { UNDEFINED, // hasn't been set yet
            INITIAL, // from initial creation
            MUTATION,
            CROSSOVER }
            
            
            
    // begin instance variables
    protected static final Random random = new Random();
    private Source source = Source.UNDEFINED;
    // end instance variables

    /** 
     * The GA crossover operation, to be used in breeding.
     * Given another genotype, it blends this genotype with
     * the given genotype, and returns the blend as a new
     * genotype.
     *
     * @param other The other genotype to cross with
     *
     * @return A new genotype, a mix of this genotype and the
     *         given genotype
     *
     * @exception GenotypeCastException If the underlying implementation
     *            of the genotype in other is incompatible with this one
     */
    public abstract Genotype crossover( Genotype other ) 
        throws GenotypeCastException;

    /**
     * Gets the underlying phenotype of this genotype
     * (array, bit string, etc.)
     *
     * @return The underlying phenotype
     */
    public abstract Object getPhenotype();

    /**
     * The GA mutation operation.
     * Causes a mutation to befall this genotype.
     * Does not affect the original genotype; merely returns
     * a mutated copy.
     *
     * @param prob The probability of any given gene within
     *             to mutate
     *
     * @return The mutated copy of this genotype; null if no
     *         mutations occurred
     */
    public abstract Genotype mutate( double prob );

    /**
     * The toString() method.
     * Should display some information about this genotype, regarding
     * it's contents.
     *
     * @return A string describing this genotype.
     */
    public abstract String toString();

    /**
     * Gets the source of this genotype.
     *
     * @return The source of this genotype
     */
    public Source getSource() {
        return source;
    }

    /**
     * Sets the source of this genotype.
     *
     * @param source The source of this genotype
     */
    public void setSource( Source source ) {
        this.source = source;
    }
}

    