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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import linefit.FitAlgorithms.FitType;
import linefit.FitAlgorithms.LinearFitFactory;
import linefit.IO.GeneralIO;
import linefit.IO.HasDataToSave;
import linefit.IO.HasOptionsToSave;


/** The main operating class in LineFit that is called on start up and sets up the base and layout and that handles all
 * the functions calls and methods of the program
 * 
 * @author Keith Rice
 * @version 2.0
 * @since &lt;0.98.0 */
public class LineFit extends JFrame implements HasOptionsToSave, HasDataToSave
{
    /** Main - This is the method that starts up the instance of LineFit. If a File path is inputed in the args, it will
     * try to load the file at the specified location on startup
     * 
     * @param args Either empty of the file path of the File to load on start up */
    public static void main(String args[])
    {
        if (args.length == 0)
        {
            new LineFit();
        }
        else if (args.length == 1)
        {
            new LineFit(args[0]);
        }
    }

    /** The Serial Version UID so that we know what version it is when we are using it. See
     * http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html for full discussion on its uses and purpose */
    private final static long serialVersionUID = 42L;

    // Variables for the visible components of LineFit
    /** The GraphArea of this instance of LineFit in which the Graph is drawn */
    private GraphArea graphingArea;
    /** The Panel that contains all the core pieces of LineFit */
    private JPanel mainDisplayPanel;
    /** The panel along the right side of the LineFit window that contains the current {@link DataSet} information as
     * well as the linear fit's results if the current DataSet has a selected fit */
    private JPanel rightSideBar;
    /** The Panel that contains all the often used options, navigation and editing features such as selecting or adding
     * new DataSets */
    private JPanel quickBar;
    /** The Panel that is part of the right side bar that contains the currently selected DataSet's linear fit results
     * if a fit is selected */
    private JPanel fitDataPanel;
    /** The main layout for LineFit that lets us position panels where we desire */
    private SpringLayout mainLayout;
    /** The Layout for the Quick Bar that lets us position buttons where we desire */
    private SpringLayout quickBarLayout;

    // Sizing variables
    /** The height of the quick bar in pixels */
    final static int QUICK_BAR_HEIGHT = 30;
    /** The width of the DataColumns in the DataSets */
    final static int DATA_COLUMN_WIDTH = 110;
    /** the Default minimum value on the x-axis of the Graph */
    final static double DEFAULT_X_AXIS_MINIMUM_VALUE = 0;
    /** The Default maximum value on the x-axis of the Graph */
    final static double DEFAULT_X_AXIS_MAXIMUM_VALUE = 10;
    /** The Default minimum value on the y-axis of the Graph */
    final static double DEFAULT_Y_AXIS_MINIMUM_VALUE = 0;
    /** The Default maximum value on the y-axis of the Graph */
    final static double DEFAULT_Y_AXIS_MAXIMUM_VALUE = 10;
    /** The width of the DataSetTable that displays and allows the current DataSet's data to be edited */
    private int dataSetTableWidth = 0;

    // Variables for the drop downs and options on the quick bar
    /** The drop down menu that contains the DataSets as well as the currently selected DataSet */
    private JComboBox<DataSet> dataSetSelector = new JComboBox<DataSet>();
    /** The drop down box that allows the user to select the FitType of the current DataSet */
    private JComboBox<FitType> fitSelector = new JComboBox<FitType>();
    /** The drop down box that allows the user to select the Color of the current DataSet */
    private JComboBox<Color> colorSelector = new JComboBox<Color>();
    /** Renderer that is used to display the colors in the colorSelector combo box */
    private ColorBoxRenderer colorSelectorRenderer = new ColorBoxRenderer();
    /** The drop down box that allows the user to select the Shape of the current DataSet */
    private JComboBox<Shape> shapeSelector = new JComboBox<Shape>();
    /** The JSpinner that allows the user to set the number of DataColumns for the current DataSet */
    private JSpinner columnSelector;
    /** The JLabel for the number of DataColumns JSpinner */
    private JLabel columnLabel = new JLabel("Columns:");
    /** The JCheckBox that allows the user to make the currently selected DataSet hidden or visible on the Graph */
    private JCheckBox visibleCheckBox;
    /** The JButton on the quickBar that allows the user to open up the options for the GraphArea */
    private JButton graphOptionsButton;
    /** The JTextArea in which the linear fit's results are displayed to the user. This is a TextArea instead of a Label
     * so that the user can copy the results */
    private JTextArea fitResultsArea;

