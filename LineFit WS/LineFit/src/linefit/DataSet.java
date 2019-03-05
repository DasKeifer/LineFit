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


import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import linefit.FitAlgorithms.FitType;
import linefit.FitAlgorithms.FixedVariable;
import linefit.FitAlgorithms.LinearFitStrategy;
import linefit.IO.ChangeTracker;
import linefit.IO.HasDataToSave;


/** The class that keeps track of the data in each DataSet in the GraphArea. It contains the columns with the x,y, and
 * error/uncertainty value as well as the color and shape of the Set. Other names include: GraphDataSet, DataSet,
 * GraphSet
 * 
 * @author Keith Rice
 * @version 2.0
 * @since &lt;0.98.0 */
public class DataSet extends JScrollPane implements HasDataToSave
{
    /** The static variable that keeps track of the current number of GraphDataSets in the GraphArea. Used to determine
     * the number used for the next GraphDataSet */
    private static int numberOfGraphDataSets = 0;

    /** The Serial Version UID so that we know what version it is when we are using it. See
     * http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html for full discussion on its uses and purpose */
    private final static long serialVersionUID = 42L;
    /** The Default number of columns in each GraphDataSet when it is created. By Default it is two: one for the x
     * values and one for the y values */
    final static int DEFAULT_NUMBER_OF_COLUMNS = 2;
    /** The Default number or rows in each column in the GraphDataSet */
    final static int DEFAULT_NUMBER_OF_ROWS = 10;

    /** The object that keeps track of if any changes have been made */
    private ChangeTracker changeTracker;

    /** The table that contains and allows us to input data */
    private JTable tableContainingData;
    /** The model of the Table to use for inputting and storing data */
    DataSetTableModel dataTableModel;
    /** The model of the Table to use for inputting and storing data */
    DataSetTableListener dataTableListener;
    /** The name of this graph set to be displayed to the user */
    private String dataSetName;
    /** If the current GraphDataSet is visible and should be drawn to the GraphArea */
    public boolean visibleGraph;

    /** The list of all the DataColumns in this DataSet */
    final DataColumn[] dataColumns;
    /** The list of all the error DataColumns in this DataSet regardless of if they are visible or not */
    final DataColumn[] errorColumns;
    /** The number of error DataColumns that are displayed */
    int errorColumnsDisplayed = 0;
    /** The order of the error DataDimension/DataColumns */
    DataDimension[] errorColumnsOrder = DataDimension.values();

    /** The FitAlgrorithm we are using to fit this DataSet that also keeps track of the fit's data */
    public LinearFitStrategy linearFitStrategy; // TODO: encapsulate
    /** The currently selected FitType of this DataSet (i.e. no fit, x error fit) */
    private FitType dataSetFitType;
    /** The color of this DataSet when drawn to the GraphArea */
    private Color dataSetColor;
    /** The custom color of this DataSet */
    private Color dataSetCustomColor;
    /** The shape of this DataSet when drawn to the GraphArea */
    private Shape dataSetShape;

    /** Boolean to keep track of if the dataset is in the process of reading in data */
    private boolean inProcessesOfReading = false;
    /** The error columns in the file being read */
    private ArrayList<DataDimension> errorColumnsInFile = new ArrayList<DataDimension>();

    // TODO: Have a preferred fit type that was the last selected so that we can update it as the input data

    /** The predefined colors that are used for DataSets */
    public static final Color[] predefinedColors = new Color[] { Color.BLACK, Color.YELLOW, Color.BLUE, Color.GREEN,
            Color.ORANGE, Color.RED };
    /** The names associated with the predefined colors for DataSets */
    public static final String[] predefinedColorNames = new String[] { "black", "yellow", "blue", "green", "orange",
            "red" };

