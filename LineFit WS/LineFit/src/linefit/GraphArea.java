package linefit;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import linefit.FitAlgorithms.FitType;
import linefit.FitAlgorithms.LinearFitFactory;
import linefit.IO.ChangeTracker;

/**
 * The main interface of LineFit. This class is responsible for drawing and calculating the graph as well 
 * as getting and allowing users to input data (all but menus)
 * 
 * @author	Unknown
 * @version	1.1.0
 * @since 	&lt;0.98.0
 */
public class GraphArea extends JPanel 
{
	/** The current serial version UID that changes when the interface of the class is changed */
	private final static long serialVersionUID = 42;
	
	private ChangeTracker changeTracker;
	
	//spacing variables
	/** The width of the points we are using to draw the shapes on the graph */
	final static int GRAPH_DATAPOINT_WIDTH = 6; 
	/** The pixels heigh the top bar which shows the cursor position is */
	final static int GRAPH_AREA_TOP_BAR_HEIGHT = 25;
	/** The ratio used for determining the amount of space between labels in the Graph Area */
	final static double GRAPH_LABEL_SPACING_RATIO = 0.5;
	
	/** The spacing in pixels from the left side of the graphing area container and the actual graph (same as to the y-axis line) */
	private int graphAreaLeftSpacing;
	/** The spacing in pixels from the right of the graphing area container to the right of the actual graph (same as the leftmost point of the x-axis) */
	private int graphAreaRightSpacing;
	/** The spacing in pixels from the bottom of the graphing area container and the actual graph (same as to the x-axis line) */
	private int graphAreaBottomSpacing;
	/** The spacing in pixels from the top of the graphing area container to the top of the actual graph (same as the topmost point of the y-axis) */
	private int graphAreaTopSpacing;

	/** The spacing in pixels between the x-axis and its label */
	private int xAxisNameLabelSpacing;
	/** The spacing in pixels between the y-axis and its label */
	private int yAxisNameLabelSpacing;
	/** The spacing in pixels between the top of the graph and the graph name */
	private int graphAreaNameSpacing;
	/** The spacing in pixels between the x-axis and the x10 power label at its end */
	private int xAxisPowerSpacing;
	/** The spacing in pixels between the y-axis and the x10 power label at its end */
	private int yAxisPowerSpacing;
	/** The spacing in pixels between the lines in the results when they are displayed on the graph */
	private int resultsInbetweenSpacing;
	
	/** The height in pixels of the tick marks on the axes */
	final static int TICK_MARK_HEIGHT = 5;
	/** The spacing in pixels between the tick marks on the x-axis and the numeral label under it */
	private int xAxisTickLabelSpacing;
	/** The spacing in pixels between the tick marks on the y-axis and the right side of the numeral label to the right tick mark */
	private int yAxisTickLabelSpacing;
	/** The spacing in pixels in order to center the numeral below the tick marks on the x-axis */
	private int xAxisTickLabelCentering;
	/** The spacing in pixels in order to center the numeral to the left of the tick marks on the y-axis */
	private int yAxisTickLabelCentering;
	
	//variables for the tick marks on the axes
	/** The default numbers of tick marks on each of the axes */
	final static int DEFAULT_NUMBER_OF_TICK_MARKS = 10;
	/** The current number of tick marks on the x-axis */
	int xAxisNumberOfTickMarks = DEFAULT_NUMBER_OF_TICK_MARKS;
	/** The current number of tick marks on the y-axis */
	int yAxisNumberOfTickMarks = DEFAULT_NUMBER_OF_TICK_MARKS;
	/** Keeps track of whether the x-axis currently has tick marks on it */
	boolean xAxisHasTickMarks = true;
	/** Keeps track of whether the x-axis currently has labels on its tick marks */
	boolean xAxisHasTickMarkLabels = true;
	/** Keeps track of whether the y-axis currently has tick marks on it */
	boolean yAxisHasTickMarks = true;
	/** Keeps track of whether the x-axis currently has labels on its tick marks */
	boolean yAxisHasTickMarkLabels = true;

	//Graph and drawing Variables
	/** The current x position of the cursor */
	private int cursorPositionX;
	/** The current y position of the cursor */
	private int cursorPositionY;
	/** The width in pixels of the graph's name displayed above the graph */
	private int graphNameWidth;
	/** The pixel location of the origin in the x direction */
	private int positionOfOriginX; 
	/** The pixel location of the origin in the y direction */
	private int positionOfOriginY;
	/** The width of the graph area in pixels after the pixels have been subtracted to account for the spacing padding on the sides of the graph */
	private int graphWidthAfterPadding;
	/** The height of the graph area in pixels after the pixels have been subtracted to account for the spacing padding on the top and bottom of the graph */
	private int graphHeightAfterPadding;
	/** The distance along the x-axis that each tick mark represents */
	private double tickMarkRelativeValueX;
	/** The distance along the y-axis that each tick mark represents */
	private double tickMarkRelativeValueY;
	/** The object that contains the current font's dimensional measurements */
	private FontMetrics currentFontMeasurements;
	/** The current dimensions in pixels of the graph area before any are removed for padding */
	private Dimension graphAreaDimensions;
	/** The color used for the background of the graph area */
	final static Color BACKGROUND_COLOR = new Color(250, 250, 250);

	// Graph Property Variables
	/** The name of the current graph */
	private String graphName = "New Graph";
	/** The description that tells what the x-axis measures on this graph */
	private String xAxisDescription = "New X-Axis Description";
	/** The description that tells what the y-axis measures on this graph */
	private String yAxisDescription = "New Y-Axis Description";
	/** The minimum value on the x-axis */
	double xAxisMinimumValue;
	/** The maximum value on the x-axis */
	double xAxisMaximumValue;
	/** The minimum value on the y-axis */
	double yAxisMinimumValue;
	/** The maximum value on the  y-axis */
	double yAxisMaximumValue;	
	
	/** Keeps track of if powers are allowed to be extracted from the axes tick mark values and placed at the end of the axis */
	boolean useAxesPowers = true;
	/** Keeps track of if the user has specified the powers to be used on each axis */
	boolean userDefinedAxesPowers = false;
	/** The power used/extracted from the values on the x-axis */
	int xAxisPower = 0;
	/** The power used/extracted from the values on the x-axis */
	int yAxisPower = 0;
	
	/** Keeps track of if the user has specified the minimums and maximums on the axes */
	boolean userDefinedAxes = false;
	
	/** The number of digits right of the decimal point to use for the values on the x-axis tick marks 
	 * (Note: negative rounds to the left of the decimal place) */
	int xAxisDecimalPlaces = 2;
	/** The number of digits right of the decimal point to use for the values on the y-axis tick marks 
	 * (Note: negative rounds to the left of the decimal place) */
	int yAxisDecimalPlaces = 2;
	
	//Linear fit results options
	/** The number of decimal places to use in the linear fit results */
	int resultsDecimalPlaces = 4;
	/** Keeps track of if the results should be drawn on the graph itself */
	public boolean resultsAreDisplayedOnGraph = true;
	/** The x position in pixels of the bottom right corner of the results from the bottom right corner of the square formed by the axes */
	int resultsPositionX = 5;
	/** The y position in pixels of the bottom right corner of the results from the bottom right corner of the square formed by the axes */
	int resultsPositionY = 25;
	/** Whether or not to use scientific notation for the linear fit's results */
	boolean resultsUseScientificNotation = true;

