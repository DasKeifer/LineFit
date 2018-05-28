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
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import linefit.FitAlgorithms.FitType;
import linefit.FitAlgorithms.FixedVariable;
import linefit.FitAlgorithms.LinearFitFactory;
import linefit.IO.GeneralIO;

/**
 * This class handles the creation and the display of the graph options menu
 * 
 * @author	Unknown
 * @version	1.1.0
 * @since 	&lt;0.98.0
 */
class GraphOptionsMenu extends JFrame 
{
	private final static long serialVersionUID = 42;
    
    //Pointers we need
	/** The GraphArea that the options are being changed of */
    private GraphArea graphingArea;
    private GeneralIO ioHandler;
    
    //Graph name and axes labels variables
    /** The label for the graph name TextField */
    private JLabel graphNameLabel;
    /** The label for the x-axis name TextField */
    private JLabel xAxisNameLabel;
    /** The label for the y-axis name TextField */
    private JLabel yAxisNameLabel;
    /** The label for the name of the graph, displayed above the graph */
    private JTextField graphNameField;
    /** The label for the x-axis label */
    private JTextField xAxisNameField;
    /** The label for the y-axis label */
    private JTextField yAxisNameField;
	
	//Minimum and maximum setting variables
    /** The checkbox that allows the user to specify the axes minimum and maximums instead of being dynamically adjusted by the program */
    private JCheckBox customAxesCheckBox;
    /** The label for the x-axis minimum value TextField */
    private JLabel xMinLabel;
    /** The TextField for the x-axis minimum value */
    private JTextField xMinField;
    /** The label for the x-axis maximum value TextField */
    private JLabel xMaxLabel;
    /** The TextField for the x-axis maximum value */
    private JTextField xMaxField;
    /** The label for the y-axis minimum value TextField */
    private JLabel yMinLabel;
    /** The TextField for the y-axis minimum value */
    private JTextField yMinField;
    /** The label for the y-axis maximum value TextField */
    private JLabel yMaxLabel;
    /** The TextField for the y-axis maximum value */
    private JTextField yMaxField;
    
    //Powers on the axes setting variables
    /** The check box that lets the user specify whether or not they want to use powers on the axes (extract powers of ten from the labels) */
    private JCheckBox usePowersCheckBox;
    /** The check box that lets the user specify the powers to use (extract from the labels) on the axes */
    private JCheckBox customPowersCheckBox;
    /** The label for the x-axis power TextField */
    private JLabel xPowerLabel;
    /** The label for the y-axis power TextField */
    private JLabel yPowerLabel;
    /** The TextField for the power used/extracted from the x-axis */
    private JTextField xPowerField;
    /** The TextField for the power used/extracted from the y-axis */
    private JTextField yPowerField;

    //apply buttons
    /** The button that applies any changes and then closes the window */
    private JButton applyCloseButton;
    /** The button that only applies changes and leaves the window open */
    private JButton applyButton;
    /** The button that restores all the default settings and values in the GraphOptions */
    private JButton defaultsButton;
    
    //PDF sizing variables
    /** The label for the PDF export size TextFields */
    private JLabel pdfSizeLabel;
    /** The label for the multiplication mark between the PDF export size textFields */
    private JLabel pdfTimesLabel;
    /** The TextField for the PDF export width in inches */
    private JTextField pdfWidthField;
    /** The TextField for the  PDF export height in inches */
    private JTextField pdfHeightField;
    
    //Tick mark and decimals setting variables
    /** The label for the x-axis tick marks section */
    private JLabel xAxisTickMarksSectionLabel;
    /** The checkbox that determines whether or not there are tick marks on the x-axis */
    private JCheckBox xShowTicksCheckBox;
    /** The label for the x-axis number of tick marks TextField */
    private JLabel xNumberOfTicksLabel;
    /** The TextField for the number of tick marks on the x-axis */
    private JTextField xNumberOfTicksField;
    /** The checkbox that determines whether or not the tick marks have labels on the x-axis */
    private JCheckBox xShowTicksNumberLabelsCheckBox;
    /** The label for the x-axis number of decimal places to use for the labels TextField */
    private JLabel xAxisDecimalPlacesLabel;
    /** The TextField for the number of decimal places to use on the x-axis */
    private JTextField xAxisDecimalPlacesField;
    
    /** The label for the y-axis tick marks section */
    private JLabel yAxisTickMarksSectionLabel;
    /** The checkbox that determines whether or not the tick marks are shown on y-axis */
    private JCheckBox yShowTicksCheckBox;
    /** The label for the y-axis number of tick marks TextField */
    private JLabel yNumberOfTicksLabel;
    /** The TextField for the number of tick marks on the y-axis */
    private JTextField yNumberOfTicksField;
    /** The checkbox that determines whether or not the tick marks have labels on the y-axis */
    private JCheckBox yShowTicksNumberLabelsCheckBox;
    /** The label for the y-axis number of decimal places to use for the labels TextField */
    private JLabel yAxisDecimalPlacesLabel;
    /** The TextField for the number of decimal places to use on the y-axis */
    private JTextField yAxisDecimalPlacesField;
    
    //LaTex export option variables
    /** The label for the The LaTex export size TextFields */
    private JLabel LaTexSizeLabel;
    /** The TextField for the LaTex export width in cm */
    private JTextField LaTexWidthField;
    /** The label for the LaTex export multiplication sign between the two TextField */
    private JLabel LaTexTimesSymbol;
    /** The TextField for the LaTex export height in cm */
    private JTextField LaTexHeightField;
    /** The label for the export font size to use */
    private JLabel exportFontSizeLabel;
    /** The Spinner that allows the user to change the export font size with */
    private JSpinner exportFontSize;
    
    //Results on graph setting variables
    /** The checkbox that determines whether or not the results are displayed in the GraphArea on the Graph */
    private JCheckBox displayResultsOnGraphCheckBox;
    /** The label for the x location of the results on the graph */
    private JLabel xResultsLocationLabel;
    /** The TextField for the x location of the results on the graph */
    private JTextField xResultsLocationField;
    /** The label for the y location of the results on the graph */
    private JLabel yResultsLocationLabel;
    /** The TextField for the y-location of the results on the graph */
    private JTextField yResultsLocationField;
    
    //Results formatting setting variables
    /** The label for the decimal places to use in the linear fit's results */
    private JLabel decimalsInResultsLabel;
    /** The TextField for the number of decimal places to use in the linear fit's results */
    private JTextField decimalsInResultsField;
    /** The checkbox that determines whether or not the results are displayed in Scientific Notation */
    private JCheckBox useScientificNotationInResultsCheckBox;
    
    //X errors only setting variable
    /** The checkbox that determines whether or not the third column in the DataSets, when there are only three, is the x or y error/uncertainty (checked is x, unchecked is y) */
    private JCheckBox xErrorsOnlyCheckBox;
    
    //for choosing the fit type
    /** the label for selecting which Fit Algorithm to use */
    private JLabel fitAlgorithmLabel;
    /** The drop down that allows the user to select which fit algorithm to use */
    private JComboBox<LinearFitFactory> fitAlgorithmFactoryComboBox;
    
    //Fixed value setting variables
    /** The label for the group of variables that deal with fixing values of the fit */
    private JLabel whatIsFixedGroupLabel;
    /** The label for the GraphDataSet selector to modify */
    private JLabel whichGraphSetLabel;
    /** The JComboBox that chooses which GraphDataSet is currently being edited */
    private JComboBox<DataSet> whichGraphSetComboBox;
    /** The index of the current GraphDataSet being modified */
    private int currentGraphSetIndex;
    /** An array of ints that keeps track of what fixed variable is selected for each GraphDataSet so the user does not have to apply changes before modifying another GraphDataSet */
    private FixedVariable[] graphSetsTempWhatFixed;
    /** An array of the values of the fixed varaible for each of the GraphDataSets so the user does not have to apply changes before modifying another GraphDataSet */
    private double[] graphSetsTempFixedValues;
    /** The group of three buttons that allows the user to specify what variable is fixed in the linear fit for the current GraphDataSet */
    private ButtonGroup whatIsFixedButtonGroup;
    /** The radio button in the what variable is fixed button group that represents no value is fixed */
    private JRadioButton noneFixedRadio;
    /** The radio button in the what variable is fixed button group that represents the slope being fixed */
    private JRadioButton slopeFixedRadio;
    /** The radio button in the what variable is fixed button group that represents the intercept being fixed */
    private JRadioButton interceptFixedRadio;
    /** The label for the value of the fixed variable for the current GraphDataSet TextField */
    private JLabel fixedValueLabel;
    /** The textField for the value of the fixed variable for the current GraphDataSet */
    private JTextField fixedValueField;
    
