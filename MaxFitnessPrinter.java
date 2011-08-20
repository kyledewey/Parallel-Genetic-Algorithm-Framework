/*
 * MaxFitnessPrinter.java
 *
 * Version:
 *     $Id: MaxFitnessPrinter.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: MaxFitnessPrinter.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.3  2009/10/27 21:42:10  kyle
 *      Now prints out "Most fit individual" before
 *      printing out the most fit individual.
 *
 *      Revision 1.2  2009/10/27 18:15:22  kyle
 *      Now uses population's methods instead of environments
 *      to get needed information.
 *
 *      Revision 1.1  2009/10/20 19:16:59  kyle
 *      Initial revision
 *
 *
 */

/**
 * Has an environment print out the fitness value
 * of the most fit individual at each generation.
 * At GA termination, prints out the best individual.
 *
 * @author Kyle Dewey
 */
public class MaxFitnessPrinter implements EnvironmentPrinter {
    /**
     * String that is printed out at the start of the GA.
     *
     * @param environment The environment
     *
     * @return A string that is to be printed out at the
     *         start of the GA
     */
    public String printGAStart( Environment environment ) {
        return "Max Fitnesses:\n";
    }

    /**
     * String that is printed out at the end of every generation
     *
     * @param environment The environment to print
     *
     * @return A string to print out at the end of the
     *         generation
     */
    public String printGAGeneration( Environment environment ) {
        double maxFitness;

        maxFitness = environment.getPopulation().getMaxFitness();

        return "Generation " + 
            Long.toString( environment.getPopulation().getCurrentGeneration() ) +
            ": " +
            Double.toString( maxFitness ) + "\n";
    }

    /**
     * String that is printed out at the end of the GA.
     *
     * @param environment The environment to print
     *
     * @return A string to print out at the end of the GA
     */
    public String printGAEnd( Environment environment ) {
        Individual best; // best individual in population

        best = environment.getPopulation().getBestIndividual();
        
        return "Most fit individual:\n" + best.toString() + "\n";
    }
}
