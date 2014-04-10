/*
 * PhylogeneticTreeIndividual.java
 *
 * Version:
 *     $Id: PhylogeneticTreeIndividual.java,v 1.3 2009/10/25 15:31:27 kyle Exp $
 *
 * Revisions:
 *      $Log: PhylogeneticTreeIndividual.java,v $
 *      Revision 1.3  2009/10/25 15:31:27  kyle
 *      Added caching for fitness values;
 *      made getFitness() non abstract and added
 *      abstract method calculateFitness()
 *
 *      Revision 1.2  2009/10/20 01:49:09  kyle
 *      Implemented the Sankoff algorithm for scoring.
 *
 *      Revision 1.1  2009/10/09 01:44:47  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;
import java.io.*;

/**
 * Represents an individual in the large parsimony problem.
 *
 * @author Kyle Dewey
 */
public class PhylogeneticTreeIndividual extends Individual {
    // begin constants

    // cost matrix for the Sankoff algorithm
    // position 0 is understood to be A, 1 C, 2 G, and 3 T (nucleotides)
    // note that every 2.0 value is more accurately 2.0 * R, where R is
    // the amount that transitions are favored over transversions, which can 
    // differ based on data set; we assume 1.0
    public static final double[][] c = { { 0.0, 2.0, 1.0, 2.0 },
                                         { 2.0, 0.0, 2.0, 1.0 },
                                         { 1.0, 2.0, 0.0, 2.0 },
                                         { 2.0, 1.0, 2.0, 0.0 } };
    public static final String NUCLEOTIDES = "ACGT";

    // cache for msa files to taxa
    public static final Map< String, List< PhylogeneticTreeItem > > cache =
	new HashMap< String, List< PhylogeneticTreeItem > >();
    // end constants

    /**
     * Given a filename, it will get the taxa in the file.
     * If the file has already been read in, it will use the cached value.
     * @param msaFile The msa file name
     * @return The taxa associated with the file
     */
    public static List< PhylogeneticTreeItem > getTaxaFromFile( String msaFile ) 
	throws FileNotFoundException, IOException {
	if ( !cache.containsKey( msaFile ) ) {
	    cache.put( msaFile,
		       new ReadClustalWMSA().readMSAFile( new File( msaFile ) ) );
	}
	return cache.get( msaFile );
    }
	    
    /**
     * Creates a new individual, based on the taxa found in the given file.
     * @param msaFile File containing a multiple sequence alignment
     */
    public PhylogeneticTreeIndividual( String msaFile ) 
	throws FileNotFoundException, IOException {
	this( getTaxaFromFile( msaFile ) );
    }

    /**
     * Creates a new individual.
     * The individual is given a random phenotype, within
     * the constraints of the problem.
     *
     * @param taxa Listing of taxa with which to populate the tree with
     */
    public PhylogeneticTreeIndividual( List< PhylogeneticTreeItem > taxa ) {
        this( new PhylogeneticTreeGenotype( taxa ) );
    }

    /**
     * Creates a new individual, based on a preexisting phenotype.
     *
     * @param phenotype The phenotype to use
     */
    public PhylogeneticTreeIndividual( Genotype phenotype ) {
        super( phenotype );
    }

    /**
     * Gets the cost of substituting onf nucleotide with another.
     * To be used in the Sankoff algorithm.
     *
     * @param nuc1 The first nucleotide
     * @param nuc2 The second nucleotide
     *
     * @return The cost of the substitution
     */
    public static double cost( char nuc1, char nuc2 ) {
        double retval = Double.POSITIVE_INFINITY;
        int nuc1Pos;
        int nuc2Pos;

        // make sure that both are valid
        nuc1Pos = NUCLEOTIDES.indexOf( nuc1 );
        nuc2Pos = NUCLEOTIDES.indexOf( nuc2 );
        if ( nuc1Pos != -1 &&
             nuc2Pos != -1 ) {
            retval = c[ nuc1 ] [ nuc2 ];
        }

        return retval;
    }