    /** Creates a new empty DataSet that is linked to the GraphArea
     * 
     * @param parentsChangeTracker The ChangeTracker that is notified when this DataSet changes
     * @param onUpdateFitTypesAction The function to call when this DataSet is updated */
    DataSet(ChangeTracker parentsChangeTracker, Runnable onUpdateFitTypesAction)
    {
        changeTracker = parentsChangeTracker;

        // Set the default column order
        dataSetFitType = FitType.NONE;
        visibleGraph = true;

        linearFitStrategy = LineFit.currentFitAlgorithmFactory.createNewLinearFitStartegy(this);

        dataSetName = "DataSet " + (numberOfGraphDataSets + 1); // +1 so its 1 based instead of 0 based
        dataColumns = new DataColumn[DataDimension.getNumberOfDimensions()];
        errorColumns = new DataColumn[DataDimension.getNumberOfDimensions()];
        dataTableModel = new DataSetTableModel();
        dataTableListener = new DataSetTableListener(onUpdateFitTypesAction);
        tableContainingData = new JTable(dataTableModel);
        tableContainingData.setGridColor(Color.gray);

        // Clean up JTable to make cell selection work more like excel
        tableContainingData.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        tableContainingData.setRowSelectionAllowed(true);
        tableContainingData.setColumnSelectionAllowed(true);
        tableContainingData.setCellSelectionEnabled(true);
        tableContainingData.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        setViewportView(tableContainingData);

        for (int i = 0; i < DEFAULT_NUMBER_OF_ROWS; i++)
        {
            dataTableModel.insertRow(dataTableModel.getRowCount(), new Object[0]);
        }

        for (DataDimension dim : DataDimension.values())
        {
            dataColumns[dim.getColumnIndex()] = new DataColumn(dim.getDisplayString(), changeTracker);
            errorColumns[dim.getColumnIndex()] = new DataColumn(dim.getErrorDisplayString(), changeTracker);
        }

        for (int i = 0; i < DataDimension.getNumberOfDimensions(); i++)
        {
            dataTableModel.addColumn(dataColumns[i].getName());
        }

        dataTableModel.addTableModelListener(dataTableListener);

        tableContainingData.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false), "MY_CUSTOM_ACTION");
        dataSetColor = Color.BLACK;
        dataSetCustomColor = dataSetColor;
        dataSetShape = new Rectangle2D.Double();

        numberOfGraphDataSets++;

        // Add our table listener for this DataSet
        new SpreadSheetAdapter(tableContainingData);
    }

    /** A private constructor for an empty DataSet that is only used to make a placeholder DataSet */
    private DataSet()
    {
        dataColumns = new DataColumn[0];
        errorColumns = new DataColumn[0];
    }

    /** Returns an empty DataSet with no initialization to be used for the new DataSet option in the drop down menu
     * 
     * @param displayed The String to display on the DataSet drop down placeholder object
     * @return Returns a DataSet object to be put in the DataSetSelector to hold the place for creating a new DataSet */
    static DataSet createDropDownPlaceHolder(String displayed)
    {
        DataSet placeHolder = new DataSet();
        placeHolder.dataSetName = displayed;
        return placeHolder;
    }

    /** Sets the error/uncertainty column order to the passed order. All dimensions must be present in the passed array
     * or else the order will not be updated.
     * 
     * @param columnOrder The order to use for the error/uncertainty columns
     * @return true if the order was set successfully, false if it wasn't */
    public boolean setErrorColumnOrder(DataDimension[] columnOrder)
    {
        if (columnOrder.length != DataDimension.getNumberOfDimensions())
        {
            System.err.println("setErrorColumnOrder: Incorrect number of DataDimension passed!");
            return false;
        }

        for (DataDimension dim : DataDimension.values())
        {
            boolean found = false;
            for (DataDimension passedDim : columnOrder)
            {
                if (passedDim == dim)
                {
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                System.err.println("setErrorColumnOrder: Dimension " + dim.getDisplayString() +
                        " was not found in passed order!");
                return false;
            }
        }

        int numCurrentlyDisplayed = errorColumnsDisplayed;
        dataTableListener.setListenerEnabled(false); // prevents some unnecessary recalculation
        while (errorColumnsDisplayed > 0)
        {
            hideLastErrorColumn();
        }

        errorColumnsOrder = columnOrder;

        while (errorColumnsDisplayed < numCurrentlyDisplayed)
        {
            showNextErrorColumn();
        }
        dataTableListener.setListenerEnabled(true);

        // Only update if we removed and readded some columns
        if (numCurrentlyDisplayed > 0)
        {
            dataTableListener.signalDataChanged();
        }

        return true;
    }

    /** Gets the index of the error column of the passed DataDimension
     * 
     * @param toGetErrorIndexOf The DataDimension error/uncertainty values to get the index of
     * @param relativeToErrors true if the index returned should be relative to the error/uncertainty columns or false
     *        if it should be relative to all columns (both the data and the errors/uncertainties)
     * @return The index for the error DataColumn of the passed dimension */
    public int getIndexOfErrorColumn(DataDimension toGetErrorIndexOf, boolean relativeToErrors)
    {
        for (int i = 0; i < errorColumnsOrder.length; i++)
        {
            if (errorColumnsOrder[i] == toGetErrorIndexOf)
            {
                if (relativeToErrors)
                {
                    return i;
                }
                else
                {
                    return dataColumns.length + i;
                }
            }
        }

        return -1;
    }

    /** Sets the number of visible columns (including data and error/uncertainty columns) to the passed number
     * 
     * @param numColumns The number of columns that should be visible
     * @return True if successful, false if the passed number of columns is invalid */
    public boolean setNumberOfDisplayedColumns(int numColumns)
    {
        int errorColums = numColumns - DataDimension.getNumberOfDimensions();
        if (errorColums < 0 || errorColums > DataDimension.getNumberOfDimensions())
        {
            return false;
        }
        else
        {
            int diffColumns = errorColums - errorColumnsDisplayed;
            boolean addColumns = diffColumns > 0;
            for (int i = 0; i < Math.abs(diffColumns); i++)
            {
                if (addColumns)
                {
                    showNextErrorColumn();
                }
                else
                {
                    hideLastErrorColumn();
                }
            }

            return true;
        }
    }

    /** Shows the next error column that should be displayed based on the currently set order for error dimensions if
     * there are more columns to show */
    public void showNextErrorColumn()
    {
        if (errorColumnsDisplayed < DataDimension.getNumberOfDimensions())
        {
            int tableIndex = dataColumns.length + errorColumnsDisplayed;
            DataDimension toAdd = errorColumnsOrder[errorColumnsDisplayed];
            DataColumn error = errorColumns[toAdd.getColumnIndex()];

            // increment first so the values are the new values when the listeners are called
            errorColumnsDisplayed++;
            dataTableModel.addColumn(error.getName());

            if (toAdd.getErrorColumnIndex() < tableIndex)
            {
                tableContainingData.moveColumn(tableIndex, toAdd.getErrorColumnIndex());
            }

            for (int i = 0; i < error.dataSize(); i++)
            {
                if (!error.isNull(i))
                {
                    dataTableModel.setValueAt(error.readDouble(i), i, tableIndex);
                }
            }
        }
    }

    /** Hides the last displayed error column if there is at least one error column to hide */
    public void hideLastErrorColumn()
    {
        if (errorColumnsDisplayed > 0)
        {
            // decrement first so the values are the new values when the listeners are called
            errorColumnsDisplayed--;
            dataTableModel.removeLastColumn();
        }
    }

    /** Updates the available FitTypes we can use on this DataSet based on the amount of data in them
     * 
     * @return Returns the array list of the fit types that can be used for this dataset */
    ArrayList<FitType> getAllowableFits()
    {
        ArrayList<FitType> fits = new ArrayList<FitType>();
        fits.add(FitType.NONE);

        ArrayList<Integer> validPoints = getIndexesOfValidPoints();
        if (validPoints.size() > 1)
        {
            fits.add(FitType.REGULAR);

            ArrayList<DataDimension> validDims = new ArrayList<DataDimension>();
            for (DataDimension dim : DataDimension.values())
            {
                if (isErrorDataVisible(dim) && checkAllHaveErrors(validPoints, dim))
                {
                    validDims.add(dim);
                }
            }

            FitType.appendAllAllowedFitsForErrorDimensions(fits, validDims);
        }

        return fits;
    }

    /** Recalculates the FitData with our current FitType and data */
    void refreshFitData()
    {
        linearFitStrategy.refreshFitData();
    }

    /** returns how many rows have valid points, meaning that they have both x and y data for
     * 
     * @return The number of points containing at least an x and a y value in this DataSet */
    private ArrayList<Integer> getIndexesOfValidPoints()
    {
        ArrayList<Integer> validPoints = new ArrayList<Integer>();
        boolean pointValid;
        for (int i = 0; i < dataColumns[0].getDataSize(); i++)
        {
            pointValid = true;
            for (int column = 0; column < DataDimension.getNumberOfDimensions(); column++)
            {
                if (dataColumns[column].isNull(i))
                {
                    pointValid = false;
                    break;
                }
            }

            if (pointValid)
            {
                validPoints.add(i);
            }
        }

        return validPoints;
    }

    /** Returns the min and max values of the passed data dimension taking into account errors if told to do so
     * 
     * @param dim The dimension to get the min and max values of
     * @param withErrors True if the min and max values should include the error values of the passed dimension
     * @return An array containing the min value at 0 and the max at 1 */
    public double[] getMinMax(DataDimension dim, boolean withErrors)
    {
        boolean hasInit = false;
        double dataMax = 0;
        double dataMin = 0;

        refreshFitData();
        DataColumn data = dataColumns[dim.getColumnIndex()];
        DataColumn error = errorColumns[dim.getColumnIndex()];

        for (int i = 0; i < data.getDataSize(); i++)
        {
            if (!data.isNull(i))
            {
                double tmp = data.readDouble(i);
                double tmpErr = 0;
                if (withErrors && !error.isNull(i))
                {
                    tmpErr = Math.abs(error.readDouble(i));
                }

                if (hasInit)
                {
                    if (tmp + tmpErr > dataMax)
                    {
                        dataMax = tmp + tmpErr;
                    }
                    else if (tmp - tmpErr < dataMin)
                    {
                        dataMin = tmp - tmpErr;
                    }
                }
                else
                {
                    dataMax = tmp + tmpErr;
                    dataMin = tmp - tmpErr;
                    hasInit = true;
                }
            }
        }

        return new double[] { dataMin, dataMax };
    }

    /** Checks if all the points at the specified indexes in the passed dimension has an associated error/uncertainty
     * value
     * 
     * @param indexes The list of the indexes to check for error values at
     * @param dimension The data dimension to check the errors for
     * @return True if all the points at the passed indexes have an error/uncertainty associated with them for the
     *         passed dimension and false otherwise */
    private boolean checkAllHaveErrors(ArrayList<Integer> indexes, DataDimension dimension)
    {
        DataColumn error = errorColumns[dimension.getColumnIndex()];

        for (Integer index : indexes)
        {
            if (error.isNull(index))
            {
                return false;
            }
        }

        return true;
    }

    /** Determines whether or not their is data in this DataSet
     * 
     * @return Returns true if there is data and false if no data was found */
    public boolean hasData()
    {
        return dataTableModel.hasData();
    }

    /** Reads in data or an option related to the data from the passed in line
     * 
     * @param line The line that contains the data or option related to the data
     * @param unused Unused parameter required for the HasDataToSave interface
     * @return Returns true if the data or option for the data was read in from the line */
    public boolean readInDataAndDataOptions(String line, boolean unused)
    {
        // Enusre we know that we are reading in the data
        inProcessesOfReading = true;

        // now split the input into the two parts
        // we can't use split because it will mess up on names as well as points since they have multiple spaces
        int firstSpaceIndex = line.indexOf(' ');
        String field = line.substring(0, firstSpaceIndex).toLowerCase();
        String valueForField = line.substring(firstSpaceIndex + 1).toLowerCase();

        boolean found = true;
        try
        {
            switch (field)
            {
                case "colnum":
                case "numberofcolumns":
                {
                    // No longer used - required for old versions though
                    int numCols = Integer.parseInt(valueForField);
                    setNumberOfDisplayedColumns(numCols);
                    break;
                }
                case "fittype":
                {
                    // loop through all the fit types checking them against their toString methods
                    boolean foundFitType = false;
                    for (FitType ft : FitType.values())
                    {
                        if (valueForField.equals(ft.toString().toLowerCase()))
                        {
                            foundFitType = true;
                            this.setFitType(ft);
                            break;
                        }
                    }
                    // if we didn't find it for whatever strange reason, default to none
                    if (!foundFitType)
                    {
                        this.setFitType(FitType.NONE);
                        System.err.println("Error reading fit type - Defaulting to None - Continuing: " + line);
                    }
                    break;
                }
                case "whatisfixed":
                {
                    // loop though all the fixed variables checking them against the toString methods
                    boolean foundFixedVariable = false;
                    for (FixedVariable fv : FixedVariable.values())
                    {
                        if (valueForField.equals(fv.toString().toLowerCase()))
                        {
                            linearFitStrategy.setWhatIsFixed(fv, linearFitStrategy.getFixedValue());
                            foundFixedVariable = true;
                            break;
                        }
                    }
                    // if we didnt find anyone just default to none
                    if (!foundFixedVariable)
                    {
                        linearFitStrategy.setWhatIsFixed(FixedVariable.NONE, linearFitStrategy.getFixedValue());
                    }
                    break;
                }

                case "fixedvalue":
                    linearFitStrategy.setWhatIsFixed(linearFitStrategy.getWhatIsFixed(), Double.parseDouble(
                            valueForField));
                    break;
                case "visible":
                    visibleGraph = valueForField.toLowerCase().equals("true");
                    break;
                case "shape":
                {
                    if (valueForField.equals("rectangle"))
                    {
                        setShape(new Rectangle2D.Double());
                    }
                    else if (valueForField.equals("circle"))
                    {
                        setShape(new Ellipse2D.Double());
                    }
                    else
                    {
                        setShape(new Polygon());
                    }
                    break;
                }
                case "color":
                {
                    boolean foundColor = false;
                    for (int i = 0; i < predefinedColorNames.length; i++)
                    {
                        if (valueForField.equals(predefinedColorNames[i]))
                        {
                            setColor(predefinedColors[i]);
                            foundColor = true;
                            break;
                        }
                    }

                    if (!foundColor)
                    {
                        String[] colorInputExploded = valueForField.split(" ");
                        if (colorInputExploded.length == 3)
                        {
                            // get the rgb as ints and set up the color
                            try
                            {
                                int red = Integer.parseInt(colorInputExploded[0]);
                                int green = Integer.parseInt(colorInputExploded[1]);
                                int blue = Integer.parseInt(colorInputExploded[2]);
                                setColor(new Color(red, green, blue));
                            }
                            catch (NumberFormatException e)
                            {
                                setColor(Color.BLACK);
                            }
                        }
                        else
                        {
                            setColor(Color.BLACK);
                        }
                    }
                    break;
                }
                case "errordims":
                    errorColumnsInFile.clear();
                    String[] splitDimValuesInput = valueForField.split(" ");
                    int columnCount = 0;
                    boolean foundDim;
                    for (int i = 0; i < errorColumnsOrder.length; i++)
                    {
                        foundDim = false;
                        for (int j = 0; j < splitDimValuesInput.length; j++)
                        {
                            if (DataDimension.parseDim(splitDimValuesInput[j]) == errorColumnsOrder[i])
                            {
                                foundDim = true;
                                columnCount = i;
                            }
                        }

                        if (!foundDim)
                        {
                            errorColumnsInFile.add(errorColumnsOrder[i]);
                        }
                    }

                    setNumberOfDisplayedColumns(DataDimension.getNumberOfDimensions() + columnCount + 1);
                    break;
                case "colname":
                    break; // we don't use this anymore but we don't want to cause errors when reading old files in
                case "coldesc":
                    break; // we don't use this anymore but we don't want to cause errors when reading old files in
                case "p":
                case "datapoint":
                {
                    // Temporarily disable the dataset table listener to prevent it from updating prematurely
                    dataTableListener.setListenerEnabled(false);

                    // split it up into the separate string parts
                    String[] splitPointValuesInput = valueForField.split(" ");

                    // Reads should only take place when a set is created so we
                    // can just use the data size of the first column to determine
                    // the next row to add at.
                    int row = dataColumns[0].dataSize();
                    int nextErrorIdx = 0;
                    for (int column = 0; column < splitPointValuesInput.length; column++)
                    {
                        String pointValueString = splitPointValuesInput[column];

                        Double value = null;

                        if (!pointValueString.equals("null"))
                        {
                            value = Double.parseDouble(pointValueString);
                        }
                        try
                        {
                            if (column < DataDimension.getNumberOfDimensions())
                            {
                                dataColumns[column].writeData(row, value);
                            }
                            else
                            {
                                // Check and get the next unskipped error
                                // For each skipped error, check if the next column is this one
                                for (int skip = 0; skip < errorColumnsInFile.size(); skip++)
                                {
                                    if (errorColumnsInFile.get(skip).getColumnIndex() == nextErrorIdx)
                                    {
                                        nextErrorIdx++;
                                    }
                                }
                                errorColumns[nextErrorIdx++].writeData(row, value);
                            }
                            dataTableModel.setValueAt(value, row, column);
                        }
                        catch (IndexOutOfBoundsException iobe)
                        {
                            System.err.println(
                                    "Error reading in DataPoint - More values specified than columns in data model - Continuing: " +
                                            line);
                            break;
                        }
                    }
                    // Don't forget to reenable the dataset table listener
                    dataTableListener.setListenerEnabled(true);
                    break;
                }
                default:
                    found = false;
                    break;
            }

        }
        catch (NumberFormatException nfe)
        {
            JOptionPane.showMessageDialog(this, "Error reading in number from line: " + line, "NFE Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return found;
    }

    /** Performs any processing needed after all the data has been read in */
    public void finishedReadingInData()
    {
        if (inProcessesOfReading)
        {
            // Clean up our temporary read in data
            inProcessesOfReading = false;
            errorColumnsInFile.clear();
        }
    }

    /** Retrieve all the data and options associated with the data in the passed in array lists
     * 
     * @param variableNames The ArrayList of the names of the options
     * @param variableValues The ArrayList of the values of the options (indexed matched to the names) */
    public void retrieveAllDataAndDataOptions(ArrayList<String> variableNames, ArrayList<String> variableValues)
    {
        if (dataTableModel.hasData())
        {
            variableNames.add("FitType");
            variableValues.add(dataSetFitType.toString());
            variableNames.add("WhatIsFixed");
            if (linearFitStrategy.getWhatIsFixed() == FixedVariable.SLOPE)
            {
                variableValues.add("slope");
            }
            else if (linearFitStrategy.getWhatIsFixed() == FixedVariable.INTERCEPT)
            {
                variableValues.add("intercept");
            }
            else
            {
                variableValues.add("none");
            }

            variableNames.add("FixedValue");
            variableValues.add(Double.toString(linearFitStrategy.getFixedValue()));
            variableNames.add("Visible");
            variableValues.add(Boolean.toString(visibleGraph));
            variableNames.add("Shape");
            variableValues.add(getShapeString());
            variableNames.add("Color");
            variableValues.add(getColorString());

            if (errorColumnsDisplayed > 0)
            {
                variableNames.add("ErrorDims");
                String errorDims = "";
                for (DataDimension dim : DataDimension.values())
                {
                    for (int i = 0; i < errorColumnsDisplayed; i++)
                    {
                        if (dim == errorColumnsOrder[i])
                        {
                            errorDims += dim.getDisplayString() + " ";
                        }
                    }
                }
                variableValues.add(errorDims);

            }

            String datapoint;
            ArrayList<Integer> indexes = getIndexesOfValidPoints();
            for (Integer index : indexes)
            {
                datapoint = "";
                variableNames.add("DataPoint");
                for (int j = 0; j < DataDimension.getNumberOfDimensions(); j++)
                {
                    if (j > 0)
                    {
                        datapoint += " ";
                    }
                    datapoint += dataColumns[j].getDataAt(index);
                }

                // For each data dimension (in order)
                for (int j = 0; j < DataDimension.getNumberOfDimensions(); j++)
                {
                    // If we find its index in the columns being displayed, then add it to the line
                    for (int k = 0; k < errorColumnsDisplayed; k++)
                    {
                        if (errorColumnsOrder[k].getColumnIndex() == j)
                        {
                            datapoint += " " + errorColumns[j].getDataAt(index);
                        }
                    }
                }

                variableValues.add(datapoint);
            }
        }
    }

    /** Returns the DataSet's name */
    public String toString()
    {
        return dataSetName;
    }

    // getters and setters
    /** Returns the Chi squared value for this dataset's current fit
     * 
     * @return The Chi squared as a double */
    public double getChiSquared()
    {
        return this.linearFitStrategy.calculateChiSquared(this.linearFitStrategy.getSlope(), this.linearFitStrategy
                .getIntercept());
    }

    /** Gets the shape this DataSet as a String is using when being draw to the GraphArea
     * 
     * @return A String representing the shape of the points in this DataSet */
    public String getShapeString()
    {
        String output = "";

        Rectangle2D.Double rect = new Rectangle2D.Double();
        Ellipse2D.Double circ = new Ellipse2D.Double();
        Polygon tri = new Polygon();

        if (dataSetShape.getClass() == rect.getClass())
        {
            output = "rectangle";
        }
        else if (dataSetShape.getClass() == circ.getClass())
        {
            output = "circle";
        }
        else if (dataSetShape.getClass() == tri.getClass())
        {
            output = "triangle";
        }
        else
        {
            output = "rectangle";
        }
        return output;
    }

    /** Gets the color of this DataSet as a String that it is drawn with on the GraphArea
     * 
     * @return A String representing this DataSet's color */
    public String getColorString()
    {
        for (int i = 0; i < predefinedColorNames.length; i++)
        {
            if (dataSetColor == predefinedColors[i])
            {
                return predefinedColorNames[i];
            }
        }

        return dataSetColor.getRed() + " " + dataSetColor.getGreen() + " " + dataSetColor.getBlue();
    }

    /** Checks to see if the passed color is a custom color or one of the default colors provided
     * 
     * @param color The color to check to see if it is a custom color or not
     * @return true if it is a custom color, false if it is a predefined color */
    public static boolean isColorACustomColor(Color color)
    {
        for (int i = 0; i < predefinedColorNames.length; i++)
        {
            if (color == predefinedColors[i])
            {
                return false;
            }
        }

        return true;
    }

    /** Checks to see if the dataset is using a custom color or a predefined color
     * 
     * @return true if the dataset is using a custom color. False if it uses a predefined color */
    public boolean isColorCustom()
    {
        return isColorACustomColor(dataSetColor);
    }

    /** Gets the number of columns that are currently being displayed. This includes both the data columns and any error
     * columns
     * 
     * @return The number of displayed columns */
    public int getNumberOfDisplayedColumns()
    {
        return dataColumns.length + errorColumnsDisplayed;
    }

    /** Gets the current Color that is being used by this DataSet
     * 
     * @return The Color that is being used to draw this DataSet */
    public Color getColor()
    {
        return dataSetColor;
    }

    /** Gets the last custom color that was used for this dataset
     * 
     * @return The last custom color that was selected for this dataset */
    public Color getLastCustomColor()
    {
        return dataSetCustomColor;
    }

    /** Gets the shape of the points used when drawing to the GraphArea
     * 
     * @return The Shape to use when drawing this DataSet to the GraphArea */
    public Shape getShape()
    {
        return dataSetShape;
    }

    /** Gets the Table that contains the data for this DataSet
     * 
     * @return The Table containing this DataSet's data */
    public JTable getDataTable()
    {
        return tableContainingData;
    }

    /** Gets the name of this DataSet
     * 
     * @return The String containing this DataSet's name */
    public String getName()
    {
        return dataSetName;
    }

    /** Gets the FitType that this DataSet is using
     * 
     * @return The FitType this DataSet is using */
    public FitType getFitType()
    {
        return dataSetFitType;
    }

    /** Checks to see if the passed index is an error column or a data column. The passed index must be the "displayed"
     * index which is the index as it appears in the GUI (i.e. includes both data columns and error columns)
     * 
     * @param columnIndex The index to check
     * @return true if the passed index corresponds to an error index. False if it is a data column */
    boolean isIndexDisplayedErrorColumn(int columnIndex)
    {
        return columnIndex >= DataDimension.getNumberOfDimensions();
    }

    /** Converts the passed "displayed" error column index (index as it appears in the display including the data and
     * error columns) into the internal error column index for accessing the internal data structure
     * 
     * @param columnIndex The "displayed" index to convert into the internal index for accessing data structures
     * @return Returns the internal error index of the passed "displayed" index for accessing data structures */
    private int convertErrorIndexDisplayedToInternal(int columnIndex)
    {
        return errorColumnsOrder[columnIndex - DataDimension.getNumberOfDimensions()].getColumnIndex();
    }

    /** Converts the passed internal error column index into the "displayed" error column index (index as it appears in
     * the display including the data and error columns)
     * 
     * @param columnIndex The internal index to convert into the "displayed" index for showing in the GUI
     * @return Returns the "displayed" error index of the passed internal for showing in the GUI */
    private int convertErrorIndexInternalToDisplayed(int columnIndex)
    {
        for (int i = 0; i < errorColumnsOrder.length; i++)
        {
            if (columnIndex == errorColumnsOrder[i].getColumnIndex())
            {
                return i;
            }
        }

        // shouldn't really ever happen - a developer issue
        System.err.println(
                "Warning: Matching displayed index doesn't match - most likely the index passed is larger than the number of error columns");
        return -1;
    }

    /** Gets the DataColumn (data or error) at the passed "displayed" index
     * 
     * @param columnIndex The "displayed" index to get the DataColumn of
     * @return The DataColumn at the passed index */
    private DataColumn getDisplayedColumn(int columnIndex)
    {
        if (columnIndex < DataDimension.getNumberOfDimensions())
        {
            return dataColumns[columnIndex];
        }
        else if (columnIndex < getNumberOfDisplayedColumns())
        {
            return errorColumns[convertErrorIndexDisplayedToInternal(columnIndex)];
        }
        else // shouldn't really ever happen - a developer issue
        {
            System.err.println("Warning: Index passed (" + columnIndex +
                    ") is larger than the number of currently displayed columns (" + getNumberOfDisplayedColumns() +
                    ")");
            return null;
        }
    }

    /** Gets an array of the data or errors/uncertainties at the passed "displayed" index. This array may contain null
     * values
     * 
     * @param columnIndex The "displayed" index to get the data column of
     * @return An array of Double containing the data in the column at the passed index (potentially with null
     *         values) */
    public Double[] getDisplayedData(int columnIndex)
    {
        try
        {
            return getDisplayedColumn(columnIndex).getData();
        }
        catch (NullPointerException npe) // shouldn't really ever happen - a developer issue
        {
            return new Double[0];
        }
    }

    /** Gets an array of the data (non-error data) at the passed index. This array may contain null values
     * 
     * @param index The index of the data column to get the data of
     * @return An array of Double containing the data in the column at the passed index (potentially with null
     *         values) */
    private Double[] getData(int index)
    {
        try
        {
            return dataColumns[index].getData();
        }
        catch (IndexOutOfBoundsException iobe) // shouldn't really ever happen - a developer issue
        {
            System.err.println("Warning: Index passed is larger than the number of non-error columns");
            return new Double[0];
        }
    }

    /** Gets an array of the data of the passed dimension. This array may contain null values
     * 
     * @param dim The dimension to get the data of
     * @return An array of Double containing the data for the passed dimension (potentially with null values) */
    public Double[] getData(DataDimension dim)
    {
        return getData(dim.getColumnIndex());
    }

    /** Gets the length of the data at the passed dimension. This includes any null values in between non-null values.
     * Or to put another way, the 1 based row index of the last populated value for this dimension
     * 
     * @param dim The dimension to get the length of the data of
     * @return The length of the data in the passed dimension */
    public int getDataSize(DataDimension dim)
    {
        return dataColumns[dim.getColumnIndex()].getDataSize();
    }

    /** Gets an array of the error/uncertainty values for the passed dimension. This array may contain null values. If
     * the errors/uncertainties for the passed dimension are not displayed, then it returns an empty array
     * 
     * @param dim The dimension to get the error/uncertainty values of
     * @return An array of Double containing the error/uncertainty values for the passed dimension (potentially with
     *         null values) or null if the error for the passed dimension is not displayed */
    public Double[] getErrorData(DataDimension dim)
    {
        if (convertErrorIndexInternalToDisplayed(dim.getColumnIndex()) < errorColumnsDisplayed)
        {
            return errorColumns[dim.getColumnIndex()].getData();
        }
        return new Double[0];
    }

    /** Gets the length of the error/uncertainty values at the passed dimension. This includes any null values in
     * between non-null values. Or to put another way, the 1 based row index of the last populated value for this
     * error/uncertainty data associated with the passed dimension
     * 
     * @param dim The dimension to get the length of the error/uncertainty values of
     * @return The length of the error/uncertainty values in the passed dimension */
    public int getErrorDataSize(DataDimension dim)
    {
        if (convertErrorIndexInternalToDisplayed(dim.getColumnIndex()) < errorColumnsDisplayed)
        {
            return errorColumns[dim.getColumnIndex()].getDataSize();
        }
        return 0;
    }

    /** Checks if the error/uncertainty values for the passed dimension are displayed/visible
     * 
     * @param dim The dimension to check if the error/uncertainty values are visible for
     * @return True if the error/uncertainty for the passed dimension are visible and false otherwise */
    public boolean isErrorDataVisible(DataDimension dim)
    {
        return convertErrorIndexInternalToDisplayed(dim.getColumnIndex()) < errorColumnsDisplayed;
    }

    /** Gets all the data (potentially including null values)in the DataSet including the error values if specified. If
     * getting all the data including errors is specified, then any dimension whose errors are not displayed will have
     * an empty array at the respective index in the returned data.
     * 
     * The returned data is a 2-D array of each of the dimensions' data followed by the error values of each of the
     * dimensions in the same order. For example:
     * 
     * [Dimesnion1Data, Dimension2Data, Dimension1Error, Dimension2Error]
     * 
     * @param withErrors True if the data should also contain the error/uncertainty values for the columns
     * @return A 2-D array containing the data for this dataset as specified in the description */
    public Double[][] getAllData(boolean withErrors)
    {
        ArrayList<Double[]> allData = new ArrayList<Double[]>();

        // Add all the data columns
        int longest = 0;
        for (DataDimension dim : DataDimension.values())
        {
            allData.add(getData(dim));
            if (getDataSize(dim) > longest)
            {
                longest = getDataSize(dim);
            }
        }

        // and the error columns
        if (withErrors)
        {
            for (DataDimension dim : DataDimension.values())
            {
                allData.add(getErrorData(dim));
                if (getErrorDataSize(dim) > longest)
                {
                    longest = getErrorDataSize(dim);
                }
            }
        }

        // Make a "square" 2d array so they are all the same length to make processing easier
        int index = 0;
        Double[][] alignedData = new Double[allData.size()][longest];
        for (Double[] column : allData)
        {
            System.arraycopy(column, 0, alignedData[index], 0, column.length);
            index++;
        }

        return alignedData;
    }

    /** Gets all the data of all the valid points in the DataSet including the error values if specified. If getting all
     * the data including errors is specified, then any dimension whose errors are not displayed or are not set will
     * have the array at the respective index set to null in the returned data.
     * 
     * The returned data is a 2-D array of each of the dimensions' data followed by the error values of each of the
     * dimensions in the same order. For example:
     * 
     * [Dimesnion1Data, Dimension2Data, Dimension1Error, Dimension2Error]
     * 
     * @param withErrors True if the data should also contain the error/uncertainty values for the columns
     * @return A 2-D array containing the data for this dataset as specified in the description. Any non-specified error
     *         values are set to null */
    public Double[][] getAllValidPointsData(boolean withErrors)
    {
        ArrayList<Integer> validPoints = getIndexesOfValidPoints();

        Double[][] data = getValidPoints_Data(validPoints);

        if (withErrors)
        {
            Double[][] allData = new Double[dataColumns.length + errorColumns.length][];
            Double[][] errorData = getValidPoints_Errors(validPoints);

            // Ensure the validity of the data
            if (errorData[0].length != data[0].length)
            {
                // If there are fewer points in the errors, we need to re-get the data as well with the fewer points
                data = getValidPoints_Data(validPoints);
            }

            // Add the data then the errors to the full data value
            int colIdx = 0;
            for (Double[] dataCol : data)
            {
                allData[colIdx++] = dataCol;
            }

            for (Double[] dataCol : errorData)
            {
                allData[colIdx++] = dataCol;
            }
            return allData;
        }
        else
        {
            return data;
        }
    }

    /** Gets all the data of all the valid points in the DataSet
     * 
     * The returned data is a 2-D array of each of the dimensions' data For example:
     * 
     * [Dimesnion1Data, Dimension2Data]
     * 
     * @param validPoints The list of thus far valid points to get the data for. Any point that was detected as invalid
     *        by this function will be removed from the list
     * 
     * @return A 2-D array containing the data for this dataset as specified in the description */
    private Double[][] getValidPoints_Data(ArrayList<Integer> validPoints)
    {
        Double[][] data = new Double[dataColumns.length][];
        int columnIdx = 0;

        int numPoints = validPoints.size();
        Iterator<Integer> pointsIter;
        Integer pointIdx;
        boolean failedToFindPoint = false;

        int validIndex = 0;
        Double[] validData;

        // add the data columns
        Double[] columnData;
        for (DataDimension dim : DataDimension.values())
        {
            validIndex = 0;
            validData = new Double[numPoints];
            columnData = getData(dim);

            pointsIter = validPoints.iterator();
            while (pointsIter.hasNext())
            {
                pointIdx = pointsIter.next();
                if (pointIdx >= columnData.length)
                {
                    // This should never occur!
                    System.err.println(
                            "Failed to retreive point information for a point that was returned as valid at row " +
                                    pointIdx + ". Removing it from the returned data");
                    pointsIter.remove();
                    failedToFindPoint = true;
                }
                validData[validIndex] = columnData[pointIdx];
                validIndex++;
            }

            // If we found the point fine, add it to the list
            if (!failedToFindPoint)
            {
                data[columnIdx++] = validData;
            }
            else
            {
                break;
            }
        }

        // If we found all the points successfully, then return the data
        if (!failedToFindPoint)
        {
            return data;
        }
        // If we failed call ourselves again with the trimmed down points list
        else
        {
            return getValidPoints_Data(validPoints);
        }
    }

    /** Gets all the error data of all the valid points in the DataSet including the error values if specified. Any
     * dimension whose errors are not displayed or are not set will have the array at the respective index set to null
     * in the returned data.
     * 
     * The returned data is a 2-D array of each of the dimensions' error data. For example:
     * 
     * [Dimension1Error, Dimension2Error]
     * 
     * @param validPoints The list of thus far valid points to get the error data for. Any point that was detected as
     *        invalid by this function will be removed from the list
     * 
     * @return A 2-D array containing the erro data for this dataset as specified in the description. Any non-specified
     *         error values are set to null */
    private Double[][] getValidPoints_Errors(ArrayList<Integer> validPoints)
    {
        Double[][] data = new Double[dataColumns.length][];
        int columnIdx = 0;

        int numPoints = validPoints.size();
        Iterator<Integer> pointsIter;
        Integer pointIdx;
        boolean failedToFindPoint = false;

        int validIndex = 0;
        Double[] validData;

        // add the data columns
        Double[] columnData;

        // and the error columns
        DataDimension[] requiredDims = FitType.getRequiredErrorDimsForFitType(dataSetFitType);
        boolean isRequired = false;

        // Use values() to keep the order consistent regardless of how its
        // being displayed currently
        for (DataDimension dim : DataDimension.values())
        {
            isRequired = false;
            // First determine if it is required for the fit type and if so ensure it is displayed
            for (DataDimension requiredDim : requiredDims)
            {
                if (dim == requiredDim)
                {
                    // This should never occur - return an array of empty arrays
                    if (!isErrorDataVisible(dim))
                    {
                        System.err.println("Required error data for fit type " + 1 + " is not visible!");
                        for (int colIdx = 0; colIdx < data.length; colIdx++)
                        {
                            data[colIdx] = new Double[0];
                        }
                        validPoints.clear();
                        return data;
                    }
                    isRequired = true;
                    break;
                }
            }

            validIndex = 0;
            validData = new Double[numPoints];
            columnData = getErrorData(dim);

            // Now go through each of our valid point indexes
            pointsIter = validPoints.iterator();
            while (pointsIter.hasNext())
            {
                pointIdx = pointsIter.next();
                // If its visible we attempt to add the value in the column
                if (isErrorDataVisible(dim))
                {
                    if (pointIdx >= columnData.length || columnData[validIndex] == null)
                    {
                        // If we didn't find the value and it was required for the fit then we must remove it and
                        // start over. This should never occur!
                        if (isRequired)
                        {
                            System.err.println(
                                    "Failed to retreive point's required error information for a point that was returned as valid at row " +
                                            pointIdx + ". Removing it from the returned data");
                            pointsIter.remove();
                            failedToFindPoint = true;
                        }
                        // Otherwise just set it to null
                        else
                        {
                            validData[validIndex] = null;
                        }
                    }
                    // If we successfully retrieved the value then add it to the list
                    else
                    {
                        validData[validIndex] = columnData[pointIdx];
                    }
                    validIndex++;
                }
                // If its not visible, return all nulls
                // We already ensured if it was required it is visible
                else
                {
                    validData[validIndex] = null;
                }
            }

            // If we found the errors fine, add it to the list
            if (!failedToFindPoint)
            {
                data[columnIdx++] = validData;
            }
            // otherwise bail out
            else
            {
                break;
            }
        }

        // If we found all the points fine, then return the data
        if (!failedToFindPoint)
        {
            return data;
        }
        // If we failed call ourselves again with the trimmed down points list
        else
        {
            return getValidPoints_Errors(validPoints);
        }
    }

    /** Sets the Color to be used when drawing this DataSet to the given Color
     * 
     * @param color The desired Color to use when drawing this DataSet to the GraphArea */
    public void setColor(Color color)
    {
        changeTracker.setFileModified();

        dataSetColor = color;

        if (isColorCustom())
        {
            dataSetCustomColor = color;
        }
    }

    /** Sets the shape used for the points of this DataSet when drawing it to the GraphArea to the given Shape
     * 
     * @param shape The desired Shape to use when drawing this DataSet's points */
    public void setShape(Shape shape)
    {
        changeTracker.setFileModified();
        dataSetShape = shape;
    }

    /** Sets the FitType to use for this DataSet to the given FitType
     * 
     * @param fit The FitType to use for this DataSet's linear fit */
    public void setFitType(FitType fit)
    {
        changeTracker.setFileModified();
        dataSetFitType = fit;
    }

    /** Sets the name of this DataSet to the desired passed name
     * 
     * @param name The new Name of this DataSet */
    public void setName(String name)
    {
        dataSetName = name;
    }

    // private classes
    /** A Listener class on the DataSet table that allows us to update the GraphArea whenever we make changes to the
     * columns
     * 
     * @author Keith Rice
     * @version 2.0
     * @since &lt;0.98.0 */
    private class DataSetTableListener implements TableModelListener
    {
        /** True if we are in the process of updating the table so we can tell if we called ourself to prevent infinite
         * recursion */
        boolean alreadyUpdatingTable = false;
        /** True if the listener is enabled, false otherwise. This allows us to change data internally without calling
         * the listener */
        boolean enabled = true;
        /** The action to run when the allowable fit types are updated */
        Runnable onUpdateFitTypesAction;

        /** Constructor for the DataSetTableListener
         * 
         * @param inOnUpdateFitTypesAction The action to run when the fit types for this data are updated */
        DataSetTableListener(Runnable inOnUpdateFitTypesAction)
        {
            onUpdateFitTypesAction = inOnUpdateFitTypesAction;
        }

        /** Enables or disables the listener to allow for data updates internally without triggering the listener
         * 
         * @param enable True to enable it, false to disable it */
        public void setListenerEnabled(boolean enable)
        {
            enabled = enable;
        }

        /** Used to signal LineFit that the data has changed or has potentially changed */
        public void signalDataChanged()
        {
            refreshFitData();
            onUpdateFitTypesAction.run();
        }

        /** Validates and Updates the data in the column at the passed index as appropriate
         * 
         * @param e The event containing information on what was changed
         * @param columnIndex The index of the column that was changed */
        private void updateColumn(TableModelEvent e, int columnIndex)
        {
            DataColumn data = getDisplayedColumn(columnIndex);

            // we should never have a negative here but add the check just in case
            if (e.getFirstRow() < 0)
            {
                System.err.println("Warning: detected negative row index for table update for row " + columnIndex +
                        ". Continuing");
                return;
            }

            for (int i = e.getFirstRow(); i <= e.getLastRow(); i++)
            {
                Object entryObj = dataTableModel.getValueAt(i, columnIndex);
                Double entry = null;
                if (entryObj != null)
                {
                    // Get the value entered and see if it is a valud entry
                    boolean badEntry = false;
                    try
                    {
                        entry = Double.parseDouble(entryObj.toString());

                        // ensure it is not infinity or any other strange value
                        if (!Double.isFinite(entry))
                        {
                            badEntry = true;
                        }

                        // Don't allow zero errors!
                        if (isIndexDisplayedErrorColumn(columnIndex) && entry == 0)
                        {
                            badEntry = true;
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        badEntry = true;
                    }

                    // If the entry was bad, set it back to what it was
                    if (badEntry)
                    {
                        entry = data.getDataAt(e.getFirstRow());
                    }
                }

                // always set it so that it will convert to double format if it wasn't entered that way
                dataTableModel.setValueAt(entry, i, columnIndex);
                data.writeData(e.getFirstRow(), entry);
            }

            // if there are no more rows, then add one
            if (e.getLastRow() + 1 == dataTableModel.getRowCount())
            {
                dataTableModel.addRow(new Object[dataTableModel.getColumnCount()]);
            }
        }

        /** The event that is called whenever the values in the table have been modified
         * 
         * @param e The event containing information on what was changed */
        public void tableChanged(TableModelEvent e)
        {
            // if this event was fired while we were modifying the table, then ignore it because it was due to our
            // modifications.
            if (enabled && !alreadyUpdatingTable)
            {
                alreadyUpdatingTable = true;

                // If we are adding or removing a new column, its index will be -1
                if (e.getColumn() >= 0)
                {
                    if (e.getColumn() == TableModelEvent.ALL_COLUMNS)
                    {
                        for (int i = 0; i < dataTableModel.getColumnCount(); i++)
                        {
                            updateColumn(e, i);
                        }
                    }
                    else
                    {
                        updateColumn(e, e.getColumn());
                    }

                }

                signalDataChanged();
                alreadyUpdatingTable = false;
            }
        }
    }
}