    /**
     * This function when called restores the default start up option for the graph options
     */
    private void resetToDefaults()
    {
		graphNameField.setText("New Graph");
		xAxisNameField.setText("New X-Axis Description");
		yAxisNameField.setText("New Y-Axis Description");
		
		customAxesCheckBox.setSelected(false);
		xMinField.setText("" + LineFit.DEFAULT_X_AXIS_MINIMUM_VALUE);
		xMaxField.setText("" + LineFit.DEFAULT_X_AXIS_MAXIMUM_VALUE);
		yMinField.setText("" + LineFit.DEFAULT_Y_AXIS_MINIMUM_VALUE);
		yMaxField.setText("" + LineFit.DEFAULT_Y_AXIS_MAXIMUM_VALUE);

		usePowersCheckBox.setSelected(true);
		customPowersCheckBox.setSelected(false);
		xPowerField.setText("1");
		yPowerField.setText("1");

		pdfWidthField.setText("8.50");
		pdfHeightField.setText("8.50");
		
		xShowTicksCheckBox.setSelected(true);
		xNumberOfTicksField.setText("10");
		xShowTicksNumberLabelsCheckBox.setSelected(true);
		xAxisDecimalPlacesField.setText("2");
		
		yShowTicksCheckBox.setSelected(true);
		yNumberOfTicksField.setText("10");
		yShowTicksNumberLabelsCheckBox.setSelected(true);
		yAxisDecimalPlacesField.setText("2");
		
		LaTexWidthField.setText("15.0");
		LaTexHeightField.setText("15.0");
		exportFontSize.setValue(12.0);
		
		displayResultsOnGraphCheckBox.setSelected(true);
		xResultsLocationField.setText("5");
		yResultsLocationField.setText("25");
		
		decimalsInResultsField.setText("4");
		useScientificNotationInResultsCheckBox.setSelected(true);
		
		xErrorsOnlyCheckBox.setSelected(false);
		
		for(int i = 0; i < graphSetsTempWhatFixed.length; i++) 
		{
			graphSetsTempWhatFixed[i] = FixedVariable.NONE;
			graphSetsTempFixedValues[i] = 0.0;
		}
		noneFixedRadio.setSelected(true);
		fixedValueField.setText("0.0");

		updateGraphOptionsEnabledStatuses();
	}

