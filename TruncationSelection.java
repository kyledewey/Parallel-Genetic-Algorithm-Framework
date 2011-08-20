/*
 * TruncationSelection.java
 *
 * Version:
 *     $Id: TruncationSelection.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: TruncationSelection.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.1  2009/10/27 18:15:22  kyle
 *      Initial revision
 *
 *
 */

import java.util.*; // for lists

/**
 * Implementation of truncation selection.
 * The best individuals of the population are chosen, and
 * these are returned.
 *
 * @author Kyle Dewey
 */
public class TruncationSelection< T extends Individual > 
    extends Selection< T > {
    /**
     * Creates a new TruncationSelection object with
     * the given value for as to whether or not
     * low values are good.
     *
     * @param lowGood If low fitness values correlate to high
     *        fitnesses
     */
    public TruncationSelection( boolean lowGood ) {
        super( lowGood );
    }

    /**
     * Gets the best individuals in the population.
     *
     * @param list The original population
     * @param fixed The number of individuals to return; <=0
     *        or >= [list] will return all the individuals
     *        in the population (defeating the purpose)
     * @param lowGood true if low fitness values are good,
     *        else false
     *
     * @return The best individuals in the population
     */
    public List< T > performSelection( List< T > list,
                                       int fixed,
                                       boolean lowGood ) {
        List< T > retval; // what will be returned

        // see if we actually do anything
        if ( fixed <= 0 ||
             fixed >= list.size() ) {
            retval = new ArrayList< T >( list );
        } else {
            // we need to truncate something
            List< T > copy = new ArrayList< T >( list );
            retval = new ArrayList< T >();

            for( int x = 0; x < fixed; x++ ) {
                int pos;

                if ( lowGood ) {
                    pos = getMinFitness( copy );
                } else {
                    pos = getMaxFitness( copy );
                }

                retval.add( copy.get( pos ) );
                copy.remove( pos );
            }
        }

        return retval;
    }

    /**
     * Gets the index of the individual with the maximal fitness in
     * the given list.
     *
     * @param list The list of individuals
     *
     * @return The index of the individual with the max fitness
     */
    public static < T extends Individual > int getMaxFitness( List< T > list ) {
        double fitness = list.get( 0 ).getFitness();
        int retval = 0;

        for( int x = 1; x < list.size(); x++ ) {
            double currentFitness;

            currentFitness = list.get( x ).getFitness();
            if ( currentFitness > fitness ) {
                fitness = currentFitness;
                retval = x;
            }
        }

        return retval;
    }

    /**
     * Gets the index of the individual with the minimal fitness in
     * the given list.
     *
     * @param list The list of individuals
     *
     * @return The index of the individual with the min fitness
     */
    public static < T extends Individual > int getMinFitness( List< T > list ) {
        double fitness = list.get( 0 ).getFitness();
        int retval = 0;

        for( int x = 1; x < list.size(); x++ ) {
            double currentFitness;

            currentFitness = list.get( x ).getFitness();
            if ( currentFitness < fitness ) {
                fitness = currentFitness;
                retval = x;
            }
        }

        return retval;
    }
}
