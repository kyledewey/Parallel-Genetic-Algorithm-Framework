/*
 * BinaryTournamentSelection.java
 *
 * Version:
 *     $Id: BinaryTournamentSelection.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: BinaryTournamentSelection.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.1  2009/10/27 23:45:58  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;

/**
 * Implementation of binary tournament selection.
 *
 * @author Kyle Dewey
 */
public class BinaryTournamentSelection< T extends Individual >
    extends Selection< T > {
    /**
     * Uses the given value of lowGood.
     * @param lowGood if low fitness values mean high fitness
     */
    public BinaryTournamentSelection( boolean lowGood ) {
        super( lowGood );
    }

    /**
     * Gets individuals in the population.  Picks them in pairs,
     * and the better of the two ends up in the final population.
     *
     * @param list The list of individuals
     * @param fixed The number of individuals to return
     * @param lowGood if low fitnesses are good, else false
     *
     * @return Fittish individuals in the population
     */
    public List< T > performSelection( List< T > list,
                                       int fixed,
                                       boolean lowGood ) {
        List< T > retval = new ArrayList< T >( (int)fixed );
	
	for( int x = 0; x < fixed; x++ ) {
	    retval.add( chooseIndividual( list,
					  lowGood ) );
	}

	return retval;
    }

    /**
     * Given a list of individuals, it will return one according to binary
     * tournament selection.  Two individuals are selected at random, and
     * the one with greater fitness is returned.
     * @param indiv The individuals
     * @param lowGood If low fitness is better
     * @return An individual according to the above description
     */
    public static <T extends Individual> T chooseIndividual( List< T > indiv,
							     boolean lowGood ) {
	int size = indiv.size();
	int randomIndiv1 = random.nextInt( size );
	int randomIndiv2 = random.nextInt( size );

	if ( size > 1 ) {
	    while( randomIndiv2 == randomIndiv1 ) {
		randomIndiv2 = random.nextInt( size );
	    }
	}

	return bestFitness( indiv.get( randomIndiv1 ),
			    indiv.get( randomIndiv2 ),
			    lowGood );
    }

    /**
     * Given two individuals, returns the one with the best fitness.
     * @param indiv1 The first individual
     * @param indiv2 The second individual
     * @param lowGood If low fitness is better
     * @return The individual with better fitness
     */
    public static <T extends Individual> T bestFitness( T indiv1,
							T indiv2,
							boolean lowGood ) {
	double fitness1 = indiv1.getFitness();
	double fitness2 = indiv2.getFitness();
	T retval;

	if ( ( lowGood && fitness1 < fitness2 ) ||
	     ( !lowGood && fitness1 > fitness2 ) ) {
	    retval = indiv1;
	} else {
	    retval = indiv2;
	}

	return retval;
    }
}