	//Graph Data variables 
	/** The drop down selector box that allows us to add new datasets and that selects the current 
	 * dataset to display the data of in the columns */
	public JComboBox<DataSet> dataSetSelector;
	/** The linear fit's results as a string */
	private String fitResultsString;
	/** The JTextArea in which the linear fit's results are displayed to the user. This is a TextArea instead of a Label so that the user can copy the results */
	private JTextArea fitResultsArea;

	/** Determines whether the user wants to use x or y errors/uncertainties when there is only three DataColumns
	 * in a DataSet. True means to use x errors/uncertainties and false means to use y errors/uncertainties. */
	boolean xErrorsOnly = false;
	
	/**
	 * Constructor for our graph area that is called by LineFit to create the visual graph
	 * Note: Each LineFit should have only one GraphArea
	 * 
	 * @param defaultXAxisMinimum The starting minimum value on the x-axis
	 * @param defaultXAxisMaximum The starting maximum value on the x-axis
	 * @param defaultYAxisMinimum The starting minimum value on the y-axis
	 * @param defaultYAxisMaximum The starting maximum value on the y-axis
	 * @param dataSetSelectorToUse The data set selector that contains all the data sets to draw on the graph
	 * @param resultsPanelToUse The Results Panel to display the currently selected DataSet's results data as a String in
	 */
	GraphArea(double defaultXAxisMinimum, double defaultXAxisMaximum, double defaultYAxisMinimum, 
			double defaultYAxisMaximum, JComboBox<DataSet> dataSetSelectorToUse, JTextArea resultsPanelToUse,
			ChangeTracker parentsChangeTracker) 
	{
		changeTracker = parentsChangeTracker;
		xAxisMinimumValue = defaultXAxisMinimum;
		xAxisMaximumValue = defaultXAxisMaximum;
		yAxisMinimumValue = defaultYAxisMinimum;
		yAxisMaximumValue = defaultYAxisMaximum;

		dataSetSelector = dataSetSelectorToUse;
		fitResultsArea = resultsPanelToUse;
		
		graphAreaDimensions = getSize();

		addMouseMotionListener(new GraphAreaMouseListener());
	}
	
	/**
	 * Registers the given data set to this graph area by placing it in the DataSet selector box at the end but before the New Data Set option
	 * @param toRegister The DataSet to add to the DataSet selector box
	 */
	void registerDataSet(DataSet toRegister)
	{
		DataSet newDataSet = dataSetSelector.getItemAt(dataSetSelector.getItemCount() - 1);
		dataSetSelector.removeItem(newDataSet);
		dataSetSelector.addItem(toRegister);
		dataSetSelector.setSelectedItem(toRegister);
		dataSetSelector.addItem(newDataSet);
	}

	/**
	 * This method determines the spacing in the x direction for the given tick mark that is being drawn on the x-axis
	 * 
	 * @param tickNumber The index number of the tick that is to be positioned by this method
	 * @return Returns the x pixel position of where the tick mark should be placed
	 */
	int translateTickMarkInX(int tickNumber) 
	{
		return graphAreaLeftSpacing + (int) (tickNumber * (graphWidthAfterPadding / (double)xAxisNumberOfTickMarks));
	}

	/**
	 * This method determines the spacing in the y direction for the given tick mark that is being drawn on the y-axis
	 * 
	 * @param tickNumber The index number of the tick that is to be positioned by this method
	 * @return Returns the y pixel position of where the tick mark should be placed
	 */
	int translateTickMarkInY(int tickNumber) 
	{
		return graphAreaTopSpacing + (int)(((double)yAxisNumberOfTickMarks - tickNumber) * (graphHeightAfterPadding / (double)yAxisNumberOfTickMarks));
	}
	
	/**
	 * Finds the pixel location on the graph of where the given x value is on the graph in the x direction
	 * 
	 * @param pointOnGraphX The x value on the graph to find the pixel location of
	 * @return The pixel that corresponds with the given value on the graph
	 */
	int convertXCoordinateToPixel(double pointOnGraphX) 
	{
		return graphAreaLeftSpacing + (int) ((xAxisMinimumValue - pointOnGraphX) * tickMarkRelativeValueX * -1);
	}
	
	/**
	 * Finds the pixel location on the graph of where the given y value is on the graph in the y direction
	 * 
	 * @param pointOnGraphY The y value on the graph to find the pixel location of
	 * @return The pixel that corresponds with the given value on the graph
	 */
	int convertYCoordinateToPixel(double pointOnGraphY) 
	{
		return graphAreaTopSpacing + (int) ((yAxisMaximumValue - pointOnGraphY) * tickMarkRelativeValueY);
	}

	/**
	 * We overwriting the paint function so that we can access the graphics in order to draw our graph 
	 * This is also we were recalculate our graph in order to update changes
	 */
	public void paint(Graphics g)
	{
		Graphics2D graphAreaGraphics2D = (Graphics2D) g;
		Rectangle size = graphAreaGraphics2D.getClipBounds();
		
		//if it is not the cursor position part we need to redraw the graph first
		if(size.height != GRAPH_AREA_TOP_BAR_HEIGHT || size.width != 500)
		{ 
			graphAreaDimensions = getSize();
			makeGraph(graphAreaGraphics2D, graphAreaDimensions, false, null);
		}
	}
	