    /**
     * Creates the options frame and ties it back to our GraphArea so we can edit its values.
     * It is possible to have multiple GraphOptionFrames at once.
     * 
     * @param graphAreaToEditOptionsOf the GraphArea whose options and settings will be edited
     * @param currentSetInGraphArea The currently selected GraphDataSet that is select in the given GraphArea
     */
	GraphOptionsMenu(GraphArea graphAreaToEditOptionsOf, DataSet currentSetInGraphArea, GeneralIO parentIoHandler) 
	{
		super("Graph Settings");
		ioHandler = parentIoHandler;

		//set our icon
		this.setIconImage(ioHandler.getLineFitIcon());
		
		setResizable(false);
		graphingArea = graphAreaToEditOptionsOf;
		GraphOptionsFrameLayout customLayout = new GraphOptionsFrameLayout();

		getContentPane().setFont(new Font("Arial", Font.PLAIN, 12));
		getContentPane().setLayout(customLayout);
		
		FocusListener onlyNumbers = new OnlyAllowNumbersListener();
		
		xMinField = new JTextField("textfield_1");
		getContentPane().add(xMinField);
		xMinField.setText("" + graphingArea.xAxisMinimumValue);
		xMinField.addFocusListener(onlyNumbers);
		
		xMaxField = new JTextField("textfield_1");
		getContentPane().add(xMaxField);
		//xMaxField.setText(df.format(draw.xMax));
		xMaxField.setText("" + graphingArea.xAxisMaximumValue);
		xMaxField.addFocusListener(onlyNumbers);

		yMinField = new JTextField("textfield_3");
		getContentPane().add(yMinField);
		//yMinField.setText(df.format(draw.yMin));
		yMinField.setText("" + graphingArea.yAxisMinimumValue);
		yMinField.addFocusListener(onlyNumbers);

		yMaxField = new JTextField("textfield_2");
		getContentPane().add(yMaxField);
		//yMaxField.setText(df.format(draw.yMax));
		yMaxField.setText("" + graphingArea.yAxisMaximumValue);
		yMaxField.addFocusListener(onlyNumbers);

		xMinLabel = new JLabel("X Min");
		getContentPane().add(xMinLabel);

		xMaxLabel = new JLabel("X Max");
		getContentPane().add(xMaxLabel);

		yMinLabel = new JLabel("Y Min");
		getContentPane().add(yMinLabel);

		yMaxLabel = new JLabel("Y Max");
		getContentPane().add(yMaxLabel);

		getContentPane().add(new JLabel()); //temporary to keep the values from changing
		getContentPane().add(new JLabel());
		getContentPane().add(new JLabel());
		getContentPane().add(new JLabel());

		applyCloseButton = new JButton("Apply and Close");
		getContentPane().add(applyCloseButton);

		applyButton = new JButton("Apply");
		getContentPane().add(applyButton);
		
		defaultsButton = new JButton("Defaults");
		getContentPane().add(defaultsButton);

		getContentPane().add(new JLabel());
		getContentPane().add(new JLabel());
		getContentPane().add(new JLabel());
		getContentPane().add(new JLabel());

		xAxisNameLabel = new JLabel("Name the X-Axis");
		getContentPane().add(xAxisNameLabel);

		yAxisNameLabel = new JLabel("Name the Y-Axis");
		getContentPane().add(yAxisNameLabel);

		graphNameLabel = new JLabel("Name the Graph");
		getContentPane().add(graphNameLabel);

		graphNameField = new JTextField("");
		graphNameField.setText(graphingArea.getGraphName());
		getContentPane().add(graphNameField);

		xAxisNameField = new JTextField("");
		xAxisNameField.setText(graphingArea.getXAxisDescription());
		getContentPane().add(xAxisNameField);

		yAxisNameField = new JTextField("");
		yAxisNameField.setText(graphingArea.getYAxisDescription());
		getContentPane().add(yAxisNameField);

		pdfSizeLabel = new JLabel("PDF Export Size (in inches)");
		pdfSizeLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		getContentPane().add(pdfSizeLabel);

		pdfWidthField = new JTextField("");
		pdfWidthField.setText("" + ioHandler.exportIO.pdfPageWidth);
		//PDFWidthField.setHorizontalAlignment(JTextField.CENTER);
		getContentPane().add(pdfWidthField);
		pdfWidthField.addFocusListener(onlyNumbers);

		pdfTimesLabel = new JLabel("x");
		pdfTimesLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		getContentPane().add(pdfTimesLabel);
		//getContentPane().add(new JLabel());

		pdfHeightField = new JTextField("");
		pdfHeightField.setText("" + ioHandler.exportIO.pdfPageHeight);
		//PDFHeightField.setHorizontalAlignment(JTextField.CENTER);
		getContentPane().add(pdfHeightField);
		pdfHeightField.addFocusListener(onlyNumbers);
		
		xAxisTickMarksSectionLabel = new JLabel("<html> <u> X-Axis </u> </html>");
		xAxisTickMarksSectionLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		getContentPane().add(xAxisTickMarksSectionLabel);

		yAxisTickMarksSectionLabel = new JLabel("<html> <u> Y-Axis </u> </html>");
		yAxisTickMarksSectionLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		getContentPane().add(yAxisTickMarksSectionLabel);

		xNumberOfTicksLabel = new JLabel("Number of Ticks");
		getContentPane().add(xNumberOfTicksLabel);

		xNumberOfTicksField = new JTextField("");
		xNumberOfTicksField.setText("" + graphingArea.xAxisNumberOfTickMarks);
		getContentPane().add(xNumberOfTicksField);
		xNumberOfTicksField.addFocusListener(onlyNumbers);

		yNumberOfTicksLabel = new JLabel("Number of Ticks");
		getContentPane().add(yNumberOfTicksLabel);

		yNumberOfTicksField = new JTextField("");
		yNumberOfTicksField.setText("" + graphingArea.yAxisNumberOfTickMarks);
		getContentPane().add(yNumberOfTicksField);
		yNumberOfTicksField.addFocusListener(onlyNumbers);

		xShowTicksCheckBox = new JCheckBox("Show Tick Marks");
		xShowTicksCheckBox.setToolTipText("When checked, tick marks are drawn on the X-axis");
		xShowTicksCheckBox.setSelected(graphingArea.xAxisHasTickMarks);
		getContentPane().add(xShowTicksCheckBox);

		xShowTicksNumberLabelsCheckBox = new JCheckBox("Show Tick Mark Numbers");
		xShowTicksNumberLabelsCheckBox.setToolTipText("When checked, puts the location labels below the ticks on the X-axis");
		xShowTicksNumberLabelsCheckBox.setSelected(graphingArea.xAxisHasTickMarkLabels);
		getContentPane().add(xShowTicksNumberLabelsCheckBox);

		yShowTicksCheckBox = new JCheckBox("Show Tick Marks");
		yShowTicksCheckBox.setToolTipText("When checked, tick marks are drawn on the Y-axis");
		yShowTicksCheckBox.setSelected(graphingArea.yAxisHasTickMarks);
		getContentPane().add(yShowTicksCheckBox);

		yShowTicksNumberLabelsCheckBox = new JCheckBox("Show Tick Mark Numbers");
		yShowTicksNumberLabelsCheckBox.setToolTipText("When checked, puts the location labels below the ticks on the Y-axis");
		yShowTicksNumberLabelsCheckBox.setSelected(graphingArea.yAxisHasTickMarkLabels);
		getContentPane().add(yShowTicksNumberLabelsCheckBox);
		
		xErrorsOnlyCheckBox = new JCheckBox("x errors only");
		xErrorsOnlyCheckBox.setToolTipText("When checked, the third column by itself will represent x errors. Otherwise, it will represent y errors.");
		xErrorsOnlyCheckBox.setSelected(graphingArea.xErrorsOnly);
		getContentPane().add(xErrorsOnlyCheckBox); 
		
		
		usePowersCheckBox = new JCheckBox("Use Powers On Axes");
		usePowersCheckBox.setToolTipText("When checked, powers of ten are taken out from the axes and placed on the end of the axes");
		usePowersCheckBox.setSelected(graphingArea.useAxesPowers);
		getContentPane().add(usePowersCheckBox);
		customPowersCheckBox = new JCheckBox("Use Custom Powers On Axes");
		customPowersCheckBox.setToolTipText("When checked, how many powers of ten to take from each axes is determined by the fields below instead of it being automatically determined");
		customPowersCheckBox.setSelected(graphingArea.userDefinedAxesPowers);
		getContentPane().add(customPowersCheckBox);
		xPowerLabel = new JLabel("X Axis Power");
		getContentPane().add(xPowerLabel);
		xPowerField = new JTextField();
		xPowerField.setText("" + graphingArea.xAxisPower);
		getContentPane().add(xPowerField);
		xPowerField.addFocusListener(onlyNumbers);
		yPowerLabel = new JLabel("Y Axis Power");
		getContentPane().add(yPowerLabel);
		yPowerField = new JTextField();
		yPowerField.setText("" + graphingArea.yAxisPower);
		getContentPane().add(yPowerField);
		yPowerField.addFocusListener(onlyNumbers);
		
		customAxesCheckBox = new JCheckBox("Use Custom Axes Sizes");
		customAxesCheckBox.setToolTipText("When checked, the axes starts and ends are determines by the fields below instead of being automatically determined");
		customAxesCheckBox.setSelected(graphingArea.userDefinedAxes);
		getContentPane().add(customAxesCheckBox);
		
		displayResultsOnGraphCheckBox = new JCheckBox("Display Results on the Graph");
		displayResultsOnGraphCheckBox.setToolTipText("When checked, the slope and intercept of fits are displayed on the graph and will be exported along with the graph");
		displayResultsOnGraphCheckBox.setSelected(graphingArea.resultsAreDisplayedOnGraph);
		getContentPane().add(displayResultsOnGraphCheckBox);
		xResultsLocationLabel = new JLabel("X pos. of results");
		getContentPane().add(xResultsLocationLabel);
		xResultsLocationField = new JTextField();
		xResultsLocationField.setText("" + graphingArea.resultsPositionX);
		getContentPane().add(xResultsLocationField);
		xResultsLocationField.addFocusListener(onlyNumbers);
		yResultsLocationLabel = new JLabel("Y pos. of results");
		getContentPane().add(yResultsLocationLabel);
		yResultsLocationField = new JTextField();
		yResultsLocationField.setText("" + graphingArea.resultsPositionY);
		getContentPane().add(yResultsLocationField);
		yResultsLocationField.addFocusListener(onlyNumbers);
		
		decimalsInResultsLabel = new JLabel("Decimals in Results");
		getContentPane().add(decimalsInResultsLabel);
		decimalsInResultsField = new JTextField();
	    decimalsInResultsField.setText("" + graphingArea.resultsDecimalPlaces);
		getContentPane().add(decimalsInResultsField);
		decimalsInResultsField.addFocusListener(onlyNumbers);
		
		xAxisDecimalPlacesLabel = new JLabel("Decimals on X Axis");
		getContentPane().add(xAxisDecimalPlacesLabel);
		xAxisDecimalPlacesField = new JTextField();
	    xAxisDecimalPlacesField.setText("" + graphingArea.xAxisDecimalPlaces);
		getContentPane().add(xAxisDecimalPlacesField);	    
		xAxisDecimalPlacesField.addFocusListener(onlyNumbers);
		yAxisDecimalPlacesLabel = new JLabel("Decimals on Y Axis");
		getContentPane().add(yAxisDecimalPlacesLabel);
		yAxisDecimalPlacesField = new JTextField();
		yAxisDecimalPlacesField.setText("" + graphingArea.yAxisDecimalPlaces);
		getContentPane().add(yAxisDecimalPlacesField);	
		yAxisDecimalPlacesField.addFocusListener(onlyNumbers);
		
		
	    LaTexSizeLabel = new JLabel("LaTex Export Size (in cm)");
	    LaTexSizeLabel.setFont(new Font("Verdana", Font.BOLD, 12));
	    getContentPane().add(LaTexSizeLabel);
	    LaTexWidthField = new JTextField();
	    LaTexWidthField.setText("" + ioHandler.exportIO.laTexGraphWidthInCm);
	    getContentPane().add(LaTexWidthField);
	    LaTexWidthField.addFocusListener(onlyNumbers);
		LaTexTimesSymbol = new JLabel("x");
		LaTexTimesSymbol.setFont(new Font("Verdana", Font.BOLD, 12));
		getContentPane().add(LaTexTimesSymbol);
		LaTexHeightField = new JTextField();
	    LaTexHeightField.setText("" + ioHandler.exportIO.laTexGraphHeightInCm);
		getContentPane().add(LaTexHeightField);
		LaTexHeightField.addFocusListener(onlyNumbers);
		exportFontSizeLabel = new JLabel("Exporting Font Size");		
		getContentPane().add(exportFontSizeLabel);
		SpinnerNumberModel laTexSpinnerModel = new SpinnerNumberModel(ioHandler.exportIO.exportFontSize, 4.0, 32.0, 0.5);
		exportFontSize = new JSpinner(laTexSpinnerModel);
		JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(exportFontSize, "0.0");
		exportFontSize.setEditor(numberEditor);
		getContentPane().add(exportFontSize);
		
		useScientificNotationInResultsCheckBox = new JCheckBox("Results Use Scientific Notation");
		useScientificNotationInResultsCheckBox.setToolTipText("When checked, powers of ten are taken out of the result and they are displayed in scientific notation");
		useScientificNotationInResultsCheckBox.setSelected(graphingArea.resultsUseScientificNotation);
		getContentPane().add(useScientificNotationInResultsCheckBox);

		fitAlgorithmLabel = new JLabel("Fit Algortithm:");
		getContentPane().add(fitAlgorithmLabel);
		fitAlgorithmFactoryComboBox = new JComboBox<LinearFitFactory>();
		for(int i = 0; i < LinearFitFactory.fitAlgorithmFactories.length; i++)
		{
			fitAlgorithmFactoryComboBox.addItem(LinearFitFactory.fitAlgorithmFactories[i]);
		}
		fitAlgorithmFactoryComboBox.setSelectedItem(LineFit.currentFitAlgorithmFactory);
		getContentPane().add(fitAlgorithmFactoryComboBox);
		
		whatIsFixedGroupLabel = new JLabel("Fix the Following Value:");
		getContentPane().add(whatIsFixedGroupLabel);
		whichGraphSetLabel = new JLabel("For");
		getContentPane().add(whichGraphSetLabel);
		whichGraphSetComboBox = new JComboBox<DataSet>();
		currentGraphSetIndex = graphingArea.dataSetSelector.getSelectedIndex();
		graphSetsTempWhatFixed = new FixedVariable[graphingArea.dataSetSelector.getItemCount()];
		graphSetsTempFixedValues = new double[graphingArea.dataSetSelector.getItemCount()];
	    
		for(int i = 0; i < graphingArea.dataSetSelector.getItemCount() - 1; i++) //-1 so we do not include the new dataset option
		{
			whichGraphSetComboBox.addItem(graphingArea.dataSetSelector.getItemAt(i));
			graphSetsTempWhatFixed[i] = ((DataSet) graphingArea.dataSetSelector.getItemAt(i)).linearFitStrategy.getWhatIsFixed();
			graphSetsTempFixedValues[i] = ((DataSet) graphingArea.dataSetSelector.getItemAt(i)).linearFitStrategy.getFixedValue();
		}
		whichGraphSetComboBox.setSelectedIndex(currentGraphSetIndex);
		getContentPane().add(whichGraphSetComboBox);
		
		whatIsFixedButtonGroup = new ButtonGroup();
		DataSet fitData = ((DataSet) graphingArea.dataSetSelector.getItemAt(currentGraphSetIndex));
	    noneFixedRadio = new JRadioButton("None", fitData.linearFitStrategy.getWhatIsFixed() == FixedVariable.NONE);
	    whatIsFixedButtonGroup.add(noneFixedRadio);
		getContentPane().add(noneFixedRadio);
	    slopeFixedRadio = new JRadioButton("Slope", fitData.linearFitStrategy.getWhatIsFixed() == FixedVariable.SLOPE);
	    whatIsFixedButtonGroup.add(slopeFixedRadio);
		getContentPane().add(slopeFixedRadio);
	    interceptFixedRadio = new JRadioButton("Intercept", fitData.linearFitStrategy.getWhatIsFixed() == FixedVariable.INTERCEPT);
	    whatIsFixedButtonGroup.add(interceptFixedRadio);
		getContentPane().add(interceptFixedRadio);
		
		fixedValueLabel = new JLabel("Fixed Value");
		getContentPane().add(fixedValueLabel);
		fixedValueField = new JTextField("" + fitData.linearFitStrategy.getFixedValue());
		getContentPane().add(fixedValueField);
		fixedValueField.addFocusListener(onlyNumbers);
		

		updateFixValueForCurrDataSet();
		setTickLabelEnabledStatus();		
		setPowersEnabledStatus();	
		setCustomAxesEnabledStatus();
		setResultOnGraphEnabledStatus();
		setFixedValueEnabledStatus();
		setDataSetCanFixValueEnabledStatus();

		setSize(getPreferredSize());

		ActionListener optionsListener = new GraphOptionsListener();
		applyButton.addActionListener(optionsListener);
		applyCloseButton.addActionListener(optionsListener);
		//pdfDefaultsButton.addActionListener(optionsListener);
		defaultsButton.addActionListener(optionsListener);
		xShowTicksCheckBox.addActionListener(optionsListener);
		xShowTicksNumberLabelsCheckBox.addActionListener(optionsListener);
		yShowTicksCheckBox.addActionListener(optionsListener);
		yShowTicksNumberLabelsCheckBox.addActionListener(optionsListener);
		usePowersCheckBox.addActionListener(optionsListener);
		customPowersCheckBox.addActionListener(optionsListener);
		customAxesCheckBox.addActionListener(optionsListener);
		displayResultsOnGraphCheckBox.addActionListener(optionsListener);
		noneFixedRadio.addActionListener(optionsListener);
		slopeFixedRadio.addActionListener(optionsListener);
		interceptFixedRadio.addActionListener(optionsListener);
		fitAlgorithmFactoryComboBox.addActionListener(optionsListener);
		whichGraphSetComboBox.addActionListener(optionsListener);

		this.setVisible(true);
		setTabStops();
	}
	
