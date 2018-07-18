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

import linefit.IO.ChangeTracker;


/** This class provides the columns for the GraphSet that are used to input and store data
 * 
 * @author Unknown
 * @version 1.0
 * @since &lt;0.98.0 */
public class DataColumn
{
    private ChangeTracker changeTracker;

    /** The name that is displayed and associated with this column */
    private String columnName;
    /** The List of all the data stored this column */
    private ArrayList<Double> data;

    /** The default constructor for this class */
    public DataColumn(ChangeTracker parentChangeTracker)
    {
        changeTracker = parentChangeTracker;
        data = new ArrayList<Double>();
    }

    /** Creates a GraphColumn with the name "column " with the integer appended at the end
     * 
     * @param num the number one less than the number to use for the column number to account for zero indexing */
    public DataColumn(int num)
    {
        // this adds one so to account for zero indexing
        setColumnName("Column " + ++num);
        data = new ArrayList<Double>();
    }

    /** Returns the number of data points with or without null entries */
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
     * @param toWrite The String to write into the row with the given index */
    void writeData(int rowIndex, String toWrite)
    {
        // We have to use a string so we can catch the null exception
        while (rowIndex >= data.size())
        {
            data.add(null);
        }

        try
        {
            changeTracker.setFileModified();
            data.set(rowIndex, Double.parseDouble(toWrite));
        }
        catch (Exception e)
        {
            data.set(rowIndex, null);
        }
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
    /** Gets an array list that contains the double values of all the rows in this column
     * 
     * @return Returns an array with all the row's data in it */
    public ArrayList<Double> getData()
    {
        return data;
    }

    /** Gets the name of this GraphColumn
     * 
     * @return Returns the String that is this GraphColumn's name */
    public String getName()
    {
        return getColName();
    }

    // Setters
    /** Sets the data in the rows of this GraphColumn to the values in the passed array list
     * 
     * @param data The array list of data to be put into the Graph Column */
    public void setData(ArrayList<Double> data)
    {
        this.data = data;
    }

    /** Sets the name of this GraphColumn
     * 
     * @param name The name to use */
    public void setName(String name)
    {
        changeTracker.setFileModified();
        setColumnName(name);
    }

    /** Sets this GraphColumn's name to the given name
     * 
     * @param columnName Sets this column's name to the passed name */
    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }

    /** Gets this GraphColumn's name
     * 
     * @return Returns a String containing the name of this GraphColumn */
    public String getColName()
    {
        return columnName;
    }
}