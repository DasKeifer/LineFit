package linefit.IO;

import java.util.ArrayList;


/** This interface should be used for classes and objects that have options that should be saved in the LineFit file.
 * These options may or may not be displayed to the user but should typically be able to be modified by the user either
 * directly or indirectly. It provides a set of standardized functions to read in an option from a file line by line and
 * to retrieve all the options and their settings as strings for saving them.
 * 
 * @author Das Keifer
 * @version 1.0
 * @since 0.99.0 */
public interface HasOptionsToSave
{
    /** Reads in the options associated with exporting in from the LineFit data file
     * 
     * @returns True if an export option was found in the passed line and False if the line did not contain an export
     * option */
    public boolean readInOption(String lineRead);

    /** Adds the names of the options as saved in the LineFit file and the values associated with them to the respective
     * passed ArrayLists
     * 
     * @param variableNames The ArrayList of the names of the options
     * @param variableValues The ArrayList of the values of the options (indexed matched to the names) */
    public void retrieveAllOptions(ArrayList<String> variableNames, ArrayList<String> variableValues);
}
