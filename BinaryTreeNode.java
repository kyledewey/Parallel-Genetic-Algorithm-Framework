/*
 * BinaryTreeNode.java
 *
 * Version:
 *     $Id: BinaryTreeNode.java,v 1.1 2009/10/09 01:44:47 kyle Exp $
 *
 * Revisions:
 *      $Log: BinaryTreeNode.java,v $
 *      Revision 1.1  2009/10/09 01:44:47  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;

/**
 * Represents a node in a binary tree.
 *
 * @author Kyle Dewey
 */
public class BinaryTreeNode< T extends Comparable< T > >
    implements Iterable< BinaryTreeNode< T > >,
               Comparable< BinaryTreeNode< T > > {
    
    // begin instance variables
    private BinaryTreeNode< T > left; // the left node
    private BinaryTreeNode< T > right; // the right node
    private BinaryTreeNode< T > parent; // the parent node
    private T item; // the item to store
    // end instance variables

    /**
     * Creates a new BinaryTreeNode, with everything initialzed.
     *
     * @param left What to put as the left node
     * @param right What to put as the right node
     * @param parent What to put as the parent
     * @param item What to store as the item
     */
    public BinaryTreeNode( BinaryTreeNode< T > left,
                           BinaryTreeNode< T > right,
                           BinaryTreeNode< T > parent,
                           T item ) {
        this.left = left;
        this.right = right;
        this.parent = parent;
        this.item = item;
    }

    /**
     * Creates a new BinaryTreeNode, with everything
     * initialized to null.
     */
    public BinaryTreeNode() {
        this( null, null, null, null );
    }

    /**
     * Gets the left node of this node.
     *
     * @return The left node of this node
     */
    public BinaryTreeNode< T > getLeft() {
        return left;
    }

    /**
     * Sets the left node of this node.
     *
     * @param left What to set the left node to
     */
    public void setLeft( BinaryTreeNode< T > left ) {
        this.left = left;
        left.setParent( this );
    }

    /**
     * Gets the right node of this node.
     *
     * @return The right node of this node
     */
    public BinaryTreeNode< T > getRight() {
        return right;
    }

    /**
     * Sets the right node of this node.
     *
     * @param right What to set the right node to
     */
    public void setRight( BinaryTreeNode< T > right ) {
        this.right = right;
        right.setParent( this );
    }

    /**
     * Sets the item that this node stores.
     *
     * @param item The new item to store
     */
    public void setItem( T item ) {
        this.item = item;
    }

    /**
     * Gets the item that this node stores
     *
     * @return The item that this node stores
     */
    public T getItem() {
        return item;
    }

    /**
     * Gets the parent of this node.
     *
     * @return The parent of this node
     */
    public BinaryTreeNode< T > getParent() {
        return parent;
    }

    /**
     * Sets the parent of this node.
     *
     * @param parent The new parent of this node
     */
    public void setParent( BinaryTreeNode< T > parent ) {
        this.parent = parent;
    }

    /**
     * Gets the hash code of this node.
     * This is defined as the hash code of the item stored within.
     *
     * @return The hash code of this node
     */
    public int hashCode() {
        return item.hashCode();
    }

    /**
     * Gets the iterator for this node.
     * Treats this node as the root.
     * Items returned are in post-order.
     *
     * @return A post-order iterator over the tree starting at
     *         this node
     */
    public Iterator< BinaryTreeNode< T > > iterator() {
        return new BinaryTreePostOrderIterator< BinaryTreeNode< T > >( this );
    }

    /**
     * Compares this BinaryTreeNode to another.
     * Comparison is done based upon the item.
     *
     * @param other The other BinaryTreeNode to compare to
     *
     * @return -1 if this comes before the other, 0 if
     *         they are the same, or 1 if this comes after
     *         the other
     */
    public int compareTo( BinaryTreeNode< T > other ) {
        return item.compareTo( other.getItem() );
    }

    /**
     * Determines if this node is a leaf.
     *
     * @return True if this node is a leaf, else false
     */
    public boolean isLeaf() {
        return BinaryTree.isLeaf( this );
    }

    /**
     * Determines if this node is an internal node.
     *
     * @return True if this node is an internal node
     */
    public boolean isInternalNode() {
        return BinaryTree.isInternalNode( this );
    }

    /**
     * Prints out this node.
     * Merely calls the toString() method of
     * the contained item.
     *
     * @return A string representing the item within.
     */
    public String toString() {
        return ( ( item == null ) ? "" : item.toString() );
    }
}
