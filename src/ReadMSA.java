/*
 * ReadMSA.java
 *
 * Version:
 *     $Id: ReadMSA.java,v 1.1 2009/10/09 01:44:47 kyle Exp $
 *
 * Revisions:
 *      $Log: ReadMSA.java,v $
 *      Revision 1.1  2009/10/09 01:44:47  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;
import java.io.*;

/**
 * Reads in a multiple sequence alignment (MSA) file.
 * Note that different programs have different output formats,
 * which is why this interface exists.
 *
 * @author Kyle Dewey
 */
public interface ReadMSA {
    /**
     * Reads in an MSA file.
     * 
     * @param file The file to open
     *
     * @return Listing of the taxa in the file
     *
     * @exception FileNotFoundException If the given file could not be opened
     * @exception IOException If an error occurred on reading the file
     */
    public List< PhylogeneticTreeItem > readMSAFile( File file ) 
        throws FileNotFoundException, IOException;
}
