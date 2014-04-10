/**
 * Holds together the GA.
 * @author Kyle Dewey
 */
public class RunGA {
    // begin constants
    public static final double MIN_PERCENTAGE = 0.0;
    public static final double MAX_PERCENTAGE = 1.0;
    public static final String DELIM = ":";
    public static final int SPLIT_DEFAULT_LENGTH = 2;
    // end constants

    // begin instance variables
    private Environment environment;
    private Population population;
    private double crossoverRate;
    private double mutationRate;
    private double elitism;
    private int maxPopulationSize;
    private Selection parentSelection;
    private Selection survivalSelection;
    private Terminator terminator;
    // end instance variables

    /**
     * Prints usage information for the GA.
     */
    public static void usage() {
	System.out.println( "Takes the following params:\n" +
			    "crossover rate (between 0-1)\n" +
			    "mutation rate (between 0-1)\n" +
			    "elitism (between 0-1)\n" +
			    "max population size (>0)\n" +
			    "parent selection mechanism (class name)\n" +
			    "survival selection mechanism (class name)\n" +
			    "termination condition (class name:num generations)\n" +
			    "if lower fitness values are better than higher values (boolean)\n" +
			    "individual class name:individual parameter\n" );
    }

    /**
     * Creates a new GA.
     * @param args command line arguments.  See usage() for details.
     * @exception NumberFormatException If we expected a double but received none.
     * @exception ArgsException If an argument is invalid.
     */
    public RunGA( String[] args ) throws Exception {
	if ( args.length != 9 ) {
	    usage();
	    throw new ArgsException( "Needs nine parameters" );
	}
	boolean lowGood = Boolean.parseBoolean( args[ 7 ] );
	crossoverRate = readDouble( args[ 0 ] );
	mutationRate = readDouble( args[ 1 ] );
	elitism = readDouble( args[ 2 ] );
	maxPopulationSize = readPopulationSize( args[ 3 ] );
	parentSelection = readSelection( args[ 4 ],
					 lowGood );
	survivalSelection = readSelection( args[ 5 ],
					   lowGood );
	terminator = readTerminatorArg( args[ 6 ],
					lowGood );
	population = makePopulation( args[ 8 ],
				     crossoverRate,
				     mutationRate,
				     elitism,
				     maxPopulationSize,
				     parentSelection,
				     survivalSelection,
				     lowGood );
	environment = new Environment( population,
				       terminator );
    }

    /**
     * Runs the GA until completion.
     */
    public void start() {
	environment.runGA();
    }

    /**
     * Splits a string on the given delimiter.
     * Verifies that it is the given length
     * @param string The string to split
     * @param delim The delimiter
     * @param size The expected size of the split
     * @return The split
     * @exception ArgsException If the actual size differs from the expected
     */
    public static String[] split( String string,
				  String delim,
				  int size )
	throws ArgsException {
	String[] retval = string.split( delim );
	if ( retval.length != size ) {
	    throw new ArgsException( "Expected split length: " + retval.length + "\n" +
				     "Actual split length: " + size );
	}
	return retval;
    }

    /**
     * Splits a string on DELIM, using SPLIT_DEFAULT_LENGTH.
     * @param string The string to split
     * @return The split
     * @exception ArgsException If the actual size differs from the expected
     */
    public static String[] split( String string ) 
	throws ArgsException {
	return split( string,
		      DELIM,
		      SPLIT_DEFAULT_LENGTH );
    }
		      
    /**
     * Creates a random individual.
     * @param arg The argument
     * @return A new random individual
     */
    public static Individual createIndividual( String arg ) 
	throws Exception {
	String[] split = split( arg );
	return (Individual)Class.forName( split[ 0 ] )
	    .getConstructor( String.class )
	    .newInstance( split[ 1 ] );
    }
	    