	/** 
	 * Calculates the lengths of the axes based on the data points, unless the user has overridden this functionality, so that they are all on screen
	 */
	void calculateAxesMinimumAndMaximumValues() 
	{
	//if (xData != null && yData != null) {
		if(!userDefinedAxes) 
		{
			//we have to subtract one for the new dataset button
			int numberOfDataSets = dataSetSelector.getItemCount() - 1;
			
			boolean xHasInit = false;
			double xDataMax = 0; 
			double xDataMin = 0;
			//look for our largest and smallest values with errors across the datasets
			//boolean hasMoreThanOnePoint = false;
			for(int c = 0; c < numberOfDataSets; c++)
			{
				DataSet current = (DataSet) dataSetSelector.getItemAt(c);
				if (current.visibleGraph)
				{
					/*if((c == 0 && current.xData.getNonNullDataSize() > 1) || (c > 0 && current.xData.getNonNullDataSize() > 0)) {
						hasMoreThanOnePoint = true;
					}*/
					DataColumn dataX = current.xData;
					DataColumn dataXError = current.xErrorData;
					current.refreshFitData();
					for (int i = 0; i < dataX.getData().size(); i++)
					{
						if(!dataX.isNull(i)) 
						{
							double tmpX = dataX.readDouble(i);
							double tmpXErr = 0;
							if(dataXError != null && !dataXError.isNull(i))
							{
								tmpXErr = Math.abs(dataXError.readDouble(i));
							}	
							
							if(xHasInit) 
							{
								if(tmpX + tmpXErr > xDataMax)
								{
									xDataMax = tmpX + tmpXErr;
									//xDataMaxIndex = i;
								} 
								else if(tmpX - tmpXErr < xDataMin) 
								{
									xDataMin = tmpX - tmpXErr;
									//xDataMinIndex = i;
								}
							} 
							else 
							{
								xDataMax = tmpX + tmpXErr;
								xDataMin = tmpX - tmpXErr;
								xHasInit = true;
							}
						}
					}
				}
			}
			
			boolean yHasInit = false;
			double yDataMax = 0;
			double yDataMin = 0;
			//boolean hasMoreThanOnePoint = false;
			for(int c = 0; c < numberOfDataSets; c++)	
			{
				DataSet current = (DataSet) dataSetSelector.getItemAt(c);
				if (current.visibleGraph) 
				{
					/*if((c == 0 && current.yData.getNonNullDataSize() > 1) || (c > 0 && current.yData.getNonNullDataSize() > 0)) {
						hasMoreThanOnePoint = true;
					}*/
					DataColumn dataY = current.yData;
					DataColumn dataYError = current.yErrorData;
					current.refreshFitData();
					for (int i = 0; i < dataY.getData().size(); i++) 
					{
						if(!dataY.isNull(i))
						{
							double tmpY = dataY.readDouble(i);
							double tmpYErr = 0;
							if(dataYError != null && !dataYError.isNull(i)) 
							{
								tmpYErr = Math.abs(dataYError.readDouble(i));
							}	
							
							if(yHasInit)
							{
								if(tmpY + tmpYErr > yDataMax)	
								{
									yDataMax = tmpY + tmpYErr;
									//yDataMaxIndex = i;
								} 
								else if(tmpY - tmpYErr < yDataMin) 
								{
									yDataMin = tmpY - tmpYErr;
									//yDataMinIndex = i;
								}
							} 
							else
							{
								yDataMax = tmpY + tmpYErr;
								yDataMin = tmpY - tmpYErr;
								yHasInit = true;
							}
						}
					}
				}
			}
			
			if(xHasInit && yHasInit) 
			{ //adds some spacing to the sides of the graph so the point is not right on the edge of the graph
				if(xDataMax != xDataMin) 
				{
					double distBetween = xDataMax - xDataMin;
					xAxisMinimumValue = xDataMin - distBetween * 0.1;
					xAxisMaximumValue = xDataMax + distBetween * 0.1;
				} 
				else 
				{
					xAxisMinimumValue = xDataMax - xDataMax * 0.1;
					xAxisMaximumValue = xDataMax + xDataMax * 0.1;
				}

				//xMin = xDataMinToUse - xDataMaxToUse * 0.05;
				//xMax = xDataMaxToUse + xDataMaxToUse * 0.05;
				refreshAxesPower();
				xAxisMaximumValue = Math.ceil(xAxisMaximumValue * Math.pow(10, xAxisDecimalPlaces - xAxisPower - 1)) / Math.pow(10, xAxisDecimalPlaces - xAxisPower - 1);
				xAxisMinimumValue = Math.floor(xAxisMinimumValue * Math.pow(10, xAxisDecimalPlaces - xAxisPower - 1)) / Math.pow(10, xAxisDecimalPlaces - xAxisPower - 1);
				//xMax = Double.parseDouble(ScientificNotation.numberString(xMax, xAxisPower, xAxisDecPlaces - 1)) * Math.pow(10, xAxisPower);
				//xMin = Double.parseDouble(ScientificNotation.numberString(xMin, xAxisPower, xAxisDecPlaces - 1)) * Math.pow(10, xAxisPower);
				refreshAxesPower();
				
				
				if(yDataMax != yDataMin) 
				{
					double distBetween = yDataMax - yDataMin;
					yAxisMinimumValue = yDataMin - distBetween * 0.1;
					yAxisMaximumValue = yDataMax + distBetween * 0.1;
				} 
				else
				{
					yAxisMinimumValue = yDataMax - yDataMax * 0.1;
					yAxisMaximumValue = yDataMax + yDataMax * 0.1;
				}

				//yMin = yDataMinToUse - yDataMaxToUse * 0.05;
				//yMax = yDataMaxToUse + yDataMaxToUse * 0.05;
				refreshAxesPower();
				yAxisMaximumValue = Math.ceil(yAxisMaximumValue * Math.pow(10, yAxisDecimalPlaces - yAxisPower - 1)) / Math.pow(10, yAxisDecimalPlaces - yAxisPower - 1);
				yAxisMinimumValue = Math.floor(yAxisMinimumValue * Math.pow(10, yAxisDecimalPlaces - yAxisPower - 1)) / Math.pow(10, yAxisDecimalPlaces - yAxisPower - 1);
				//yMax = Double.parseDouble(ScientificNotation.numberString(yMax, yAxisPower, yAxisDecPlaces - 1)) * Math.pow(10, yAxisPower);
				//yMin = Double.parseDouble(ScientificNotation.numberString(yMin, yAxisPower, yAxisDecPlaces - 1)) * Math.pow(10, yAxisPower);
			}
		}
	}

	/**
	 * Determines the power to use on the axes so that they stay small unless it is overridden by the user
	 */
	void refreshAxesPower()
	{
		if(useAxesPowers && !userDefinedAxesPowers) 
		{
			xAxisPower = ScientificNotation.getPowerOf(xAxisMaximumValue);
			yAxisPower = ScientificNotation.getPowerOf(yAxisMaximumValue);
		}
	}
	
	/** Calculates and places the correct values in the spacing variables in order to space them correctly base on how big the font is
	 * @param fontSize The size of the fon to be drawn on the graph to find the correct spacing for
	 * @param spaceForCursorLocation Whether or not to calculate leaving space for the cursor location to be drawn at the top on the GraphArea
	 */
	void calculatePaddingForGraphArea(double fontSize, boolean spaceForCursorLocation)
	{
		xAxisTickLabelCentering = currentFontMeasurements.stringWidth(ScientificNotation.withoutTimesTen(xAxisMaximumValue, xAxisPower, xAxisDecimalPlaces)) / 2; 			
		yAxisTickLabelCentering = (currentFontMeasurements.getLeading() + currentFontMeasurements.getAscent() - currentFontMeasurements.getDescent()) / 2;
		xAxisTickLabelSpacing = (int)(fontSize * (1 + GRAPH_LABEL_SPACING_RATIO)); 
		yAxisTickLabelSpacing = (int)(fontSize * GRAPH_LABEL_SPACING_RATIO);

		int spacingStrLength = currentFontMeasurements.stringWidth(ScientificNotation.withoutTimesTen(yAxisMaximumValue, yAxisPower, yAxisDecimalPlaces));
		int spacingStrBasedOnMin = currentFontMeasurements.stringWidth(ScientificNotation.withoutTimesTen(yAxisMinimumValue, yAxisPower, yAxisDecimalPlaces));
		if(spacingStrBasedOnMin > spacingStrLength)
		{
			spacingStrLength = spacingStrBasedOnMin;
		}
			
		yAxisNameLabelSpacing = yAxisTickLabelSpacing + (int)(fontSize * GRAPH_LABEL_SPACING_RATIO * 2) + spacingStrLength; 
		graphAreaLeftSpacing = yAxisNameLabelSpacing + (int)(fontSize * (1 + GRAPH_LABEL_SPACING_RATIO));
		xAxisNameLabelSpacing = xAxisTickLabelSpacing + (int)(fontSize * (1 + GRAPH_LABEL_SPACING_RATIO));
		graphAreaBottomSpacing = xAxisNameLabelSpacing + (int)(fontSize * GRAPH_LABEL_SPACING_RATIO);
		graphAreaNameSpacing = (int)(fontSize * GRAPH_LABEL_SPACING_RATIO);
		graphAreaTopSpacing = graphAreaNameSpacing + (int)(fontSize * (1 + GRAPH_LABEL_SPACING_RATIO));
		if(spaceForCursorLocation)
		{
			//if its for the GUI then we need to account for the cursor position box
			graphAreaTopSpacing += GRAPH_AREA_TOP_BAR_HEIGHT;
		}
		xAxisPowerSpacing = (int)(fontSize * GRAPH_LABEL_SPACING_RATIO);
		calculateGraphAreaRightSpacing(fontSize);
		yAxisPowerSpacing = (int)(fontSize * GRAPH_LABEL_SPACING_RATIO);
		resultsInbetweenSpacing = 0; //just in case people want more space between (or less!)
	}
	