    /**
     * Gets the cost of substituting one nucleotide with another.
     * To be used in the Sankoff algorithm.
     * This version is based on positions.
     *
     * @param nuc1 The first nucleotide
     * @param nuc2 The second nucleotide
     *
     * @return The cost of the substitution
     */
    public static double cost( int nuc1, int nuc2 ) {
        return c[ nuc1 ] [ nuc2 ];
    }

    /**
     * Gets the underlying phylogenetic tree.
     *
     * @return the underlying phylogenetic tree
     */
    private BinaryTree< PhylogeneticTreeItem > getTree() {
        return (BinaryTree< PhylogeneticTreeItem >)getPhenotype().getPhenotype();
    }

    /**
     * Clears out the Sankoff algorithm matrix for every node
     * in the tree.
     * Sets every element to 0.
     */
    private void clearSankoff() {
        BinaryTree< PhylogeneticTreeItem > tree;

        tree = getTree();
        for( BinaryTreeNode< PhylogeneticTreeItem > current : tree ) {
            double[] sankoff;

            sankoff = current.getItem().getSankoff();
            for( int x = 0; x < sankoff.length; x++ ) {
                sankoff[ x ] = 0.0;
            }
        }
    }

    /**
     * Fills the sankoff array for the given leaf node, with 
     * the given nucleotide in mind.
     *
     * @param leaf The leaf node to calculate
     * @param nuc The nucleotide at this node
     */
    private void calculateSankoffLeaf( BinaryTreeNode< PhylogeneticTreeItem > node,
                                       char nucleotide ) {
        double[] sankoff;
        int nucPos; // position of nucleotide in the array
        
        sankoff = node.getItem().getSankoff();
        nucPos = NUCLEOTIDES.indexOf( nucleotide );
        for( int x = 0; x < sankoff.length; x++ ) {
            double sankoffValue = Double.POSITIVE_INFINITY;

            if ( x == nucPos ) {
                sankoffValue = 0.0;
            }

            sankoff[ x ] = sankoffValue;
        }
    }

    /**
     * Fills in the sankoff array for the given internal node.
     * Assumes that all child nodes of this node have been filled in.
     *
     * @param node The internal node to calculate the sankoff array of
     */
    private void calculateSankoffInternalNode( BinaryTreeNode< PhylogeneticTreeItem > node ) {
        double[] leftSankoff;
        double[] rightSankoff;
        double[] mySankoff;

        leftSankoff = node.getLeft().getItem().getSankoff();
        rightSankoff = node.getRight().getItem().getSankoff();
        mySankoff = node.getItem().getSankoff();

        for( int position = 0; position < mySankoff.length; position++ ) {
            double[] leftValues = new double[ leftSankoff.length ];
            double[] rightValues = new double[ rightSankoff.length ];
            double left; // value at this position for the left node
            double right; // value at this position for the right node

            for( int leftPosition = 0; leftPosition < leftSankoff.length; leftPosition++ ) {
                leftValues[ leftPosition ] = cost( position, leftPosition ) +
                    leftSankoff[ leftPosition ];
            }
            left = getMin( leftValues );

            for( int rightPosition = 0; rightPosition < rightSankoff.length; rightPosition++ ) {
                rightValues[ rightPosition ] = cost( position, rightPosition ) +
                    rightSankoff[ rightPosition ];
            }
            right = getMin( rightValues );

            mySankoff[ position ] = left + right;
        }
    }

