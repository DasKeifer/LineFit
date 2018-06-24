/*
Copyright (C) 2013  Covenant College Physics Department

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see http://www.gnu.org/licenses/.
*/
package linefit;


import java.awt.*;
import java.awt.geom.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;

import linefit.FitAlgorithms.FixedVariable;
import linefit.FitAlgorithms.LinearFitStrategy;
import linefit.IO.ChangeTracker;
import linefit.IO.HasDataToSave;
import linefit.FitAlgorithms.FitType;

/**
 * The class that keeps track of the data in each DataSet in the GraphArea. It contains the columns with 
 * the x,y, and error/uncertainty value as well as the color and shape of the Set.
 * 
 * Other names include: GraphDataSet, DataSet, GraphSet
 * 
 * @author	Unknown
 * @version	1.0
 * @since 	&lt;0.98.0
 */
public class DataSet extends JScrollPane implements HasDataToSave
{
	/** The Serial Version UID so that we know what version it is when we are using it.
	 * See http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html for full 
	 * discussion on its uses and purpose */
	private final static long serialVersionUID = 42L;
	/** The static variable that keeps track of the current number of GraphDataSets in the GraphArea. Used to determine the number used for the next GraphDataSet */
	private static int numberOfGraphDataSets = 1;
	/** The Default number of columns in each GraphDataSet when it is created. By Default it is two: one for the x values and one for the y values */
	final static int DEFAULT_NUMBER_OF_COLUMNS = 2;
	/** The Default number or rows in each column in the GraphDataSet */
	final static int DEFAULT_NUMBER_OF_ROWS = 10;

	/** The GraphArea this GraphDataSet is linked to */
	private GraphArea graphArea;
	
	private ChangeTracker changeTracker;
	
	/** The table that contains and allows us to input data */
	private JTable tableContainingData;
	/** The model of the Table to use for inputting and storing data */
	DataSetTableModel dataTableModel;
	/** The name of this graph set to be displayed to the user */
	private String dataSetName;
	/** If the current GraphDataSet is visible and should be drawn to the GraphArea */
	public boolean visibleGraph;
	/** Whether or not the linearFit should be updated automatically. This allows us to disable updates on it when we are modifying the data to save some unnecessary computation */
	private boolean fitLock = false;
	
	/** The list of all the visible DataColumns in this DataSet */
	ArrayList<DataColumn> visibleDataColumns;
	/** the list of all the columns that are not being displayed currently in the DataSet. This allows us to keep the values in case the user wants to bring back the column they removed earlier */
	ArrayList<DataColumn> invisibleDataColumns;
	
	/** The DataColumn that keeps track of the x data for this DataSet */
	public DataColumn xData;
	/** The DataColumn that keeps track of the y data for this DataSet */
	public DataColumn yData;
	/** The DataColumn that keeps track of the x error/uncertainty data for this DataSet */
	public DataColumn xErrorData;
	/** The DataColumn that keeps track of the y error/uncertainty data for this DataSet */
	public DataColumn yErrorData;
	/** The currently selected FitType of this DataSet (i.e. no fit, x error fit) */
	private FitType currentFitType;
	/** The drop down box that allows the user to specify the type of linear fit to use (i.e. no fit, x error fit) */
	JComboBox<FitType> fitTypeSelector; 
	/** The FitAlgrorithm we are using to fit this DataSet that also keeps track of the fit's data */
	public LinearFitStrategy linearFitStrategy; //TODO: encapsulate
	/** The color of this DataSet when drawn to the GraphArea */
	private Color dataSetColor;
	/** The shape of this DataSet when drawn to the GraphArea */
	private Shape dataSetShape;
	/** The color selector that is chosen when the set is selected this way there is not multiple ones for the same DataSet leading to some potentially awkward situations */
	private CustomColorMenu customColorMenu;
	