	/** calculates the graph area's righ side spacing based on whether or not the power is displayed and the length of the last tick label
	 * 
	 * @param fontSize The current size of the font
	 */
	private void calculateGraphAreaRightSpacing(double fontSize)
	{
		int tempSpacing = 0;
		//we have to account for if there are powers
		if(useAxesPowers && this.xAxisPower != 0)
		{
			tempSpacing = currentFontMeasurements.stringWidth(ScientificNotation.onlyTimesTen(xAxisPower));
		}
		//if there are labels
		if(xAxisHasTickMarkLabels && xAxisNumberOfTickMarks > 0)
		{	
			int labelSpacing = currentFontMeasurements.stringWidth(ScientificNotation.withoutTimesTen(xAxisMaximumValue, xAxisPower, xAxisDecimalPlaces)) / 2;
			//if the labels spacing is larger than the exponent spacing use them
			if(labelSpacing > tempSpacing)
			{
				tempSpacing = labelSpacing;
			}
		}
		
		//add the other spacing variables
		graphAreaRightSpacing = tempSpacing + xAxisPowerSpacing + (int)(fontSize * GRAPH_LABEL_SPACING_RATIO);
	}
	
	/**
	 * The main workhorse function. It is here that the data all gets plotted and the graph gets drawn on the screen and to a file
	 * 
	 * @param graphGraphics The graphics we are using to draw the graph with
	 * @param graphMaximumDimensions The dimensions of the Graph area to which we are drawing
	 * @param areExporting Whether or not this graph is being drawn to be exported to a file. This changes the top spacing proportions slightly and changes the font size and the spacing to account for the size differnce
	 */
	public void makeGraph(Graphics2D graphGraphics, Dimension graphMaximumDimensions, boolean paintCursorLocation, Font fontToUse)
	{
		refreshAxesPower();
		calculateAxesMinimumAndMaximumValues();
		
		if(fontToUse != null)
		{
			graphGraphics.setFont(fontToUse);
		}
		currentFontMeasurements = graphGraphics.getFontMetrics();

		//populate the padding variables with the correct sizes
		//have to do this so that we dont draw the Jpanel with different sized font when we change the export Font size
		double fontSize = graphGraphics.getFont().getSize();
		calculatePaddingForGraphArea(fontSize, paintCursorLocation);

		
		double gWidth = xAxisMaximumValue - xAxisMinimumValue;
		double gHeight = yAxisMaximumValue - yAxisMinimumValue;
		
		graphWidthAfterPadding = graphMaximumDimensions.width - (graphAreaLeftSpacing + graphAreaRightSpacing);
		graphHeightAfterPadding = graphMaximumDimensions.height - (graphAreaBottomSpacing + graphAreaTopSpacing);
		
		//allows it to scale the position of the results so they stay in relatively the same position when were exporting the graph
		int xResultOnGraphThisPass = resultsPositionX;
		int yResultOnGraphThisPass = resultsPositionY;

		// Determine the translation factors according to the boundaries
		tickMarkRelativeValueX = (graphWidthAfterPadding / gWidth);
		tickMarkRelativeValueY = (graphHeightAfterPadding / gHeight);
		
		// Find where zero is
		positionOfOriginX = convertXCoordinateToPixel(0);
		positionOfOriginY = convertYCoordinateToPixel(0);

		graphGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Build the background rectangle
		Rectangle2D.Double background = new Rectangle2D.Double(0, 0, graphMaximumDimensions.width, graphMaximumDimensions.height);
		graphGraphics.setColor(Color.white);
		graphGraphics.fill(background);

		for (int c = 0; c < dataSetSelector.getItemCount(); c++) 
		{
			DataSet current = (DataSet) dataSetSelector.getItemAt(c);
			//if its not our new set option we draw it
			if(!current.getName().equals("New DataSet")) 
			{
				if (current.visibleGraph) 
				{
					// Get the column associations
					DataColumn dataX = current.xData;
					DataColumn dataY = current.yData;
					DataColumn dataXError = current.xErrorData;
					DataColumn dataYError = current.yErrorData;
					Color currentColor = current.getColor();
					Shape currentShape = current.getShape();
					FitType dataFitType = current.getFitType();
	
					// Plot the points
					if (dataX != null && dataY != null)
					{					
						for (int i = 0; i < Math.max(dataX.getData().size(), dataY.getData().size()); i++)
						{
							graphGraphics.setColor(currentColor);
							// Get the graph points
							double gpX = dataX.readDouble(i);
							double gpY = dataY.readDouble(i);
	
	
							// Calculate the coordinate points
							int cpX = convertXCoordinateToPixel(gpX);
							int cpY = convertYCoordinateToPixel(gpY);
	
							// Draw the points that are in the window area
							if (cpX >= graphAreaLeftSpacing
									&& cpX <= graphMaximumDimensions.width - graphAreaRightSpacing
									&& cpY >= graphAreaTopSpacing
									&& cpY <= graphMaximumDimensions.height - graphAreaBottomSpacing) 
							{
								// If both X and Y values exist, graph, otherwise,
								// don't.
								if (!dataX.isNull(i) && !dataY.isNull(i)) 
								{
									Ellipse2D.Double ellipse = new Ellipse2D.Double();
									Rectangle2D.Double rectangle = new Rectangle2D.Double();
									Polygon triangle = new Polygon();
									if (currentShape.getClass() == ellipse.getClass()) 
									{
										ellipse.setFrame(cpX - (GRAPH_DATAPOINT_WIDTH / 2), cpY - (GRAPH_DATAPOINT_WIDTH / 2), GRAPH_DATAPOINT_WIDTH, GRAPH_DATAPOINT_WIDTH);
										currentShape = (Shape) ellipse;
									} 
									else if (currentShape.getClass() == triangle.getClass()) 
									{
										triangle.addPoint(cpX, cpY - GRAPH_DATAPOINT_WIDTH / 2);
										triangle.addPoint(cpX - GRAPH_DATAPOINT_WIDTH / 2, cpY + GRAPH_DATAPOINT_WIDTH / 2);
										triangle.addPoint(cpX + GRAPH_DATAPOINT_WIDTH / 2, cpY + GRAPH_DATAPOINT_WIDTH / 2);
										currentShape = (Shape) triangle;
									}
									else 
									{
										rectangle.setFrame(cpX - (GRAPH_DATAPOINT_WIDTH / 2), cpY - (GRAPH_DATAPOINT_WIDTH / 2), GRAPH_DATAPOINT_WIDTH, GRAPH_DATAPOINT_WIDTH);
										currentShape = (Shape) rectangle;
									}
									// Ellipse2D.Double drawPoint = new
									// Ellipse2D.Double(cpX-(POINT_WIDTH/2),cpY-(POINT_WIDTH/2),POINT_WIDTH,POINT_WIDTH);
									graphGraphics.fill(currentShape);
									//g2.setColor(Color.gray);
	
									// Draw the X Error Bars
									if (dataXError != null) 
									{
										double heB = dataXError.readDouble(i);
										int heBa = (int) ((heB) * tickMarkRelativeValueX * -1);
										Line2D.Double hErrorBar = new Line2D.Double(cpX + heBa, cpY, cpX - heBa, cpY);
										graphGraphics.draw(hErrorBar);
									}
	
									// Draw the Y Error Bars
									if (dataYError != null) 
									{
										double veB = dataYError.readDouble(i);
										int veBa = (int) ((veB) * tickMarkRelativeValueY);
										Line2D.Double vErrorBar = new Line2D.Double(cpX, cpY + veBa, cpX, cpY - veBa);
										graphGraphics.draw(vErrorBar);
									}
								}
							}
						}
					}
	
					//fitError = false;
					
					current.refreshFitData();
	
					//draw the line for the dataset
					if (dataFitType != FitType.NONE) 
					{
						graphGraphics.setColor(currentColor);
						Line2D.Double fitLine = new Line2D.Double(graphAreaLeftSpacing,
								convertYCoordinateToPixel(xAxisMinimumValue * current.linearFitStrategy.getSlope() + current.linearFitStrategy.getIntercept()),
								graphMaximumDimensions.width - graphAreaRightSpacing, convertYCoordinateToPixel(
										xAxisMaximumValue * current.linearFitStrategy.getSlope() + current.linearFitStrategy.getIntercept()));
						graphGraphics.draw(fitLine);
	
						//extracted this functionality out of this class and into the fitData class
						String slopeDisplay = current.linearFitStrategy.getSlopeAsString(resultsDecimalPlaces, resultsUseScientificNotation, false);
						String interceptDisplay = "";
						interceptDisplay = current.linearFitStrategy.getInterceptAsString(resultsDecimalPlaces, yAxisPower, resultsUseScientificNotation, false);			
						
						//Reintroduced slope on the graph but along with options for a 
						// 	    custom location as well as not displaying it at all
						if(resultsAreDisplayedOnGraph) 
						{
							String cStr = "";
							if(dataSetSelector.getItemCount() > 2) 
							{ //2 because we have the new set option in the set selector
								cStr = convertToSubScript(c + 1);
							}
							int lineHeight = (int)graphGraphics.getFont().getSize() + resultsInbetweenSpacing;
							int relPosX = xResultOnGraphThisPass + getLongestResultsLength();
							int relPosY = yResultOnGraphThisPass + 3 * lineHeight * (dataSetSelector.getItemCount() - 1); // -1 because of the new DataSet option

							graphGraphics.drawString("y" + cStr + " = m" + cStr + "x + b" + cStr, graphMaximumDimensions.width - relPosX - graphAreaRightSpacing, 
									graphMaximumDimensions.height - (relPosY - (c * 3 * lineHeight)) - graphAreaBottomSpacing + lineHeight);
							graphGraphics.drawString("m" + cStr + " = " + slopeDisplay /*+ slopeErrorDisplay*/, graphMaximumDimensions.width - relPosX - graphAreaRightSpacing, 
									graphMaximumDimensions.height - (relPosY - (c * 3 * lineHeight)) - graphAreaBottomSpacing + 2 * lineHeight);
							graphGraphics.drawString("b" + cStr + " = " + interceptDisplay /*+ intErrorDisplay*/, graphMaximumDimensions.width - relPosX - graphAreaRightSpacing, 
									graphMaximumDimensions.height - (relPosY - (c * 3 * lineHeight)) - graphAreaBottomSpacing + 3 * lineHeight); 
						}
						
						//New approach where data goes on sideBar
						if (c == dataSetSelector.getSelectedIndex())
						{
							fitResultsString = "y = mx + b\nm = " + slopeDisplay
									+ /*slopeErrorDisplay +*/ "\nb = " + interceptDisplay
									+ "\n\u03c7\u00B2 = " + current.getChiSquared();
	
							fitResultsArea.setText(fitResultsString);
						}	
					} 
					else 
					{
						if (c == dataSetSelector.getSelectedIndex())
						{
							fitResultsArea.setText("");
						}
					}
				}
			}
		}
		
		// Cover up margins to hide lines and points
		Rectangle2D.Double topMargin = new Rectangle2D.Double(0, 0, graphMaximumDimensions.width,graphAreaTopSpacing);
		Rectangle2D.Double botMargin = new Rectangle2D.Double(0, graphMaximumDimensions.height - graphAreaBottomSpacing, graphMaximumDimensions.width, graphAreaBottomSpacing);
		Rectangle2D.Double leftMargin = new Rectangle2D.Double(0, 0,graphAreaLeftSpacing, graphMaximumDimensions.height);
		Rectangle2D.Double rightMargin = new Rectangle2D.Double(graphMaximumDimensions.width - graphAreaRightSpacing, 0, graphMaximumDimensions.width, graphMaximumDimensions.height);
		graphGraphics.setColor(BACKGROUND_COLOR);
		graphGraphics.fill(topMargin);
		graphGraphics.fill(botMargin);
		graphGraphics.fill(leftMargin);
		graphGraphics.fill(rightMargin);

		// Build the X-Axis
		Line2D.Double xAxis = new Line2D.Double(graphAreaLeftSpacing, graphMaximumDimensions.height
				- graphAreaBottomSpacing, graphMaximumDimensions.width - graphAreaRightSpacing, graphMaximumDimensions.height - graphAreaBottomSpacing);

		// Build the Y-Axis
		Line2D.Double yAxis = new Line2D.Double(graphAreaLeftSpacing, graphAreaTopSpacing,
				graphAreaLeftSpacing, graphMaximumDimensions.height - graphAreaBottomSpacing);

		graphGraphics.setColor(Color.black);
		graphGraphics.draw(xAxis);
		graphGraphics.draw(yAxis);

		// Draw the Graph Title		
		graphNameWidth = currentFontMeasurements.stringWidth(getGraphName());
		graphGraphics.drawString(graphName, graphMaximumDimensions.width / 2 - graphNameWidth / 2, graphAreaTopSpacing - graphAreaNameSpacing);
		
		// Place the X-Axis Label
		graphNameWidth = currentFontMeasurements.stringWidth(getXAxisDescription());
		graphGraphics.drawString(getXAxisDescription(), graphMaximumDimensions.width / 2 - graphNameWidth / 2, graphMaximumDimensions.height - 
				(graphAreaBottomSpacing - xAxisNameLabelSpacing));

		//draw the y axis rotated
		graphGraphics.rotate(-Math.PI / 2.0);
		graphNameWidth = currentFontMeasurements.stringWidth(getYAxisDescription());
		graphGraphics.drawString(getYAxisDescription(), -(graphMaximumDimensions.height / 2 + graphNameWidth / 2), graphAreaLeftSpacing - yAxisNameLabelSpacing);
		graphGraphics.rotate(Math.PI / 2.0); 

		//A new algorithm that places a set number of tick marks given by the user
		//     in the same spacing pattern on the graph regardless of the range or values
		if (xAxisNumberOfTickMarks != 0 && xAxisHasTickMarks) 
		{	
			for (int i = 0; i <= xAxisNumberOfTickMarks; i++) 
			{
				Line2D.Double tickMark = new Line2D.Double(translateTickMarkInX(i),
						graphMaximumDimensions.height - graphAreaBottomSpacing - TICK_MARK_HEIGHT,
						translateTickMarkInX(i), graphMaximumDimensions.height - graphAreaBottomSpacing);
				graphGraphics.draw(tickMark);
				
				if (xAxisHasTickMarkLabels) 
				{
					double labelNum = (xAxisMinimumValue + i * (xAxisMaximumValue - xAxisMinimumValue)/ xAxisNumberOfTickMarks);
					graphGraphics.drawString(ScientificNotation.withoutTimesTen(labelNum, xAxisPower, xAxisDecimalPlaces), translateTickMarkInX(i)
							- xAxisTickLabelCentering, graphMaximumDimensions.height - graphAreaBottomSpacing + xAxisTickLabelSpacing);
				}
			}
			graphGraphics.drawString(ScientificNotation.onlyTimesTen(xAxisPower), graphMaximumDimensions.width - (graphAreaRightSpacing - xAxisPowerSpacing), 
					graphMaximumDimensions.height - graphAreaBottomSpacing);
		}
		if (yAxisNumberOfTickMarks != 0 && yAxisHasTickMarks)
		{
			for (int i = 0; i <= yAxisNumberOfTickMarks; i++) 
			{
				Line2D.Double tickMark = new Line2D.Double(graphAreaLeftSpacing
						+ TICK_MARK_HEIGHT, translateTickMarkInY(i), graphAreaLeftSpacing,
						translateTickMarkInY(i));
				graphGraphics.draw(tickMark);
				if (yAxisHasTickMarkLabels) 
				{
					double labelNum = yAxisMinimumValue + i * (yAxisMaximumValue - yAxisMinimumValue) / yAxisNumberOfTickMarks;
					graphGraphics.drawString(ScientificNotation.withoutTimesTen(labelNum, yAxisPower, yAxisDecimalPlaces), graphAreaLeftSpacing
							- yAxisTickLabelSpacing - currentFontMeasurements.stringWidth(ScientificNotation.withoutTimesTen(labelNum, yAxisPower, yAxisDecimalPlaces)), 
							translateTickMarkInY(i) + yAxisTickLabelCentering);
				}
			}	
			graphGraphics.drawString(ScientificNotation.onlyTimesTen(yAxisPower), graphAreaLeftSpacing - 10, graphAreaTopSpacing - yAxisPowerSpacing);
		}
		
		if(paintCursorLocation)
		{
			addCursorLocationToGraph(graphGraphics);
		}
	}

