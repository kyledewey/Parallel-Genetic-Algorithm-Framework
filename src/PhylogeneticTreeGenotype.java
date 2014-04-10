/*
 * PhylogeneticTreeGenotype.java
 *
 * Version:
 *     $Id: PhylogeneticTreeGenotype.java,v 1.4 2009/10/27 21:42:10 kyle Exp $
 *
 * Revisions:
 *      $Log: PhylogeneticTreeGenotype.java,v $
 *      Revision 1.4  2009/10/27 21:42:10  kyle
 *      Added probability for mutation happening.
 *
 *      Revision 1.3  2009/10/27 16:05:04  kyle
 *      Added a source for individuals.
 *
 *      Revision 1.2  2009/10/27 15:33:24  kyle
 *      Implemented proper crossover operator from Kill et. al.
 *
 *      Revision 1.1  2009/10/09 01:44:47  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;

/**
 * The genotype for a phylogenetic tree.
 *
 * @author Kyle Dewey
 */
public class PhylogeneticTreeGenotype extends Genotype {
    // begin global variables
    private static int remainingTaxa; // for random tree creation
    private static int remainingAncestors; // for random tree creation
    // end global variables

    // begin instance variables
    private BinaryTree< PhylogeneticTreeItem > phenotype; // the phenotype
    private int numNodes; // the number of nodes in this phenotype
    private int numTaxa; // the number of taxa in this tree
    // end instance variables

    /**
     * Creates a new PhylogeneticTreeGenotype with
     * the given tree.
     *
     * @param tree The tree to use
     */
    public PhylogeneticTreeGenotype( BinaryTree< PhylogeneticTreeItem > tree ) {
        phenotype = tree;
        numNodes = phenotype.getNumNodes();
        numTaxa = phenotype.getNumLeaves();
    }

    /**
     * Creates a new, randomly generated phylogenetic tree 
     * genotype based on the given list of taxa.  Each taxa will
     * be a leaf node.  The given list is not modified in any way.
     *
     * @param taxa The list of taxa
     */
    public PhylogeneticTreeGenotype( List< PhylogeneticTreeItem > taxa ) {
        this( createRandomTree( taxa ) );
        setSource( Source.INITIAL );
    }

    /**
     * Gets the underlying phenotype.
     *
     * @return The underlying phenotype
     */
    public Object getPhenotype() {
        return phenotype;
    }

    /**
     * The crossover operation.  Merely combining two subtrees will
     * almost always result in an invalid phenotype.  As such,
     * this uses the method proposed in Hill et. al.  One parent is
     * used as the base.  A random subtree is selected,
     * and the taxa of within that tree stored.  Then, those taxa are
     * found in the other parent's tree.  The taxa in the base
     * are reearranged to match the order of the taxa found in the
     * other tree.
     *
     * @param other The other phenotype to crossover with
     *
     * @return A new genotype, a mixture of the two, as described above
     *
     * @exception GenotypeCastException If the underlying genotype is
     *            incompatible with the given genotype
     */
    public Genotype crossover( Genotype other )
        throws GenotypeCastException {
        Genotype retval; // what will be returned
        BinaryTree< PhylogeneticTreeItem > otherPhenotype; // other phenotype
        BinaryTree< PhylogeneticTreeItem > newPhenotype; // new phenotype to return
        BinaryTreeNode< PhylogeneticTreeItem > subtree; // random subtree
        TreeMap< Integer, String > subtreePositions; // positions of taxa in subtree
        TreeMap< Integer, String > otherPositions; // positions of taxa in other tree
        HashMap< String, BinaryTreeNode< PhylogeneticTreeItem > > subtreeTaxaNodes; // where nodes are by taxa

        // make sure that the genotypes are compatible
        if ( !this.getClass().equals( other.getClass() ) ) {
            throw new GenotypeCastException( "Incompatible genotypes tried to cross: " +
                                             this.getClass().toString() + " and " +
                                             other.getClass().toString() );
        }

        // get a random subtree that isn't a single node
        newPhenotype = deepCopy();
        do {
            subtree = BinaryTree.getRandomNode( newPhenotype,
                                                numNodes );
        } while ( !subtree.isInternalNode() );

        // get the positions of taxa
        subtreeTaxaNodes = getTaxaNodes( subtree );
        subtreePositions = getTaxaPositions( subtree );
        otherPhenotype = (BinaryTree< PhylogeneticTreeItem >)other.getPhenotype();
        otherPositions = getTaxaPositions( otherPhenotype.getRoot() );

        // make it so we only look at taxa that both have
        trimTaxaMap( subtreePositions, otherPositions );

        // now put the nodes in the subtree in the same order as the other
        // note that both these iterators will iterate over the same number of 
        // elements
        Iterator< Integer > otherIt = otherPositions.keySet().iterator();
        Iterator< Integer > subtreeIt = subtreePositions.keySet().iterator();
        while ( otherIt.hasNext() ) {
            int otherPos = otherIt.next();
            int subtreePos = subtreeIt.next();
            String otherTaxa = otherPositions.get( otherPos );
            String subtreeTaxa = subtreePositions.get( subtreePos );

            if ( !otherTaxa.equals( subtreeTaxa ) ) {
                BinaryTree.swapNodes( subtreeTaxaNodes.get( otherTaxa ),
                                      subtreeTaxaNodes.get( subtreeTaxa ) );
            }
        }

        retval = new PhylogeneticTreeGenotype( newPhenotype );
        retval.setSource( Source.CROSSOVER );

        return retval;
    }

