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


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import linefit.FitAlgorithms.FitType;
import linefit.FitAlgorithms.FixedVariable;
import linefit.FitAlgorithms.LinearFitFactory;
import linefit.IO.GeneralIO;


/** This class handles the creation and the display of the graph options menu
 * 
 * @author Unknown, Keith Rice
 * @version 1.1
 * @since &lt;0.98.0 */
public class GraphOptionsMenu extends JFrame
{
    private final static long serialVersionUID = 42;

    // Pointers we need
    /** The GraphArea that the options are being changed of */
    private GraphArea graphingArea;
    private GeneralIO ioHandler;

    // Spacing variables
    public final static int STANDARD_ELEMENT_HEIGHT = 28;

    // Graph name and axes labels variables
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

    // Minimum and maximum setting variables
    /** The checkbox that allows the user to specify the axes minimum and maximums instead of being dynamically adjusted
     * by the program */
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

    // Powers on the axes setting variables
    /** The check box that lets the user specify whether or not they want to use powers on the axes (extract powers of
     * ten from the labels) */
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

    // apply buttons
    /** The button that applies any changes and then closes the window */
    private JButton applyCloseButton;
    /** The button that only applies changes and leaves the window open */
    private JButton applyButton;
    /** The button that restores all the default settings and values in the GraphOptions */
    private JButton defaultsButton;

    // Tick mark and decimals setting variables
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

    // Results on graph setting variables
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

    // Results formatting setting variables
    /** The label for the decimal places to use in the linear fit's results */
    private JLabel decimalsInResultsLabel;
    /** The TextField for the number of decimal places to use in the linear fit's results */
    private JTextField decimalsInResultsField;
    /** The checkbox that determines whether or not the results are displayed in Scientific Notation */
    private JCheckBox useScientificNotationInResultsCheckBox;

    // X errors only setting variable
    /** The checkbox that determines whether or not the third column in the DataSets, when there are only three, is the
     * x or y error/uncertainty (checked is x, unchecked is y) */
    private JCheckBox xErrorsOnlyCheckBox;

    // for choosing the fit type
    /** the label for selecting which Fit Algorithm to use */
    private JLabel fitAlgorithmLabel;
    /** The drop down that allows the user to select which fit algorithm to use */
    private JComboBox<LinearFitFactory> fitAlgorithmFactoryComboBox;

    // Fixed value setting variables
    /** The label for the group of variables that deal with fixing values of the fit */
    private JLabel whatIsFixedGroupLabel;
    /** The label for the GraphDataSet selector to modify */
    private JLabel whichGraphSetLabel;
    /** The JComboBox that chooses which GraphDataSet is currently being edited */
    private JComboBox<DataSet> whichGraphSetComboBox;
    /** The index of the current GraphDataSet being modified */
    private int currentGraphSetIndex;
    /** An array of ints that keeps track of what fixed variable is selected for each GraphDataSet so the user does not
     * have to apply changes before modifying another GraphDataSet */
    private FixedVariable[] graphSetsTempWhatFixed;
    /** An array of the values of the fixed varaible for each of the GraphDataSets so the user does not have to apply
     * changes before modifying another GraphDataSet */
    private double[] graphSetsTempFixedValues;
    /** The group of three buttons that allows the user to specify what variable is fixed in the linear fit for the
     * current GraphDataSet */
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

    /** This function when called restores the default start up option for the graph options */
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

        xShowTicksCheckBox.setSelected(true);
        xNumberOfTicksField.setText("10");
        xShowTicksNumberLabelsCheckBox.setSelected(true);
        xAxisDecimalPlacesField.setText("2");

        yShowTicksCheckBox.setSelected(true);
        yNumberOfTicksField.setText("10");
        yShowTicksNumberLabelsCheckBox.setSelected(true);
        yAxisDecimalPlacesField.setText("2");

        displayResultsOnGraphCheckBox.setSelected(true);
        xResultsLocationField.setText("5");
        yResultsLocationField.setText("25");

        decimalsInResultsField.setText("4");
        useScientificNotationInResultsCheckBox.setSelected(true);

        xErrorsOnlyCheckBox.setSelected(false);

        for (int i = 0; i < graphSetsTempWhatFixed.length; i++)
        {
            graphSetsTempWhatFixed[i] = FixedVariable.NONE;
            graphSetsTempFixedValues[i] = 0.0;
        }
        noneFixedRadio.setSelected(true);
        fixedValueField.setText("0.0");