	private void addCursorLocationToGraph(Graphics2D graphGraphics)
	{
		
		//now we need to update the cursor location bar
		Rectangle2D.Double background = new Rectangle2D.Double(0, 0, this.getWidth(), 25);
		graphGraphics.setColor(Color.white);
		graphGraphics.fill(background);
			
		// Create cursor position display string
		graphGraphics.setColor(Color.gray);
			
		//Prevents rounding errors so that mouse_over coordinates don't display "-0.0"
		double mouseX;
		double mouseY;
		if(Math.abs((cursorPositionX - positionOfOriginX) / tickMarkRelativeValueX) < 0.09999999) 
		{
			mouseX = 0;
		} 
		else 
		{ 
			mouseX = (cursorPositionX - positionOfOriginX) / tickMarkRelativeValueX;
		}
		
		if(Math.abs((cursorPositionY - positionOfOriginY) / tickMarkRelativeValueY * -1) < 0.09999999)
		{
			mouseY = 0;
		} 
		else
		{
			mouseY = (cursorPositionY - positionOfOriginY) / tickMarkRelativeValueY * -1;
		}
		
		String cursorPosition = "(" + ScientificNotation.withNoError(mouseX, xAxisPower, xAxisDecimalPlaces) + "," + 
									ScientificNotation.withNoError(mouseY, yAxisPower, yAxisDecimalPlaces) + ")";
		graphGraphics.drawString(cursorPosition, 5, 15);
	}
	