    /**
     * Gets the positions of each taxa in a given tree.
     *
     * @param tree The tree to get the positions of
     *
     * @return a mapping of tree positions to taxa
     */
    private TreeMap< Integer, String > getTaxaPositions( BinaryTreeNode< PhylogeneticTreeItem > tree ) {
        TreeMap< Integer, String > retval; // what will be returned
        int position = 0; // which position we are on

        // create the return value
        retval = new TreeMap< Integer, String >();

        // go over each position
        for( BinaryTreeNode< PhylogeneticTreeItem > current : tree ) {
            PhylogeneticTreeItem item;

            item = current.getItem();
            if ( item.isTaxa() ) {
                retval.put( position,
                            item.getName() );
            }

            position++;
        }

        return retval;
    }

    /**
     * Gets the nodes associated with each taxa.
     *
     * @param tree The tree to get positions of
     *
     * @return A mapping of taxa to the nodes they are associated with
     */
    private HashMap< String, BinaryTreeNode< PhylogeneticTreeItem > > 
        getTaxaNodes( BinaryTreeNode< PhylogeneticTreeItem > tree ) {
        HashMap< String, BinaryTreeNode< PhylogeneticTreeItem > > retval;

        // create the return value
        retval = new HashMap< String, BinaryTreeNode< PhylogeneticTreeItem > >();

        // go over each node in the tree
        for( BinaryTreeNode< PhylogeneticTreeItem > current : tree ) {
            if ( current.getItem().isTaxa() ) {
                retval.put( current.getItem().getName(),
                            current );
            }
        }

        return retval;
    }

    /**
     * Trims a mapping of taxa, such that the second map contains only
     * the taxa seen in the first.
     *
     * @param base The base map, that contains all the taxa we want to see
     *        in the second map
     * @param toTrim The map to trim taxa out of
     */
    private void trimTaxaMap( TreeMap< Integer, String > base,
                              TreeMap< Integer, String > toTrim ) {
        Iterator< Integer > toTrimIt;

        toTrimIt = toTrim.keySet().iterator();
        while( toTrimIt.hasNext() ) {
            Integer key;
            String value;

            key = toTrimIt.next();
            value = toTrim.get( key );
            if ( !mapContainsValue( base, value ) ) {
                toTrimIt.remove();
            }
        }
    }

