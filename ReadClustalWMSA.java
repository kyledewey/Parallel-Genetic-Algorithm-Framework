/*
 * ReadClustalWMSA.java
 *
 * Version:
 *     $Id: ReadClustalWMSA.java,v 1.3 2009/10/21 00:29:48 kyle Exp $
 *
 * Revisions:
 *      $Log: ReadClustalWMSA.java,v $
 *      Revision 1.3  2009/10/21 00:29:48  kyle
 *      Removed the isUsefulString() method;
 *      more proper error checking implemented.
 *
 *      Revision 1.2  2009/10/20 01:49:09  kyle
 *      No longer reads in lines with additional alignment
 *      information accidently as taxa;
 *      any point with gaps is no longer considered
 *      parsimony informative.
 *
 *      Revision 1.1  2009/10/09 01:44:47  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;
import java.io.*;

/**
 * Reads in an alignment (.aln) from ClustalW.
 *
 * @author Kyle Dewey
 */
public class ReadClustalWMSA implements ReadMSA {
    // begin constants
    public static final String FILE_HEADER = "CLUSTAL";
    // end constants

    /**
     * Given a taxa name, a sequence, and a map, will add it to the map.
     * If it doesn't already exist in the map, then it will add
     * it with the seqeuence.  If it does exist, then it will
     * append the given sequence to the existing sequence for the
     * given taxa.
     *
     * @param name The name of the taxa
     * @param sequence The sequence associated with this taxa
     * @param map The map that holds the taxa
     */
    public static void addTaxa( String name, 
                                String sequence, 
                                Map< String, String > map ) {
        if ( !map.containsKey( name ) ) {
            // just add it
            map.put( name, sequence );
        } else {
            // append this sequence to it
            String existing; // existing sequence
            
            existing = map.get( name );
            map.put( name, existing + sequence );
        }
    }

    /**
     * Given a mapping of taxa by name to taxa sequences,
     * converts it to a listing of phylogenetic tree items.
     *
     * @param map The mapping of taxa names to sequences
     *
     * @return A listing of phylogenetic tree items that
     *         represent the items in the map
     */
    public static List< PhylogeneticTreeItem > 
        mapToList( Map< String, String > map ) {
        List< PhylogeneticTreeItem > retval; // what will be returned

        // make the list
        retval = new ArrayList< PhylogeneticTreeItem >( map.size() );

        // put every item in it
        for( String name : map.keySet() ) {
            retval.add( new PhylogeneticTreeItem( name,
                                                  map.get( name ) ) );
        }

        return retval;
    }

    /**
     * Removes all the parsimony non-informative points from a
     * mapping of taxa names to sequences.
     *
     * @param map The mapping of names to sequences
     */
    public static void removeNonInformativePoints( Map< String, String > map ) {
        int length; // length of the strings in the map

        length = map.values().toArray( new String[ 0 ] )[ 0 ].length();
        for( int x = 0; x < length; x++ ) {
            if ( !informativeSite( x, map ) ) {
                removePoint( x, map );
                x--; // we will skip the next point without this
                length--; // one shorter
            }
        }
    }

    /**
     * Removes a given point from all sequences within a map.
     *
     * @param point Which point to remove
     * @param map The map that holds the sequences
     */
    public static void removePoint( int point, Map< String, String > map ) {
        for( String name : map.keySet() ) {
            map.put( name,
                     removePoint( point, map.get( name ) ) );
        }
    }

    /**
     * Removes a character at the given index, and returns the
     * new string without the given character.
     *
     * @param point The point to remove. Assumes it's valid
     * @param string The string to remove from
     *
     * @return A new string that this lacking the given point
     */
    public static String removePoint( int point, String string ) {
        String before; // everything before the point
        String after; // everything after the point

        before = string.substring( 0, point );

        if ( point + 1 >= string.length() ) {
            after = "";
        } else {
            after = string.substring( point + 1 );
        }

        return before + after;
    }