    /**
     * Converts an integer into a string containing subscript numbers
     * 
     * @param integerToConvert The integer to convert into a subScript
     * @return Returns the integer as a string with the same integer value, but as a subscript
     */
	String convertToSubScript(int integerToConvert)
	{
		String subScr = "";
		String tmpPow = Integer.toString(integerToConvert);
		for(int j = 0; j < tmpPow.length(); j++) 
		{
			switch(tmpPow.charAt(j)) 
			{
				case '0': subScr += "\u2080";break;
				case '1': subScr += "\u2081";break;
				case '2': subScr += "\u2082";break;
				case '3': subScr += "\u2083";break;
				case '4': subScr += "\u2084";break;
				case '5': subScr += "\u2085";break;
				case '6': subScr += "\u2086";break;
				case '7': subScr += "\u2087";break;
				case '8': subScr += "\u2088";break;
				case '9': subScr += "\u2089";break;
			}
		}
		return subScr;
	}
	
	/**
	 * Finds the pixel length of the line of the data sets' linear fits with the longest length
	 * 
	 * @return Returns an integer of the length of the longest line in the results string (0 if none are being displayed)
	 */
	public int getLongestResultsLength()
	{
		String longString = "";
		int numDataSets = dataSetSelector.getItemCount() - 1;
		String numStr = "";
		
		for(int i = 0; i < numDataSets; i++) 
		{
			if(numDataSets > 1) 
			{
				numStr = convertToSubScript(i + 1);
			}
			DataSet current = (DataSet) dataSetSelector.getItemAt(i);
			if (current.visibleGraph && current.hasData()) 
			{
				//current.refreshFitData();

				String slopeString = "m" + numStr + " = " + current.linearFitStrategy.getSlopeAsString(resultsDecimalPlaces, resultsUseScientificNotation, false);
				String interceptString = "b" + numStr + " = " + current.linearFitStrategy.getInterceptAsString(resultsDecimalPlaces, yAxisPower, resultsUseScientificNotation, false);
				
				if(currentFontMeasurements.stringWidth(slopeString) > currentFontMeasurements.stringWidth(longString)) 
				{
					longString = slopeString;
				}
				
				if(currentFontMeasurements.stringWidth(interceptString) > currentFontMeasurements.stringWidth(longString))
				{
					longString = interceptString;
				}
			}
		}
		
		String formula = "y" + numStr + " = m" + numStr + "x + b" + numStr;
		if(currentFontMeasurements.stringWidth(formula) > currentFontMeasurements.stringWidth(longString)) 
		{
			longString = formula;				
		}

		return currentFontMeasurements.stringWidth(longString);
	}

    /**
     * Makes sure all the dataset's third columns are updated so we can change whether we use only x errors of y errors
     * This has to be done because the third column can be either x or y errors/uncertainties
     */
	void refreshAllSetsThirdColumn() 
	{
		//We have to subtract one for the new dataset
		for(int i = 0; i < dataSetSelector.getItemCount() - 1; i++)
		{
			DataSet current = (DataSet) dataSetSelector.getItemAt(i);
			current.refreshThirdColumn();
		}	
		calculateAxesMinimumAndMaximumValues();
	}
	
