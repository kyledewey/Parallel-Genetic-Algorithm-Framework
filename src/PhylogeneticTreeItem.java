/*
 * PhylogeneticTreeItem.java
 *
 * Version:
 *     $Id: PhylogeneticTreeItem.java,v 1.2 2009/10/20 01:49:09 kyle Exp $
 *
 * Revisions:
 *      $Log: PhylogeneticTreeItem.java,v $
 *      Revision 1.2  2009/10/20 01:49:09  kyle
 *      Implemented the Sankoff algorithm for scoring.
 *
 *      Revision 1.1  2009/10/09 01:44:47  kyle
 *      Initial revision
 *
 *
 */

/**
 * Creates a new item that is stored in a phylogenetic tree.
 * These items represent either ancestor nodes or the leaf
 * nodes, which are taxa.  Taxa must keep track of their names,
 * along with information for the Sankoff algorithm.
 *
 * @author Kyle Dewey
 */
public class PhylogeneticTreeItem 
    implements Comparable< PhylogeneticTreeItem > {
    // begin constants
    public static final int NUM_NUCLEOTIDES = 4;
    // end constants

    // begin instance variables
    private String name; // the name of this species
    private double[] sankoff; // information for the sankoff algorithm
    private boolean isAncestor; // if this node is an ancestor node
    private String informativePoints; // points informative to parsimony
    // end instance variables

    /**
     * Creates a new PhylogeneticTreeItem, with the given name.
     * This is intended to be a leaf node.
     *
     * @param name The name to use
     * @param informativePoints Points informative to parsimony
     */
    public PhylogeneticTreeItem( String name, String informativePoints ) {
        this.name = name;
        this.informativePoints = informativePoints;
        sankoff = new double[ NUM_NUCLEOTIDES ];
        isAncestor = false;
    }

    /**
     * Creates a new PhylogeneticTreeItem, based on another.
     * The name and informative points are shallow copied over;
     * everything else is generated anew.
     *
     * @param other The other PhylogeneticTreeItem to base this on
     */
    public PhylogeneticTreeItem( PhylogeneticTreeItem other ) {
        this.name = other.getName();
        this.informativePoints = other.getInformativePoints();
        sankoff = new double[ NUM_NUCLEOTIDES ];
        isAncestor = other.isAncestor();
    }

    /**
     * Creates a new PhylogeneticTreeItem, which is an
     * ancestor node.
     */
    public PhylogeneticTreeItem() {
        name = null;
        informativePoints = null;
        sankoff = new double[ NUM_NUCLEOTIDES ];
        isAncestor = true;
    }

    /**
     * Gets the name of this item.
     *
     * @return The name of this item.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the information for running the sankoff algorithm.
     *
     * @return The information for the sankoff algorithm
     */
    public double[] getSankoff() {
        return sankoff;
    }

    /**
     * Gets the points that are informative to parsimony.
     *
     * @return The informative points to parsimony
     */
    public String getInformativePoints() {
        return informativePoints;
    }

    /**
     * Gets whether or not this is an ancestor node
     *
     * @return true if this is an ancestor node, else false
     */
    public boolean isAncestor() {
        return isAncestor;
    }

    /**
     * Gets whether ot not this is a leaf node, representing
     * a taxa.
     *
     * @return true if this node represents a taxa, else false
     */
    public boolean isTaxa() {
        return !isAncestor;
    }

    /**
     * Gets the hash code of this item.
     * This is defined as the hash code of the item's name.
     * Note that if the name is null, as in an
     * ancestor node, then the hashCode is 0.
     *
     * @return The hash code of the name
     */
    public int hashCode() {
        return ( ( name == null ) ? 0 : name.hashCode() );
    }

    /**
     * Gets the name of this item.
     *
     * @return A string representing this item - the name
     */
    public String toString() {
        return name;
    }

    /**
     * Compares this tree item to another.
     * This is done using the name as a comparison.
     * Note that null names always go last.
     *
     * @param other The other item to compare to
     *
     * @return -1 if this item if before the other, 0 if they
     *         are the same, or 1 if this is after the other (or
     *         this has a null name)
     */
    public int compareTo( PhylogeneticTreeItem other ) {
        int retval = 0; // what will be returned
        boolean oneNull = false; // set to true if either name is null

        // do comparison expecting null
        if ( name == null && other.getName() == null ) {
            oneNull = true;
            retval = 0;
        } else if ( name == null ) {
            oneNull = true;
            retval = 1;
        } else if ( other.getName() == null ) {
            oneNull = true;
            retval = -1;
        }

        // do comparison without nulls
        if ( !oneNull ) {
            retval = name.compareTo( other.getName() );
        }

        return retval;
    }
}