    /**
     * The mutation operation.
     * Makes a copy of this genotype, and mutates that.
     * Mutation will swap one random subtree with another
     *
     * @param prob The probability of any given node to be swapped
     *        with another
     *
     * @return The mutated copy of this phenotype, or null if
     *         no mutations occurred
     */
    public Genotype mutate( double prob ) {
        int numMutations = 0; // number of mutations to undergo
        Genotype retval = null; // what will be returned

        // and see if we mutate it. Problems:
        // 1) We must pick another node to swap with - essentially
        //    doubles the mutation rate anyway
        // 2) We can't simply swap for nondistinct subtrees - process
        //    is far more complicated without a lot of gain
        // As such, we just see how many mutation events we should
        // undergo, and do that
        for( int x = 0; x < numNodes; x++ ) {
            double randomNum = random.nextDouble();
            if ( prob >= randomNum ) {
                numMutations++;
            }
        }

        if ( numMutations > 0 ) {
            BinaryTree< PhylogeneticTreeItem > newTree;
            newTree = deepCopy();
            for( int x = 0; x < numMutations; x++ ) {
                // find two nodes to swap
                BinaryTreeNode< PhylogeneticTreeItem > node1;
                BinaryTreeNode< PhylogeneticTreeItem > node2;
                do {
                    node1 = BinaryTree.getRandomNode( newTree, numNodes );
                    node2 = BinaryTree.getRandomNode( newTree, numNodes );
                } while( node1 == node2 ||
                         !BinaryTree.inDistinctSubtrees( node1, node2 ) );
                BinaryTree.swapNodes( node1, node2 );
            }
            

            // now create the new genotype
            retval = new PhylogeneticTreeGenotype( newTree );
            retval.setSource( Source.MUTATION );
        }

        return retval;
    }

    /**
     * Returns a string representation of this genotype.
     * This will show the entire tree.
     *
     * @return A string representing this genotype
     */
    public String toString() {
        return phenotype.toString();
    }

    /**
     * Creates a deep copy of this phenotype's tree.
     * 
     * @return A new tree, a deep copy of this one's
     */
    private BinaryTree< PhylogeneticTreeItem > deepCopy() {
        BinaryTree< PhylogeneticTreeItem > retval; // what will be returned
        BinaryTreeNode< PhylogeneticTreeItem > newRoot; // the new tree root

        newRoot = deepCopy( phenotype.getRoot() );
        retval = new BinaryTree< PhylogeneticTreeItem >( newRoot );

        return retval;
    }

    /**
     * Creates a deep copy of a subtree, starting from the given node.
     *
     * @param node The subtree to start at
     *
     * @return A deep copy of the node, along with it's corresponding 
     *         subtree.
     */
    private BinaryTreeNode< PhylogeneticTreeItem > deepCopy( BinaryTreeNode< PhylogeneticTreeItem > node ) {
        BinaryTreeNode< PhylogeneticTreeItem > retval; // what will be returned

        retval = new BinaryTreeNode< PhylogeneticTreeItem >();
        retval.setItem( new PhylogeneticTreeItem( node.getItem() ) );
        if ( node.getLeft() != null ) {
            retval.setLeft( deepCopy( node.getLeft() ) );
        }
        if ( node.getRight() != null ) {
            retval.setRight( deepCopy( node.getRight() ) );
        }

        return retval;
    }