	boolean readInDataSetLine(String line, boolean newDataSet)
	{
		//if this is a new dataset then see if the last one is a blank one
		//and if it isn't then add one
		if (newDataSet && this.dataSetSelector.getItemAt(dataSetSelector.getItemCount() - 2).hasData())
		{
			//create a new dataset and then read into it
			DataSet readDataSet = new DataSet(this, changeTracker);
			
			//attempt to read in the setting and add it if it was successfully read
			if (readDataSet.readInLine(line))
			{
				//now add it to the drop down
				registerDataSet(readDataSet);
				return true;
			}
			//otherwise don't add it and let the dataset get garbage collected...
			else
			{
				return false;
			}
		}
		else
		{
			//now actually process the line in the dataset
			return this.dataSetSelector.getItemAt(dataSetSelector.getItemCount() - 2).readInLine(line);
		}
	}

	/** Reads in the graph settings from the passed String and stores it in its proper value
	 * @param lineRead The String that contains the line of data which contains a particular graph setting and its value
	 */
	boolean readInGraphSetting(String lineRead) 
	{		
		//split the input into the two parts
		//we can't use split because it will mess up on names
		int firstSpaceIndex = lineRead.indexOf(' ');
		String field = lineRead.substring(0, firstSpaceIndex).toLowerCase();
		String valueForField = lineRead.substring(firstSpaceIndex + 1);
		
		//now read in the option we want
		boolean found = true;
		try
		{
			switch(field)
			{
				case "graphname": setGraphName(valueForField); break;
				case "xaxisdescription": setXAxisDescription(valueForField); break;
				case "yaxisdescription": setYAxisDescription(valueForField); break;
				case "customaxes": case "usecustomaxes": userDefinedAxes = valueForField.toLowerCase().equals("true"); break;
				case "xmin": case "xaxismin": xAxisMinimumValue = Double.parseDouble(valueForField); break;		
				case "xmax": case "xaxismax": xAxisMaximumValue = Double.parseDouble(valueForField); break;
				case "ymin": case "yaxismin": yAxisMinimumValue = Double.parseDouble(valueForField); break;
				case "ymax": case "yaxismax": yAxisMaximumValue = Double.parseDouble(valueForField); break;
				case "poweronaxes": case "usepowersonaxes": useAxesPowers = valueForField.toLowerCase().equals("true"); break;
				case "customaxespowers": userDefinedAxesPowers = valueForField.toLowerCase().equals("true"); break;
				case "xpower": case "xaxispower": xAxisPower = Integer.parseInt(valueForField); break;
				case "ypower": case "yaxispower": yAxisPower = Integer.parseInt(valueForField); break;
				case "hastickmarksx": case "xaxishastickmarks": xAxisHasTickMarks = valueForField.toLowerCase().equals("true"); break;
				case "hasticklabelsx": case "xaxishasticklabels": xAxisHasTickMarkLabels = valueForField.toLowerCase().equals("true"); break;
				case "ticksx": case "xaxisnumberofticks": xAxisNumberOfTickMarks = Integer.parseInt(valueForField); break;
				case "hastickmarksy": case "yaxishastickmarks": yAxisHasTickMarks = valueForField.toLowerCase().equals("true"); break;
				case "hasticklabelsy": case "yaxishasticklabels": yAxisHasTickMarkLabels = valueForField.toLowerCase().equals("true"); break;
				case "ticksy": case "yaxisnumberofticks": yAxisNumberOfTickMarks = Integer.parseInt(valueForField); break;
				case "xdecimals": case "xaxisdecimals": xAxisDecimalPlaces = Integer.parseInt(valueForField); break;
				case "ydecimals": case "yaxisdecimals": yAxisDecimalPlaces = Integer.parseInt(valueForField); break;
				case "resultsongraph": case "displayresultsongraph": resultsAreDisplayedOnGraph = valueForField.toLowerCase().equals("true"); break;
				case "customresultpos": break; //we dont use this anymore
				case "resultposx": case "resultspositionx": resultsPositionX = Integer.parseInt(valueForField); break;
				case "resultposy": case "resultspositiony": resultsPositionY = Integer.parseInt(valueForField); break;
				case "resultdecimals": case "resultsdecimals": resultsDecimalPlaces = Integer.parseInt(valueForField); break;
				case "resultsscinot": case "resultsusescientificnotation": resultsUseScientificNotation = valueForField.toLowerCase().equals("true"); break;
				case "xerrors": case "xerrorsbeforeyerrors": xErrorsOnly = valueForField.toLowerCase().equals("true");
					refreshAllSetsThirdColumn(); break;
				case "fitalgorithm": LineFit.currentFitAlgorithmFactory = LinearFitFactory.getAlgorithmWithName(valueForField);
				default: found = false; break; //if it wasn't an export option return false
			}
		} 
		catch (NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(this, "Error reading in number from line: " + lineRead,
				    "NFE Error", JOptionPane.ERROR_MESSAGE);
		}
		return found;
	}
	
	/** Continues the recursive save of the LineFit File. This function saves the GraphArea and down's data.
	 * Note: Not to be used independently of LineFit.RecursivelySaveLineFitFile()!
	 * @param output The Formatter being used to write the file */
	void retrieveAllSettingsVariables(ArrayList<String> variableNames, ArrayList<String> variableValues)
	{
		variableNames.add("GraphName");
		variableValues.add(getGraphName());
		variableNames.add("XAxisDescription");
		variableValues.add(getXAxisDescription());
		variableNames.add("YaxisDescription");
		variableValues.add(getYAxisDescription());
		
		variableNames.add("UseCustomAxes");
		variableValues.add(Boolean.toString(userDefinedAxes));
		variableNames.add("XAxisMin");
		variableValues.add(Double.toString(xAxisMinimumValue));
		variableNames.add("XAxisMax");
		variableValues.add(Double.toString(xAxisMaximumValue));
		variableNames.add("YAxisMin");
		variableValues.add(Double.toString(yAxisMinimumValue));
		variableNames.add("YAxisMax");
		variableValues.add(Double.toString(yAxisMaximumValue));

		variableNames.add("UsePowersOnAxes");
		variableValues.add(Boolean.toString(useAxesPowers));
		variableNames.add("CustomAxesPowers");
		variableValues.add(Boolean.toString(userDefinedAxesPowers));
		variableNames.add("XAxisPower");
		variableValues.add(Double.toString(yAxisPower));
		variableNames.add("YAxisPower");
		variableValues.add(Double.toString(yAxisPower));
		
		//For tick marks and PDF dimensions
		//output.format("%s", "# PDFPageWidth " + ExportIO.PDFPageWidth + System.getProperty("line.separator"));
		//output.format("%s", "# PDFPageHeight " + ExportIO.PDFPageHeight + System.getProperty("line.separator"));
		//output.format("%s", "# ExportFontSize " + ExportIO.exportFontSize + System.getProperty("line.separator"));

		variableNames.add("XAxisHasTickMarks");
		variableValues.add(Boolean.toString(xAxisHasTickMarks));
		variableNames.add("XAxisHasTickLabels");
		variableValues.add(Boolean.toString(xAxisHasTickMarkLabels));
		variableNames.add("XAxisNumberOfTicks");
		variableValues.add(Double.toString(xAxisNumberOfTickMarks));
		
		variableNames.add("YAxisHasTickMarks");
		variableValues.add(Boolean.toString(yAxisHasTickMarks));
		variableNames.add("YAxisHasTickLabels");
		variableValues.add(Boolean.toString(yAxisHasTickMarkLabels));
		variableNames.add("YAxisNumberOfTicks");
		variableValues.add(Double.toString(yAxisNumberOfTickMarks));
		
		variableNames.add("XAxisDecimals");
		variableValues.add(Double.toString(xAxisDecimalPlaces));
		variableNames.add("YAxisDecimals");
		variableValues.add(Double.toString(yAxisDecimalPlaces));
		
		variableNames.add("DisplayResultsOnGraph");
		variableValues.add(Boolean.toString(resultsAreDisplayedOnGraph));
		variableNames.add("ResultsPositionX");
		variableValues.add(Double.toString(resultsPositionX));
		variableNames.add("ResultsPositionY");
		variableValues.add(Double.toString(resultsPositionY));
		variableNames.add("ResultsDecimals");
		variableValues.add(Integer.toString(resultsDecimalPlaces));
		variableNames.add("ResultsUseScientificNotation");
		variableValues.add(Boolean.toString(resultsUseScientificNotation));
		
		variableNames.add("XErrorsBeforeYErrors");
		variableValues.add(Boolean.toString(xErrorsOnly));

		variableNames.add("FitAlgorithm");
		variableValues.add(LineFit.currentFitAlgorithmFactory.toString());
	}
	

