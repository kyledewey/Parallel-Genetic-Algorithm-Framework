/*
 * Environment.java
 *
 * Version:
 *     $Id: Environment.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: Environment.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.5  2009/11/03 04:24:11  kyle
 *      Added cleanup method for Individuals.
 *
 *      Revision 1.4  2009/10/27 18:15:22  kyle
 *      Removed methods that were redundant with those
 *      in Population.
 *
 *      Revision 1.3  2009/10/20 19:16:59  kyle
 *      Now uses EnvironmentPrinters to print out information.
 *
 *      Revision 1.2  2009/10/09 02:28:41  kyle
 *      Added methods to make extension more friendly.
 *
 *      Revision 1.1  2009/09/27 22:54:59  kyle
 *      Initial revision
 *
 *
 */

/**
 * Represents the environment, which consists of a listing
 * of populations.  Populations can interact with each other, if
 * so desired.  The environment holds the terminating condition.
 *
 * @author Kyle Dewey
 */
public class Environment {
    // begin instance variables
    private Population population;
    private EnvironmentPrinter printer; // used for printing information
    private Terminator terminator; // when the population is done
    // end instance variables

    /**
     * Creates a new Environment, with the given termination
     * condition.  Printer defaults to VerbosePrinter.
     *
     * @param population The population to use
     * @param terminator The termination condition for the environment
     */
    public Environment( Population population, 
                        Terminator terminator ) {
        this( population,
              terminator,
              new VerbosePrinter() );
    }

    /**
     * Creates a new environment, with the given params.
     *
     * @param population The population to use
     * @param numGenerations The number of generations to undergo
     * @param printer The printer to use
     */
    public Environment( Population population, 
                        Terminator terminator,
                        EnvironmentPrinter printer ) {
        this.population = population;
        this.terminator = terminator;
        this.printer = printer;
    }

    /**
     * Gets the population in this environment.
     *
     * @return The population in this environment
     */
    public Population getPopulation() {
        return population;
    }

    /**
     * Runs the GA.
     * Stops when the termination condition has been met.
     * For this, it means that 10 generations have passed.
     * At each generation, it prints information.
     */
    public void runGA() {
        System.out.print( printer.printGAStart( this ) );
        System.out.print( printer.printGAGeneration( this ) );
        while ( !terminator.shouldTerminate( population ) ) {
            population.undergoGeneration();
            System.out.print( printer.printGAGeneration( this ) );
        }
        System.out.print( printer.printGAEnd( this ) );
        Individual.cleanup();
    }
}

