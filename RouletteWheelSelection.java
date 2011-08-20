/*
 * RouletteWheelSelection.java
 *
 * Version:
 *     $Id: RouletteWheelSelection.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: RouletteWheelSelection.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.2  2009/10/31 14:36:34  kyle
 *      Fixed bug that allowed individual to be selected
 *      twice, and it no longer ignores lowGood.
 *
 *      Revision 1.1  2009/10/27 23:47:46  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;

/**
 * Implementation of Roulette Wheel selection.
 * The fitness of the individual is proportional
 * to it's chance of being selected.
 *
 * @author Kyle Dewey
 */
public class RouletteWheelSelection< T extends Individual >
    extends Selection< T > {

    /**
     * Creates a new RouletteWheelSelection object with
     * the given value for as to whether or not
     * low values are good.
     * @param lowGood If low fitness values correlate to
     *        high fitnesses
     */
    public RouletteWheelSelection( boolean lowGood ) {
        super( lowGood );
    }

    /**
     * Gets individuals in the population, where better
     * fitnesses have higher probabilities of being chosen.
     *
     * @param list The list of individuals
     * @param fixed The number of individuals to return
     * @param lowGood true if low fitnesses are good, else false
     *
     * @return The most fit individuals in the population, based
     *         on probability
     */
    public List< T > performSelection( List< T > list,
                                       int fixed,
                                       boolean lowGood ) {
	return chooseIndividuals( list,
				  fixed,
				  lowGood );
    }

    /**
     * Given a list of individuals, returns a list of individual/probability
     * pairs.
     * @param indiv The individuals
     * @param lowGood If low fitness value corresponds to more fit individuals
     * @return A parallel array of selection probabilities, arranged from most likely to least
     * likely
     */
    public static <T extends Individual> List< Pair< T, Double > > rouletteProbabilities( List< T > indiv,
											  boolean lowGood ) {
	double totalFitness = Population.totalFitness( indiv );
	int size = indiv.size();
	List< Pair< T, Double > > retval = new ArrayList< Pair< T, Double > >( size );

	for( int x = 0; x < size; x++ ) {
	    T current = indiv.get( x );
	    double prob = current.getFitness() / totalFitness;
	    if ( lowGood ) {
		prob = 1 - prob;
	    }
	    retval.add( new Pair< T, Double >( current,
					       new Double( prob ) ) );
	}

	Collections.sort( retval,
			  new Comparator< Pair< T, Double > >() {
			      public int compare( Pair< T, Double > first,
						  Pair< T, Double > second ) {
				  return -first.second.compareTo( second.second );
			      }
			  } );
	return retval;
    }

    /**
     * Given a list of individuals, returns a new list of individuals 
     * of the given size.  The new list is the result of roulette wheel selection.
     * @param list The base list of individuals to select from
     * @param newSize The size of the new list to make
     * @param lowGood If low fitness values are good
     * @return The new list
     */
    public static <T extends Individual> List< T > chooseIndividuals( List< T > list,
								      int newSize,
								      boolean lowGood ) {
	List< Pair< T, Double > > prob = rouletteProbabilities( list,
								lowGood );
	List< T > retval = new ArrayList< T >( newSize );
	int oldSize = prob.size();
	int numPlaced = 0;

	while ( numPlaced < newSize ) {
	    for( int x = 0; x < oldSize; x++ ) {
		if ( numPlaced >= newSize ) {
		    break;
		} else {
		    Pair< T, Double > current = prob.get( x );
		    if ( choose( current.second ) ) {
			retval.add( current.first );
			numPlaced++;
		    }
		}
	    }
	}

	return retval;
    }
}