	/** Closes this instance of the graph options frame */
	private void closeOptionsFrame() 
	{
		this.dispose();
	}
    
	/** Sets the order of the items when using the tabs key and other traversal keys */
    private void setTabStops() 
    {
    	MyFocusTraversalPolicy policy = new MyFocusTraversalPolicy();
    	policy.addComponentToTabsList(graphNameField);
    	policy.addComponentToTabsList(xAxisNameField);
    	policy.addComponentToTabsList(yAxisNameField);
    	policy.addComponentToTabsList(customAxesCheckBox);
    	policy.addComponentToTabsList(xMinField);
    	policy.addComponentToTabsList(xMaxField);
    	policy.addComponentToTabsList(yMinField);
    	policy.addComponentToTabsList(yMaxField);
    	policy.addComponentToTabsList(usePowersCheckBox);
    	policy.addComponentToTabsList(customPowersCheckBox);
    	policy.addComponentToTabsList(xPowerField);
    	policy.addComponentToTabsList(yPowerField);
    	policy.addComponentToTabsList(pdfWidthField);
    	policy.addComponentToTabsList(pdfHeightField);
    	policy.addComponentToTabsList(xShowTicksCheckBox);
    	policy.addComponentToTabsList(xShowTicksNumberLabelsCheckBox);
    	policy.addComponentToTabsList(xNumberOfTicksField);
    	policy.addComponentToTabsList(yShowTicksCheckBox);
    	policy.addComponentToTabsList(yShowTicksNumberLabelsCheckBox);
    	policy.addComponentToTabsList(yNumberOfTicksField);
    	policy.addComponentToTabsList(xAxisDecimalPlacesField);
    	policy.addComponentToTabsList(yAxisDecimalPlacesField);
    	policy.addComponentToTabsList(LaTexWidthField);
    	policy.addComponentToTabsList(LaTexHeightField);
    	policy.addComponentToTabsList(exportFontSize);
    	policy.addComponentToTabsList(displayResultsOnGraphCheckBox);
    	policy.addComponentToTabsList(xResultsLocationField);
    	policy.addComponentToTabsList(yResultsLocationField);
    	policy.addComponentToTabsList(decimalsInResultsField);
    	policy.addComponentToTabsList(useScientificNotationInResultsCheckBox);
    	policy.addComponentToTabsList(fitAlgorithmFactoryComboBox);
    	policy.addComponentToTabsList(whichGraphSetComboBox);
    	policy.addComponentToTabsList(noneFixedRadio);
    	policy.addComponentToTabsList(slopeFixedRadio);
    	policy.addComponentToTabsList(interceptFixedRadio);
    	policy.addComponentToTabsList(fixedValueField);
    	policy.addComponentToTabsList(xErrorsOnlyCheckBox);
    	policy.addComponentToTabsList(applyCloseButton);
    	policy.addComponentToTabsList(applyButton);
    	policy.addComponentToTabsList(defaultsButton);
    	setFocusTraversalPolicy(policy);	
    }
    
    /** Disables and enables the different fields in the axis tick marks options section so that they are only enabled when they are applicable based on the other selections */
    void setTickLabelEnabledStatus()
    {
    	if(!xShowTicksCheckBox.isSelected())
    	{
    		xShowTicksNumberLabelsCheckBox.setSelected(false);
			xShowTicksNumberLabelsCheckBox.setEnabled(false);
			xNumberOfTicksLabel.setEnabled(false);
			xNumberOfTicksField.setEnabled(false);
		} 
    	else 
    	{
			xShowTicksNumberLabelsCheckBox.setEnabled(true);
			xNumberOfTicksLabel.setEnabled(true);
			xNumberOfTicksField.setEnabled(true);
		}
    	
    	if(!xShowTicksNumberLabelsCheckBox.isSelected()) 
    	{
			xAxisDecimalPlacesLabel.setEnabled(false);
			xAxisDecimalPlacesField.setEnabled(false);
		} 
    	else 
    	{
			xAxisDecimalPlacesLabel.setEnabled(true);
			xAxisDecimalPlacesField.setEnabled(true);
		}
    	
    	if(!yShowTicksCheckBox.isSelected()) 
    	{
    		yShowTicksNumberLabelsCheckBox.setSelected(false);
    		yShowTicksNumberLabelsCheckBox.setEnabled(false);
			yNumberOfTicksLabel.setEnabled(false);
			yNumberOfTicksField.setEnabled(false);
		} 
    	else 
    	{
			yShowTicksNumberLabelsCheckBox.setEnabled(true);
			yNumberOfTicksLabel.setEnabled(true);
			yNumberOfTicksField.setEnabled(true);
		}
    	
    	if(!yShowTicksNumberLabelsCheckBox.isSelected()) 
    	{
			yAxisDecimalPlacesLabel.setEnabled(false);
			yAxisDecimalPlacesField.setEnabled(false);
		} 
    	else
    	{
			yAxisDecimalPlacesLabel.setEnabled(true);
			yAxisDecimalPlacesField.setEnabled(true);
		}
    }
    
    /** Disables and Enables the options for the powers on the axis based on whether or not the options are applicable with the current options */
    void setPowersEnabledStatus() 
    {
    	if(usePowersCheckBox.isSelected()) 
    	{
    		customPowersCheckBox.setEnabled(true);
	    	if(!customPowersCheckBox.isSelected()) 
	    	{
				xPowerField.setEnabled(false);
				xPowerLabel.setEnabled(false);
				yPowerField.setEnabled(false);
				yPowerLabel.setEnabled(false);
			} 
	    	else
	    	{
				xPowerField.setEnabled(true);
				xPowerLabel.setEnabled(true);
				yPowerField.setEnabled(true);
				yPowerLabel.setEnabled(true);
			}
    	} 
    	else
    	{
    		customPowersCheckBox.setEnabled(false);
    		customPowersCheckBox.setSelected(false);
			xPowerField.setEnabled(false);
			xPowerLabel.setEnabled(false);
			yPowerField.setEnabled(false);
			yPowerLabel.setEnabled(false);
    	}
    }
    
    /** Prevents the axis minimums and maximums from being changed when the custom axes box is not selected */
    void setCustomAxesEnabledStatus() 
    {
    	if(!customAxesCheckBox.isSelected()) 
    	{
    		xMinField.setEnabled(false);
    		xMinLabel.setEnabled(false);
    		xMaxField.setEnabled(false);
    		xMaxLabel.setEnabled(false);
    		yMinField.setEnabled(false);
    		yMinLabel.setEnabled(false);
    		yMaxField.setEnabled(false);
    		yMaxLabel.setEnabled(false);
		} 
    	else 
    	{
    		xMinField.setEnabled(true);
    		xMinLabel.setEnabled(true);
    		xMaxField.setEnabled(true);
    		xMaxLabel.setEnabled(true);
    		yMinField.setEnabled(true);
    		yMinLabel.setEnabled(true);
    		yMaxField.setEnabled(true);
    		yMaxLabel.setEnabled(true);
		}
    }
    
