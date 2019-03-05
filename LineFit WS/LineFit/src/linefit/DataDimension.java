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

package linefit;


/** The enum class that contains the different dimensions that LineFit uses as well as some convenience functions for
 * displaying them and converting them to numbers
 * 
 * @author Keith Rice
 * @version 1.0
 * @since 0.99.0 */
public enum DataDimension
{
    X("x"), Y("y");

    /** An internal class for allowing the enums to have integers and strings associated with them while being able to
     * easily determine how many are defined in this enum
     * 
     * @author Keith Rice
     * @version 1.0
     * @since 0.99.0 */
    private static final class StaticFields
    {
        static int numDimensions = 0;
    }

    /** The base String that will be used for displaying this DataDimension */
    private final String display;

    /** The additional character to use for error/uncertainty associated with this DataDimension */
    private final char errorChar = '\u03B4';

    /** An index-friendly number for this DatatDimension */
    private final int index;

    /** The constructor of this enum that is used to initialize its members and assign their values
     * 
     * @param displayString The name of the DataDimension that will be displayed in the GUI */
    DataDimension(String displayString)
    {
        this.display = displayString;
        index = StaticFields.numDimensions++;
    }

    /** Gets the number of DatatDimension defined in this enum
     * 
     * @return The number of DatatDimension defined */
    public static int getNumberOfDimensions()
    {
        return StaticFields.numDimensions;
    }

    /** Retrieves the DataDimension from its passed string representation
     * 
     * @param dimString The string representation of the data dimension
     * @return The DataDimension that the string represents or null if the passed string was not a valid string
     *         representation of a data dimension */
    public static DataDimension parseDim(String dimString)
    {
        for (DataDimension dim : DataDimension.values())
        {
            if (dimString.equals(dim.getDisplayString()))
            {
                return dim;
            }
        }
        return null;
    }

    /** Gets the display string of this DatatDimension for display in the GUI
     * 
     * @return Returns a String that contains this DatatDimension's display name */
    public String getDisplayString()
    {
        return display;
    }

    /** Gets the display string of this DatatDimension's error/uncertainty for display in the GUI
     * 
     * @return Returns a String that contains this DatatDimension's error/uncertainty display name */
    public String getErrorDisplayString()
    {
        return errorChar + display;
    }

    /** Gets the index-friendly number associated with this DataDimension for putting the dimensions and error values in
     * a data structure
     * 
     * @return Returns the index-friendly number associated with this DataDimension */
    public int getColumnIndex()
    {
        return index;
    }

    /** Gets the index-friendly number associated with this DataDimension error/uncertainty values for putting the
     * dimensions and error values in a data structure
     * 
     * @return Returns the index-friendly number associated with this DataDimension's error/uncertainty values */
    public int getErrorColumnIndex()
    {
        return index + StaticFields.numDimensions;
    }
}
