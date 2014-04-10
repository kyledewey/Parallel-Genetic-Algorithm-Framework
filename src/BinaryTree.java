/*
 * BinaryTree.java 
 *
 * Version:
 *     $Id: BinaryTree.java,v 1.1 2009/10/09 01:44:47 kyle Exp $
 *
 * Revisions:
 *      $Log: BinaryTree.java,v $
 *      Revision 1.1  2009/10/09 01:44:47  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;

/**
 * Represents a binary tree.
 * Note that this is a recursively defined structure.
 *
 * @author Kyle Dewey
 */
public class BinaryTree< T extends Comparable< T > > implements Iterable< BinaryTreeNode< T > > {
    // begin constants
    protected static final Random random = new Random();
    // end constants

    // begin instance variables
    private BinaryTreeNode< T > root; // the head of the tree
    // end instance variables

    /**
     * Creates a new BinaryTree with the root initialized.
     *
     * @param root The root to use
     */
    public BinaryTree( BinaryTreeNode< T > root ) {
        this.root = root;
    }

    /**
     * Creates a new, empty binary tree.
     */
    public BinaryTree() {
        root = null;
    }

    /**
     * Gets the root node of the tree.
     *
     * @return The root node of the tree
     */
    public BinaryTreeNode< T > getRoot() {
        return root;
    }

    /**
     * Sets the root node of the tree.
     *
     * @param root The new tree root to use
     */
    public void setRoot( BinaryTreeNode< T > root ) {
        this.root = root;
    }

    /**
     * Gets the number of leaves in this binary tree.
     *
     * @return The number of leaves in this binary tree
     */
    public int getNumLeaves() {
        return getNumLeaves( this );
    }

    /**
     * Gets the number of nodes in this binary tree.
     *
     * @return The number of nodes in this binary tree
     */
    public int getNumNodes() {
        return getNumNodes( this );
    }

    /**
     * Gets an iterator for this tree.
     * The iterator will return nodes as in a post-order traversal.
     *
     * @return An iterator over this tree
     */
    public Iterator< BinaryTreeNode< T > > iterator() {
        return new BinaryTreePostOrderIterator< BinaryTreeNode< T > >( this );
    }

    /**
     * Gets the nth node from this tree.
     *
     * @param n The node to get
     *
     * @return The nth node, or null if n is out of range
     */
    public BinaryTreeNode< T > getNthNode( int n ) {
        return getNthNode( this, n );
    }

    /**
     * Prints out this tree.
     * Does so in a pre-order traversal, unlike most everything
     * else which works in post-order.
     *
     * @return A string representing this tree
     */
    public String toString() {
        return toString( root );
    }

    /**
     * Prints out a tree, starting from the given node.
     * Note that it only prints out leaf nodes - internal
     * nodes are printed with parenthesis.
     *
     * @param node The node to start from
     *
     * @return A string representing this tree
     */
    private String toString( BinaryTreeNode< T > node ) {
        if ( node != null ) {
            if ( isLeaf( node ) ) {
                return node.toString();
            } else {
                // internal node
                return "(" + toString( node.getLeft() ) + ", " +
                    toString( node.getRight() ) + ")";
            }
        }

        // in case of null
        return "";
    }

    /**
     * Determines if a given node is a leaf node.
     *
     * @param node The node to check if it's a leaf
     *
     * @return true if the given node is a leaf node, else false
     */
    public static < T extends Comparable< T > > boolean isLeaf( BinaryTreeNode< T > node ) {
        return ( node.getLeft() == null &&
                 node.getRight() == null );
    }

    /**
     * Determines if a given node is an internal node
     * This is simply if it is not a leaf
     *
     * @param node The node to check if it's an internal node
     *
     * @return true if this is an internal node, else false
     */
    public static < T extends Comparable< T > > boolean isInternalNode( BinaryTreeNode< T > node ) {
        return !isLeaf( node );
    }

     /**
     * Gets the number of leaves underneath the given node.
     *
     * @param node The binary tree node to start from
     *
     * @return The number of leaves underneath the given node
     */
    public static < T extends Comparable< T > > int getNumLeaves( BinaryTreeNode< T > node ) {
        if ( isLeaf( node ) ) {
            return 1;
        } else {
            int numLeavesBelow = 0;

            // recurse on each child node
            if ( node.getLeft() != null ) {
                numLeavesBelow += getNumLeaves( node.getLeft() );
            }
            if ( node.getRight() != null ) {
                numLeavesBelow += getNumLeaves( node.getRight() );
            }

            return numLeavesBelow;
        }
    }

    /**
     * Gets the number of leaves within the given binary tree.
     *
     * @param tree The binary tree to get the leaves of
     *
     * @return The number of leaves in the tree
     */
    public static < T extends Comparable< T > > int getNumLeaves( BinaryTree< T > tree ) {
        return getNumLeaves( tree.getRoot() );
    }