    // Titles in the file drop down item on the menu bar
    /** The String to be displayed for the new file option in the menu bar drop down */
    private static final String menuTitles_NewWindow = "New Window";
    /** The keyboard shortcut for the new file option in the menu bar drop down */
    private static final String menuTitles_NewWindow_Shortcut = "control N";

    /** The String to be displayed for the open file option in the menu bar drop down */
    private static final String menuTitles_OpenFile = "Open...";
    /** The keyboard shortcut for the open file option in the menu bar drop down */
    private static final String menuTitles_OpenFile_Shortcut = "control O";

    /** The String to be displayed for the open file in a new window option in the menu bar drop down */
    private static final String menuTitles_OpenFileNewWindow = "Open in New Window...";
    /** The keyboard shortcut for the check for updates option in the menu bar drop down */
    private static final String menuTitles_OpenFileNewWindow_Shortcut = "control shift O";

    /** The String to be displayed for the open file in a new window option in the menu bar drop down */
    private static final String menuTitles_SaveFile = "Save";
    /** The keyboard shortcut for the check for updates option in the menu bar drop down */
    private static final String menuTitles_SaveFile_Shortcut = "control S";

    /** The String to be displayed for the new Data Set option in the menu bar drop down */
    private static final String menuTitles_NewDataSet = "New Data Set";
    /** The keyboard shortcut for the new Data Set option in the menu bar drop down */
    private static final String menuTitles_NewDataSet_Shortcut = "control shift D";

    /** The String to be displayed for the graph options option in the menu bar drop down */
    private static final String menuTitles_GraphOptions = "Graph Options...";
    /** The keyboard shortcut for the graph options option in the menu bar drop down */
    private static final String menuTitles_GraphOptions_Shortcut = "control shift G";

    /** The String to be displayed for the export as a JPEG option in the menu bar drop down */
    private static final String menuTitles_ExportJPG = "Export graph as JPEG...";
    /** The keyboard shortcut for the export as a JPEG option in the menu bar drop down */
    private static final String menuTitles_ExportJPG_Shortcut = "control shift J";

    /** The String to be displayed for the export as a PDF option in the menu bar drop down */
    private static final String menuTitles_ExportPDF = "Export graph as PDF...";
    /** The keyboard shortcut for the export as a PDF option in the menu bar drop down */
    private static final String menuTitles_ExportPDF_Shortcut = "control shift P";

    /** The String to be displayed for the export as a LaTex file option in the menu bar drop down */
    private static final String menuTitles_ExportTex = "Export graph as LaTex...";
    /** The keyboard shortcut for the export as a LaTex option in the menu bar drop down */
    private static final String menuTitles_ExportTex_Shortcut = "control shift L";

    /** The String to be displayed for the exit LineFit option in the menu bar drop down */
    private static final String menuTitles_Exit = "Exit";
    /** The keyboard shortcut for the exit LineFit option in the menu bar drop down */
    private static final String menuTitles_Exit_Shortcut = "ctrl E";

    /** The String to be displayed for the LineFit help option in the menu bar drop down */
    private static final String menuTitles_LineFitHelp = "LineFit Help";
    /** The keyboard shortcut for the LineFit help option in the menu bar drop down */
    private static final String menuTitles_LineFitHelp_Shortcut = "F1";

    /** The String to be displayed for the about LineFit option in the menu bar drop down */
    private static final String menuTitles_AboutLineFit = "About LineFit";
    /** The keyboard shortcut for the about LineFit option in the menu bar drop down */
    private static final String menuTitles_AboutLineFit_Shortcut = "F2";

    /** The String to be displayed for the check for updates option in the menu bar drop down */
    private static final String menuTitles_CheckForUpdates = "Check for newer version";
    /** The keyboard shortcut for the check for updates option in the menu bar drop down */
    private static final String menuTitles_CheckForUpdates_Shortcut = "F3";

    // Other variables
    // TODO: encapsulate in a class or make it a semaphore (seems like alot of overhead)
    /** Tracks whether or not the quick menu listener is being called already. This is used to prevent it from being
     * called when we programmatically make changes to it. It is an int so we can go multiple layers deep if need be */
    private int temporarilyDisableQuickMenuListener = 0;

    // Classes that should be set once upon initialization
    /** The object that handles the IO for LineFit */
    private final GeneralIO ioHandler;
    /** The action to perform when the allowable fit types for the displayed DataSet are updated */
    private final Runnable onUpdateFitTypesAction;
    /** The action to perform when the color of the selected DataSet is updated */
    private final Runnable onUpdateColorAction;
    /** The custom color menu object to allow users to custom choose colors for the DataSets */
    private CustomColorMenu customColorMenu;

