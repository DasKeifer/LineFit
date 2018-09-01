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
 * @author Unknown
 * @version 1.0
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
    /** The name of this graph set to be displayed to the user */
    private String dataSetName;
    /** If the current GraphDataSet is visible and should be drawn to the GraphArea */
    public boolean visibleGraph;

    /** The list of all the visible DataColumns in this DataSet */
    final DataColumn[] dataColumns;
    final DataColumn[] errorColumns;

    int errorColumnsDisplayed = 0;
    DataDimension[] errorColumnsOrder = DataDimension.values();

    /** The FitAlgrorithm we are using to fit this DataSet that also keeps track of the fit's data */
    public LinearFitStrategy linearFitStrategy; // TODO: encapsulate
    /** The currently selected FitType of this DataSet (i.e. no fit, x error fit) */
    private FitType dataSetFitType;
    /** The color of this DataSet when drawn to the GraphArea */
    private Color dataSetColor;
    private Color dataSetCustomColor;
    /** The shape of this DataSet when drawn to the GraphArea */
    private Shape dataSetShape;

    public static final Color[] predefinedColors = new Color[] { Color.BLACK, Color.YELLOW, Color.BLUE, Color.GREEN,
            Color.ORANGE, Color.RED };
    public static final String[] predefinedColorNames = new String[] { "black", "yellow", "blue", "green", "orange",
            "red" };

    /** Creates a new empty DataSet that is linked to the GraphArea
     * 
     * @param parentGraphArea The GraphArea that this DataSet belongs to and will be drawn to */
    DataSet(ChangeTracker parentsChangeTracker, Runnable onUpdateFitTypesAction)
    {
        changeTracker = parentsChangeTracker;

        dataSetFitType = FitType.NONE;
        visibleGraph = true;

        linearFitStrategy = LineFit.currentFitAlgorithmFactory.createNewLinearFitStartegy(this);

        dataSetName = "DataSet " + (numberOfGraphDataSets + 1); // +1 so its 1 based instead of 0 based
        dataColumns = new DataColumn[DataDimension.getNumberOfDimensions()];
        errorColumns = new DataColumn[DataDimension.getNumberOfDimensions()];
        dataTableModel = new DataSetTableModel();
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

        dataTableModel.addTableModelListener(new GraphSetListener(onUpdateFitTypesAction));

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

    public void setErrorColumnOrder(DataDimension[] columnOrder)
    {
        int numCurrentlyDisplayed = errorColumnsDisplayed;
        while (errorColumnsDisplayed > 0)
        {
            hideLastErrorColumn();
        }

        errorColumnsOrder = columnOrder;

        while (errorColumnsDisplayed < numCurrentlyDisplayed)
        {
            showNextErrorColumn();
        }
    }

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
                    return dataColumns.length;
                }
            }
        }

        return -1;
    }

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

    public void showNextErrorColumn()
    {
        if (errorColumnsDisplayed < DataDimension.getNumberOfDimensions())
        {
            int tableIndex = dataColumns.length + errorColumnsDisplayed;
            DataColumn error = errorColumns[errorColumnsOrder[errorColumnsDisplayed++].getColumnIndex()];
            dataTableModel.addColumn(error.getName());

            for (int i = 0; i < error.dataSize(); i++)
            {
                if (!error.isNull(i))
                {
                    dataTableModel.setValueAt(error.readDouble(i), i, tableIndex);
                }
            }
        }
    }

    public void hideLastErrorColumn()
    {
        if (errorColumnsDisplayed > 0)
        {
            dataTableModel.removeLastColumn();
            errorColumnsDisplayed--;
        }
    }

    /** Updates the available FitTypes we can use on this DataSet based on the amount of data in them */
    ArrayList<FitType> getAllowableFits()
    {
        ArrayList<FitType> fits = new ArrayList<FitType>();

        fits.add(FitType.NONE);
        ArrayList<Integer> validPoints = getIndexesOfValidPoints();
        if (validPoints.size() > 1)
        {
            fits.add(FitType.REGULAR);

            if (checkAllHaveErrors(validPoints, DataDimension.X))
            {
                fits.add(FitType.X_ERROR);

                if (checkAllHaveErrors(validPoints, DataDimension.Y))
                {
                    fits.add(FitType.Y_ERROR);
                    fits.add(FitType.BOTH_ERRORS);
                }
            }
            else if (checkAllHaveErrors(validPoints, DataDimension.Y))
            {
                fits.add(FitType.Y_ERROR);
            }
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
        for (int i = 0; i < dataColumns[0].getData().size(); i++)
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

    public double[] getMinMax(DataDimension dim, boolean withErrors)
    {
        boolean hasInit = false;
        double dataMax = 0;
        double dataMin = 0;

        refreshFitData();
        DataColumn data = dataColumns[dim.getColumnIndex()];
        DataColumn error = errorColumns[dim.getColumnIndex()];

        for (int i = 0; i < data.getData().size(); i++)
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

    /** Checks if all the points that have an x and a y also have an associated y error/uncertainty value. This is used
     * to determine if we can do a fit with y errors/uncertainties
     * 
     * @return True if all the points of this DataSet have a y error/uncertainty associated with them and false
     *         otherwise */
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
     * @param newDataSet Signals that the line passed in is the beginning of a new data set
     * @return Returns true if the data or option for the data was read in from the line */
    public boolean readInDataAndDataOptions(String line, boolean unused)
    {
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
                    // Not used anymore
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
                case "colname":
                    break; // we don't use this anymore but we don't want to cause errors when reading old files int.
                           // visibleDataColumns.get(colNum).setName(valueForField); break;
                case "coldesc":
                    break; // we don't use this anymore but we don't want to cause errors when reading old files in
                case "p":
                case "datapoint":
                {
                    // split it up into the separate string parts
                    String[] splitPointValuesInput = valueForField.split(" ");

                    // Reads should only take place when a set is created so we
                    // can just use the data size of the first column to determine
                    // the next row to add at.
                    int row = dataColumns[0].dataSize();
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
                            dataColumns[column].writeData(row, value);
                            dataTableModel.setValueAt(value, row, column);
                        }
                        catch (IndexOutOfBoundsException iobe)
                        {
                            System.err.println(
                                    "Error reading in DataPoint - More values specified than columns - Continuing: " +
                                            line);
                            break;
                        }
                    }
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
                    datapoint += dataColumns[j].getData().get(index);
                }

                for (int j = 0; j < errorColumnsDisplayed; j++)
                {
                    datapoint += " " + errorColumns[j].getData().get(index);
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

    public boolean isColorCustom()
    {
        return isColorACustomColor(dataSetColor);
    }

    public int getNumberOfDisplayedColumns()
    {
        return DataDimension.getNumberOfDimensions() + errorColumnsDisplayed;
    }

    /** Gets the current Color that is being used by this DataSet
     * 
     * @return The Color that is being used to draw this DataSet */
    public Color getColor()
    {
        return dataSetColor;
    }

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

    public DataColumn getColumn(int columnIndex)
    {
        if (columnIndex < DataDimension.getNumberOfDimensions())
        {
            return getData(columnIndex);
        }
        else
        {
            return getErrorData(errorColumnsOrder[columnIndex - DataDimension.getNumberOfDimensions()]);
        }
    }

    /** The DataColumn that keeps track of the x data for this DataSet
     * 
     * @return The DataColumn that keeps track of the x data values for this DataSet */
    public DataColumn getData(int index)
    {
        return dataColumns[index];
    }

    public DataColumn getData(DataDimension data)
    {
        return getData(data.getColumnIndex());
    }

    /** The DataColumn that keeps track of the x data for this DataSet
     * 
     * @return The DataColumn that keeps track of the x data values for this DataSet */
    public DataColumn getErrorData(int errorColumnIndex)
    {
        return errorColumns[errorColumnIndex];
    }

    public DataColumn getErrorData(DataDimension data)
    {
        return getErrorData(data.getColumnIndex());
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
     * @version 1.0
     * @since &lt;0.98.0 */
    private class GraphSetListener implements TableModelListener
    {
        boolean alreadyUpdatingTable = false;
        Runnable onUpdateFitTypesAction;

        GraphSetListener(Runnable inOnUpdateFitTypesAction)
        {
            onUpdateFitTypesAction = inOnUpdateFitTypesAction;
        }

        private void updateColumn(TableModelEvent e, int columnIndex)
        {
            DataColumn data = getColumn(columnIndex);

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
                    // ensure it is a double and if not clear the entry
                    try
                    {
                        entry = Double.parseDouble(entryObj.toString());

                        // ensure it is not infinity or any other strange value
                        if (!Double.isFinite(entry))
                        {
                            entry = null;
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        entry = null;
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

        /** The event that is called whenever the values in the table have been modified */
        public void tableChanged(TableModelEvent e)
        {
            // if this event was fired while we were modifying the table, then ignore it because it was due to our
            // modifications. Also, if we are adding a new column, its index will be -1
            if (!(alreadyUpdatingTable || e.getColumn() < 0))
            {
                alreadyUpdatingTable = true;

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

                refreshFitData();
                onUpdateFitTypesAction.run();
                alreadyUpdatingTable = false;
            }
        }
    }
}