    /**
     * Determines if the given data shows a parsimony informative
     * site.
     *
     * @param numA The number of A's at this site
     * @param numT The number of T's at this site
     * @param numC The number of C's at this site
     * @param numG The number of G's at this site
     * @param numGaps The number of gaps at this site
     * @return true if the data shows an informative site
     */
    public static boolean informativeSite( int numA,
                                           int numT,
                                           int numC,
                                           int numG,
                                           int numGaps ) {
        // by definition, there must be at least two nucleotides
        // observed, and at least two of the nucleotides observed
        // must be observed at least twice
        // additionally, there must be no gaps at this site
        List< Integer > temp;
        int first;
        int second;
        
        if ( numGaps > 0 ) {
            return false;
        }

        temp = new ArrayList< Integer >( 4 );
        temp.add( numA );
        temp.add( numT );
        temp.add( numC );
        temp.add( numG );
        Collections.sort( temp );
        first = temp.get( 3 );
        second = temp.get( 2 );
        
        return ( ( first >= 2  && second >= 2 ) ? true : false );
    }
                     
    /**
     * Determines if the given point is parsimony informative, given
     * a mapping of taxa names to sequences.
     *
     * @param point Which point is in question.  Assumes it's valid
     * @param map The mapping of taxa names to sequences
     *
     * @return true if the point is parimony informative, else false
     */
    public static boolean informativeSite( int point, 
                                           Map< String, String > map ) {
        int numA = 0; // number of A at this site
        int numT = 0; // number of T at this site
        int numC = 0; // number of C at this site
        int numG = 0; // number of G at this site
        int numGaps = 0; // number of gaps at this site
        boolean retval; // what will be returned

        // go through the map, totaling the points
        for( String name : map.keySet() ) {
            char current; // current char at this point

            current = map.get( name ).charAt( point );
            switch( current ) {
            case 'A':
                numA++;
                break;
            case 'T':
                numT++;
                break;
            case 'C':
                numC++;
                break;
            case 'G':
                numG++;
                break;
            default:
                // note that not everything else is a gap
                // however, since gaps ar not part of the 
                // question of informative points,
                // this is not a problem
                numGaps++;
                break;
            }
        }

        // see if this is informative
        return informativeSite( numA, numT, numC, numG, numGaps );
    }
     
    /**
     * Reads in the .aln file, holding the multiple
     * sequence alignment.
     *
     * @param file The file to read in
     *
     * @return Listing of the taxa in the file
     *
     * @exception FileNotFoundException If the given file could not be opened
     * @exception IOException If an error occurred on reading
     */
    public List< PhylogeneticTreeItem > readMSAFile( File file ) 
        throws FileNotFoundException, IOException {
        Scanner input; // what to read in with
        Map< String, String > taxaMap; // map of taxa; set{ taxa } = sequence

        // create the scanner and the set
        taxaMap = new HashMap< String, String >();
        input = new Scanner( file );

        // read it in
        while( input.hasNextLine() ) {
            String line; // the current line of the file
            String taxa; // the name of the taxa
            String sequence; // sequence associated with this taxa

            line = input.nextLine();
            if ( line.equals( "" ) ||
                 line.startsWith( " " ) ||
                 line.startsWith( FILE_HEADER ) ) {
                // skip non-information
                continue;
            }

            // read in the line
            taxa = line.substring( 0, 
                                   line.indexOf( ' ' ) );
            sequence = line.substring( line.lastIndexOf( ' ' ) + 1 );

            // add it to the set, if it is a taxa
            addTaxa( taxa, sequence, taxaMap );
        }
        input.close();

        // now eliminate any positions that are not informative
        removeNonInformativePoints( taxaMap );

        return mapToList( taxaMap );
    }

    /**
     * Prints out the given map, for debugging purposes.
     * Items are in the following format:
     * key: value
     *
     * @param map The map to print out
     */
    public static < T, U > void printMap( Map< T, U > map ) {
        System.out.println( "Size: " + Integer.toString( map.keySet().size() ) );
        for( T key : map.keySet() ) {
            System.out.println( key.toString() + ": " +
                                map.get( key ).toString() );
        }
    }
}