        updateGraphOptionsEnabledStatuses();
    }

    /** Creates the options frame and ties it back to our GraphArea so we can edit its values. It is possible to have
     * multiple GraphOptionFrames at once.
     * 
     * @param graphAreaToEditOptionsOf the GraphArea whose options and settings will be edited
     * @param currentSetInGraphArea The currently selected GraphDataSet that is select in the given GraphArea
     * @param parentIoHandler The GeneralIO object used for handling IO related functionality */
    GraphOptionsMenu(GraphArea graphAreaToEditOptionsOf, DataSet currentSetInGraphArea, GeneralIO parentIoHandler)
    {
        super("Graph Settings");
        ioHandler = parentIoHandler;

        // set our icon
        this.setIconImage(ioHandler.getLineFitIcon());

        setResizable(false);
        graphingArea = graphAreaToEditOptionsOf;
        GraphOptionsFrameLayout customLayout = new GraphOptionsFrameLayout();
        Container contentPane = getContentPane();

        contentPane.setFont(new Font("Arial", Font.PLAIN, 12));
        contentPane.setLayout(customLayout);

        FocusListener onlyNumbers = new OnlyAllowNumbersListener();

        xMinField = new JTextField("textfield_1");
        contentPane.add(xMinField);
        xMinField.setText("" + graphingArea.xAxisMinimumValue);
        xMinField.addFocusListener(onlyNumbers);

        xMaxField = new JTextField("textfield_1");
        contentPane.add(xMaxField);
        // xMaxField.setText(df.format(draw.xMax));
        xMaxField.setText("" + graphingArea.xAxisMaximumValue);
        xMaxField.addFocusListener(onlyNumbers);

        yMinField = new JTextField("textfield_3");
        contentPane.add(yMinField);
        // yMinField.setText(df.format(draw.yMin));
        yMinField.setText("" + graphingArea.yAxisMinimumValue);
        yMinField.addFocusListener(onlyNumbers);

        yMaxField = new JTextField("textfield_2");
        contentPane.add(yMaxField);
        // yMaxField.setText(df.format(draw.yMax));
        yMaxField.setText("" + graphingArea.yAxisMaximumValue);
        yMaxField.addFocusListener(onlyNumbers);

        xMinLabel = new JLabel("X Min");
        contentPane.add(xMinLabel);

        xMaxLabel = new JLabel("X Max");
        contentPane.add(xMaxLabel);

        yMinLabel = new JLabel("Y Min");
        contentPane.add(yMinLabel);

        yMaxLabel = new JLabel("Y Max");
        contentPane.add(yMaxLabel);

        contentPane.add(new JLabel()); // temporary to keep the values from changing
        contentPane.add(new JLabel());
        contentPane.add(new JLabel());
        contentPane.add(new JLabel());

        applyCloseButton = new JButton("Apply and Close");
        contentPane.add(applyCloseButton);

        applyButton = new JButton("Apply");
        contentPane.add(applyButton);

        defaultsButton = new JButton("Defaults");
        contentPane.add(defaultsButton);

        contentPane.add(new JLabel());
        contentPane.add(new JLabel());
        contentPane.add(new JLabel());
        contentPane.add(new JLabel());

        xAxisNameLabel = new JLabel("Name the X-Axis");
        contentPane.add(xAxisNameLabel);

        yAxisNameLabel = new JLabel("Name the Y-Axis");
        contentPane.add(yAxisNameLabel);

        graphNameLabel = new JLabel("Name the Graph");
        contentPane.add(graphNameLabel);

        graphNameField = new JTextField("");
        graphNameField.setText(graphingArea.getGraphName());
        contentPane.add(graphNameField);

        xAxisNameField = new JTextField("");
        xAxisNameField.setText(graphingArea.getXAxisDescription());
        contentPane.add(xAxisNameField);

        yAxisNameField = new JTextField("");
        yAxisNameField.setText(graphingArea.getYAxisDescription());
        contentPane.add(yAxisNameField);

        xAxisTickMarksSectionLabel = new JLabel("<html> <u> X-Axis </u> </html>");
        xAxisTickMarksSectionLabel.setFont(new Font("Verdana", Font.BOLD, 12));
        contentPane.add(xAxisTickMarksSectionLabel);

        yAxisTickMarksSectionLabel = new JLabel("<html> <u> Y-Axis </u> </html>");
        yAxisTickMarksSectionLabel.setFont(new Font("Verdana", Font.BOLD, 12));
        contentPane.add(yAxisTickMarksSectionLabel);

        xNumberOfTicksLabel = new JLabel("Number of Ticks");
        contentPane.add(xNumberOfTicksLabel);

        xNumberOfTicksField = new JTextField("");
        xNumberOfTicksField.setText("" + graphingArea.xAxisNumberOfTickMarks);
        contentPane.add(xNumberOfTicksField);
        xNumberOfTicksField.addFocusListener(onlyNumbers);

        yNumberOfTicksLabel = new JLabel("Number of Ticks");
        contentPane.add(yNumberOfTicksLabel);

        yNumberOfTicksField = new JTextField("");
        yNumberOfTicksField.setText("" + graphingArea.yAxisNumberOfTickMarks);
        contentPane.add(yNumberOfTicksField);
        yNumberOfTicksField.addFocusListener(onlyNumbers);

        xShowTicksCheckBox = new JCheckBox("Show Tick Marks");
        xShowTicksCheckBox.setToolTipText("When checked, tick marks are drawn on the X-axis");
        xShowTicksCheckBox.setSelected(graphingArea.xAxisHasTickMarks);
        contentPane.add(xShowTicksCheckBox);

        xShowTicksNumberLabelsCheckBox = new JCheckBox("Show Tick Mark Numbers");
        xShowTicksNumberLabelsCheckBox.setToolTipText(
                "When checked, puts the location labels below the ticks on the X-axis");
        xShowTicksNumberLabelsCheckBox.setSelected(graphingArea.xAxisHasTickMarkLabels);
        contentPane.add(xShowTicksNumberLabelsCheckBox);

        yShowTicksCheckBox = new JCheckBox("Show Tick Marks");
        yShowTicksCheckBox.setToolTipText("When checked, tick marks are drawn on the Y-axis");
        yShowTicksCheckBox.setSelected(graphingArea.yAxisHasTickMarks);
        contentPane.add(yShowTicksCheckBox);

        yShowTicksNumberLabelsCheckBox = new JCheckBox("Show Tick Mark Numbers");
        yShowTicksNumberLabelsCheckBox.setToolTipText(
                "When checked, puts the location labels below the ticks on the Y-axis");
        yShowTicksNumberLabelsCheckBox.setSelected(graphingArea.yAxisHasTickMarkLabels);
        contentPane.add(yShowTicksNumberLabelsCheckBox);

        xErrorsOnlyCheckBox = new JCheckBox("x errors only");
        xErrorsOnlyCheckBox.setToolTipText(
                "When checked, the third column by itself will represent x errors. Otherwise, it will represent y errors.");
        xErrorsOnlyCheckBox.setSelected(graphingArea.xErrorsOnly);
        contentPane.add(xErrorsOnlyCheckBox);

        usePowersCheckBox = new JCheckBox("Use Powers On Axes");
        usePowersCheckBox.setToolTipText(
                "When checked, powers of ten are taken out from the axes and placed on the end of the axes");
        usePowersCheckBox.setSelected(graphingArea.useAxesPowers);
        contentPane.add(usePowersCheckBox);
        customPowersCheckBox = new JCheckBox("Use Custom Powers On Axes");
        customPowersCheckBox.setToolTipText(
                "When checked, how many powers of ten to take from each axes is determined by the fields below instead of it being automatically determined");
        customPowersCheckBox.setSelected(graphingArea.userDefinedAxesPowers);
        contentPane.add(customPowersCheckBox);
        xPowerLabel = new JLabel("X Axis Power");
        contentPane.add(xPowerLabel);
        xPowerField = new JTextField();
        xPowerField.setText("" + graphingArea.xAxisPower);
        contentPane.add(xPowerField);
        xPowerField.addFocusListener(onlyNumbers);
        yPowerLabel = new JLabel("Y Axis Power");
        contentPane.add(yPowerLabel);
        yPowerField = new JTextField();
        yPowerField.setText("" + graphingArea.yAxisPower);
        contentPane.add(yPowerField);
        yPowerField.addFocusListener(onlyNumbers);

        customAxesCheckBox = new JCheckBox("Use Custom Axes Sizes");
        customAxesCheckBox.setToolTipText(
                "When checked, the axes starts and ends are determines by the fields below instead of being automatically determined");
        customAxesCheckBox.setSelected(graphingArea.userDefinedAxes);
        contentPane.add(customAxesCheckBox);

        displayResultsOnGraphCheckBox = new JCheckBox("Display Results on the Graph");
        displayResultsOnGraphCheckBox.setToolTipText(
                "When checked, the slope and intercept of fits are displayed on the graph and will be exported along with the graph");
        displayResultsOnGraphCheckBox.setSelected(graphingArea.resultsAreDisplayedOnGraph);
        contentPane.add(displayResultsOnGraphCheckBox);
        xResultsLocationLabel = new JLabel("X pos. of results");
        contentPane.add(xResultsLocationLabel);
        xResultsLocationField = new JTextField();
        xResultsLocationField.setText("" + graphingArea.resultsPositionX);
        contentPane.add(xResultsLocationField);
        xResultsLocationField.addFocusListener(onlyNumbers);
        yResultsLocationLabel = new JLabel("Y pos. of results");
        contentPane.add(yResultsLocationLabel);
        yResultsLocationField = new JTextField();
        yResultsLocationField.setText("" + graphingArea.resultsPositionY);
        contentPane.add(yResultsLocationField);
        yResultsLocationField.addFocusListener(onlyNumbers);

        decimalsInResultsLabel = new JLabel("Decimals in Results");
        contentPane.add(decimalsInResultsLabel);
        decimalsInResultsField = new JTextField();
        decimalsInResultsField.setText("" + graphingArea.resultsDecimalPlaces);
        contentPane.add(decimalsInResultsField);
        decimalsInResultsField.addFocusListener(onlyNumbers);

        xAxisDecimalPlacesLabel = new JLabel("Decimals on X Axis");
        contentPane.add(xAxisDecimalPlacesLabel);
        xAxisDecimalPlacesField = new JTextField();
        xAxisDecimalPlacesField.setText("" + graphingArea.xAxisDecimalPlaces);
        contentPane.add(xAxisDecimalPlacesField);
        xAxisDecimalPlacesField.addFocusListener(onlyNumbers);
        yAxisDecimalPlacesLabel = new JLabel("Decimals on Y Axis");
        contentPane.add(yAxisDecimalPlacesLabel);
        yAxisDecimalPlacesField = new JTextField();
        yAxisDecimalPlacesField.setText("" + graphingArea.yAxisDecimalPlaces);
        contentPane.add(yAxisDecimalPlacesField);
        yAxisDecimalPlacesField.addFocusListener(onlyNumbers);

        useScientificNotationInResultsCheckBox = new JCheckBox("Results Use Scientific Notation");
        useScientificNotationInResultsCheckBox.setToolTipText(
                "When checked, powers of ten are taken out of the result and they are displayed in scientific notation");
        useScientificNotationInResultsCheckBox.setSelected(graphingArea.resultsUseScientificNotation);
        contentPane.add(useScientificNotationInResultsCheckBox);

        fitAlgorithmLabel = new JLabel("Fit Algortithm:");
        contentPane.add(fitAlgorithmLabel);
        fitAlgorithmFactoryComboBox = new JComboBox<LinearFitFactory>();
        for (int i = 0; i < LinearFitFactory.fitAlgorithmFactories.length; i++)
        {
            fitAlgorithmFactoryComboBox.addItem(LinearFitFactory.fitAlgorithmFactories[i]);
        }
        fitAlgorithmFactoryComboBox.setSelectedItem(LineFit.currentFitAlgorithmFactory);
        contentPane.add(fitAlgorithmFactoryComboBox);

        whatIsFixedGroupLabel = new JLabel("Fix the Following Value:");
        contentPane.add(whatIsFixedGroupLabel);
        whichGraphSetLabel = new JLabel("For");
        contentPane.add(whichGraphSetLabel);
        whichGraphSetComboBox = new JComboBox<DataSet>();
        currentGraphSetIndex = graphingArea.dataSetSelector.getSelectedIndex();
        graphSetsTempWhatFixed = new FixedVariable[graphingArea.dataSetSelector.getItemCount()];
        graphSetsTempFixedValues = new double[graphingArea.dataSetSelector.getItemCount()];

        for (int i = 0; i < graphingArea.dataSetSelector.getItemCount() - 1; i++) // -1 so we do not include the new
                                                                                  // dataset option
        {
            whichGraphSetComboBox.addItem(graphingArea.dataSetSelector.getItemAt(i));
            graphSetsTempWhatFixed[i] = ((DataSet) graphingArea.dataSetSelector.getItemAt(i)).linearFitStrategy
                    .getWhatIsFixed();
            graphSetsTempFixedValues[i] = ((DataSet) graphingArea.dataSetSelector.getItemAt(i)).linearFitStrategy
                    .getFixedValue();
        }
        whichGraphSetComboBox.setSelectedIndex(currentGraphSetIndex);
        contentPane.add(whichGraphSetComboBox);

        whatIsFixedButtonGroup = new ButtonGroup();
        DataSet fitData = ((DataSet) graphingArea.dataSetSelector.getItemAt(currentGraphSetIndex));
        noneFixedRadio = new JRadioButton("None", fitData.linearFitStrategy.getWhatIsFixed() == FixedVariable.NONE);
        whatIsFixedButtonGroup.add(noneFixedRadio);
        contentPane.add(noneFixedRadio);
        slopeFixedRadio = new JRadioButton("Slope", fitData.linearFitStrategy.getWhatIsFixed() == FixedVariable.SLOPE);
        whatIsFixedButtonGroup.add(slopeFixedRadio);
        contentPane.add(slopeFixedRadio);
        interceptFixedRadio = new JRadioButton("Intercept", fitData.linearFitStrategy
                .getWhatIsFixed() == FixedVariable.INTERCEPT);
        whatIsFixedButtonGroup.add(interceptFixedRadio);
        contentPane.add(interceptFixedRadio);

        fixedValueLabel = new JLabel("Fixed Value");
        contentPane.add(fixedValueLabel);
        fixedValueField = new JTextField("" + fitData.linearFitStrategy.getFixedValue());
        contentPane.add(fixedValueField);
        fixedValueField.addFocusListener(onlyNumbers);

        updateFixValueForCurrDataSet();
        setTickLabelEnabledStatus();
        setPowersEnabledStatus();
        setCustomAxesEnabledStatus();
        setResultOnGraphEnabledStatus();
        setFixedValueEnabledStatus();
        setDataSetCanFixValueEnabledStatus();

        ActionListener optionsListener = new GraphOptionsListener();
        applyButton.addActionListener(optionsListener);
        applyCloseButton.addActionListener(optionsListener);
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

        // create the export options gui elements
        ioHandler.exportIO.createOptionsGuiElements(contentPane);

        setTabStops();
        setSize(getPreferredSize());
        this.setVisible(true);
    }

    /** Closes this instance of the graph options frame */
    private void closeOptionsFrame()
    {
        this.dispose();
    }

    /** Sets the order of the items when using the tabs key and other traversal keys */
    private void setTabStops()
    {
        TabsFocusTraversalPolicy policy = new TabsFocusTraversalPolicy();

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
        policy.addComponentToTabsList(xShowTicksCheckBox);
        policy.addComponentToTabsList(xShowTicksNumberLabelsCheckBox);
        policy.addComponentToTabsList(xNumberOfTicksField);
        policy.addComponentToTabsList(yShowTicksCheckBox);
        policy.addComponentToTabsList(yShowTicksNumberLabelsCheckBox);
        policy.addComponentToTabsList(yNumberOfTicksField);
        policy.addComponentToTabsList(xAxisDecimalPlacesField);
        policy.addComponentToTabsList(yAxisDecimalPlacesField);
        policy.addComponentToTabsList(displayResultsOnGraphCheckBox);
        policy.addComponentToTabsList(xResultsLocationField);
        policy.addComponentToTabsList(yResultsLocationField);
        policy.addComponentToTabsList(decimalsInResultsField);
        policy.addComponentToTabsList(useScientificNotationInResultsCheckBox);

        // add the export items to the tab order
        ioHandler.exportIO.addOptionsGuiElementsToTabs(policy);

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

    /** Disables and enables the different fields in the axis tick marks options section so that they are only enabled
     * when they are applicable based on the other selections */
    void setTickLabelEnabledStatus()
    {
        if (!xShowTicksCheckBox.isSelected())
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

        if (!xShowTicksNumberLabelsCheckBox.isSelected())
        {
            xAxisDecimalPlacesLabel.setEnabled(false);
            xAxisDecimalPlacesField.setEnabled(false);
        }
        else
        {
            xAxisDecimalPlacesLabel.setEnabled(true);
            xAxisDecimalPlacesField.setEnabled(true);
        }

        if (!yShowTicksCheckBox.isSelected())
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

        if (!yShowTicksNumberLabelsCheckBox.isSelected())
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

    /** Disables and Enables the options for the powers on the axis based on whether or not the options are applicable
     * with the current options */
    void setPowersEnabledStatus()
    {
        if (usePowersCheckBox.isSelected())
        {
            customPowersCheckBox.setEnabled(true);
            if (!customPowersCheckBox.isSelected())
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
        if (!customAxesCheckBox.isSelected())
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
        if (!displayResultsOnGraphCheckBox.isSelected())
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

    /** Prevents the fixed value from being changed for individual linear fits when we have selected to have no fixed
     * value */
    void setFixedValueEnabledStatus()
    {
        if (noneFixedRadio.isSelected())
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

    /** Prevents the fixed value and what is fixed from being changed if the dataset selected does not have a line
     * fitted to it */
    void setDataSetCanFixValueEnabledStatus()
    {
        if (((DataSet) graphingArea.dataSetSelector.getItemAt(whichGraphSetComboBox.getSelectedIndex()))
                .getFitType() != FitType.NONE)
        {
            noneFixedRadio.setEnabled(true);
            if (((LinearFitFactory) fitAlgorithmFactoryComboBox.getSelectedItem()).canFixSlopeForGeneratedFits())
            {
                slopeFixedRadio.setEnabled(true);
            }

            if (((LinearFitFactory) fitAlgorithmFactoryComboBox.getSelectedItem()).canFixInterceptForGeneratedFits())
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
        if (whichGraphSetComboBox.getSelectedIndex() != currentGraphSetIndex)
        {
            // save our old values in our temp spots
            if (noneFixedRadio.isSelected())
            {
                graphSetsTempWhatFixed[currentGraphSetIndex] = FixedVariable.NONE;
            }
            else if (slopeFixedRadio.isSelected())
            {
                graphSetsTempWhatFixed[currentGraphSetIndex] = FixedVariable.SLOPE;
            }
            else
            {
                graphSetsTempWhatFixed[currentGraphSetIndex] = FixedVariable.INTERCEPT;
            }
            graphSetsTempFixedValues[currentGraphSetIndex] = Double.parseDouble(fixedValueField.getText());

            // switch to our ne one
            currentGraphSetIndex = whichGraphSetComboBox.getSelectedIndex();

            // now set our new values
            if (graphSetsTempWhatFixed[currentGraphSetIndex] == FixedVariable.SLOPE &&
                    ((LinearFitFactory) fitAlgorithmFactoryComboBox.getSelectedItem()).canFixSlopeForGeneratedFits())
            {
                slopeFixedRadio.setSelected(true);
            }
            else if (graphSetsTempWhatFixed[currentGraphSetIndex] == FixedVariable.INTERCEPT &&
                    ((LinearFitFactory) fitAlgorithmFactoryComboBox.getSelectedItem())
                            .canFixInterceptForGeneratedFits())
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

    /** Saves all the fields values from the options into the GraphArea so that the values actually get changed in the
     * graph */
    void applyChanges()
    {
        ioHandler.changeTracker.setFileModified(); // so we know we have unsaved changes

        graphingArea.userDefinedAxes = customAxesCheckBox.isSelected();
        if (graphingArea.userDefinedAxes)
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

        graphingArea.setThirdColumn(xErrorsOnlyCheckBox.isSelected());

        graphingArea.xAxisNumberOfTickMarks = Integer.parseInt(xNumberOfTicksField.getText());
        graphingArea.yAxisNumberOfTickMarks = Integer.parseInt(yNumberOfTicksField.getText());
        graphingArea.xAxisHasTickMarks = xShowTicksCheckBox.isSelected();
        graphingArea.xAxisHasTickMarkLabels = xShowTicksNumberLabelsCheckBox.isSelected();
        graphingArea.yAxisHasTickMarks = yShowTicksCheckBox.isSelected();
        graphingArea.yAxisHasTickMarkLabels = yShowTicksNumberLabelsCheckBox.isSelected();

        graphingArea.useAxesPowers = usePowersCheckBox.isSelected();
        graphingArea.userDefinedAxesPowers = customPowersCheckBox.isSelected();
        if (graphingArea.useAxesPowers)
        {
            if (!graphingArea.userDefinedAxesPowers)
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

        graphingArea.resultsAreDisplayedOnGraph = displayResultsOnGraphCheckBox.isSelected();

        graphingArea.resultsPositionX = Integer.parseInt(xResultsLocationField.getText());
        graphingArea.resultsPositionY = Integer.parseInt(yResultsLocationField.getText());

        graphingArea.resultsDecimalPlaces = Integer.parseInt(decimalsInResultsField.getText());

        graphingArea.xAxisDecimalPlaces = Integer.parseInt(xAxisDecimalPlacesField.getText());
        graphingArea.yAxisDecimalPlaces = Integer.parseInt(yAxisDecimalPlacesField.getText());
        graphingArea.resultsUseScientificNotation = useScientificNotationInResultsCheckBox.isSelected();

        // check if we need to create new fit strategies
        LinearFitFactory selectedFactory = (LinearFitFactory) this.fitAlgorithmFactoryComboBox.getSelectedItem();
        boolean createNewFitStrategy = false;
        if (selectedFactory != LineFit.currentFitAlgorithmFactory)
        {
            System.out.println("here");
            createNewFitStrategy = true;
            LineFit.currentFitAlgorithmFactory = selectedFactory;
        }

        // update our fit strategies by either creating new ones or at least updating the fixed values
        for (int i = 0; i < whichGraphSetComboBox.getItemCount(); i++)
        {
            // get the DataSet to process, create new fit data and then store that fit data in the DataSet
            DataSet currentlyProcessingDataSet = (DataSet) graphingArea.dataSetSelector.getItemAt(i);

            // if we need to create new strategies for the sets
            if (createNewFitStrategy)
            {
                LineFit.currentFitAlgorithmFactory.createNewLinearFitStartegy(currentlyProcessingDataSet);
            }

            // figure out what to set for the new FitData
            if (i != currentGraphSetIndex)
            {
                currentlyProcessingDataSet.linearFitStrategy.setWhatIsFixed(graphSetsTempWhatFixed[i],
                        graphSetsTempFixedValues[i]);
            }
            else
            {
                if (noneFixedRadio.isSelected())
                {
                    currentlyProcessingDataSet.linearFitStrategy.setWhatIsFixed(FixedVariable.NONE, Double.parseDouble(
                            fixedValueField.getText()));
                }
                else if (slopeFixedRadio.isSelected())
                {
                    currentlyProcessingDataSet.linearFitStrategy.setWhatIsFixed(FixedVariable.SLOPE, Double.parseDouble(
                            fixedValueField.getText()));
                }
                else
                {
                    currentlyProcessingDataSet.linearFitStrategy.setWhatIsFixed(FixedVariable.INTERCEPT, Double
                            .parseDouble(fixedValueField.getText()));
                }
            }
        }

        // apply the export options as well
        ioHandler.exportIO.applyValuesInOptionsGuiElements();

        graphingArea.repaint();
    }

    // check the fitalgorithm switching

    /** A generic listener class that picks up any button or checkbox event and handles it properly, either saving and
     * closing or disable or enabling options and fields that are no longer applicable with the selected options
     * 
     * @author Keith Rice
     * @version 1.0
     * @since &lt;0.98.0 */
    private class GraphOptionsListener implements ActionListener
    {
        @Override
        /** The event handler that determines which button or checkbox was selected and then handles the event
         * properly */
        public void actionPerformed(ActionEvent e)
        {
            if (e.getActionCommand().equals("Apply and Close"))
            {
                applyChanges();
                closeOptionsFrame();
            }
            else if (e.getActionCommand().equals("Apply"))
            {
                applyChanges();
            }
            else if (e.getActionCommand().equals("Defaults"))
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
     * options to disable and enable parts that are relevant or not now that the option has been selected or
     * deselcted */
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

    public static void setElementBoundsIfVisible(Component element, Insets insets, int leftOffset, int topOffset,
            int width)
    {
        if (element.isVisible())
        {
            element.setBounds(insets.left + leftOffset, insets.top + topOffset, width,
                    GraphOptionsMenu.STANDARD_ELEMENT_HEIGHT);
        }
    }

    /** Allows us to position groups of options together and allows us to to deal with larger chunks on the high level
     * of building the graph options menu
     * 
     * @param componentsInGroup The list of components that make up this group
     * @param xShiftOfGroup The y Location of the start of the group (from the top)
     * @param yShiftOfGroup The x Location of the start of the group (from the left) */
    public static void shiftElementGroup(ArrayList<Component> componentsInGroup, int xShiftOfGroup, int yShiftOfGroup)
    {
        Component currComponent = null;
        for (int i = 0; i < componentsInGroup.size(); i++)
        {
            currComponent = componentsInGroup.get(i);
            if (currComponent.isVisible())
            {
                currComponent.setBounds(currComponent.getX() + xShiftOfGroup, currComponent.getY() + yShiftOfGroup,
                        currComponent.getWidth(), currComponent.getHeight());
            }
        }
    }

    /** A Layout class that positions all the buttons and options in the frame
     * 
     * @author Keith Rice
     * @version 1.0
     * @since &lt;0.98.0 */
    private class GraphOptionsFrameLayout implements LayoutManager
    {
        GraphOptionsFrameLayout()
        {
        }

        public void addLayoutComponent(String name, Component comp)
        {
        }

        public void removeLayoutComponent(Component comp)
        {
        }

        /** Sets the dimensions of the graph options frame */
        public Dimension preferredLayoutSize(Container parent)
        {
            Dimension graphOptionsDimensions = new Dimension(0, 0);

            Insets insets = parent.getInsets();
            graphOptionsDimensions.width = 720 + insets.left + insets.right;
            graphOptionsDimensions.height = 530 + insets.top + insets.bottom;

            return graphOptionsDimensions;
        }

        /** Sets the minimum size of the graph options frame */
        public Dimension minimumLayoutSize(Container parent)
        {
            Dimension dim = new Dimension(100, 100);
            return dim;
        }

        /** Describes the layout of the options within their groups as well as the positions of the groups in the
         * frame */
        public void layoutContainer(Container parent)
        {
            Insets insets = parent.getInsets();
            final int acButtonWidth = 144;
            final int applyButtonWidth = 76;
            final int defaultsButtonWidth = 87;

            // Graph title and axis names
            setElementBoundsIfVisible(graphNameLabel, insets, 0, 0, 208);
            setElementBoundsIfVisible(graphNameField, insets, 0, 20, 208);
            setElementBoundsIfVisible(yAxisNameLabel, insets, 0, 50, 208);
            setElementBoundsIfVisible(yAxisNameField, insets, 0, 70, 208);
            setElementBoundsIfVisible(xAxisNameLabel, insets, 0, 100, 208);
            setElementBoundsIfVisible(xAxisNameField, insets, 0, 120, 208);

            ArrayList<Component> titleAxesGroup = new ArrayList<Component>();
            titleAxesGroup.add(graphNameLabel);
            titleAxesGroup.add(graphNameField);
            titleAxesGroup.add(yAxisNameLabel);
            titleAxesGroup.add(yAxisNameField);
            titleAxesGroup.add(xAxisNameLabel);
            titleAxesGroup.add(xAxisNameField);

            // X and Y Max and Min
            setElementBoundsIfVisible(customAxesCheckBox, insets, 0, 0, 184);
            setElementBoundsIfVisible(xMinLabel, insets, 0, 30, 80);
            setElementBoundsIfVisible(xMinField, insets, 88, 30, 104);
            setElementBoundsIfVisible(xMaxLabel, insets, 0, 62, 80);
            setElementBoundsIfVisible(xMaxField, insets, 88, 62, 104);
            setElementBoundsIfVisible(yMinLabel, insets, 0, 94, 80);
            setElementBoundsIfVisible(yMinField, insets, 88, 94, 104);
            setElementBoundsIfVisible(yMaxLabel, insets, 0, 126, 80);
            setElementBoundsIfVisible(yMaxField, insets, 88, 126, 104);

            ArrayList<Component> maxMinGroup = new ArrayList<Component>();
            maxMinGroup.add(customAxesCheckBox);
            maxMinGroup.add(xMinLabel);
            maxMinGroup.add(xMinField);
            maxMinGroup.add(xMaxLabel);
            maxMinGroup.add(xMaxField);
            maxMinGroup.add(yMinLabel);
            maxMinGroup.add(yMinField);
            maxMinGroup.add(yMaxLabel);
            maxMinGroup.add(yMaxField);

            // Powers on the axes
            setElementBoundsIfVisible(usePowersCheckBox, insets, 0, 0, 200);
            setElementBoundsIfVisible(customPowersCheckBox, insets, 0, 30, 230);
            setElementBoundsIfVisible(xPowerLabel, insets, 4, 60, 100);
            setElementBoundsIfVisible(xPowerField, insets, 129, 60, 60);
            setElementBoundsIfVisible(yPowerLabel, insets, 4, 92, 100);
            setElementBoundsIfVisible(yPowerField, insets, 129, 92, 60);

            ArrayList<Component> customPowersGroup = new ArrayList<Component>();
            customPowersGroup.add(usePowersCheckBox);
            customPowersGroup.add(customPowersCheckBox);
            customPowersGroup.add(xPowerLabel);
            customPowersGroup.add(xPowerField);
            customPowersGroup.add(yPowerLabel);
            customPowersGroup.add(yPowerField);

            // "Apply and Close" Button and "Apply" Button
            setElementBoundsIfVisible(applyCloseButton, insets, 0, 0, acButtonWidth);
            setElementBoundsIfVisible(applyButton, insets, acButtonWidth + 6, 0, applyButtonWidth);
            setElementBoundsIfVisible(defaultsButton, insets, acButtonWidth + 6 + applyButtonWidth + 6, 0,
                    defaultsButtonWidth);

            ArrayList<Component> applyButtonGroup = new ArrayList<Component>();
            applyButtonGroup.add(applyCloseButton);
            applyButtonGroup.add(applyButton);
            applyButtonGroup.add(defaultsButton);

            // Tick Marks and Labeling
            setElementBoundsIfVisible(xAxisTickMarksSectionLabel, insets, 4, 0, 100);
            setElementBoundsIfVisible(xShowTicksCheckBox, insets, 0, 21, 200);
            setElementBoundsIfVisible(xNumberOfTicksLabel, insets, 4, 51, 120);
            setElementBoundsIfVisible(xNumberOfTicksField, insets, 129, 51, 60);
            setElementBoundsIfVisible(xShowTicksNumberLabelsCheckBox, insets, 0, 81, 200);
            setElementBoundsIfVisible(xAxisDecimalPlacesLabel, insets, 4, 111, 125);
            setElementBoundsIfVisible(xAxisDecimalPlacesField, insets, 129, 111, 60);
            setElementBoundsIfVisible(yAxisTickMarksSectionLabel, insets, 4, 136, 100);
            setElementBoundsIfVisible(yShowTicksCheckBox, insets, 0, 157, 200);
            setElementBoundsIfVisible(yNumberOfTicksLabel, insets, 4, 187, 120);
            setElementBoundsIfVisible(yNumberOfTicksField, insets, 129, 187, 60);
            setElementBoundsIfVisible(yShowTicksNumberLabelsCheckBox, insets, 0, 217, 200);
            setElementBoundsIfVisible(yAxisDecimalPlacesLabel, insets, 4, 247, 125);
            setElementBoundsIfVisible(yAxisDecimalPlacesField, insets, 129, 247, 60);

            ArrayList<Component> tickMarkAndLabelGroup = new ArrayList<Component>();
            tickMarkAndLabelGroup.add(xAxisTickMarksSectionLabel);
            tickMarkAndLabelGroup.add(xShowTicksCheckBox);
            tickMarkAndLabelGroup.add(xNumberOfTicksLabel);
            tickMarkAndLabelGroup.add(xNumberOfTicksField);
            tickMarkAndLabelGroup.add(xShowTicksNumberLabelsCheckBox);
            tickMarkAndLabelGroup.add(xAxisDecimalPlacesLabel);
            tickMarkAndLabelGroup.add(xAxisDecimalPlacesField);
            tickMarkAndLabelGroup.add(yAxisTickMarksSectionLabel);
            tickMarkAndLabelGroup.add(yShowTicksCheckBox);
            tickMarkAndLabelGroup.add(yNumberOfTicksLabel);
            tickMarkAndLabelGroup.add(yNumberOfTicksField);
            tickMarkAndLabelGroup.add(yShowTicksNumberLabelsCheckBox);
            tickMarkAndLabelGroup.add(yAxisDecimalPlacesLabel);
            tickMarkAndLabelGroup.add(yAxisDecimalPlacesField);

            // Where the results are displayed on the graph
            setElementBoundsIfVisible(displayResultsOnGraphCheckBox, insets, 0, 0, 250);
            setElementBoundsIfVisible(xResultsLocationLabel, insets, 4, 30, 142);
            setElementBoundsIfVisible(xResultsLocationField, insets, 145, 30, 60);
            setElementBoundsIfVisible(yResultsLocationLabel, insets, 4, 62, 142);
            setElementBoundsIfVisible(yResultsLocationField, insets, 145, 62, 60);

            ArrayList<Component> resultOnGraphGroup = new ArrayList<Component>();
            resultOnGraphGroup.add(displayResultsOnGraphCheckBox);
            resultOnGraphGroup.add(xResultsLocationLabel);
            resultOnGraphGroup.add(xResultsLocationField);
            resultOnGraphGroup.add(yResultsLocationLabel);
            resultOnGraphGroup.add(yResultsLocationField);

            // How many places to round the answers to
            setElementBoundsIfVisible(decimalsInResultsLabel, insets, 4, 0, 142);
            setElementBoundsIfVisible(decimalsInResultsField, insets, 145, 0, 60);
            setElementBoundsIfVisible(useScientificNotationInResultsCheckBox, insets, 0, 30, 220);

            ArrayList<Component> resultDecGroup = new ArrayList<Component>();
            resultDecGroup.add(decimalsInResultsLabel);
            resultDecGroup.add(decimalsInResultsField);
            resultDecGroup.add(useScientificNotationInResultsCheckBox);

            // allows to select fitting algorithm
            setElementBoundsIfVisible(fitAlgorithmLabel, insets, 4, 0, 200);
            setElementBoundsIfVisible(fitAlgorithmFactoryComboBox, insets, 4, 32, 200);

            ArrayList<Component> algorithmGroup = new ArrayList<Component>();
            algorithmGroup.add(fitAlgorithmLabel);
            algorithmGroup.add(fitAlgorithmFactoryComboBox);

            // Fixes the slope or the intercept or none
            setElementBoundsIfVisible(whatIsFixedGroupLabel, insets, 4, 0, 200);
            setElementBoundsIfVisible(whichGraphSetLabel, insets, 4, 32, 100);
            setElementBoundsIfVisible(whichGraphSetComboBox, insets, 88, 32, 120);
            setElementBoundsIfVisible(noneFixedRadio, insets, 0, 64, 64);
            setElementBoundsIfVisible(slopeFixedRadio, insets, 60, 64, 68);
            setElementBoundsIfVisible(interceptFixedRadio, insets, 124, 64, 90);
            setElementBoundsIfVisible(fixedValueLabel, insets, 4, 92, 80);
            setElementBoundsIfVisible(fixedValueField, insets, 88, 92, 120);

            ArrayList<Component> fixedGroup = new ArrayList<Component>();
            fixedGroup.add(whatIsFixedGroupLabel);
            fixedGroup.add(whichGraphSetLabel);
            fixedGroup.add(whichGraphSetComboBox);
            fixedGroup.add(noneFixedRadio);
            fixedGroup.add(slopeFixedRadio);
            fixedGroup.add(interceptFixedRadio);
            fixedGroup.add(fixedValueLabel);
            fixedGroup.add(fixedValueField);

            // X errors only and Y errors only
            setElementBoundsIfVisible(xErrorsOnlyCheckBox, insets, 0, 0, 150);

            ArrayList<Component> errorsGroup = new ArrayList<Component>();
            errorsGroup.add(xErrorsOnlyCheckBox);

            // Positions each group on the frame
            shiftElementGroup(titleAxesGroup, 24, 5);
            shiftElementGroup(maxMinGroup, 24, 159);
            shiftElementGroup(customPowersGroup, 20, 320);
            shiftElementGroup(applyButtonGroup, 20, 462);
            shiftElementGroup(tickMarkAndLabelGroup, 260, 5);
            shiftElementGroup(resultOnGraphGroup, 260, 295);
            shiftElementGroup(resultDecGroup, 260, 395);
            ioHandler.exportIO.positionOptionsGuiElements(insets, 484, 5);
            shiftElementGroup(algorithmGroup, 480, 180);
            shiftElementGroup(fixedGroup, 480, 255);
            shiftElementGroup(errorsGroup, 480, 420);
        }
    }
}