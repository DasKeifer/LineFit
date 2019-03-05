/* Copyright (C) 2018 Covenant College Physics Department
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General License for more details.
 * 
 * You should have received a copy of the GNU Affero General License along with this program. If not, see
 * http://www.gnu.org/licenses/. */

package linefit.IO;


import java.util.ArrayList;


/** This interface should be used for classes and objects that have data that should be saved in the LineFit file. It
 * provides a set of standardized functions to read in data and/or options related to the data from a file line by line
 * and to retrieve all the data and the options related to the data and their settings as strings for saving them.
 * 
 * @author Keith Rice
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

    /** Performs any processing needed after all the data has been read in */
    public void finishedReadingInData();

    /** Retrieve all the data and options associated with the data in the passed in array lists
     * 
     * @param variableNames The ArrayList of the names of the options
     * @param variableValues The ArrayList of the values of the options (indexed matched to the names) */
    public void retrieveAllDataAndDataOptions(ArrayList<String> variableNames, ArrayList<String> variableValues);
}