    /** The default FitAlgorithm to use when creating linear fits for the DataSets */
    static LinearFitFactory currentFitAlgorithmFactory = LinearFitFactory.fitAlgorithmFactories[0];

    /** The default constructor for LineFit that creates a new instance with no data in it */
    private LineFit()
    {
        // make our title and set our size
        super("LineFit");
        setSize(1000, 750);

        // Set up some of the update actions and the IO
        onUpdateFitTypesAction = new updateDataSetAction();
        onUpdateColorAction = new updateDataSetColorAction();
        ioHandler = new GeneralIO(this);
        this.setIconImage(ioHandler.getLineFitIcon());

        // make the results area that looks like a label so it doesnt look out of place
        fitResultsArea = new JTextArea();
        fitResultsArea.setEditable(false);
        fitResultsArea.setBackground(null);

        // make our graph area
        graphingArea = new GraphArea(DEFAULT_X_AXIS_MINIMUM_VALUE, DEFAULT_X_AXIS_MAXIMUM_VALUE,
                DEFAULT_Y_AXIS_MINIMUM_VALUE, DEFAULT_Y_AXIS_MAXIMUM_VALUE, dataSetSelector, fitResultsArea);

        ioHandler.InitializeExportIO(graphingArea);

        // create our panels and menu bars
        mainDisplayPanel = new JPanel();
        createMenuBar();
        createQuickBar();
        createRightSideBar();

        // Now that all the GUI components are created, set up the layout of the entire program
        setupLayout();

        // Change what we do when we are closed
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                ioHandler.closeApplication();
            }
        });

        createNewDataSet();

        // So it pops up on the center of the screen
        this.centerOnScreen();

        // check for the version
        ioHandler.isUpdateAvailable(false);
    }

    /** Creates a new LineFit and then populates it with the data from the file at the specified file path
     * 
     * @param filePathOfFileToLoad The file path of the file to load into the new instance of LineFit */
    private LineFit(String filePathOfFileToLoad)
    {
        this(); // first create a blank one using the default constructor and then populate it
        ioHandler.fileIO.openLineFitFile(filePathOfFileToLoad, false);
    }

    /** Creates and initializes the right side bar for the LineFit display where the current DataSet and its linear fit
     * results are displayed */
    private void createRightSideBar()
    {
        rightSideBar = new JPanel(new GridLayout(2, 1));
        rightSideBar.setFocusable(true);

        fitDataPanel = new JPanel();
        TitledBorder tb = new TitledBorder("Fit Data");
        fitDataPanel.setBorder(tb);
        fitDataPanel.add(fitResultsArea);
    }

    /** Creates and initializes the quick bar at the top of the LineFit below the menu bar that allows us to change
     * GraphSets as well as the FitType, Color, etc. */
    private void createQuickBar()
    {
        graphOptionsButton = new JButton("Graph Options");
        visibleCheckBox = new JCheckBox("Visible");

        // Set up the Column Selector (in TopBar)
        // See
        // http://java.sun.com/docs/books/tutorial/uiswing/examples/components/SpinnerDemoProject/src/components/SpinnerDemo.java
        SpinnerNumberModel columnSelectorModel = new SpinnerNumberModel(DataSet.DEFAULT_NUMBER_OF_COLUMNS, 2, 4, 1);
        columnSelector = new JSpinner(columnSelectorModel);
        columnSelector.setToolTipText("Add or Subtract a Column");

        // create the new DataSet option in the dataSet selector combo box
        DataSet newDataSet = DataSet.createDropDownPlaceHolder("New DataSet");
        dataSetSelector.addItem(newDataSet);

        // Set up line color selection ComboBox
        colorSelector.setRenderer(colorSelectorRenderer);

        // Add possible colors to colorSelector
        for (Color color : DataSet.predefinedColors)
        {
            colorSelector.addItem(color);
        }
        colorSelector.addItem(ColorBoxRenderer.RESERVED_FOR_CUSTOM_COLOR);

        // set up the shape selector drop down
        ShapeBoxRenderer shapeRenderer = new ShapeBoxRenderer();
        shapeSelector.setRenderer(shapeRenderer);
        shapeSelector.addItem(new Rectangle2D.Double()); // square
        shapeSelector.addItem(new Ellipse2D.Double()); // circle
        shapeSelector.addItem(new Polygon()); // triangle

        setupQuickBar();

        // Add Action Listeners
        ActionListener quickBarList = new QuickBarListener();
        visibleCheckBox.addActionListener(quickBarList);
        fitSelector.addActionListener(quickBarList);
        dataSetSelector.addActionListener(quickBarList);
        colorSelector.addActionListener(quickBarList);
        shapeSelector.addActionListener(quickBarList);
        graphOptionsButton.addActionListener(quickBarList);

        columnSelector.addChangeListener(new changeNumberOfColumnsListener());
    }

    /** Creates and initializes the menu bar where the user can save, open, export, etc. */
    private void createMenuBar()
    {
        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create Main Menus
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        // Add Main Menus to main Menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        // Create items to be placed in "File" menu and add their shortcuts
        ActionListener fileMenuListener = new FileMenuBarDropDownButtonsListener();

        Utils.createAndAddMenuItem(fileMenu, menuTitles_NewWindow, fileMenuListener, menuTitles_NewWindow_Shortcut);
        Utils.createAndAddMenuItem(fileMenu, menuTitles_OpenFile, fileMenuListener, menuTitles_OpenFile_Shortcut);
        Utils.createAndAddMenuItem(fileMenu, menuTitles_OpenFileNewWindow, fileMenuListener,
                menuTitles_OpenFileNewWindow_Shortcut);
        fileMenu.addSeparator();
        Utils.createAndAddMenuItem(fileMenu, menuTitles_SaveFile, fileMenuListener, menuTitles_SaveFile_Shortcut);
        fileMenu.addSeparator();
        Utils.createAndAddMenuItem(fileMenu, menuTitles_NewDataSet, fileMenuListener, menuTitles_NewDataSet_Shortcut);
        fileMenu.addSeparator();
        Utils.createAndAddMenuItem(fileMenu, menuTitles_GraphOptions, fileMenuListener,
                menuTitles_GraphOptions_Shortcut);
        fileMenu.addSeparator();
        Utils.createAndAddMenuItem(fileMenu, menuTitles_ExportJPG, fileMenuListener, menuTitles_ExportJPG_Shortcut);
        Utils.createAndAddMenuItem(fileMenu, menuTitles_ExportPDF, fileMenuListener, menuTitles_ExportPDF_Shortcut);
        Utils.createAndAddMenuItem(fileMenu, menuTitles_ExportTex, fileMenuListener, menuTitles_ExportTex_Shortcut);
        fileMenu.addSeparator();
        Utils.createAndAddMenuItem(fileMenu, menuTitles_Exit, fileMenuListener, menuTitles_Exit_Shortcut);

        // Install the menu bar in the frame
        setJMenuBar(menuBar);

        // Create items to be placed in "Help" menu and add their shortcuts
        ActionListener helpMenuListener = new HelpMenuBarDropDownButtonsListener();

        Utils.createAndAddMenuItem(helpMenu, menuTitles_AboutLineFit, helpMenuListener,
                menuTitles_AboutLineFit_Shortcut);
        Utils.createAndAddMenuItem(helpMenu, menuTitles_LineFitHelp, helpMenuListener, menuTitles_LineFitHelp_Shortcut);
        helpMenu.addSeparator();
        Utils.createAndAddMenuItem(helpMenu, menuTitles_CheckForUpdates, helpMenuListener,
                menuTitles_CheckForUpdates_Shortcut);
    }

    /** Creates an initializes the QuickBar which contains things such as the current DataSet selector and the FitType
     * selector */
    private void setupQuickBar()
    {
        // Set up the top bar
        quickBar = new JPanel();
        quickBarLayout = new SpringLayout();

        quickBar.setLayout(quickBarLayout);

        // Constraints items in the quickbar
        Utils.inlayGuiItem(quickBarLayout, quickBar, columnSelector, false, quickBar, -50, -8, 4, -4);
        Utils.inlayGuiItem(quickBarLayout, quickBar, columnLabel, true, columnSelector, -70, -4, 4, -4);
        Utils.inlayGuiItem(quickBarLayout, quickBar, dataSetSelector, true, columnLabel, -120, -4, 4, -4);
        Utils.inlayGuiItem(quickBarLayout, quickBar, colorSelector, true, dataSetSelector, -80, -4, 4, -4);
        Utils.inlayGuiItem(quickBarLayout, quickBar, shapeSelector, true, colorSelector, -50, -4, 4, -4);
        Utils.inlayGuiItem(quickBarLayout, quickBar, fitSelector, true, shapeSelector, -120, -4, 4, -4);
        Utils.inlayGuiItem(quickBarLayout, quickBar, visibleCheckBox, true, fitSelector, -80, -4, 4, -4);
        Utils.inlayGuiItem(quickBarLayout, quickBar, graphOptionsButton, true, quickBar, 8, 138, 4, -4);
    }

    /** Sets up the layout of LineFit and places each panel in the right spot */
    private void setupLayout()
    {
        mainLayout = new SpringLayout();

        mainDisplayPanel.removeAll();

        mainDisplayPanel.setLayout(mainLayout);

        mainLayout.putConstraint(SpringLayout.WEST, quickBar, 0, SpringLayout.WEST, mainDisplayPanel);
        mainLayout.putConstraint(SpringLayout.EAST, quickBar, 0, SpringLayout.EAST, mainDisplayPanel);
        mainLayout.putConstraint(SpringLayout.NORTH, quickBar, 0, SpringLayout.NORTH, mainDisplayPanel);
        mainLayout.putConstraint(SpringLayout.SOUTH, quickBar, QUICK_BAR_HEIGHT, SpringLayout.NORTH, mainDisplayPanel);

        mainLayout.putConstraint(SpringLayout.WEST, rightSideBar, -dataSetTableWidth, SpringLayout.EAST,
                mainDisplayPanel);
        mainLayout.putConstraint(SpringLayout.EAST, rightSideBar, 0, SpringLayout.EAST, mainDisplayPanel);
        mainLayout.putConstraint(SpringLayout.NORTH, rightSideBar, QUICK_BAR_HEIGHT, SpringLayout.NORTH,
                mainDisplayPanel);
        mainLayout.putConstraint(SpringLayout.SOUTH, rightSideBar, 0, SpringLayout.SOUTH, mainDisplayPanel);

        mainLayout.putConstraint(SpringLayout.WEST, graphingArea, 0, SpringLayout.WEST, mainDisplayPanel);
        mainLayout.putConstraint(SpringLayout.EAST, graphingArea, 0, SpringLayout.WEST, rightSideBar);
        mainLayout.putConstraint(SpringLayout.NORTH, graphingArea, QUICK_BAR_HEIGHT, SpringLayout.NORTH,
                mainDisplayPanel);
        mainLayout.putConstraint(SpringLayout.SOUTH, graphingArea, 0, SpringLayout.SOUTH, mainDisplayPanel);

        mainDisplayPanel.add(quickBar);
        mainDisplayPanel.add(rightSideBar);
        mainDisplayPanel.add(graphingArea);

        this.add(mainDisplayPanel);
        this.setVisible(true);
    }

    /** updates the layout of LineFit for when the window is resized */
    private void updateLayout()
    {
        mainLayout.putConstraint(SpringLayout.WEST, rightSideBar, -dataSetTableWidth, SpringLayout.EAST,
                mainDisplayPanel);

        // validating causes the layout to be updated
        validate();
    }

    /** Creates and returns a new DataSet and makes it the currently selected DataSet
     * 
     * @return The newly created DataSet */
    private DataSet createNewDataSet()
    {
        // create a new dataset
        DataSet current = new DataSet(ioHandler.changeTracker, onUpdateFitTypesAction);

        // Register it with the graphing area which will keep the new DataSet option at the end of the list and select
        // it which will then trigger the listener to update the GUI with the new dataset
        graphingArea.registerDataSet(current);

        return current;
    }

    /** Updates the GUI for the DataSet that is being displayed. Normally used when switching displayed DataSets */
    private void updateDataSetDisplayed()
    {
        temporarilyDisableQuickMenuListener++;

        // get the currently selected dataset
        DataSet current = (DataSet) dataSetSelector.getSelectedItem();

        // remove all and then add the dataset we are displaying the table for and the fit data panel
        rightSideBar.removeAll();
        rightSideBar.add(current);
        rightSideBar.add(fitDataPanel);

        // Update the fit types that are available
        updateFitTypes();

        dataSetTableWidth = current.getNumberOfDisplayedColumns() * DATA_COLUMN_WIDTH;

        // Update the current selected color
        if (current.isColorCustom())
        {
            colorSelector.setSelectedItem(ColorBoxRenderer.RESERVED_FOR_CUSTOM_COLOR);
        }
        else
        {
            colorSelector.setSelectedItem(current.getColor());
        }
        colorSelectorRenderer.setCustomColor(current.getLastCustomColor());

        Shape currentShape = current.getShape();
        shapeSelector.setSelectedItem(currentShape);
        // for whatever reason it does not like setSelectedItem to a Polygon so we have to do this so it
        // gets the triangle shape drop down
        if (currentShape.getClass() == new Polygon().getClass())
        {
            shapeSelector.setSelectedIndex(2);
        }

        visibleCheckBox.setSelected(current.visibleGraph);

        // update column selector when a new dataset is selected
        // since the column selector has a listener, it will automatically fire which will cause the correct number of
        // columns to be displayed
        columnSelector.setValue(current.getNumberOfDisplayedColumns());

        // update the layout which will trigger a repaint
        updateLayout();

        temporarilyDisableQuickMenuListener--;
    }

    /** Makes it so that the number of columns in the current DataSet is equal to the given number
     * 
     * @param desiredColumns The desired number of DataColumns to have in the currently selected DataSet */
    private void setNumberOfVisibleColumns(int desiredColumns)
    {
        rightSideBar.removeAll();
        DataSet current = (DataSet) dataSetSelector.getSelectedItem();
        rightSideBar.add(current);
        rightSideBar.add(fitDataPanel);

        current.setNumberOfDisplayedColumns(desiredColumns);

        updateDataSetTableWidth();
    }

    private void updateDataSetTableWidth()
    {
        dataSetTableWidth = ((DataSet) dataSetSelector.getSelectedItem()).getNumberOfDisplayedColumns() *
                DATA_COLUMN_WIDTH;
        updateLayout();
    }

    /** Updates the combo box containing the fit types for the DataSet being displayed */
    public void updateFitTypes()
    {
        // get the currently selected dataset
        DataSet current = (DataSet) dataSetSelector.getSelectedItem();

        FitType currentFit = current.getFitType();
        boolean changedFitType = fitSelector.getSelectedItem() == currentFit;

        if (!changedFitType)
        {
            // Disable the action that happens when we set the selected item because all it does is set the fit type but
            // we are setting the selected based on what is already set so its pointless
            temporarilyDisableQuickMenuListener++;
        }

        // get the currently selected type of fit
        // clear and re-add the items
        ArrayList<FitType> dataSetFits = current.getAllowableFits();
        fitSelector.removeAllItems();
        for (FitType fit : dataSetFits)
        {
            fitSelector.addItem(fit);
        }
        fitSelector.setSelectedItem(currentFit);

        // don't forget to re-enable the quickbar actions if needed!
        if (!changedFitType)
        {
            temporarilyDisableQuickMenuListener--;
        }
    }

    /** Shows the about frame - called by the action listener */
    private void showAboutBox()
    {
        JOptionPane.showMessageDialog(this, About.getAboutString(), "About LineFit", JOptionPane.PLAIN_MESSAGE);
    }

    /** Reads in the options associated with exporting in from the LineFit data file
     * 
     * @param line The line to attempt to read a setting from
     * 
     * @return True if an export option was found in the passed line and False if the line did not contain an export
     *         option */
    public boolean readInOption(String line)
    {
        return graphingArea.readInOption(line);
    }

    /** Reads in data or an option related to the data from the passed in line
     * 
     * @param line The line that contains the data or option related to the data
     * @param newDataSet Signals that the line passed in is the beginning of a new data set
     * @return Returns true if the data or option for the data was read in from the line */
    public boolean readInDataAndDataOptions(String line, boolean newDataSet)
    {
        if (newDataSet && graphingArea.hasData())
        {
            createNewDataSet();
        }
        return graphingArea.readInDataAndDataOptions(line, newDataSet);
    }

    /** Performs any processing needed after all the data has been read in */
    public void finishedReadingInData()
    {
        graphingArea.finishedReadingInData();
        updateDataSetDisplayed();
    }

    /** Refreshes/redraws the graph. This should be called when a change is made that will impact what is shown on the
     * graph */
    public void refreshGraph()
    {
        // this just makes it so that it updates our quickbar
        dataSetSelector.setSelectedIndex(dataSetSelector.getSelectedIndex());
        graphingArea.repaint();
    }

    /** Adds the names of the options as saved in the LineFit file and the values associated with them to the respective
     * passed ArrayLists
     * 
     * @param variableNames The ArrayList of the names of the options
     * @param variableValues The ArrayList of the values of the options (indexed matched to the names) */
    public void retrieveAllOptions(ArrayList<String> variableNames, ArrayList<String> variableValues)
    {
        // do a recursive save so we don't have to access graph area - it can take care of itself
        graphingArea.retrieveAllOptions(variableNames, variableValues);
    }

    /** Retrieve all the data and options associated with the data in the passed in array lists
     * 
     * @param variableNames The ArrayList of the names of the options
     * @param variableValues The ArrayList of the values of the options (indexed matched to the names) */
    public void retrieveAllDataAndDataOptions(ArrayList<String> variableNames, ArrayList<String> variableValues)
    {
        // do a recursive save so we don't have to access graph area - it can take care of itself
        graphingArea.retrieveAllDataAndDataOptions(variableNames, variableValues);
    }

    /** Centers the LineFit window on the screen */
    void centerOnScreen()
    {
        Toolkit tk = getToolkit();
        Dimension size = tk.getScreenSize();
        setLocation(size.width / 2 - getWidth() / 2, size.height / 2 - getHeight() / 2);
    }

    /** Centers the passed frame on this window
     * 
     * @param frameToCenter The frame to center on this window */
    private void centerOnThis(JFrame frameToCenter)
    {
        Dimension size = this.getSize();
        Point pos = this.getLocation();
        frameToCenter.setLocation(size.width / 2 - frameToCenter.getWidth() / 2 + pos.x, size.height / 2 - frameToCenter
                .getHeight() / 2 + pos.y);
    }

    /** Returns the current GraphArea's graph name
     * 
     * @return The graph name of this LineFit's GraphArea */
    public String getGraphName()
    {
        return graphingArea.getGraphName();
    }

    // Private Classes
    /** A Listener class that is used for the buttons in the "File" drop down menu from the menu bar at the top of the
     * LineFit window
     * 
     * @author Keith Rice
     * @version 1.0
     * @since 0.99.0 */
    private class FileMenuBarDropDownButtonsListener implements ActionListener
    {
        /** Handles the action and determines which of the menu buttons was clicked and then acts accordingly */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            switch (e.getActionCommand())
            {
                case menuTitles_NewWindow:
                    ioHandler.newLineFitInstance();
                    break;
                case menuTitles_OpenFileNewWindow:
                    ioHandler.newLineFitInstancePromptForFile();
                    break;
                case menuTitles_OpenFile:
                    ioHandler.fileIO.chooseAndOpenLineFitFile(true);
                    break;
                case menuTitles_SaveFile:
                    ioHandler.fileIO.saveLineFitFile();
                    break;
                case menuTitles_NewDataSet:
                    createNewDataSet();
                    break;
                case menuTitles_GraphOptions:
                    DataSet current = (DataSet) dataSetSelector.getSelectedItem();
                    new GraphOptionsMenu(graphingArea, current, ioHandler);
                    break;
                case menuTitles_ExportJPG:
                    ioHandler.exportIO.exportJPG();
                    break;
                case menuTitles_ExportPDF:
                    ioHandler.exportIO.exportPDF();
                    break;
                case menuTitles_ExportTex:
                    ioHandler.exportIO.exportLaTex();
                    break;
                case menuTitles_Exit:
                    ioHandler.closeApplication();
                    break;
            }
        }
    }

    /** A Listener class that is used for the buttons in the "Help" drop down menu from the menu bar at the top of the
     * LineFit window
     * 
     * @author Keith Rice
     * @version 1.0
     * @since 0.99.0 */
    private class HelpMenuBarDropDownButtonsListener implements ActionListener
    {
        /** Handles the action and determines which of the menu buttons was clicked and then acts accordingly */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            switch (e.getActionCommand())
            {
                case menuTitles_LineFitHelp:
                    ioHandler.showPDFHelpFile();
                    break;
                case menuTitles_AboutLineFit:
                    showAboutBox();
                    break;
                case menuTitles_CheckForUpdates:
                    ioHandler.isUpdateAvailable(true);
                    break;
            }
        }
    }

    /** A Listener class which handles the functionality of all the components on the Quick bar, which is below the Menu
     * Bar and above the GraphArea and the DataSet Table, with the exception of the number of columns selector buttons
     * 
     * @author Keith Rice
     * @version 2.0
     * @since &lt;0.98.0 */
    private class QuickBarListener implements ActionListener
    {
        @Override
        /** Determines which component of the QuickBar was clicked and then behaves accordingly */
        public void actionPerformed(ActionEvent e)
        {
            if (temporarilyDisableQuickMenuListener <= 0)
            {
                if (e.getActionCommand().equals("Visible"))
                {
                    DataSet current = (DataSet) dataSetSelector.getSelectedItem();
                    ioHandler.changeTracker.setFileModified();
                    current.visibleGraph = !current.visibleGraph;
                    graphingArea.repaint();
                }
                else if (e.getSource() == dataSetSelector)
                {
                    DataSet current = (DataSet) dataSetSelector.getSelectedItem();
                    if (current == null)
                    {
                        return;
                    }
                    if (current.getName().equals("New DataSet"))
                    {
                        // create the new dataset which will also select it. We still need to refresh the GUI here
                        // though because the listener is disabled while it is being executed to help avoid endless
                        // loops
                        current = createNewDataSet();
                    }

                    // refresh all the needed components for this dataset
                    updateDataSetDisplayed();
                }
                else if (e.getSource() == colorSelector)
                {
                    // Event handler for colorSelector
                    DataSet current = (DataSet) dataSetSelector.getSelectedItem();
                    Color color = (Color) colorSelector.getSelectedItem();

                    // if its the reserved color then do some special things
                    if (color == ColorBoxRenderer.RESERVED_FOR_CUSTOM_COLOR)
                    {
                        if (customColorMenu == null)
                        {
                            customColorMenu = new CustomColorMenu(onUpdateColorAction);
                        }

                        customColorMenu.setDataSetAndFocus(current);

                        // we repaint when we close the custom color window and not now
                    }
                    // only set the color if we are not using the reserved color value
                    else
                    {
                        current.setColor(color);
                        colorSelectorRenderer.setCustomColor(current.getLastCustomColor());

                        // repaint the graph area
                        graphingArea.repaint();
                    }
                }
                else if (e.getSource() == shapeSelector)
                {
                    DataSet current = (DataSet) dataSetSelector.getSelectedItem();
                    Shape shape = (Shape) shapeSelector.getSelectedItem();
                    current.setShape(shape);

                    graphingArea.repaint();

                }
                else if (e.getSource() == fitSelector)
                {
                    // Event handler for fitSelector
                    DataSet current = (DataSet) dataSetSelector.getSelectedItem();
                    FitType fit = (FitType) fitSelector.getSelectedItem();

                    if (fit != null)
                    {
                        // Set the fit type and repaint the graph
                        current.setFitType(fit);
                        graphingArea.repaint();
                    }
                }
                else if (e.getSource() == graphOptionsButton)
                {
                    DataSet current = (DataSet) dataSetSelector.getSelectedItem();
                    centerOnThis(new GraphOptionsMenu(graphingArea, current, ioHandler));
                }
            }
        }
    }

    /** A Listener class that handles when the user changes the number of DataColumns in the current DataSet
     * 
     * @author Unknown
     * @version 1.0
     * @since &lt;0.98.0 */
    private class changeNumberOfColumnsListener implements ChangeListener
    {
        /** The event that is called when the Spinner is changed to a different number which handles adding and removing
         * DataColumns from the DataSet */
        public void stateChanged(ChangeEvent e)
        {
            JSpinner mySpinner = (JSpinner) (e.getSource());
            SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
            setNumberOfVisibleColumns((Integer) myModel.getValue());
        }
    }

    /** A Runnable class that handles the actions that should be performed when the color for the displayed DataSet is
     * updated
     * 
     * @author Keith Rice
     * @version 1.0
     * @since 0.99.0 */
    private class updateDataSetColorAction implements Runnable
    {
        /** The action that is performed when the color is updated for the displayed DataSet */
        @Override
        public void run()
        {
            DataSet selected = (DataSet) dataSetSelector.getSelectedItem();
            Color currentColor = selected.getColor();

            colorSelectorRenderer.setCustomColor(selected.getLastCustomColor());

            // Disable the quickbar so it doesn't trigger the listener action which would reset the starting color
            temporarilyDisableQuickMenuListener++;
            if (!DataSet.isColorACustomColor(currentColor))
            {
                colorSelector.setSelectedItem(currentColor);
            }
            else
            {
                colorSelector.setSelectedItem(ColorBoxRenderer.RESERVED_FOR_CUSTOM_COLOR);
            }
            temporarilyDisableQuickMenuListener--;

            colorSelector.repaint();
            graphingArea.repaint();
        }
    }

    /** A Runnable class that handles the actions that should be performed when the allowable fit types for the
     * displayed DataSet are updated
     * 
     * @author Keith Rice
     * @version 1.0
     * @since 0.99.0 */
    private class updateDataSetAction implements Runnable
    {
        /** The action that is performed when the fit types are updated for the displayed DataSet */
        @Override
        public void run()
        {
            updateFitTypes();
            updateDataSetTableWidth(); // Causes a repaint
        }
    }
}