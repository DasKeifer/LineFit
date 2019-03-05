/* Copyright (C) 2013 Covenant College Physics Department
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


import java.util.ArrayList;
import java.util.Arrays;

import linefit.IO.ChangeTracker;


/** This class provides the columns for the GraphSet that are used to input and store data
 * 
 * @author Keith Rice
 * @version 2.0
 * @since &lt;0.98.0 */
public class DataColumn
{
    private ChangeTracker changeTracker;

    /** The name that is displayed and associated with this column */
    private String columnName;
    /** The List of all the data stored this column */
    private ArrayList<Double> data;

    /** The default constructor for this class
     * 
     * @param name The name of the data column
     * @param parentChangeTracker The change tracker to notify if a change in the data column occurred */
    public DataColumn(String name, ChangeTracker parentChangeTracker)
    {
        columnName = name;
        changeTracker = parentChangeTracker;
        data = new ArrayList<Double>();
    }

    /** Returns the number of data points with any null entries. For non-null values see getNonNullDataSize()
     * 
     * @return The size of the data in the column including any null rows */
    public int dataSize()
    {
        return data.size();
    }

    /** Returns the number of data points we have that have a value in them (not null)
     * 
     * @return Returns the number of filled rows we have in the current column */
    public int getNonNullDataSize()
    {
        int nonNull = 0;
        for (int i = 0; i < data.size(); i++)
        {
            if (!isNull(i))
            {
                nonNull++;
            }
        }
        return nonNull;
    }

    /** Checks if the row with the given index contains a null value
     * 
     * @param rowIndex the number of the row to check
     * @return Returns a boolean that represents whether or not the row was null, true meaning it was null */
    public boolean isNull(int rowIndex)
    {
        try
        {
            Object dataValue = data.get(rowIndex);
            if (dataValue == null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            return true;
        }
    }

    /** Writes the given value at the given row of our column and adds rows if it is farther then how many rows we
     * currently have
     * 
     * @param rowIndex The index of the row to write to
     * @param entry The Double to write into the row with the given index. This can be null. */
    void writeData(int rowIndex, Double entry)
    {
        // We have to use a string so we can catch the null exception
        while (rowIndex >= data.size())
        {
            data.add(null);
        }

        changeTracker.setFileModified();
        data.set(rowIndex, entry);
    }

    /** Reads the value from the row with the given index
     * 
     * @param rowIndex The index of the row to try and read a double from
     * @return Returns the double that was found at the given location or 0.0 if the value was null */
    public double readDouble(int rowIndex)
    {
        try
        {
            Object dataValue = data.get(rowIndex);
            if (dataValue == null)
            {
                return 0.0;
            }
            else
            {
                return (Double.parseDouble("" + dataValue));
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            return 0.0;
        }
    }

    /** Return the given GrapColumn with this GraphColumn's data and description copied into it
     * 
     * @param toCopy The GraphColumn to copy
     * @return Returns a copy of the given GraphColumn */
    public DataColumn copy(DataColumn toCopy)
    {
        toCopy.setData(this.getData());
        return toCopy;
    }

    // Getters
    /** Gets the name of this GraphColumn
     * 
     * @return Returns the String that is this GraphColumn's name */
    public String getName()
    {
        return columnName;
    }

    /** Gets an array list that contains the double values of all the rows in this column. This may have null values in
     * between other valid values
     * 
     * @return Returns an array with all the column's data in it */
    public Double[] getData()
    {
        return data.toArray(new Double[data.size()]);
    }

    /** Gets an array list that contains the double values of all the rows in this column with any null values removed
     * 
     * @return Returns an array with all the column's non-null data in it */
    public Double[] getDataNonNull()
    {
        return data.toArray(new Double[data.size()]);
    }

    /** Gets the value of the column at the passed index of null if there is no data in the passed index
     * 
     * @param index The index to retrieve the data at
     * @return Returns the value at the passed index or null if there is none */
    public Double getDataAt(int index)
    {
        try
        {
            return data.get(index);
        }
        catch (IndexOutOfBoundsException iobe)
        {
            return null;
        }
    }

    /** Gets the size/length of this DataColumn
     * 
     * @return Returns the size/length of this DataColumn */
    public int getDataSize()
    {
        return data.size();
    }

    // Setters
    /** Sets the data in the rows of this DataColumn to the values in the passed array
     * 
     * @param data The array of data to be put into the DataColumn */
    private void setData(Double[] data)
    {
        this.data = new ArrayList<>(Arrays.asList(data));
    }
}