	/** Creates a new empty DataSet that is linked to the GraphArea 
	 * @param parentGraphArea The GraphArea that this DataSet belongs to and will be drawn to */
	DataSet(GraphArea parentGraphArea, ChangeTracker parentsChangeTracker) 
	{
		changeTracker = parentsChangeTracker;
		
		currentFitType = FitType.NONE;
		visibleGraph = true;

		linearFitStrategy = LineFit.currentFitAlgorithmFactory.createNewLinearFitStartegy(this);

		dataSetName = "DataSet " + numberOfGraphDataSets;
		visibleDataColumns = new ArrayList<DataColumn>();
		invisibleDataColumns = new ArrayList<DataColumn>();
		dataTableModel = new DataSetTableModel();
		tableContainingData = new JTable(dataTableModel);
		tableContainingData.setGridColor(Color.gray);
		
		//Clean up JTable to make cell selection work more like excel
		tableContainingData.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		tableContainingData.setRowSelectionAllowed(true);
		tableContainingData.setColumnSelectionAllowed(true);
		tableContainingData.setCellSelectionEnabled(true);
		tableContainingData.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		
		setViewportView(tableContainingData);
		graphArea = parentGraphArea;
		fitTypeSelector = new JComboBox<FitType>();

		for (int i = 0; i < DEFAULT_NUMBER_OF_COLUMNS; i++) 
		{
			addColumn();
		}

		for (int i = 0; i < DEFAULT_NUMBER_OF_ROWS; i++)
		{
			//System.out.println("add a row");
			dataTableModel.insertRow(dataTableModel.getRowCount(), new Object[visibleDataColumns.size()]);
		}

		dataTableModel.addTableModelListener(new GraphSetListener());

		tableContainingData.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false),"MY_CUSTOM_ACTION");
		dataSetColor = Color.BLACK;
		dataSetShape = new Rectangle2D.Double();

		numberOfGraphDataSets++;
		
		//Add our table listener for this DataSet
		new SpreadSheetAdapter(tableContainingData);
	}

	/** A private constructor for an empty DataSet that is only used to make a placeholder DataSet */
	private DataSet() {}
	
	/** Returns an empty DataSet with no initialization to be used for the new DataSet option in the drop down menu 
	 * @param displayed The String to display on the DataSet drop down placeholder object 
	 * @return Returns a DataSet object to be put in the DataSetSelector to hold the place for creating a new DataSet
	 */
	static DataSet createDropDownPlaceHolder(String displayed)
	{
		DataSet placeHolder = new DataSet();
		placeHolder.dataSetName = displayed;
		return placeHolder;
	}
	
	/** Resets this DataSet to an empty one as if it had just been created */
	void resetToNew() 
	{
		currentFitType = FitType.NONE;
		visibleGraph = true;
		
		linearFitStrategy = LineFit.currentFitAlgorithmFactory.createNewLinearFitStartegy(this);

		visibleDataColumns = new ArrayList<DataColumn>();
		invisibleDataColumns = new ArrayList<DataColumn>();
		dataTableModel = new DataSetTableModel();
		tableContainingData = new JTable(dataTableModel);
		tableContainingData.setGridColor(Color.gray);
		
		tableContainingData.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		tableContainingData.setRowSelectionAllowed(true);
		tableContainingData.setColumnSelectionAllowed(true);
		tableContainingData.setCellSelectionEnabled(true);
		tableContainingData.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		setViewportView(tableContainingData);

		for (int i = 0; i < DEFAULT_NUMBER_OF_COLUMNS; i++) 
		{
			addColumn();
		}

		for (int i = 0; i < DEFAULT_NUMBER_OF_ROWS; i++) 
		{
			//System.out.println("add a row");
			dataTableModel.insertRow(dataTableModel.getRowCount(), new Object[visibleDataColumns.size()]);
		}

		dataTableModel.addTableModelListener(new GraphSetListener());

		// dataTable.getActionMap().put("MY_CUSTOM_ACTION", action);
		tableContainingData.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false),"MY_CUSTOM_ACTION");
		dataSetColor = Color.BLACK;
		dataSetShape = new Rectangle2D.Double();

		//Add our table listener for this DataSet
		new SpreadSheetAdapter(tableContainingData);
	}
	
	/** Updates the available FitTypes we can use on this DataSet based on the amount of data in them */
	void updateFits() 
	{
		// Checks which types of fit types should be offered,
		// and then rebuilds fitSelector
		fitLock = true;
		
		
		fitTypeSelector.removeAllItems();
		fitTypeSelector.addItem(FitType.NONE);
		if (getNumberOfValidPoints() > 1) 
		{
			fitTypeSelector.addItem(FitType.REGULAR);
			
			checkForZeroErrorValues();
			if (checkAllXHaveErrors()) 
			{
				fitTypeSelector.addItem(FitType.X_ERROR);
			}
			if (checkAllYHaveErrors()) 
			{
				fitTypeSelector.addItem(FitType.Y_ERROR);
			}
			if (checkAllXHaveErrors() && checkAllYHaveErrors()) 
			{
				fitTypeSelector.addItem(FitType.BOTH_ERRORS);
			}
		}
		fitLock = false;
		fitTypeSelector.setSelectedItem(currentFitType);
	}

	/** Recalculates the FitData with our current FitType and data */
	void refreshFitData() 
	{
		linearFitStrategy.refreshFitData();
	}

	/** returns how many rows have valid points, meaning that they have both x and y data for 
	 * @return The number of points containing at least an x and a y value in this DataSet */
	private int getNumberOfValidPoints() 
	{
		int activePoints = 0;
		if (xData != null && yData != null) 
		{
			for (int i = 0; i < Math.max(xData.getData().size(), yData.getData().size()); i++) 
			{
				if (!xData.isNull(i) && !yData.isNull(i)) 
				{
					activePoints++;
				}
			}
		}
		return activePoints;
	}

	/** Checks the x and y errors/uncertainties for this DataSet and makes sure none of them are 0 because it will throw off the fit algorithms*/
	private void checkForZeroErrorValues() 
	{
		if(yErrorData != null) 
		{
			for(int i = 0; i < yErrorData.getData().size(); i++)
			{
				if(!yErrorData.isNull(i) && yErrorData.readDouble(i) == 0.0) 
				{
					yErrorData.getData().set(i, null);
					JOptionPane.showMessageDialog(this, "Errors must be non zero if they are to be fitted to", 
							"NoDataFound Exception", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		
		if(xErrorData != null) 
		{
			for(int i = 0; i < xData.getData().size(); i++)
			{
				if(!xErrorData.isNull(i) && xErrorData.readDouble(i) == 0.0) 
				{
					xErrorData.getData().set(i, null);
					JOptionPane.showMessageDialog(this, "Errors must be non zero if they are to be fitted to", 
							"NoDataFound Exception", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}
	
	/** 
	 * Checks to see if there are y errors/uncertainties in this DataSet 
	 * @return True if there is y error/uncertainty values and false otherwise
	 */
	private boolean checkYErrors() 
	{
		if (yErrorData != null) 
		{
			return true;
		} 
		else
		{
			return false;
		}
	}

	/**
	 * Checks if all the points that have an x and a y also have an associated y error/uncertainty value. This is used
	 * to determine if we can do a fit with y errors/uncertainties
	 * 
	 * @return True if all the points of this DataSet have a y error/uncertainty associated with them and false otherwise
	 */
	private boolean checkAllYHaveErrors()
	{
		if (yErrorData != null) 
		{
			//first see which points will draw - have both a x and a y component
			boolean[] drawablePoints = new boolean[yData.getData().size()];
			for(int i = 0; i < yData.getData().size(); i++) 
			{
				if(!xData.isNull(i) && !yData.isNull(i)) 
				{
					drawablePoints[i] = true;
				}
			}
			
			//now check to make sure for each point we will draw we have an x error
			for(int i = 0; i < drawablePoints.length; i++) 
			{
				if(yErrorData.isNull(i) && drawablePoints[i]) 
				{
					return false;
				}
			}
			return true;
		} 
		else 
		{
			return false;
		}
	}

	/** 
	 * Checks to see if there are x errors/uncertainties in this DataSet 
	 * @return True if there are x error/uncertainty values and false otherwise
	 */
	private boolean checkXErrors() 
	{
		if (xErrorData != null) 
		{
			return true;
		} 
		else
		{
			return false;
		}
	}

	/**
	 * Checks if all the points that have an x and a y also have an associated x error/uncertainty value. This is used
	 * to determine if we can do a fit with x errors/uncertainties
	 * 
	 * @return True if all the points of this DataSet have a x error/uncertainty associated with them and false otherwise
	 */
	private boolean checkAllXHaveErrors() 
	{
		if (xErrorData != null) 
		{
			//first see which points will draw - have both a x and a y component
			boolean[] drawablePoints = new boolean[xData.getData().size()];
			for(int i = 0; i < xData.getData().size(); i++) 
			{
				if(!xData.isNull(i) && !yData.isNull(i)) 
				{
					drawablePoints[i] = true;
				}
			}
			
			//now check to make sure for each point we will draw we have an x error
			for(int i = 0; i < drawablePoints.length; i++) 
			{
				if(xErrorData.isNull(i) && drawablePoints[i]) 
				{
					return false;
				}
			}
			return true;
		} 
		else
		{
			return false;
		}
	}

	/** Updates the DataColumns to make sure that the DataColumns being displayed have the right data associated with them */
	private void UpdateGraphColumnAssociations() 
	{
		// Connect data to the Graph
		if (visibleDataColumns.size() == 0) 
		{
			xData = null;
			yData = null;
			xErrorData = null;
			yErrorData = null;
		} 
		else if (visibleDataColumns.size() == 1)
		{
			xData = (DataColumn) visibleDataColumns.get(0);
			yData = (DataColumn) visibleDataColumns.get(0);
			xErrorData = null;
			yErrorData = null;
		} 
		else if (visibleDataColumns.size() == 2) 
		{
			xData = (DataColumn) visibleDataColumns.get(0);
			yData = (DataColumn) visibleDataColumns.get(1);
			xErrorData = null;
			yErrorData = null;
		} 
		else if (visibleDataColumns.size() == 3) 
		{
			xData = (DataColumn) visibleDataColumns.get(0);
			yData = (DataColumn) visibleDataColumns.get(1);
			if (graphArea.xErrorsOnly) 
			{
				xErrorData = (DataColumn) visibleDataColumns.get(2);
				yErrorData = null;
			} 
			else 
			{
				xErrorData = null;
				yErrorData = (DataColumn) visibleDataColumns.get(2);
			}
		} 
		else 
		{
			xData = (DataColumn) visibleDataColumns.get(0);
			yData = (DataColumn) visibleDataColumns.get(1);
			xErrorData = (DataColumn) visibleDataColumns.get(2);
			yErrorData = (DataColumn) visibleDataColumns.get(3);
		}
	}
	
	/** Changes the current number of visible columns to the inputed amount and puts the other ones on the unusedDataColumns list
	 * @param desiredColumns The desired number of DataColumns that this DataSet should have */
	void changeNumVisibleColumns(int desiredColumns) 
	{
		// Either add or remove columns based on user input
		changeTracker.setFileModified();

		if (visibleDataColumns.size() < desiredColumns)
		{
			// We want to add columns!
			for (int i = visibleDataColumns.size(); i < desiredColumns; i++) 
			{
				this.addColumn();
			}
		} 
		else if (visibleDataColumns.size() > desiredColumns) 
		{
			// We want to remove columns!
			for (int i = visibleDataColumns.size(); i > desiredColumns; i--) 
			{
				this.removeColumn();
			}
		}
		// numVisibleColumns = desiredColumns;
	}

	/** Adds a column to our current DataSet. If there are invisible columns than it will make them visible instead of creating
	 * a new one
	 */
	private void addColumn() 
	{
		if (invisibleDataColumns.size() > 0) 
		{
			unhideColumn();
		} 
		else
		{
			addNewColumn();
		}
	}
	
	/** Adds a new DataColumn to our graph set regardless of if there are invisible ones that have already been created */
	private void addNewColumn() 
	{
		if (graphArea.xErrorsOnly) 
		{
			visibleDataColumns.add(new DataColumn(visibleDataColumns.size()));
			UpdateGraphColumnAssociations();
			updateColumnNames();
			dataTableModel.addColumn((visibleDataColumns.get(visibleDataColumns.size() - 1)).getName());
		} 
		else
		{
			if (visibleDataColumns.size() == 3) 
			{
				FitType fitBeforeChange = currentFitType;
				visibleDataColumns.add(2,new DataColumn(changeTracker));
				UpdateGraphColumnAssociations();
				updateColumnNames();
				dataTableModel.removeColumn(2);
				dataTableModel.addColumn((visibleDataColumns.get(2).getName()));
				dataTableModel.addColumn((visibleDataColumns.get(3).getName()));
				currentFitType = fitBeforeChange;
			} 
			else
			{
				visibleDataColumns.add(new DataColumn(changeTracker));
				UpdateGraphColumnAssociations();
				updateColumnNames();
				dataTableModel.addColumn((visibleDataColumns.get(visibleDataColumns.size() - 1)).getName());
			}
		}
		updateFits();
	}
	
	/** Makes the first invisible column into a visible one */
	private void unhideColumn() 
	{
		if (graphArea.xErrorsOnly) 
		{
			if (visibleDataColumns.size() == 3) 
			{
				if (invisibleDataColumns.size() > 0) 
				{
					visibleDataColumns.add(invisibleDataColumns.get(0));
					invisibleDataColumns.remove(0);
				} 
				else 
				{
					visibleDataColumns.add(new DataColumn(visibleDataColumns.size()));
				}
				UpdateGraphColumnAssociations();
				updateColumnNames();
				dataTableModel.addColumn((visibleDataColumns.get(visibleDataColumns.size() - 1)).getName());
			}
			else if (visibleDataColumns.size() == 2)
			{
				if (invisibleDataColumns.size() > 0)
				{
					visibleDataColumns.add(invisibleDataColumns.get(0));
					invisibleDataColumns.remove(0);
				} 
				else 
				{
					visibleDataColumns.add(new DataColumn(visibleDataColumns.size()));
				}
				UpdateGraphColumnAssociations();
				updateColumnNames();
				dataTableModel.addColumn((visibleDataColumns.get(visibleDataColumns.size() - 1)).getName());
			}
		}
		else 
		{
			if (visibleDataColumns.size() == 3) 
			{
				if (invisibleDataColumns.size() > 0) 
				{
					visibleDataColumns.add(2, invisibleDataColumns.get(0));
					invisibleDataColumns.remove(0);
				}
				else 
				{
					visibleDataColumns.add(2, new DataColumn(visibleDataColumns.size()));
				}
				UpdateGraphColumnAssociations();
				updateColumnNames();
				dataTableModel.removeColumn(2);
				dataTableModel.addColumn((visibleDataColumns.get(2).getName()));
				dataTableModel.addColumn((visibleDataColumns.get(3).getName()));
			} 
			else if (visibleDataColumns.size() == 2) 
			{
				if (invisibleDataColumns.size() > 1) 
				{
					visibleDataColumns.add(invisibleDataColumns.get(1));
					invisibleDataColumns.remove(1);
				} 
				else if (invisibleDataColumns.size() > 0) 
				{
					visibleDataColumns.add(invisibleDataColumns.get(0));
					invisibleDataColumns.remove(0);
				} 
				else 
				{
					visibleDataColumns.add(new DataColumn(visibleDataColumns.size()));
				}
				UpdateGraphColumnAssociations();
				updateColumnNames();
				dataTableModel.addColumn((visibleDataColumns.get(visibleDataColumns.size() - 1)).getName());
			}
		}
	}
	
	/** Makes the last visible column invisible so it is no longer displayed but the data is still kept in case we want it later */
	private void removeColumn() 
	{
		if (graphArea.xErrorsOnly) 
		{
			DataColumn column = visibleDataColumns.get(visibleDataColumns.size() - 1);
			visibleDataColumns.remove(visibleDataColumns.size() - 1);
			invisibleDataColumns.add(0,column);
			UpdateGraphColumnAssociations();
			updateColumnNames();
			dataTableModel.removeColumn(visibleDataColumns.size());
		} 
		else
		{
			DataColumn column = visibleDataColumns.get(2);
			visibleDataColumns.remove(2);
			invisibleDataColumns.add(column);
			UpdateGraphColumnAssociations();
			updateColumnNames();
			dataTableModel.removeColumn(visibleDataColumns.size() - 1);
			if (visibleDataColumns.size() == 3) 
			{
				dataTableModel.removeColumn(2);
				dataTableModel.addColumn(visibleDataColumns.get(2).getName());
			}
			if (visibleDataColumns.size() == 2) 
			{
				dataTableModel.removeColumn(1);
				dataTableModel.addColumn(visibleDataColumns.get(1).getName());
			}
		}
		updateFits();
	}

	/**
	 * Internal method used to update the last column in the DataSet being displayed
	 */
	private void refreshLastColumn()
	{
		removeColumn();
		unhideColumn();
	}
	
    /**
     * Makes sure the third column is updated so we can change whether we use only x errors of y errors
     * This has to be done because the third column can be either x or y errors/uncertainties
     */
	void refreshThirdColumn()
	{
		boolean switchErrorFit = false;
		if(this.getFitType() == FitType.X_ERROR || this.getFitType() == FitType.Y_ERROR)
		{
			switchErrorFit = true;
		}
		
		if(this.visibleDataColumns.size() == 3) 
		{
			this.refreshLastColumn();
			
			if(switchErrorFit) 
			{
				if(graphArea.xErrorsOnly)
				{
					this.setFitType(FitType.X_ERROR);
				} 
				else 
				{
					this.setFitType(FitType.Y_ERROR);
				}
				this.updateFits();
			}
		}
	}
	
	/** Makes sure that each DataColumn is appropriately named to represent what data is in it */
	private void updateColumnNames() 
	{
		if (visibleDataColumns.size() >= 1)
		{
			visibleDataColumns.get(0).setColumnName("x");
		}
		if (visibleDataColumns.size() >= 2) {
			visibleDataColumns.get(1).setColumnName("y");
		}
		if (!checkYErrors() && checkXErrors()) {
			visibleDataColumns.get(2).setColumnName('\u03B4' + "x");
		}
		if (checkYErrors() && !checkXErrors()) {
			visibleDataColumns.get(2).setColumnName('\u03B4' + "y");
		}
		if (checkXErrors() && checkYErrors()) {
			visibleDataColumns.get(2).setColumnName('\u03B4' + "x");
			visibleDataColumns.get(3).setColumnName('\u03B4' + "y");
		}
	}
	
	/** Updates the DataColumns in this DataSet to make sure they are displaying the correct values in their cells and formatted in a double format */
	void updateCellFormattingInColumns()
	{
		for ( int i = 0; i < this.visibleDataColumns.size(); i++ ) 
		{ 
			DataColumn currentColumn = this.visibleDataColumns.get(i);
			ArrayList<Double> columnData = currentColumn.getData(); 
			
			Iterator<Double> columnIterator = columnData.iterator();
			int rowNum = 0; 
			
			while (columnIterator.hasNext() ) 
			{ 
				Double valueInRow = columnIterator.next();
				this.dataTableModel.setValueAt(valueInRow, rowNum, i);
				rowNum++;
			}
		}
	}

	/**
	 * Determines whether or not their is data in this DataSet
	 * @return Returns true if there is data and false if no data was found
	 */
	public boolean hasData()
	{
		return this.dataTableModel.hasData();
	}
	
	/**
	 * Determines whether or not there is a visible CustomColorMenu for this DataSet or if it does not exist or is hidden from view
	 * @return Returns true if there is a CustomColorMenu and it is Visible
	 */
	boolean doesHaveVisibleCustomColorMenu()
	{
		//return if it is not null and it is visible
		return customColorMenu != null && customColorMenu.isVisible();
	}
	
	/** 
	 * Creates a new CustomColorMenu for this DataSet, but only if one does not already exist. If one does then it focuses on that CustomColorMenu
	 * @return Returns the CustomColorMenu that is associated with this DataSet whether it is newly created or already existed
	 */
	CustomColorMenu createOrFocusOnCustomColorMenu()
	{
		//if we have one bring it up, otherwise make one
		if(customColorMenu != null)
		{
			//if its just invisible then initialize it so it updates the color and makes it visible
			if(!customColorMenu.isVisible())
			{
				customColorMenu.initialize();
			}
			//bring it to the front
			customColorMenu.toFront();
		}
		else
		{
			customColorMenu = new CustomColorMenu(this);
		}
		return customColorMenu;
	}
	
	public boolean readInDataAndDataOptions(String line, boolean unused)
	{
		//now split the input into the two parts
		//we can't use split because it will mess up on names as well as points since they have multiple spaces
		int firstSpaceIndex = line.indexOf(' ');
		String field = line.substring(0, firstSpaceIndex).toLowerCase();
		String valueForField = line.substring(firstSpaceIndex + 1).toLowerCase();
		
		boolean found = true;
		try
		{
			switch(field)
			{			
				case "colnum": case "numberofcolumns":
				{
					int numCols = Integer.parseInt(valueForField);
									
					for (int i = visibleDataColumns.size(); i < numCols; i++)
					{
						addColumn();
					}
					break;
				}
				case "fittype": 
				{
					//loop through all the fit types checking them against their toString methods
					boolean foundFitType = false;
					for(FitType ft : FitType.values())
					{
						if(valueForField.equals(ft.toString().toLowerCase())) 
						{
							foundFitType = true;
							this.setFitType(ft);
							break;
						}
					}
					//if we didn't find it for whatever strange reason, default to none
					if(!foundFitType)
					{
						this.setFitType(FitType.NONE);
					}
					break;
				}
				case "whatisfixed":
				{
					//loop though all the fixed variables checking them against the toString methods
					boolean foundFixedVariable = false;
					for(FixedVariable fv : FixedVariable.values())
					{
						if(valueForField.equals(fv.toString().toLowerCase())) 
						{
							linearFitStrategy.setWhatIsFixed(fv, linearFitStrategy.getFixedValue());
							foundFixedVariable = true;
							break;
						}
					}
					//if we didnt find anyone just default to none
					if(!foundFixedVariable)
					{
						linearFitStrategy.setWhatIsFixed(FixedVariable.NONE, linearFitStrategy.getFixedValue());
					}
					break;
				}
				
				case "fixedvalue": linearFitStrategy.setWhatIsFixed(linearFitStrategy.getWhatIsFixed(), Double.parseDouble(valueForField)); break;
				case "visible": visibleGraph = valueForField.toLowerCase().equals("true"); break;
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
					switch(valueForField)
					{
						case "black": setColor(Color.BLACK);break;
						case "yellow": setColor(Color.YELLOW); break;
						case "blue": setColor(Color.BLUE); break;
						case "green": setColor(Color.GREEN); break;
						case "orange": setColor(Color.ORANGE); break;
						case "red": setColor(Color.RED); break;
						default://we expect three ints
						{
							String[] colorInputExploded = valueForField.split(" ");
							if(colorInputExploded.length == 3)
							{
								//get the rgb as ints and set up the color
								try
								{
									int red = Integer.parseInt(colorInputExploded[0]);
									int green = Integer.parseInt(colorInputExploded[1]);
									int blue = Integer.parseInt(colorInputExploded[2]);
									setColor(new Color(red, green, blue));
								}
								catch(NumberFormatException e)
								{
									setColor(Color.BLACK);
								}
							}
							else
							{
								setColor(Color.BLACK);
							}
							break;
						}
					}
					break;
				}
				case "colname": break;	//we don't use this anymore but we don't want to cause errors when reading old files int. visibleDataColumns.get(colNum).setName(valueForField); break;	
				case "coldesc": break;	//we don't use this anymore but we don't want to cause errors when reading old files in
				case "p": case "datapoint":
				{
					//split it up into the separate string parts
					String[] splitPointValuesInput = valueForField.split(" ");

					//Reads should only take place when a set is created so we
					//can just use the data size of the first column to determine
					//the next row to add at.
					int row = visibleDataColumns.get(0).dataSize();
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
							visibleDataColumns.get(column).writeData(row, pointValueString);
							dataTableModel.setValueAt(value, row, column);
						}
						catch (IndexOutOfBoundsException iobe)
						{
							System.err.println("Error reading in DataPoint - More values specified than columns - Continuing: " + line); break;
						}
					}
					break;
				} 
				default: found = false; break;
			}
			
		}
		catch (NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(this, "Error reading in number from line: " + line,
				    "NFE Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return found;
	}
		
	/** Recursively saves this DataSet's data into the Formatter file. 
	 * Note: This should not be used independently of the other recursive save functions! 
	 * @param output The formatter that is being used to write the file */
	public void retrieveAllDataAndDataOptions(ArrayList<String> variableNames, ArrayList<String> variableValues)
	{
		if(dataTableModel.hasData())
		{
			variableNames.add("NumberOfColumns");
			variableValues.add(Integer.toString((visibleDataColumns.size() + invisibleDataColumns.size())));
			variableNames.add("FitType");
			variableValues.add(currentFitType.toString());
			variableNames.add("WhatIsFixed");
			if(linearFitStrategy.getWhatIsFixed() == FixedVariable.SLOPE) 
			{
				variableValues.add("slope");
			}
			else if(linearFitStrategy.getWhatIsFixed() == FixedVariable.INTERCEPT) 
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
	
			if (visibleDataColumns.size() > 0) 
			{
				String datapoint;
				for (int i = 0; i < visibleDataColumns.get(0).getData().size(); i++) 
				{
					datapoint = "";
					variableNames.add("DataPoint");
					for (int j = 0; j < visibleDataColumns.size() ; j++) 
					{
						if (j > 0)
						{
							datapoint += " ";
						}
						datapoint += visibleDataColumns.get(j).getData().get(i);
					}
					if (invisibleDataColumns.size() > 0 )
					{
						for (int k = 0; k < invisibleDataColumns.size() ; k++)
						{
							datapoint += " " + invisibleDataColumns.get(k).getData().get(i);
						}
					}
					variableValues.add(datapoint);
				}
			}
		}
	}
	
	/** Returns the DataSet's name */
	public String toString() 
	{
		return dataSetName;
	}

	//getters and setters
	/** 
	 * Returns the Chi squared value for this dataset's current fit
	 * @return The Chi squared as a double
	 */
	public double getChiSquared()
	{
		return this.linearFitStrategy.calculateChiSquared(this.linearFitStrategy.getSlope(), this.linearFitStrategy.getIntercept());
	}
	
    /**
     * Gets the shape this DataSet as a String is using when being draw to the GraphArea 
     * @return A String representing the shape of the points in this DataSet 
     */
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

	/**
	 * Gets the color of this DataSet as a String that it is drawn with on the GraphArea
	 * @return A String representing this DataSet's color
	 */
	public String getColorString() 
	{
		if (dataSetColor == Color.BLACK) 
		{
			return "black";
		} 
		else if (dataSetColor == Color.YELLOW) 
		{
			return "yellow";
		} 
		else if (dataSetColor == Color.BLUE) 
		{
			return "blue";
		} 
		else if (dataSetColor == Color.GREEN) 
		{
			return "green";
		} 
		else if (dataSetColor == Color.ORANGE)
		{
			return "orange";
		}
		else if (dataSetColor == Color.RED) 
		{
			return "red";
		}
		else 
		{
			return dataSetColor.getRed() + " " + dataSetColor.getGreen() + " " + dataSetColor.getBlue();
		}
	}
	
	/** Gets the current Color that is being used by this DataSet
	 * 
	 * @return The Color that is being used to draw this DataSet
	 */
	public Color getColor() 
	{
		return dataSetColor;
	}

	/** Gets the shape of the points used when drawing to the GraphArea
	 * 
	 * @return The Shape to use when drawing this DataSet to the GraphArea
	 */
	public Shape getShape() 
	{
		return dataSetShape;
	}
	
	/** Gets the Table that contains the data for this DataSet
	 * 
	 * @return The Table containing this DataSet's data
	 */
	public JTable getDataTable() 
	{
		return tableContainingData;
	}

	/** Gets all the currently visible DataColumns in this DataSet
	 * @return An List of all the DataColumns in this DataSet */
	public ArrayList<DataColumn> getColumns() 
	{
		return visibleDataColumns;
	}
	
	/** Gets the name of this DataSet
	 * @return The String containing this DataSet's name */
	public String getName() 
	{
		return dataSetName;
	}
	
	/**
	 * Gets the FitType that this DataSet is using
	 * @return The FitType this DataSet is using
	 */
	public FitType getFitType() 
	{
		return currentFitType;
	}

	/** The DataColumn that keeps track of the x data for this DataSet 
	 * @return The DataColumn that keeps track of the x data values for this DataSet
	 */
	public DataColumn getXData()
	{
		return xData;
	}
	
	/** The DataColumn that keeps track of the y data for this DataSet  
	 * @return The DataColumn that keeps track of the y data values for this DataSet
	 */
	public DataColumn getYData()
	{
		return yData;
	}
	
	/** The DataColumn that keeps track of the x error/uncertainty data for this DataSet  
	 * @return The DataColumn that keeps track of the x errors data values for this DataSet
	 */
	public DataColumn getXErrorData()
	{
		return xErrorData;
	}
	
	/** The DataColumn that keeps track of the y error/uncertainty data for this DataSet  
	 * @return The DataColumn that keeps track of the y error data values for this DataSet
	 */
	public DataColumn getYErrorData()
	{
		return yErrorData;
	}	
	
	/** Returns whether or not this DataSet's Fit Selector is currently locked 
	 * @return Whether or not the fit is currently locked so that it cannot be changed automatically by listeners. True means it is currently locked
	 */
	public boolean isFitLocked()
	{
		return fitLock;
	}
	
	/** Sets the Color to be used when drawing this DataSet to the given Color
	 * @param color The desired Color to use when drawing this DataSet to the GraphArea */
	public void setColor(Color color) 
	{
		changeTracker.setFileModified();
		dataSetColor = color;
		graphArea.repaint();
	}

	/** Sets the shape used for the points of this DataSet when drawing it to the GraphArea to the given Shape
	 * 
	 * @param shape The desired Shape to use when drawing this DataSet's points
	 */
	public void setShape(Shape shape) 
	{
		changeTracker.setFileModified();
		dataSetShape = shape;
	}

	/** Sets the FitType to use for this DataSet to the given FitType
	 * 
	 * @param fit The FitType to use for this DataSet's linear fit
	 */
	public void setFitType(FitType fit) 
	{		
		changeTracker.setFileModified();
		currentFitType = fit;
	}
	
	/** Sets the name of this DataSet to the desired passed name 
	 * 
	 * @param name The new Name of this DataSet
	 */
	public void setName(String name) 
	{
		dataSetName = name;
	}
	
	
	//private classes
	/**
	 * A Listener class on the DataSet table that allows us to update the GraphArea whenever we make changes to the columns
	 * 
	 * @author	Keith Rice
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class GraphSetListener implements TableModelListener 
	{
		/** The event that is called whenever the values in the table have been modified */
		public void tableChanged(TableModelEvent e) 
		{
			DataColumn col;
			if (e.getColumn() >= 0) 
			{
				col = visibleDataColumns.get(e.getColumn());
				col.writeData(e.getFirstRow(), "" + dataTableModel.getValueAt(e.getFirstRow(), e.getColumn()));
				updateFits();
				if (e.getFirstRow() + 1 == dataTableModel.getRowCount()) 
				{
					dataTableModel.insertRow(dataTableModel.getRowCount(), new Object[visibleDataColumns.size()]);
					// tableHeight += 1;
				}
			}
			graphArea.repaint();
		}
	}

}