    /**
     * Creates a population.
     * @param indivArg the argument string for individuals
     * @param crossoverRate The rate of crossover
     * @param mutationRate The rate of mutation
     * @param elitism The rate of elitism
     * @param maxPopulationSize The maximum population size
     * @param parentSelection The parent selection mechanism
     * @param survivalSelection The survival selection mechanism
     * @param lowGood if low fitness is good or bad
     */
    public static Population makePopulation( String indivArg,
					     double crossoverRate,
					     double mutationRate,
					     double elitism,
					     int maxPopulationSize,
					     Selection parentSelection,
					     Selection survivalSelection,
					     boolean lowGood ) 
	throws Exception {
	Population retval;

	if ( lowGood ) {
	    retval = new InverseFitnessPopulation( crossoverRate,
						   mutationRate,
						   elitism,
						   maxPopulationSize,
						   parentSelection,
						   survivalSelection );
	} else {
	    retval = new Population( crossoverRate,
				     mutationRate,
				     elitism,
				     maxPopulationSize,
				     parentSelection,
				     survivalSelection );
	}

	for( int x = 0; x < maxPopulationSize; x++ ) {
	    retval.addIndividual( createIndividual( indivArg ) );
	}
	return retval;
    }

    /**
     * Like <code>readDouble</code>, but it uses MIN_PERCENTAGE and MAX_PERCENTAGE.
     * @param string The string to parse
     * @return The in-range double
     * @exception NumberFormatException If the given string isn't a double
     * @exception ArgsException If it's out of range
     */
    public static double readDouble( String string )
	throws NumberFormatException, ArgsException {
	return readDouble( string, 
			   MIN_PERCENTAGE,
			   MAX_PERCENTAGE );
    }

    /**
     * Reads in a number of generations.
     * @param string The string
     * @exception NumberFormatException If the given string isn't an integer
     * @exception ArgsException If it's < 0 
     */
    public static long readNumGenerations( String string )
	throws NumberFormatException, ArgsException {
	long retval = Long.parseLong( string );
	if ( retval < 0 ) {
	    throw new ArgsException( "Number of generations must be >= 0." );
	}
	return retval;
    }

    /**
     * Reads in the max population size.
     * @param string The string
     * @exception NumberFormatException If the given string isn't an integer
     * @exception ArgsException If it's < 1.
     */
    public static int readPopulationSize( String string )
	throws NumberFormatException, ArgsException {
	int retval = Integer.parseInt( string );
	if ( retval < 1 ) {
	    throw new ArgsException( "Max population size must be >= 1." );
	}
	return retval;
    }

    /**
     * Reads in the terminator argument.
     * @param arg The argument
     * @param lowGood If low fitness is good
     * @throws NumberFormatException If the number of generations isn't a number
     * @throws ArgsException If it's malformed
     */
    public static Terminator readTerminatorArg( String arg,
						boolean lowGood )
	throws Exception {
	String[] split = split( arg );
	return readTerminator( split[ 0 ],
			       readNumGenerations( split[ 1 ] ),
			       lowGood );
    }

    /**
     * Reads in a terminator
     * @param className The name of the class
     * @param numGenerations The number of generations for the terminator
     * @param lowGood whether or not low fitness is good
     */
    public static Terminator readTerminator( String className,
					     long numGenerations,
					     boolean lowGood )
	throws Exception {
	return (Terminator)Class.forName( className )
	    .getConstructor( Long.TYPE, Boolean.TYPE )
	    .newInstance( numGenerations, lowGood );
    }
	    
    /**
     * Reads in a selection mechanism.
     * @param className The name of the class
     * @param lowGood If low fitness is good or not
     */
    public static Selection readSelection( String className,
					   boolean lowGood ) 
	throws Exception {
	return (Selection)Class.forName( className )
	    .getConstructor( Boolean.TYPE )
	    .newInstance( lowGood );
    }

    /**
     * Attempts to read the given string as a double.
     * @param string The string to parse
     * @param min The minimal double value
     * @param max The maximal double value
     * @return The in-range double
     * @exception NumberFormatException If the given string isn't a double
     * @exception ArgsException If it's out of range
     */
    public static double readDouble( String string,
				     double min,
				     double max ) 
	throws NumberFormatException, ArgsException {
	double retval = Double.parseDouble( string );
	if ( retval < min ) {
	    throw new ArgsException( "Minimal value is " + min + "; " +
				     "received " + retval );
	} else if ( retval > max ) {
	    throw new ArgsException( "Maximal value is " + max + "; " +
				     "received " + retval );
	} 

	return retval;
    }

    public static void main( String[] args ) {
	try {
	    new RunGA( args ).start();
	} catch ( Exception e ) {
	    e.printStackTrace();
	    System.err.println( e );
	}
    }
}