    /**
     * Applied to an ancestor node during random tree creation.
     * Will probabilistically choose to either put a taxa
     * or an ancestor on the nodes, and will recurse on that.
     * Assumes remainingTaxa and remainingAncestors has been 
     * initialized.
     *
     * @param node The base ancestor node
     * @param taxa A list of taxa to work with
     */
    private static void populateAncestorNode( BinaryTreeNode< PhylogeneticTreeItem > node,
                                              List< PhylogeneticTreeItem > taxa ) {
        int numTaxaMade = 0; // number of taxa we made
        boolean left; // if the left node was chosen first
        BinaryTreeNode< PhylogeneticTreeItem > work; // the current node to work on
        
        // see if we make a taxa or an ancestor first
        if ( remainingAncestors == 0 ||
             random.nextInt( 2 ) == 0 ) {
            // taxa
            PhylogeneticTreeItem toPlace;

            toPlace = taxa.remove( taxa.size() - 1 );
            toPlace = new PhylogeneticTreeItem( toPlace ); // make a copy
            work = new BinaryTreeNode< PhylogeneticTreeItem >( null, 
                                                               null,
                                                               null,
                                                               toPlace );
            numTaxaMade++;
            remainingTaxa--;
        } else {
            // ancestor
            work = new BinaryTreeNode< PhylogeneticTreeItem >( null,
                                                               null,
                                                               null,
                                                               new PhylogeneticTreeItem() );
            remainingAncestors--;
        }
                                                
        // see if we insert at left or right
        if ( random.nextInt( 2 ) == 0 ) {
            left = true;
            node.setLeft( work );
        } else {
            left = false;
            node.setRight( work );
        }

        // now see what we place at the other node
        // we want to keep recursing, so we favor ancestors
        if ( numTaxaMade > 0 && remainingAncestors > 0 ) {
            // make an ancestor
            work = new BinaryTreeNode< PhylogeneticTreeItem >( null, 
                                                               null,
                                                               null,
                                                               new PhylogeneticTreeItem() );
            remainingAncestors--;
        } else {
            // make a taxa or an ancestor
            if ( remainingAncestors == 0 ||
                 random.nextInt( 2 ) == 0 ) {
                // taxa
                PhylogeneticTreeItem toPlace;

                toPlace = taxa.remove( taxa.size() - 1 );
                toPlace = new PhylogeneticTreeItem( toPlace );
                work = new BinaryTreeNode< PhylogeneticTreeItem >( null, 
                                                                   null,
                                                                   null,
                                                                   toPlace );
                numTaxaMade++;
                remainingTaxa--;
            } else {
                // ancestor
                work = new BinaryTreeNode< PhylogeneticTreeItem >( null,
                                                                   null,
                                                                   null,
                                                                   new PhylogeneticTreeItem() );
                remainingAncestors--;
            }
        }

        // insert the new node at the opposite position
        if ( left ) {
            node.setRight( work );
        } else {
            node.setLeft( work );
        }
            
        // recurse on each ancestor
        if ( numTaxaMade == 0 ) {
            // choose which one to recurse on first
            if ( random.nextInt( 2 ) == 0 ) {
                populateAncestorNode( node.getLeft(), taxa );
                populateAncestorNode( node.getRight(), taxa );
            } else {
                populateAncestorNode( node.getRight(), taxa );
                populateAncestorNode( node.getLeft(), taxa );
            }
        } else if ( numTaxaMade == 1 ) {
            // recurse on the one ancestor made
            if ( node.getLeft().getItem().isAncestor() ) {
                populateAncestorNode( node.getLeft(), taxa );
            } else {
                populateAncestorNode( node.getRight(), taxa );
            }
        }
    } // populateAncestorNode
            
    /**
     * Creates a randomly generated phylogenetic tree based on
     * the given list of taxa.  Each taxa will end up in a leaf
     * node, and everything else will be an internal node.
     *
     * @param taxa the list of taxa to use.  It will empty the list.
     */
    public static BinaryTree< PhylogeneticTreeItem > createRandomTree( List< PhylogeneticTreeItem > taxa ) {
        List< PhylogeneticTreeItem > copy;
        BinaryTreeNode< PhylogeneticTreeItem > root; // root of the tree

        copy = new ArrayList< PhylogeneticTreeItem >( taxa );

        // initialize global variables
        remainingTaxa = copy.size();
        remainingAncestors = remainingTaxa - 1;

        // start with an ancestor node
        root = new BinaryTreeNode< PhylogeneticTreeItem >( null,
                                                           null,
                                                           null,
                                                           new PhylogeneticTreeItem() );
        remainingAncestors--;
        populateAncestorNode( root, copy );

        // create the return value
        return new BinaryTree< PhylogeneticTreeItem >( root );
    }

    /**
     * Given a value, returns true if the given map contains the
     * given value.
     *
     * @param map The map of keys and values
     * @param value The value to check if it is in the map
     *
     * @return true if the given map contains the given value,
     *         else false
     */
    public static < T, U > boolean mapContainsValue( Map< T, U > map, U value ) {
        boolean retval = false; // what will be returned

        for( T key : map.keySet() ) {
            if ( map.get( key ).equals( value ) ) {
                retval = true;
                break;
            }
        }

        return retval;
    }
}
