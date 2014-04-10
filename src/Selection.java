/*
 * Selection.java
 *
 * Version:
 *     $Id: Selection.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: Selection.java,v $
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
 * Interface that defines a selection mechanism.
 * Different selection mechanisms work in different
 * ways, but they all take a list of individuals
 * and return a sublist of individuals who are
 * to survive
 *
 * @author Kyle Dewey
 */
public abstract class Selection< T extends Individual > {
    // begin global variables
    private boolean lowGood;
    protected static final Random random = new Random();
    // end global variables

    /**
     * Creates a new Selection object.
     * 
     * @param lowGood If low fitness values mean high fitness
     */
    public Selection( boolean lowGood ) {
        this.lowGood = lowGood;
    }

    /**
     * Gets the individuals who are to survive in the given list.
     *
     * @param list The list of individuals
     * @param fixed The number of individuals the population is
     *        fixed at.  If <= 0, then it may return any
     *        number.  If positive, then it will return that
     *        many individuals from the given list (or all
     *        the individuals in the given list if fixed >
     *        the number of individuals)
     * @param lowGood true if low fitness values are better than
     *        high fitness values, else false
     *
     * @return A list of individuals who have survived selection.
     *         Shallow copied.
     */
    public abstract List< T > performSelection( List< T > list,
                                                int fixed,
                                                boolean lowGood );

    /**
     * Gets the individuals who are to survive in the given list.
     * Like the other, except as to whether or not low
     * values for fitness are good is determined by stored lowGood value.
     *
     * @param list The list of individuals
     * @param fixed How many individuals we want to get out
     *
     * @return A list of individuals who have survived selection
     */
    public List< T > performSelection( List< T > list,
                                       int fixed ) {
        return performSelection( list,
                                 fixed,
                                 lowGood );
    }

    /**
     * Given a probability between 0-1, it will get whether or not a choice
     * is made.  This is stochastic.
     * @param prob The probability
     * @return true if we need to make a choice, else false
     */
    public static boolean choose( double prob ) {
	return random.nextDouble() <= prob;
    }
}
