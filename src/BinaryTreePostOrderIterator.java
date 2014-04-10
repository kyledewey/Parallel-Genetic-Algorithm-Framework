/*
 * BinaryTreePostOrderIterator.java
 *
 * Version:
 *     $Id: BinaryTreePostOrderIterator.java,v 1.1 2009/10/09 01:44:47 kyle Exp $
 *
 * Revisions:
 *      $Log: BinaryTreePostOrderIterator.java,v $
 *      Revision 1.1  2009/10/09 01:44:47  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;

/**
 * Traverses over a binary tree, returning nodes in the same
 * order as a post order travsersal.
 *
 * @author Kyle Dewey
 */
public class BinaryTreePostOrderIterator< T extends BinaryTreeNode > implements Iterator< T > {
    // begin instance variables
    private T root; // the root node
    private List< T > order; // the order in which we visit
                                               // nodes
    private Iterator< T > orderIterator; // iterates over order
    // end instance variables

    /**
     * Creates a new post order iterator.
     *
     * @param root The root node to start at
     */
    public BinaryTreePostOrderIterator( T root ) {
        this.root = root;
        order = new LinkedList< T >();
        visit( root );
        orderIterator = order.iterator();
    }

    /**
     * Creates a new post order iterator
     *
     * @param tree The tree to traverse over
     */
    public BinaryTreePostOrderIterator( BinaryTree tree ) {
        this( (T)tree.getRoot() );
    }

    /**
     * Visits all the nodes, starting at the given node.
     * Puts them in the order list in the order they were
     * visited.  This is a post-order traversal.
     *
     * @param node The starting node
     */
    private void visit( T node ) {
        if ( node != null ) {
            visit( (T)node.getLeft() );
            visit( (T)node.getRight() );
            order.add( node );
        }
    }

    /**
     * The hasNext() operation.
     * Just checks to see if something is left in the tree
     *
     * @return true if there are elements left, else false
     */
    public boolean hasNext() {
        return orderIterator.hasNext();
    }

    /**
     * The next() operation.
     * Returns the next element in the tree, in a post-order
     * traversal.
     *
     * @return The next node in the tree, as in a post-order
     *         travsersal
     *
     * @exception NoSuchElementException If there are no more elements
     */
    public T next() throws NoSuchElementException {
        return orderIterator.next();
    }

    /**
     * The remove() operation.
     * Note that although this is defined for lists, this is
     * somewhat difficult to define for trees, as destroying
     * one node could destroy an entire subtree.  As such,
     * we do not support this operation.
     *
     * @exception UnsupportedOperationException If this is called
     * @exception IllegalStateException Never; this method cannot be
     *            called
     */
    public void remove() throws UnsupportedOperationException,
        IllegalStateException {
        throw new UnsupportedOperationException( "Cannot remove() on a tree." );
    }
}