    /**
     * Gets the number of nodes under the given node,
     * including the node.
     *
     * @param node The node to start from
     *
     * @return The number of nodes underneath the given node
     */
    public static < T extends Comparable< T > > int getNumNodes( BinaryTreeNode< T > node ) {
        if ( node != null ) {
            return getNumNodes( node.getLeft() ) +
                getNumNodes( node.getRight() ) +
                1;
        } else {
            return 0;
        }
    }

    /**
     * Gets the number of nodes in the given tree.
     *
     * @param tree The tree to get the number of nodes of
     *
     * @return The number of nodes in the given tree
     */
    public static < T extends Comparable< T > > int getNumNodes( BinaryTree< T > tree ) {
        return getNumNodes( tree.getRoot() );
    }

    /**
     * Determines if two nodes are in two distinct subtrees.
     * That is, one node is not within the subtree of another node.
     * Order does not matter (as with inSubtree)
     *
     * @param node1 The first node
     * @param node2 The second node
     *
     * @return true if the two nodes are in two distinct subtrees
     */
    public static < T extends Comparable< T > > boolean inDistinctSubtrees( BinaryTreeNode< T > pos1,
                                                                            BinaryTreeNode< T > pos2 ) {
        boolean retval = false; // what will be returned

        if ( !inSubtree( pos1, pos2 ) &&
             !inSubtree( pos2, pos1 ) ) {
            retval = true;
        }

        return retval;
    }

    /**
     * Determines if the first node is in the subtree of the second.
     *
     * @param node1 The first node
     * @param node2 The second node
     *
     * @return true if the first node is in the subtree of the second
     */
    public static < T extends Comparable< T > > boolean 
        inSubtree( BinaryTreeNode< T > node1,
                   BinaryTreeNode< T > node2 ) {
        boolean retval = false; // what will be returned
        Iterator< BinaryTreeNode< T > > iterator;

        // try to find the first node in the subtree of the second
        iterator = node2.iterator();
        while ( iterator.hasNext() ) {
            if ( iterator.next() == node1 ) {
                retval = true;
                break;
            }
        }

        return retval;
    }
     
    /**
     * Swaps the two given nodes within the tree.
     * Assumes that these nodes are two distinct subtrees within
     * this tree.  That is, one node is not contained within the subtree
     * of another
     *
     * @param pos1 The first node
     * @param pos2 The second node
     */
    public static < T extends Comparable< T > > void 
        swapNodes( BinaryTreeNode< T > pos1,
                   BinaryTreeNode< T > pos2 ) {
        BinaryTreeNode< T > parentPos1; // pos1's parent
        BinaryTreeNode< T > parentPos2; // pos2's parent
        boolean pos1OnLeft; // true if pos1 is on the left of it's parent
        boolean pos2OnLeft;

        // get the parents
        parentPos1 = pos1.getParent();
        parentPos2 = pos2.getParent();

        // determine where the nodes are on their parents
        if ( pos1 == parentPos1.getLeft() ) {
            pos1OnLeft = true;
        } else {
            pos1OnLeft = false;
        }
        if ( pos2 == parentPos2.getLeft() ) {
            pos2OnLeft = true;
        } else {
            pos2OnLeft = false;
        }

        // move the nodes to these places
        // note that the setLeft() and setRight() operations
        // will change the parent accordingly
        if ( pos1OnLeft ) {
            parentPos1.setLeft( pos2 );
        } else {
            parentPos1.setRight( pos2 );
        }
        if ( pos2OnLeft ) {
            parentPos2.setLeft( pos1 );
        } else {
            parentPos2.setRight( pos1 );
        }
    }

    /**
     * Gets a random node of the given tree.
     *
     * @param tree The tree to get the random node of
     * @param numNodes The number of nodes in the tree; assumes it valid
     * 
     * @return The random node; null if numNodes is out of range
     */
    public static < T extends Comparable< T > > 
        BinaryTreeNode< T > getRandomNode( BinaryTree< T > tree,
                                      int numNodes ) {
        return getNthNode( tree, random.nextInt( numNodes ) );
    }

    /**
     * Gets the nth node from the given tree.
     *
     * @param tree The tree to get the nth node of
     * @param n The node to get
     *
     * @return The nth node, or null if n is out of range
     */
    public static < T extends Comparable< T > > 
        BinaryTreeNode< T > getNthNode( BinaryTree< T > tree,
                                        int n ) {
        int current = 0; // the node we are currently on
        BinaryTreeNode< T > retval = null; // what will be returned
        Iterator< BinaryTreeNode< T > > iterator;

        // traverse through the tree until we get to the target
        iterator = tree.iterator();
        while( iterator.hasNext() ) {
            BinaryTreeNode< T > currentNode;
            currentNode = iterator.next();
            if ( current == n ) {
                retval = currentNode;
                break;
            }
            current++;
        }

        return retval;
    }
}