    /** Prevents the results options fields from being edited when the results are not set to display on the graph */
    void setResultOnGraphEnabledStatus() 
    {
    	if(!displayResultsOnGraphCheckBox.isSelected()) 
    	{
    		xResultsLocationLabel.setEnabled(false);
    		xResultsLocationField.setEnabled(false);
    		yResultsLocationLabel.setEnabled(false);
    		yResultsLocationField.setEnabled(false);
		} 
    	else
    	{
			xResultsLocationLabel.setEnabled(true);
			xResultsLocationField.setEnabled(true);
			yResultsLocationLabel.setEnabled(true);
			yResultsLocationField.setEnabled(true);
		}
    }
    
    /** Prevents the fixed value from being changed for individual linear fits when we have selected to have no fixed value */
    void setFixedValueEnabledStatus() 
    {
    	if(noneFixedRadio.isSelected()) 
    	{
    		fixedValueField.setEnabled(false);
    		fixedValueLabel.setEnabled(false);
		} 
    	else 
    	{
			fixedValueField.setEnabled(true);
			fixedValueLabel.setEnabled(true);
		}
    }
    
    /** Prevents the fixed value and what is fixed from being changed if the dataset selected does not have a line fitted to it */
    void setDataSetCanFixValueEnabledStatus() 
    {
    	if(((DataSet)graphingArea.dataSetSelector.getItemAt(whichGraphSetComboBox.getSelectedIndex())).getFitType() != FitType.NONE) 
    	{
    		noneFixedRadio.setEnabled(true);
    		if(((LinearFitFactory)fitAlgorithmFactoryComboBox.getSelectedItem()).canFixSlopeForGeneratedFits())
    		{
    			slopeFixedRadio.setEnabled(true);
    		}

    		if(((LinearFitFactory)fitAlgorithmFactoryComboBox.getSelectedItem()).canFixInterceptForGeneratedFits())
    		{
    			interceptFixedRadio.setEnabled(true);
    		}
    		fixedValueField.setEnabled(true);
    		fixedValueLabel.setEnabled(true);
		} 
    	else 
    	{
    		noneFixedRadio.setEnabled(false);
    		slopeFixedRadio.setEnabled(false);
    		interceptFixedRadio.setEnabled(false);
    		fixedValueField.setEnabled(false);
    		fixedValueLabel.setEnabled(false);
		}
    }
    
    /** Changes the fixed value and what is fixed so it reflects the currently selected GraphDataSet */
    void updateFixValueForCurrDataSet() 
    {
    	if(whichGraphSetComboBox.getSelectedIndex() != currentGraphSetIndex) 
    	{
    		//save our old values in our temp spots
    		if(noneFixedRadio.isSelected()) 
    		{
    			graphSetsTempWhatFixed[currentGraphSetIndex] = FixedVariable.NONE;
    		} 
    		else if(slopeFixedRadio.isSelected()) 
    		{
    			graphSetsTempWhatFixed[currentGraphSetIndex] = FixedVariable.SLOPE;
    		} 
    		else 
    		{
    			graphSetsTempWhatFixed[currentGraphSetIndex] = FixedVariable.INTERCEPT;
    		}
    		graphSetsTempFixedValues[currentGraphSetIndex] = Double.parseDouble(fixedValueField.getText());
    		
    		//switch to our ne one
    		currentGraphSetIndex = whichGraphSetComboBox.getSelectedIndex();
    		
    		//now set our new values
    		if(graphSetsTempWhatFixed[currentGraphSetIndex] == FixedVariable.SLOPE && ((LinearFitFactory)fitAlgorithmFactoryComboBox.getSelectedItem()).canFixSlopeForGeneratedFits()) 
    		{
    			slopeFixedRadio.setSelected(true);
    		} 
    		else if(graphSetsTempWhatFixed[currentGraphSetIndex] == FixedVariable.INTERCEPT && ((LinearFitFactory)fitAlgorithmFactoryComboBox.getSelectedItem()).canFixInterceptForGeneratedFits()) 
    		{
    			interceptFixedRadio.setSelected(true);
    		} 
    		else
    		{
    			noneFixedRadio.setSelected(true);
    		}
    		fixedValueField.setText("" + graphSetsTempFixedValues[currentGraphSetIndex]);
    	}
    }
    
	/** Saves all the fields values from the options into the GraphArea so that the values actually get changed in the graph */
    void applyChanges()
	{
    	ioHandler.changeTracker.setFileModified(); //so we know we have unsaved changes
		
		graphingArea.userDefinedAxes = customAxesCheckBox.isSelected();
		if(graphingArea.userDefinedAxes)	
		{
			graphingArea.xAxisMinimumValue = Double.parseDouble(xMinField.getText());
			graphingArea.xAxisMaximumValue = Double.parseDouble(xMaxField.getText());
			graphingArea.yAxisMinimumValue = Double.parseDouble(yMinField.getText());
			graphingArea.yAxisMaximumValue = Double.parseDouble(yMaxField.getText());
		} 
		else 
		{
			graphingArea.calculateAxesMinimumAndMaximumValues();
			xMinField.setText("" + graphingArea.xAxisMinimumValue);
			xMaxField.setText("" + graphingArea.xAxisMaximumValue);
			yMinField.setText("" + graphingArea.yAxisMinimumValue);
			yMaxField.setText("" + graphingArea.yAxisMaximumValue);
		}
		
		graphingArea.setGraphName(graphNameField.getText());
		graphingArea.setXAxisDescription(xAxisNameField.getText());
		graphingArea.setYAxisDescription(yAxisNameField.getText());
		
		if(xErrorsOnlyCheckBox.isSelected() != graphingArea.xErrorsOnly)
		{
			graphingArea.xErrorsOnly = !graphingArea.xErrorsOnly;
			graphingArea.refreshAllSetsThirdColumn();
		}
		
		changePDFSize(Double.parseDouble(pdfWidthField.getText()),Double.parseDouble(pdfHeightField.getText()));
		
		graphingArea.xAxisNumberOfTickMarks = Integer.parseInt(xNumberOfTicksField.getText());
		graphingArea.yAxisNumberOfTickMarks = Integer.parseInt(yNumberOfTicksField.getText());
		graphingArea.xAxisHasTickMarks = xShowTicksCheckBox.isSelected();
		graphingArea.xAxisHasTickMarkLabels = xShowTicksNumberLabelsCheckBox.isSelected();
		graphingArea.yAxisHasTickMarks = yShowTicksCheckBox.isSelected();
		graphingArea.yAxisHasTickMarkLabels = yShowTicksNumberLabelsCheckBox.isSelected();

		graphingArea.useAxesPowers = usePowersCheckBox.isSelected();
		graphingArea.userDefinedAxesPowers = customPowersCheckBox.isSelected();
		if(graphingArea.useAxesPowers) 
		{
			if(!graphingArea.userDefinedAxesPowers)
			{
				graphingArea.xAxisPower = ScientificNotation.getPowerOf(graphingArea.xAxisMaximumValue);
				graphingArea.yAxisPower = ScientificNotation.getPowerOf(graphingArea.yAxisMaximumValue);
				xPowerField.setText("" + graphingArea.xAxisPower);
				yPowerField.setText("" + graphingArea.yAxisPower);
			} 
			else
			{
				graphingArea.xAxisPower = Integer.parseInt(xPowerField.getText());
				graphingArea.yAxisPower = Integer.parseInt(yPowerField.getText());
			}
		} 
		else 
		{
			graphingArea.xAxisPower = 0;
			graphingArea.yAxisPower = 0;
			xPowerField.setText("0");
			yPowerField.setText("0");
		}
			
		ioHandler.exportIO.laTexGraphWidthInCm = Double.parseDouble(LaTexWidthField.getText());
		ioHandler.exportIO.laTexGraphHeightInCm = Double.parseDouble(LaTexHeightField.getText());	
		ioHandler.exportIO.exportFontSize = ((Double) exportFontSize.getValue()).floatValue();
		
		graphingArea.resultsAreDisplayedOnGraph = displayResultsOnGraphCheckBox.isSelected();

		graphingArea.resultsPositionX = Integer.parseInt(xResultsLocationField.getText());
		graphingArea.resultsPositionY = Integer.parseInt(yResultsLocationField.getText());
	
		graphingArea.resultsDecimalPlaces = Integer.parseInt(decimalsInResultsField.getText());
		
		graphingArea.xAxisDecimalPlaces = Integer.parseInt(xAxisDecimalPlacesField.getText());
		graphingArea.yAxisDecimalPlaces = Integer.parseInt(yAxisDecimalPlacesField.getText());
		graphingArea.resultsUseScientificNotation = useScientificNotationInResultsCheckBox.isSelected();
		
		//check if we need to create new fit strategies
		LinearFitFactory selectedFactory = (LinearFitFactory)this.fitAlgorithmFactoryComboBox.getSelectedItem();
		boolean createNewFitStrategy = false;
		if(selectedFactory != LineFit.currentFitAlgorithmFactory)
		{
			System.out.println("here");
			createNewFitStrategy = true;
			LineFit.currentFitAlgorithmFactory = selectedFactory;
		}
		
		//update our fit strategies by either creating new ones or at least updating the fixed values
		for(int i = 0; i < whichGraphSetComboBox.getItemCount(); i++) 
		{
			//get the DataSet to process, create new fit data and then store that fit data in the DataSet
			DataSet currentlyProcessingDataSet = (DataSet)graphingArea.dataSetSelector.getItemAt(i);
			
			//if we need to create new strategies for the sets
			if(createNewFitStrategy)
			{
				LineFit.currentFitAlgorithmFactory.createNewLinearFitStartegy(currentlyProcessingDataSet);
			}
			
			//figure out what to set for the new FitData
			if(i != currentGraphSetIndex) 
			{
				currentlyProcessingDataSet.linearFitStrategy.setWhatIsFixed(graphSetsTempWhatFixed[i], graphSetsTempFixedValues[i]);
			} 
			else 
			{
	    		if(noneFixedRadio.isSelected())
	    		{
	    			currentlyProcessingDataSet.linearFitStrategy.setWhatIsFixed(FixedVariable.NONE, Double.parseDouble(fixedValueField.getText()));
	    		} 
	    		else if(slopeFixedRadio.isSelected()) 
	    		{
	    			currentlyProcessingDataSet.linearFitStrategy.setWhatIsFixed(FixedVariable.SLOPE, Double.parseDouble(fixedValueField.getText()));
	    		} 
	    		else 
	    		{
	    			currentlyProcessingDataSet.linearFitStrategy.setWhatIsFixed(FixedVariable.INTERCEPT, Double.parseDouble(fixedValueField.getText()));
	    		}
			}
		}
		graphingArea.repaint();	
	}
	
