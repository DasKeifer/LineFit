package linefit.IO;

import java.util.ArrayList;


/** This interface should be used for classes and objects that have data that should be saved in the LineFit file. It
 * provides a set of standardized functions to read in data and/or options related to the data from a file line by line
 * and to retrieve all the data and the options related to the data and their settings as strings for saving them.
 * 
 * @author Das Keifer
 * @version 1.0
 * @since 0.99.0 */
public interface HasDataToSave
{
    /** Reads in data or an option related to the data from the passed in line
     * 
     * @param line The line that contains the data or option related to the data
     * @param newDataSet Signals that the line passed in is the beginning of a new data set
     * @return Returns true if the data or option for the data was read in from the line */
    public boolean readInDataAndDataOptions(String line, boolean newDataSet);

    /** Retrieve all the data and options associated with the data in the passed in array lists
     * 
     * @param variableNames The ArrayList of the names of the options
     * @param variableValues The ArrayList of the values of the options (indexed matched to the names) */
    public void retrieveAllDataAndDataOptions(ArrayList<String> variableNames, ArrayList<String> variableValues);
}