	void retrieveAllDataSetVariables(ArrayList<String> variableNames, ArrayList<String> variableValues)
	{
		//pass it on to our datasets
		for(int i = 0; i < dataSetSelector.getItemCount(); i++)
		{
			//if we arent just the new set option
			if(!dataSetSelector.getItemAt(i).getName().equals("New DataSet")) 
			{
				dataSetSelector.getItemAt(i).retrieveAllDataSetVariables(variableNames, variableValues);
			}
		}
	}
	
	
	
	// Getters and Setters for graphName and axisDescriptions
	/**
	 * Sets the description of the x-axis to the given String
	 * 
	 * @param axisDescription The new description/name of the x-axis that is displayed along it
	 */
	void setXAxisDescription(String axisDescription)
	{
		xAxisDescription = axisDescription;
	}
	
	/**
	 * Sets the description of the y-axis to the given String
	 * 
	 * @param axisDescription The new description/name of the y-axis that is displayed along it
	 */
	void setYAxisDescription(String axisDescription)
	{
		yAxisDescription = axisDescription;
	}

	/**
	 * Gets the current description of the x-axis
	 * 
	 * @return Returns a String of the current x-axis description/name
	 */
	String getXAxisDescription() 
	{
		return xAxisDescription;
	}

	/**
	 * Gets the current description of the y-axis
	 * 
	 * @return Returns a String of the current y-axis description/name
	 */
	String getYAxisDescription()
	{
		return yAxisDescription;
	}

	/** 
	 * Sets the graph's name to the inputed String
	 * 
	 * @param graphName The new name of the graph
	 */
	void setGraphName(String graphName) 
	{
		this.graphName = graphName;
	}

	/**
	 * Gets the current name of the graph
	 * 
	 * @return Returns a String of the graph's current name
	 */
	String getGraphName() 
	{
		return graphName;
	}
	
	public class GraphAxesRanges
	{
		public double xAxisMinimumValue = 0;
		public double xAxisMaximumValue = 0;
		public double yAxisMinimumValue = 0;
		public double yAxisMaximumValue = 0;
	}
	
	public GraphAxesRanges GetGraphAxesRanges()
	{
		GraphAxesRanges rangesData = new GraphAxesRanges();
		rangesData.xAxisMinimumValue = xAxisMinimumValue;
		rangesData.xAxisMaximumValue = xAxisMaximumValue;
		rangesData.yAxisMinimumValue = yAxisMinimumValue;
		rangesData.yAxisMaximumValue = yAxisMaximumValue;
		return rangesData;
	}

	public class GraphAxesPowers
	{
		public int xAxisPower = 0;
		public int yAxisPower = 0;
	}
	
	public GraphAxesPowers GetGraphAxesPowers()
	{
		GraphAxesPowers powerData = new GraphAxesPowers();
		powerData.xAxisPower = xAxisPower;
		powerData.yAxisPower = yAxisPower;
		return powerData;
	}

	public class GraphMetaData
	{
		public String graphName = "";
		public String xAxisDescription = "";
		public String yAxisDescription = "";
		public Boolean xAxisHasTickMarks = false;
		public Boolean yAxisHasTickMarks = false;
		public int xAxisNumberOfTickMarks = 0;
		public int yAxisNumberOfTickMarks = 0;
		public int xAxisDecimalPlaces = 0;
		public int yAxisDecimalPlaces = 0;
	}

	public GraphMetaData GetGraphMetaData()
	{
		GraphMetaData metaData = new GraphMetaData();
		metaData.graphName = graphName;
		metaData.xAxisDescription = xAxisDescription;
		metaData.yAxisDescription = yAxisDescription;
		metaData.xAxisHasTickMarks = xAxisHasTickMarks;
		metaData.yAxisHasTickMarks = yAxisHasTickMarks;
		metaData.xAxisNumberOfTickMarks = xAxisNumberOfTickMarks;
		metaData.yAxisNumberOfTickMarks = yAxisNumberOfTickMarks;
		metaData.xAxisDecimalPlaces = xAxisDecimalPlaces;
		metaData.yAxisDecimalPlaces = yAxisDecimalPlaces;
		return metaData;
	}

	public class ResultsDisplayData
	{
		public int graphWidthAfterPadding = 0;
		public int graphHeightAfterPadding = 0;
		public int resultsPositionX = 0;
		public int resultsPositionY = 0;
		public int resultsDecimalPlaces = 0;
		public boolean resultsUseScientificNotation = false;
	}
	
	public ResultsDisplayData GetResultsDisplayData()
	{
		ResultsDisplayData displayData = new ResultsDisplayData();
		displayData.graphWidthAfterPadding = graphWidthAfterPadding;
		displayData.graphHeightAfterPadding = graphHeightAfterPadding; 
		displayData.resultsPositionX = resultsPositionX;
		displayData.resultsPositionY = resultsPositionY;
		displayData.resultsDecimalPlaces = resultsDecimalPlaces;
		displayData.resultsUseScientificNotation = resultsUseScientificNotation;
		return displayData;
	}

	public FontMetrics GetGraphFontMetrics()
	{
		// FontMetrics and its Font are both immutable so it is safe to let the
		// reference escape (not to mention they don't have copy functions)
		return currentFontMeasurements;
	}
	
	//Private Classes
	/**
	 * A private that class that allows us to get input about the mouse's position on our frame
	 * 
	 * @author Keith Rice
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class GraphAreaMouseListener implements MouseMotionListener 
	{	
		/**
		 * Function called when the cursor is moved and it repaints only the top left of the graph so
		 * we can update the cursor position without having to redraw the graph
		 * 
		 * @param passedMouseEvent The mouse event passed by the system when this listener is called
		 */
		public void mouseMoved(MouseEvent passedMouseEvent) 
		{
			cursorPositionX = passedMouseEvent.getX();
			cursorPositionY = passedMouseEvent.getY();
			repaint(0,0,500,25);
		}

		/**
		 * We do not check for dragging but we must have it because we implement MouseMotionListener, 
		 * but it is an empty function
		 * 
		 * @param passedMouseEvent The mouse event passed by the system when this listener is called
		 */
		public void mouseDragged(MouseEvent passedMouseEvent) {}
	}
	
}