    /** Allows us to change the the size of the PDF export 
     * @param width The desired width of the PDF export image size 
     * @param height The desire height of the PDF export image size */
	private void changePDFSize(double width, double height) 
	{
		ioHandler.exportIO.pdfPageWidth = width;
		ioHandler.exportIO.pdfPageHeight = height;
		pdfWidthField.setText("" + ioHandler.exportIO.pdfPageWidth);
		pdfHeightField.setText("" + ioHandler.exportIO.pdfPageHeight);
	}
	
	//check the fitalgorithm switching
	
	//private classes
	/** This String keeps track of what value was in the currently selected field at the moment it was first selected. This lets us revert the field to its previous value if an invalid number string is inputed. This is only used for places where only numbers are allowed */
	private String inputFieldValueOnFocused; 
	
	/** 
	 * A private Listener class that implements FocusListener so that we can save the value when the field is first focused
	 * on so that we can return it to the field's preselected value if the input was not a valid number String.
	 * 
	 * @author Keith Rice
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class OnlyAllowNumbersListener implements FocusListener 
	{
		@Override
		/** Saves the current value of the field in another location so we can revert back to it if we want later
		 * 
		 * @param passedFocusEvent The Focus Event that is passed to this function by the system when the field is focused on
		 */
		public void focusGained(FocusEvent passedFocusEvent) 
		{
			try 
			{
				JTextField focusedField = (JTextField)passedFocusEvent.getSource();
				inputFieldValueOnFocused = focusedField.getText();
			} 
			catch (ClassCastException cce) 
			{
				inputFieldValueOnFocused = "0";
			}
		}