    /**
     * Runs the sankoff algorithm with a given parsimony informative
     * point in mind.  Returns the score on the sankoff
     * algorithm for this point.  Assumes all Sankoff arrays have
     * been initialized to 0; resets them to 0 after calculation.
     *
     * @param point Which parsimony informative point is in mind
     */
    private double getSankoffPoint( int point ) {
        BinaryTree< PhylogeneticTreeItem > tree;
        double retval;

        // get the tree
        tree = getTree();

        // travserse each node in the tree, performing an appropriate
        // action based on the kind it is.  Note that the iterator
        // performs a post-order traversal, so this guarentees that
        // we will visit all leaves first
        for( BinaryTreeNode< PhylogeneticTreeItem > current : tree ) {
            PhylogeneticTreeItem item; // item for this node

            item = current.getItem();
            if ( item.isTaxa() ) {
                char nucleotide; // nucleotide at this position

                nucleotide = item.getInformativePoints().charAt( point );
                calculateSankoffLeaf( current,
                                      nucleotide );
            } else {
                calculateSankoffInternalNode( current );
            }
        }

        // at this point, every node has a value
        // the value of the sankoff algorithm at a given point is the minimum
        // value in the root
        retval = getMin( tree.getRoot().getItem().getSankoff() );
        clearSankoff();

        return retval;
    }

    /**
     * Gets the fitness of this individual, as per the sankoff
     * algorithm.  Note that lower fitness values correlate
     * to more fit individuals.
     *
     * @return The fitness of the individual; the score of the
     *         sankoff algorithm combined for all
     *         parsimony informative points
     */
    protected double calculateFitness() {
        BinaryTree< PhylogeneticTreeItem > tree; // the underying tree
        String informative = null; // parsimony informative points
        double retval = 0.0; // what will be returned

        // get the informative points of any taxa
        // note that all taxa have the same number of points;
        // this is needed only for the length
        // also note that iteration is post-order, so the first node
        // seen is guarenteed an leaf
        tree = getTree();
        for( BinaryTreeNode< PhylogeneticTreeItem > current : tree ) {
            informative = current.getItem().getInformativePoints();
            break;
        }

        // now get the value of sankoff for each point
        clearSankoff();
        for( int point = 0; point < informative.length(); point++ ) {
            retval += getSankoffPoint( point );
        }

        return retval;
    }

    /**
     * Gets the fitness of this individual.
     * In the future, this will be done with the Sankoff algorithm.
     * For now, this is based on a simple, arbitrary, and
     * completely useless metric: how close species
     * are put together.  This will favor trees that put
     * species next to each other under a single ancestor.  For
     * example, for 12 taxa, the max possible fitness is 6 - 6
     * pairs of taxa put next to each other.
     * Again, this means absolutely nothing in terms of the
     * actual accurracy of the tree.
     *
     * @return The fitness of the individual, according to the
     *         above metric.
     */
    public double getFitnessBunches() {
        BinaryTree< PhylogeneticTreeItem > tree; // underlying tree
        double retval; // what will be returned

        // get the tree
        tree = getTree();

        // count the number of bunches - these are the fitness
        retval = getNumBunches( tree.getRoot() );

        return retval;
    }

    /**
     * Gets the number of bunches in a tree, starting
     * at the given node.  Determines this recursively.
     *
     * @param node The starting node
     *
     * @return The number of bunches underneath this node
     */
    private int getNumBunches( BinaryTreeNode< PhylogeneticTreeItem > node ) {
        if ( node.isInternalNode() &&
             node.getLeft().isLeaf() &&
             node.getRight().isLeaf() ) {
            // this is a bunch
            return 1;
        } else if ( node.isInternalNode() ) {
            // nondescript internal node
            return getNumBunches( node.getLeft() ) +
                getNumBunches( node.getRight() );
        } else {
            // this is a leaf
            return 0;
        }
    }

    /**
     * Gets the minimum of an array
     *
     * @param array An array of doubles
     *
     * @return The minimal value in the array
     */
    public static double getMin( double[] array ) {
        double retval = array[ 0 ];

        for( int x = 1; x < array.length; x++ ) {
            if ( array[ x ] < retval ) {
                retval = array[ x ];
            }
        }

        return retval;
    }
}
