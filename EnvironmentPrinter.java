/*
 * EnvironmentPrinter.java
 *
 * Version:
 *     $Id: EnvironmentPrinter.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: EnvironmentPrinter.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.1  2009/10/20 19:16:59  kyle
 *      Initial revision
 *
 *
 */

/**
 * Interface that defines what an environment is to print out.
 * Can be used to determine the verbosity of the GA.
 *
 * @author Kyle Dewey
 */
public interface EnvironmentPrinter {
    /**
     * Returns a string holding what should be printed
     * at the start of the GA.
     *
     * @param environment The environment to print out
     *
     * @return A string that should be printed at GA start
     */
    public String printGAStart( Environment environment );

    /**
     * Returns a string that should be printed at the
     * end of each generation.
     *
     * @param environment The environment to print
     *
     * @return A string that should be printed at the end
     *         of a generation
     */
    public String printGAGeneration( Environment environment );

    /**
     * Returns a string that is to be printed at the end of
     * a GA run.
     *
     * @param environment The environment to print
     *
     * @return A string that should be printed at the end
     *         of the GA run
     */
    public String printGAEnd( Environment environment );
}

