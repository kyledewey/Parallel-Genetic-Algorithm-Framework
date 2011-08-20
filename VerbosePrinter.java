/*
 * VerbosePrinter.java
 *
 * Version:
 *     $Id: VerbosePrinter.java,v 1.2 2010/05/19 05:30:35 kyledewey Exp $
 *
 * Revisions:
 *      $Log: VerbosePrinter.java,v $
 *      Revision 1.2  2010/05/19 05:30:35  kyledewey
 *      Hack to made the neural net do predictions at the end.
 *
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.2  2009/10/27 21:42:10  kyle
 *      Now prints "Most fit individual" before printing
 *      out the most fit individual.
 *
 *      Revision 1.1  2009/10/20 19:16:59  kyle
 *      Initial revision
 *
 *
 */

/**
 * Prints out verbose information about the GA.
 * Prints out the number of the generation, the
 * min and max fitnesses seen in the given generation,
 * and the data for each individual in the population.
 *
 * @author Kyle Dewey
 */
public class VerbosePrinter implements EnvironmentPrinter {
    /**
     * String that is to be printed out at the start of the GA
     *
     * @param environment The environment
     *
     * @return A string that is to be printed out at the start of the GA
     */
    public String printGAStart( Environment environment ) {
        return "";
    }

    /**
     * String that is to be printed out at the end of each generation.
     *
     * @param environment The environment
     *
     * @return A string that is to be printed out at the end
     *         of each generation
     */
    public String printGAGeneration( Environment environment ) {
        // note that population's toString() already
        // does exactly this
        return environment.getPopulation().toString() + "\n";
    }

    /**
     * Gets the best individual in the given environment.
     * @param environment The environment
     * @return The best individual in the environment
     */
    public Individual getBestIndividual( Environment environment ) {
	return environment.getPopulation().getBestIndividual();
    }

    /**
     * String that is to be printed out at the end of the GA
     *
     * @param environment The environment
     *
     * @return A string that is to be printed out at the end
     *         of the GA
     */
    public String printGAEnd( Environment environment ) {
	return "Most fit individual:\n" +
	    getBestIndividual( environment ).toString() + "\n";
    }
}