		@Override
		/** Checks when we lose the focus to see if the input was a valid number String ans if not then it will revert it back to the original String
		 * 
		 * @param passedFocusEvent The FocusEvent that is passed to this funtion by the system when the field loses the focus
		 */
		public void focusLost(FocusEvent passedFocusEvent) 
		{
			try 
			{
				JTextField focusedField = (JTextField)passedFocusEvent.getSource();
				if(!doesFieldContainValidDoubleString(focusedField))
				{
					focusedField.setText(inputFieldValueOnFocused);
				}
			}
			catch (ClassCastException cce) {}
		}
	}
	
	/**
	 * Checks the String value in the given field to see if it contains a valid double as a String
	 * 
	 * @param fieldToCheckForNumber The text field that will be checked to see whether or not it contains a double number as a String
	 * @return A boolean of whether or not the passed TextField has a valid double as a String in it
	 */
	private boolean doesFieldContainValidDoubleString(JTextField fieldToCheckForNumber) 
	{
		try 
		{
			Double.parseDouble(fieldToCheckForNumber.getText());
			//also check for d and f at the end since it will parse it as doubles and floats respectively
			if(fieldToCheckForNumber.getText().toLowerCase().endsWith("d") || fieldToCheckForNumber.getText().toLowerCase().endsWith("f"))
			{
				return false;
			}
			return true;
		} 
		catch (NumberFormatException nfe) 
		{
			return false;
		}
	}

	/**
	 * A generic listener class that picks up any button or checkbox event and handles it properly, either saving and closing or 
	 * disable or enabling options and fields that are no longer applicable with the selected options
	 * 
	 * @author Keith Rice
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class GraphOptionsListener implements ActionListener 
	{
		@Override
		/**
		 * The event handler that determines which button or checkbox was selected and then handles the event properly
		 */
		public void actionPerformed(ActionEvent e) 
		{
			if (e.getActionCommand().equals("Apply and Close"))
			{
				applyChanges();
				closeOptionsFrame();
			}
			else if(e.getActionCommand().equals("Apply"))
			{
				applyChanges();
			} 
			else if(e.getActionCommand().equals("Defaults"))
			{
				resetToDefaults();
			} 
			else 
			{
				updateGraphOptionsEnabledStatuses();
			}
		}
	}
	
	/** This function is called when a checkbox has been toggled in the Graph Options menu and updates each block on the
	 * options to disable and enable parts that are relevant or not now that the option has been selected or deselcted
	 */
	private void updateGraphOptionsEnabledStatuses() 
	{
		updateFixValueForCurrDataSet();
		setTickLabelEnabledStatus();
		setPowersEnabledStatus();
		setCustomAxesEnabledStatus();
		setResultOnGraphEnabledStatus();
		setFixedValueEnabledStatus();
		setDataSetCanFixValueEnabledStatus();
	}
	
	/** 
	 * A listener that is called when the user hits the tabs key that sets the order through which the options in the options menu 
	 * are focused in
	 * 
	 * @author Keith Rice
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class MyFocusTraversalPolicy extends FocusTraversalPolicy 
	{
		/** The list of components that can be focused on in the Graph Options menu */
		private ArrayList<Component> components = new ArrayList<Component>();

		/**
		 * Adds the passed component into the list of components that can be focused on with the tabs key
		 * 
		 * @param component The component to add to the lists of components to be highlighted by tabs 
		 */
		void addComponentToTabsList(Component component) 
		{
			components.add(component);
		}

		/**
		 * Find the next component in the list of the components that is not disables to be selected by the Tab key
		 */
		public Component getComponentAfter(Container aContainer, Component aComponent) 
		{
			int atIndex = components.indexOf(aComponent);
			int nextIndex = (atIndex + 1) % components.size();
			while(!components.get(nextIndex).isEnabled())
			{
				nextIndex = (nextIndex + 1) % components.size();
			}
			return components.get(nextIndex);
		}

		/**
		 * Finds the component that is before this one that is not disabled in the list to be selected by Shift+Tab
		 */
		public Component getComponentBefore(Container aContainer, Component aComponent) 
		{
			int currentIndex = components.indexOf(aComponent);
			int nextIndex = (currentIndex + components.size() - 1) % components.size();
			while(!components.get(nextIndex).isEnabled())
			{
				nextIndex = (nextIndex - 1) % components.size();
			}
			return components.get(nextIndex);
		}

		/**
		 * Gets the first component in the list of focusable components
		 */
		public Component getFirstComponent(Container aContainer) 
		{
			return components.get(0);
		}

		/**
		 * Returns the default highlighted component, in this case the first one in the list
		 */
		public Component getDefaultComponent(Container arg0) 
		{
			return components.get(0);
		}

		/**
		 * Gets the last component in the list of focusable components
		 */
		public Component getLastComponent(Container arg0) 
		{
			return components.get(components.size() - 1);
		}
	}
	
	/** 
	 * A Layout class that positions all the buttons and options in the frame
	 * 
	 * @author	Keith Rice
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class GraphOptionsFrameLayout implements LayoutManager 
	{
	    GraphOptionsFrameLayout() {}
	    public void addLayoutComponent(String name, Component comp) {}
	    public void removeLayoutComponent(Component comp) {}
	    
	    /**
	     * Sets the dimensions of the graph options frame
	     */
	    public Dimension preferredLayoutSize(Container parent) 
	    {
			Dimension graphOptionsDimensions = new Dimension(0, 0);
		
			Insets insets = parent.getInsets();
			graphOptionsDimensions.width = 720 + insets.left + insets.right;
			graphOptionsDimensions.height = 530 + insets.top + insets.bottom;
		
			return graphOptionsDimensions;
	    }

	    /**
	     * Sets the minimum size of the graph options frame
	     */
	    public Dimension minimumLayoutSize(Container parent) 
	    {
			Dimension dim = new Dimension(100, 100);
			return dim;
	    }
	    
	    /**
	     * Allows us to position groups of options together and allows us to to deal with larger chunks 
	     * on the high level of building the graph options menu
	     * 
	     * @param componentsInGroup The list of components that make up this group
	     * @param xShiftOfGroup The y Location of the start of the group (from the top)
	     * @param yShiftOfGroup The x Location of the start of the group (from the left)
	     */
	    private void positionGroup(Component[] componentsInGroup, int xShiftOfGroup, int yShiftOfGroup) 
	    {
	    	for(int i = 0; i < componentsInGroup.length; i++) 
	    	{
	    		if (componentsInGroup[i].isVisible()) 
	    		{
	    			componentsInGroup[i].setBounds(componentsInGroup[i].getX() + xShiftOfGroup,componentsInGroup[i].getY() + yShiftOfGroup,componentsInGroup[i].getWidth(),componentsInGroup[i].getHeight());
	    		}
	    	}
	    }

	    /**
	     * Describes the layout of the options within their groups as well as the positions of the groups in the frame
	     */
	    public void layoutContainer(Container parent) 
	    {
			Insets insets = parent.getInsets();
			final int componentHeight = 28;
			final int acButtonWidth = 144; 
			final int applyButtonWidth = 76;
			final int defaultsButtonWidth = 87; 

			// Graph title and axis names
			Component[] titleAxesGroup = new Component[6];
			titleAxesGroup[0] = graphNameLabel;
			if (titleAxesGroup[0].isVisible()) {titleAxesGroup[0].setBounds(insets.left, insets.top, 208, componentHeight);}
			titleAxesGroup[1] = graphNameField;
			if (titleAxesGroup[1].isVisible()) {titleAxesGroup[1].setBounds(insets.left, insets.top + 20, 208, componentHeight);}
			titleAxesGroup[2] = yAxisNameLabel;
			if (titleAxesGroup[2].isVisible()) {titleAxesGroup[2].setBounds(insets.left, insets.top + 50, 208, componentHeight);}
			titleAxesGroup[3] = yAxisNameField;
			if (titleAxesGroup[3].isVisible()) {titleAxesGroup[3].setBounds(insets.left, insets.top + 70, 208, componentHeight);}
			titleAxesGroup[4] = xAxisNameLabel;
			if (titleAxesGroup[4].isVisible()) {titleAxesGroup[4].setBounds(insets.left, insets.top + 100, 208, componentHeight);}
			titleAxesGroup[5] = xAxisNameField;
			if (titleAxesGroup[5].isVisible()) {titleAxesGroup[5].setBounds(insets.left, insets.top + 120, 208, componentHeight);}
			
			// X and Y Max and Min
			Component[] maxMinGroup = new Component[9];	
			maxMinGroup[0] = customAxesCheckBox;
			if (maxMinGroup[0].isVisible()) {maxMinGroup[0].setBounds(insets.left, insets.top + 0, 184, componentHeight);}
			maxMinGroup[1] = xMinLabel;
			if (maxMinGroup[1].isVisible()) {maxMinGroup[1].setBounds(insets.left + 0, insets.top + 30, 80, componentHeight);}
			maxMinGroup[2] = xMinField;
			if (maxMinGroup[2].isVisible()) {maxMinGroup[2].setBounds(insets.left + 88, insets.top + 30, 104, componentHeight);}
			maxMinGroup[3] = xMaxLabel;
			if (maxMinGroup[3].isVisible()) {maxMinGroup[3].setBounds(insets.left + 0, insets.top + 62, 80, componentHeight);}
			maxMinGroup[4] = xMaxField;
			if (maxMinGroup[4].isVisible()) {maxMinGroup[4].setBounds(insets.left + 88, insets.top + 62, 104, componentHeight);}
			maxMinGroup[5] = yMinLabel;
			if (maxMinGroup[5].isVisible()) {maxMinGroup[5].setBounds(insets.left + 0, insets.top + 94, 80, componentHeight);}
			maxMinGroup[6] = yMinField;
			if (maxMinGroup[6].isVisible()) {maxMinGroup[6].setBounds(insets.left + 88, insets.top + 94, 104, componentHeight);}
			maxMinGroup[7] = yMaxLabel;
			if (maxMinGroup[7].isVisible()) {maxMinGroup[7].setBounds(insets.left + 0, insets.top + 126, 80, componentHeight);}
			maxMinGroup[8] = yMaxField;
			if (maxMinGroup[8].isVisible()) {maxMinGroup[8].setBounds(insets.left + 88, insets.top + 126, 104, componentHeight);}
		    
			//Powers on the axes
			Component[] customPowersGroup = new Component[6];
			customPowersGroup[0] = usePowersCheckBox;
			if (customPowersGroup[0].isVisible()) {customPowersGroup[0].setBounds(insets.left, insets.top, 200, componentHeight);}
			customPowersGroup[1] = customPowersCheckBox;
			if (customPowersGroup[1].isVisible()) {customPowersGroup[1].setBounds(insets.left, insets.top + 30, 230, componentHeight);}
			customPowersGroup[2] = xPowerLabel;
			if (customPowersGroup[2].isVisible()) {customPowersGroup[2].setBounds(insets.left + 4, insets.top + 60, 100, componentHeight);}
			customPowersGroup[3] = xPowerField;
			if (customPowersGroup[3].isVisible()) {customPowersGroup[3].setBounds(insets.left + 129, insets.top + 60, 60, componentHeight);}
			customPowersGroup[4] = yPowerLabel;	
			if (customPowersGroup[4].isVisible()) {customPowersGroup[4].setBounds(insets.left + 4, insets.top + 92, 100, componentHeight);}
			customPowersGroup[5] = yPowerField;
			if (customPowersGroup[5].isVisible()) {customPowersGroup[5].setBounds(insets.left + 129, insets.top + 92, 60, componentHeight);}
			
			// "Apply and Close" Button and "Apply" Button
			Component[] applyButtonGroup = new Component[3];
			applyButtonGroup[0] = applyCloseButton;
			if (applyButtonGroup[0].isVisible()) {applyButtonGroup[0].setBounds(insets.left, insets.top, acButtonWidth, componentHeight);}
			applyButtonGroup[1] = applyButton;
			if (applyButtonGroup[1].isVisible()) {applyButtonGroup[1].setBounds(insets.left + acButtonWidth + 6, insets.top, applyButtonWidth, componentHeight);}
			applyButtonGroup[2] = defaultsButton;
			if (applyButtonGroup[2].isVisible()) {applyButtonGroup[2].setBounds(insets.left + acButtonWidth + 6 + applyButtonWidth + 6, insets.top, defaultsButtonWidth, componentHeight);}
	
			// PDF Size with default button
			Component[] PDFSizeGroup = new Component[4];
			PDFSizeGroup[0] = pdfSizeLabel;
			if (PDFSizeGroup[0].isVisible()) {PDFSizeGroup[0].setBounds(insets.left, insets.top, 208, componentHeight);}
			PDFSizeGroup[1] = pdfWidthField;
			if (PDFSizeGroup[1].isVisible()) {PDFSizeGroup[1].setBounds(insets.left, insets.top + 35, 60, componentHeight);}
			PDFSizeGroup[2] = pdfTimesLabel;
			if (PDFSizeGroup[2].isVisible()) {PDFSizeGroup[2].setBounds(insets.left + 70, insets.top + 35, 20, componentHeight);}
			PDFSizeGroup[3] = pdfHeightField;
			if (PDFSizeGroup[3].isVisible()) {PDFSizeGroup[3].setBounds(insets.left + 89, insets.top + 35, 60, componentHeight);}
			//PDFSizeGroup[4] = pdfDefaultsButton;
			//if (PDFSizeGroup[4].isVisible()) {PDFSizeGroup[4].setBounds(insets.left + 36, insets.top + 67, defaultButtonWidth, componentHeight);}
		    
			//Tick Marks and Labeling
			Component[] tickMarkAndLabelGroup = new Component[14];
			tickMarkAndLabelGroup[0] = xAxisTickMarksSectionLabel;
			if (tickMarkAndLabelGroup[0].isVisible()) {tickMarkAndLabelGroup[0].setBounds(insets.left + 4, insets.top, 100, componentHeight);}
			tickMarkAndLabelGroup[1] = xShowTicksCheckBox;
			if (tickMarkAndLabelGroup[1].isVisible()) {tickMarkAndLabelGroup[1].setBounds(insets.left, insets.top + 21, 200, componentHeight);}
			tickMarkAndLabelGroup[2] = xNumberOfTicksLabel;
			if (tickMarkAndLabelGroup[2].isVisible()) {tickMarkAndLabelGroup[2].setBounds(insets.left + 4 , insets.top + 51, 120, componentHeight);}
			tickMarkAndLabelGroup[3] = xNumberOfTicksField;
			if (tickMarkAndLabelGroup[3].isVisible()) {tickMarkAndLabelGroup[3].setBounds(insets.left + 129, insets.top + 51, 60, componentHeight);}
			tickMarkAndLabelGroup[4] = xShowTicksNumberLabelsCheckBox;
			if (tickMarkAndLabelGroup[4].isVisible()) {tickMarkAndLabelGroup[4].setBounds(insets.left, insets.top + 81, 200, componentHeight);}
			tickMarkAndLabelGroup[5] = xAxisDecimalPlacesLabel;
			if (tickMarkAndLabelGroup[5].isVisible()) {tickMarkAndLabelGroup[5].setBounds(insets.left + 4, insets.top + 111, 125, componentHeight);}
			tickMarkAndLabelGroup[6] = xAxisDecimalPlacesField;
			if (tickMarkAndLabelGroup[6].isVisible()) {tickMarkAndLabelGroup[6].setBounds(insets.left + 129, insets.top + 111, 60, componentHeight);}
			tickMarkAndLabelGroup[7] = yAxisTickMarksSectionLabel;
			if (tickMarkAndLabelGroup[7].isVisible()) {tickMarkAndLabelGroup[7].setBounds(insets.left + 4, insets.top + 136, 100, componentHeight);}
			tickMarkAndLabelGroup[8] = yShowTicksCheckBox;
			if (tickMarkAndLabelGroup[8].isVisible()) {tickMarkAndLabelGroup[8].setBounds(insets.left, insets.top + 157, 200, componentHeight);}
			tickMarkAndLabelGroup[9] = yNumberOfTicksLabel;
			if (tickMarkAndLabelGroup[9].isVisible()) {tickMarkAndLabelGroup[9].setBounds(insets.left + 4, insets.top + 187, 120, componentHeight);}
			tickMarkAndLabelGroup[10] = yNumberOfTicksField;
			if (tickMarkAndLabelGroup[10].isVisible()) {tickMarkAndLabelGroup[10].setBounds(insets.left + 129, insets.top + 187, 60, componentHeight);}
			tickMarkAndLabelGroup[11] = yShowTicksNumberLabelsCheckBox;
			if (tickMarkAndLabelGroup[11].isVisible()) {tickMarkAndLabelGroup[11].setBounds(insets.left, insets.top + 217, 200, componentHeight);}
			tickMarkAndLabelGroup[12] = yAxisDecimalPlacesLabel;
			if (tickMarkAndLabelGroup[12].isVisible()) {tickMarkAndLabelGroup[12].setBounds(insets.left + 4, insets.top + 247, 125, componentHeight);}
			tickMarkAndLabelGroup[13] = yAxisDecimalPlacesField;
			if (tickMarkAndLabelGroup[13].isVisible()) {tickMarkAndLabelGroup[13].setBounds(insets.left + 129, insets.top + 247, 60, componentHeight);}
			
			//Export size for LaTex
			Component[] LaTexExportSizeGroup = new Component[6];
			LaTexExportSizeGroup[0] = LaTexSizeLabel;
			if (LaTexExportSizeGroup[0].isVisible()) {LaTexExportSizeGroup[0].setBounds(insets.left, insets.top, 200, componentHeight);}
			LaTexExportSizeGroup[1] = LaTexWidthField;
			if (LaTexExportSizeGroup[1].isVisible()) {LaTexExportSizeGroup[1].setBounds(insets.left + 4, insets.top + 30, 60, componentHeight);}
			LaTexExportSizeGroup[2] = LaTexTimesSymbol;
			if (LaTexExportSizeGroup[2].isVisible()) {LaTexExportSizeGroup[2].setBounds(insets.left + 74, insets.top + 30, 20, componentHeight);}
			LaTexExportSizeGroup[3] = LaTexHeightField;
			if (LaTexExportSizeGroup[3].isVisible()) {LaTexExportSizeGroup[3].setBounds(insets.left + 94, insets.top + 30, 60, componentHeight);}
			LaTexExportSizeGroup[4] = exportFontSizeLabel;
			if (LaTexExportSizeGroup[4].isVisible()) {LaTexExportSizeGroup[4].setBounds(insets.left + 4, insets.top + 62, 142, componentHeight);}
			LaTexExportSizeGroup[5] = exportFontSize;
			if (LaTexExportSizeGroup[5].isVisible()) {LaTexExportSizeGroup[5].setBounds(insets.left + 155, insets.top + 62, 50, componentHeight);}
			
			//Where the results are displayed on the graph
			Component[] resultOnGraphGroup = new Component[5];
			resultOnGraphGroup[0] = displayResultsOnGraphCheckBox;
			if (resultOnGraphGroup[0].isVisible()) {resultOnGraphGroup[0].setBounds(insets.left, insets.top, 250, componentHeight);}
			resultOnGraphGroup[1] = xResultsLocationLabel;
			if (resultOnGraphGroup[1].isVisible()) {resultOnGraphGroup[1].setBounds(insets.left + 4, insets.top + 30, 142, componentHeight);}
			resultOnGraphGroup[2] = xResultsLocationField;
			if (resultOnGraphGroup[2].isVisible()) {resultOnGraphGroup[2].setBounds(insets.left + 145, insets.top + 30, 60, componentHeight);}
			resultOnGraphGroup[3] = yResultsLocationLabel;
			if (resultOnGraphGroup[3].isVisible()) {resultOnGraphGroup[3].setBounds(insets.left + 4, insets.top + 62, 142, componentHeight);}
			resultOnGraphGroup[4] = yResultsLocationField;
			if (resultOnGraphGroup[4].isVisible()) {resultOnGraphGroup[4].setBounds(insets.left + 145, insets.top + 62, 60, componentHeight);}

			//How many places to round the answers to
			Component[] resultDecGroup = new Component[3];
			resultDecGroup[0] = decimalsInResultsLabel;
			if (resultDecGroup[0].isVisible()) {resultDecGroup[0].setBounds(insets.left + 4, insets.top, 142, componentHeight);}
			resultDecGroup[1] = decimalsInResultsField;
			if (resultDecGroup[1].isVisible()) {resultDecGroup[1].setBounds(insets.left + 145, insets.top, 60, componentHeight);}
			resultDecGroup[2] = useScientificNotationInResultsCheckBox;
			if (resultDecGroup[2].isVisible()) {resultDecGroup[2].setBounds(insets.left, insets.top + 30, 220, componentHeight);}

			//allows to select fitting alorithm
			Component[] algorithmGroup = new Component[2];
			algorithmGroup[0] = fitAlgorithmLabel;
			if (algorithmGroup[0].isVisible()) {algorithmGroup[0].setBounds(insets.left + 4, insets.top, 200, componentHeight);}
			algorithmGroup[1] = fitAlgorithmFactoryComboBox;
			if (algorithmGroup[1].isVisible()) {algorithmGroup[1].setBounds(insets.left + 4, insets.top + 32, 200, componentHeight);}
			
			//Fixes the slope or the intercept or none
			Component[] fixedGroup = new Component[8];
			fixedGroup[0] = whatIsFixedGroupLabel;
			if (fixedGroup[0].isVisible()) {fixedGroup[0].setBounds(insets.left + 4, insets.top, 200, componentHeight);}
			fixedGroup[1] = whichGraphSetLabel;
			if (fixedGroup[1].isVisible()) {fixedGroup[1].setBounds(insets.left + 4, insets.top + 32, 100, componentHeight);}
			fixedGroup[2] = whichGraphSetComboBox;
			if (fixedGroup[2].isVisible()) {fixedGroup[2].setBounds(insets.left + 88, insets.top + 32, 120, componentHeight);}
			fixedGroup[3] = noneFixedRadio;
			if (fixedGroup[3].isVisible()) {fixedGroup[3].setBounds(insets.left, insets.top + 64, 64, componentHeight);}
			fixedGroup[4] = slopeFixedRadio;
			if (fixedGroup[4].isVisible()) {fixedGroup[4].setBounds(insets.left + 60, insets.top + 64, 68, componentHeight);}
			fixedGroup[5] = interceptFixedRadio;
			if (fixedGroup[5].isVisible()) {fixedGroup[5].setBounds(insets.left + 124, insets.top + 64, 90, componentHeight);}
			fixedGroup[6] = fixedValueLabel; 
			if (fixedGroup[6].isVisible()) {fixedGroup[6].setBounds(insets.left + 4, insets.top + 92, 80, componentHeight);}
			fixedGroup[7] = fixedValueField;
			if (fixedGroup[7].isVisible()) {fixedGroup[7].setBounds(insets.left + 88, insets.top + 92, 120, componentHeight);}

			// X errors only and Y errors only
			Component[] errorsGroup = new Component[1];
			errorsGroup[0] = xErrorsOnlyCheckBox;
			if (errorsGroup[0].isVisible()) {errorsGroup[0].setBounds(insets.left, insets.top, 150, componentHeight);}
		
			//Positions each group on the frame
			positionGroup(titleAxesGroup,24,5);
			positionGroup(maxMinGroup,24,159);
			positionGroup(customPowersGroup,20,320);	
			positionGroup(applyButtonGroup,20,462);	
			positionGroup(tickMarkAndLabelGroup,260,5);
			positionGroup(resultOnGraphGroup,260,295);
			positionGroup(resultDecGroup,260,395);
			positionGroup(PDFSizeGroup,484,5);	
			positionGroup(LaTexExportSizeGroup, 484, 70);
			positionGroup(algorithmGroup,480,180);	
			positionGroup(fixedGroup,480,255);
			positionGroup(errorsGroup,480,420);
		}
	}
}