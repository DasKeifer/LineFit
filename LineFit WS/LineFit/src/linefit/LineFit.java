/*
Copyright (C) 2013  Covenant College Physics Department

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General License for more details.

You should have received a copy of the GNU Affero General License
along with this program.  If not, see http://www.gnu.org/licenses/.
*/
package linefit;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import linefit.FitAlgorithms.FitType;
import linefit.FitAlgorithms.LinearFitFactory;
import linefit.IO.*;

/**
 * The main operating class in LineFit that is called on start up and sets up the base and layout and 
 * that handles all the functions calls and methods of the program
 * 
 * @author	Unknown
 * @version	1.1.0
 * @since 	&lt;0.98.0
 */
public class LineFit extends JFrame 
{
	/** Main - This is the method that starts up the instance of LineFit. If a File path is inputed in the args,
	 * it will try to load the file at the specified location on startup
	 * @param args Either empty of the file path of the File to load on start up
	 */
	public static void main(String args[]) 
	{
		if(args.length == 0) 
		{
			new LineFit();
		} 
		else if(args.length == 1) 
		{
			new LineFit(args[0]);
		}
	}
	
	/** The Serial Version UID so that we know what version it is when we are using it.
	 * See http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html for full 
	 * discussion on its uses and purpose */
	private final static long serialVersionUID = 42L;
	
	//Variables for the visible components of LineFit
	/** The GraphArea of this instance of LineFit in which the Graph is drawn */
	private GraphArea graphingArea;
	/** The DataSet that is currently selected to be edited or viewed */
	private DataSet currentDataSet;
	/** The Panel that contains all the core pieces of LineFit */
	private JPanel mainDisplayPanel;
	/** The panel along the right side of the LineFit window that contains the current {@link DataSet} information
	 * as well as the linear fit's results if the current DataSet has a selected fit */
	private JPanel rightSideBar;
	/** The Panel that contains all the often used options, navigation and editing features such as selecting or adding
	 * new DataSets */
	private JPanel quickBar;
	/** The Panel that is part of the right side bar that contains the currently selected DataSet's linear fit results 
	 * if a fit is selected */
	private JPanel fitDataPanel;
	/** The Layout that lets us position buttons where we desire */
	private SpringLayout springLayout;
	
	//Sizing variables
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

	//Variables for the drop downs and options on the quick bar
	/** The drop down menu that contains the DataSets as well as the currently selected DataSet */
	private JComboBox<DataSet> dataSetSelector;
	/** The drop down box that allows the user to select the FitType of the current DataSet */
	private JComboBox<FitType> fitSelector;
	/** The drop down box that allows the user to select the Color of the current DataSet */
	private JComboBox<Color> colorSelector;
	/** The drop down box that allows the user to select the Shape of the current DataSet */
	private JComboBox<Shape> shapeSelector;
	/** The JSpinner that allows the user to set the number of DataColumns for the current DataSet */
	private JSpinner columnSelector;
	/** The JLabel for the number of DataColumns JSpinner */
	private JLabel columnLabel = new JLabel("Columns:");
	/** The JCheckBox that allows the user to make the currently selected DataSet hidden or visible on the Graph */
	private JCheckBox visibleCheckBox;
	/** The JButton on the quickBar that allows the user to open up the options for the GraphArea */
	private JButton graphOptionsButton;
	/** The JTextArea in which the linear fit's results are displayed to the user. This is a TextArea instead of a Label so that the user can copy the results */
	private JTextArea fitResultsArea;
	
	//Titles in the file drop down item on the menu bar
	/** The String to be displayed for the new file option in the  menu bar drop down */
	private static final String menuTitles_NewWindow = "New Window...           Ctrl+N";
	/** The String to be displayed for the import file option in the  menu bar drop down */
	private static final String menuTitles_OpenFile = "Open...         Ctrl+O";
	/** The String to be displayed for the open file option in the  menu bar drop down */
	private static final String menuTitles_OpenFileNewWindow = "Open in New Window...    Ctrl+Shift+O";
	/** The String to be displayed for the save file option in the  menu bar drop down */
	private static final String menuTitles_SaveFile = "Save...                     Ctrl+S";
	/** The String to be displayed for the export as a JPEG option in the  menu bar drop down */
	private static final String menuTitles_ExportJPG = "Export graph as JPEG...			Ctrl+J"; 
	/** The String to be displayed for the export as a PDF option in the  menu bar drop down */
	private static final String menuTitles_ExportPDF =  "Export graph as PDF...     Ctrl+P";
	/** The String to be displayed for the export as a LaTex file option in the  menu bar drop down */
	private static final String menuTitles_ExportTex =  "Export graph as LaTex...		Ctrl+L";
	/** The String to be displayed for the exit LineFit option in the  menu bar drop down */
	private static final String menuTitles_Exit = "Exit";
	/** The String to be displayed for the about LineFit option in the  menu bar drop down */
	private static final String menuTitles_AboutLineFit = "About LineFit...     F2";
	/** The String to be displayed for the LineFit help option in the  menu bar drop down */
	private static final String menuTitles_LineFitHelp = "LineFit Help...        F1";
	
	//Other variables
	private GeneralIO ioHandler;
	/** A lock that allows us to prevent the DataSets from updating so we can change things automatically with less 
	 * computational overhead */
	private boolean setLock = false;

	/** The default FitAlgorithm to use when creating linear fits for the DataSets */
	static LinearFitFactory currentFitAlgorithmFactory = LinearFitFactory.fitAlgorithmFactories[0];
	
	/** The default constructor for LineFit that creates a new instance with no data in it */
	private LineFit() 
	{
		//make our title and set our size
		super("LineFit");
		setSize(1000, 750);
		
		ioHandler = new GeneralIO(this, graphingArea);
		
		this.setIconImage(ioHandler.getLineFitIcon());
		
		//make the results area that looks like a label so it doesnt look out of place
		fitResultsArea = new JTextArea();
		fitResultsArea.setEditable(false);
		fitResultsArea.setBackground(null);

		//make our graph area		
		dataSetSelector = new JComboBox<DataSet>();
		graphingArea = new GraphArea(DEFAULT_X_AXIS_MINIMUM_VALUE, DEFAULT_X_AXIS_MAXIMUM_VALUE,
				DEFAULT_Y_AXIS_MINIMUM_VALUE, DEFAULT_Y_AXIS_MAXIMUM_VALUE, dataSetSelector,
				fitResultsArea, ioHandler.changeTracker);

		ioHandler.InitializeExportIO(graphingArea);
		
		//and then make an empty dataset to start
		currentDataSet = new DataSet(graphingArea, ioHandler.changeTracker);

		//create our panels and menu bars
		mainDisplayPanel = new JPanel();
		createMenuBar();
		createQuickBar();
		createRightSideBar();
		
		// Set up the layout of the entire program
		setupLayout();
		
		// Allows us to use key combinations (e.g. ctrl + s)
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyShortcutListener());

		// Change what we do when we are closed
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) 
			{
				ioHandler.closeApplication();
			}
		});
		
		//So it pops up on the center of the screen
		this.centerOnScreen();
	}
	
	/** Creates a new LineFit and then populates it with the data from the file at the specified file path
	 * @param filePathOfFileToLoad The file path of the file to load into the new instance of LineFit
	 */
	private LineFit(String filePathOfFileToLoad)
	{
		this(); //first create a blank one using the default constructor and then populate it
		ioHandler.fileIO.openLineFitFile(filePathOfFileToLoad, false);
	}
	
	/** Creates and initializes the right side bar for the LineFit display where the current DataSet and its linear fit results are displayed */
	private void createRightSideBar() 
	{
		rightSideBar = new JPanel(new GridLayout(2, 1));
		rightSideBar.setFocusable(true);
		rightSideBar.add(currentDataSet);
		
		fitDataPanel = new JPanel();
		TitledBorder tb = new TitledBorder("Fit Data");
		fitDataPanel.setBorder(tb);
		fitDataPanel.add(fitResultsArea);
		rightSideBar.add(fitDataPanel);

		// Add Default Number of Columns
		createDefaultNumberOfColumns();
	}
	
	/** Creates and initializes the quick bar at the top of the LineFit below the menu bar that allows 
	 * us to change GraphSets as well as the FitType, Color, etc.*/
	private void createQuickBar() 
	{
		graphOptionsButton = new JButton("Graph Options");
		 
		visibleCheckBox = new JCheckBox("Visible");
		visibleCheckBox.setSelected(true);
		
		// Set up the Column Selector (in TopBar)
		//  See
		// http://java.sun.com/docs/books/tutorial/uiswing/examples/components/SpinnerDemoProject/src/components/SpinnerDemo.java
		SpinnerNumberModel columnSelectorModel = new SpinnerNumberModel(DataSet.DEFAULT_NUMBER_OF_COLUMNS, 2, 4, 1);
		columnSelector = new JSpinner(columnSelectorModel);
		columnSelector.setToolTipText("Add or Subtract a Column");
		
		
		
		// DataSet selection ComboBox
		dataSetSelector.addItem(currentDataSet);
		dataSetSelector.setSelectedIndex(0);

		//create the new dataset option in the dataSet selector combo box 
		DataSet newDataSet = DataSet.createDropDownPlaceHolder("New DataSet");
		dataSetSelector.addItem(newDataSet);
		
		// Set up Type of linear fit selection ComboBox
		fitSelector = currentDataSet.fitTypeSelector;

		// Set up line color selection ComboBox
		colorSelector = new JComboBox<Color>();
		ColorBoxRenderer renderer = new ColorBoxRenderer();
		colorSelector.setRenderer(renderer);

		// Add possible colors to colorSelector
		colorSelector.addItem(Color.BLACK);
		colorSelector.addItem(Color.YELLOW);
		colorSelector.addItem(Color.BLUE);
		colorSelector.addItem(Color.GREEN);
		colorSelector.addItem(Color.ORANGE);
		colorSelector.addItem(Color.RED);
		colorSelector.addItem(ColorBoxRenderer.RESERVED_FOR_CUSTOM_COLOR);
		
		//set up the shape selector drop down
		shapeSelector = new JComboBox<Shape>();
		ShapeBoxRenderer renderer2 = new ShapeBoxRenderer();
		shapeSelector.setRenderer(renderer2);
		shapeSelector.addItem(new Rectangle2D.Double()); //square
		shapeSelector.addItem(new Ellipse2D.Double()); //circle
		shapeSelector.addItem(new Polygon()); //triangle
		
		setupQuickBar();
		
		//Add Action Listeners
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
		//JMenu optionsMenu = new JMenu("Options");
		JMenu helpMenu = new JMenu("Help");

		// Add Main Menus to main Menu bar
		menuBar.add(fileMenu);
		//menuBar.add(optionsMenu);
		menuBar.add(helpMenu);

		// Create items to be placed in "File" menu
		JMenuItem newWindowItem = new JMenuItem(menuTitles_NewWindow);
		JMenuItem openFileItem = new JMenuItem(menuTitles_OpenFile);
		JMenuItem openFileNewWindowItem = new JMenuItem(menuTitles_OpenFileNewWindow);
		JMenuItem saveFileItem = new JMenuItem(menuTitles_SaveFile);
		JMenuItem exportFileItem = new JMenuItem(menuTitles_ExportJPG);
		JMenuItem exportPDFItem = new JMenuItem(menuTitles_ExportPDF);
		JMenuItem exportTexItem = new JMenuItem(menuTitles_ExportTex);
		JMenuItem exitFileItem = new JMenuItem(menuTitles_Exit);

		//add them in the order we want them with the separators
		fileMenu.add(newWindowItem);
		fileMenu.add(openFileItem);
		fileMenu.add(openFileNewWindowItem);

		fileMenu.addSeparator();

		fileMenu.add(saveFileItem);
		//fileMenu.add(saveTexFileItem);

		fileMenu.addSeparator();

		fileMenu.add(exportFileItem); // JPG export option  //DNP temp
		fileMenu.add(exportPDFItem);
		fileMenu.add(exportTexItem);

		fileMenu.addSeparator();

		fileMenu.add(exitFileItem);
		
		// Create items to be placed in "Help" menu
		JMenuItem aboutLineFitItem = new JMenuItem(menuTitles_AboutLineFit);
		JMenuItem helpItem = new JMenuItem(menuTitles_LineFitHelp);

		// Add the above items to the "Help" menu
		helpMenu.add(helpItem);
		helpMenu.add(aboutLineFitItem);

		// Install the menu bar in the frame
		setJMenuBar(menuBar);
		
		// Begin Listeners for Menu Items

		// "File" menu item listeners
		ActionListener menuListener = new MenuBarDropDownButtonsListener();
		newWindowItem.addActionListener(menuListener);
		openFileNewWindowItem.addActionListener(menuListener);
		openFileItem.addActionListener(menuListener);
		saveFileItem.addActionListener(menuListener);
		//saveTexFileItem.addActionListener(this);

		exportFileItem.addActionListener(menuListener); // JPG export option
		exportPDFItem.addActionListener(menuListener);
		exportTexItem.addActionListener(menuListener);
		exitFileItem.addActionListener(menuListener);

		// "Options" menu item listeners
		//graphSettingsItem.addActionListener(menuListener);
		// columnOptionsItem.addActionListener(this);
		// newrowDataItem.addActionListener(this);
		//newdatasetDataItem.addActionListener(menuListener);

		// "Help" menu item listeners
		aboutLineFitItem.addActionListener(menuListener);
		helpItem.addActionListener(menuListener);
	}
	
	/** Creates an initializes the QuickBar which contains things such as the current DataSet selector and the FitType selector */
	private void setupQuickBar()
	{
		// Set up the top bar
		quickBar = new JPanel();
		springLayout = new SpringLayout();

		quickBar.setLayout(springLayout);
		
		// Constraints on columnSelector
		springLayout.putConstraint(SpringLayout.EAST, columnSelector, -8,
				SpringLayout.EAST, quickBar);
		springLayout.putConstraint(SpringLayout.WEST, columnSelector, -50,
				SpringLayout.EAST, quickBar);
		springLayout.putConstraint(SpringLayout.SOUTH, columnSelector, -4,
				SpringLayout.SOUTH, quickBar);
		springLayout.putConstraint(SpringLayout.NORTH, columnSelector, 4,
				SpringLayout.NORTH, quickBar);

		// Constraints on columnLabel
		springLayout.putConstraint(SpringLayout.EAST, columnLabel, -4,
				SpringLayout.WEST, columnSelector);
		springLayout.putConstraint(SpringLayout.WEST, columnLabel, -70,
				SpringLayout.WEST, columnSelector);
		springLayout.putConstraint(SpringLayout.SOUTH, columnLabel, -4,
				SpringLayout.SOUTH, quickBar);
		springLayout.putConstraint(SpringLayout.NORTH, columnLabel, 4,
				SpringLayout.NORTH, quickBar);

		// Constraints on setSelector
		springLayout.putConstraint(SpringLayout.EAST, dataSetSelector, -4,
				SpringLayout.WEST, columnLabel);
		springLayout.putConstraint(SpringLayout.WEST, dataSetSelector, -120,
				SpringLayout.WEST, columnLabel);
		springLayout.putConstraint(SpringLayout.SOUTH, dataSetSelector, -4,
				SpringLayout.SOUTH, quickBar);
		springLayout.putConstraint(SpringLayout.NORTH, dataSetSelector, 4,
				SpringLayout.NORTH, quickBar);

		// Constraints on colorSelector
		springLayout.putConstraint(SpringLayout.EAST, colorSelector, -4,
				SpringLayout.WEST, dataSetSelector);
		springLayout.putConstraint(SpringLayout.WEST, colorSelector, -80,
				SpringLayout.WEST, dataSetSelector);
		springLayout.putConstraint(SpringLayout.SOUTH, colorSelector, -4,
				SpringLayout.SOUTH, quickBar);
		springLayout.putConstraint(SpringLayout.NORTH, colorSelector, 4,
				SpringLayout.NORTH, quickBar);

		// Constraints on shapeSelector
		springLayout.putConstraint(SpringLayout.EAST, shapeSelector, -4,
				SpringLayout.WEST, colorSelector);
		springLayout.putConstraint(SpringLayout.WEST, shapeSelector, -50,
				SpringLayout.WEST, colorSelector);
		springLayout.putConstraint(SpringLayout.SOUTH, shapeSelector, -4,
				SpringLayout.SOUTH, quickBar);
		springLayout.putConstraint(SpringLayout.NORTH, shapeSelector, 4,
				SpringLayout.NORTH, quickBar);

		// Constraints on fitSelector
		springLayout.putConstraint(SpringLayout.EAST, fitSelector, -4,
				SpringLayout.WEST, shapeSelector);
		springLayout.putConstraint(SpringLayout.WEST, fitSelector, -120,
				SpringLayout.WEST, shapeSelector);
		springLayout.putConstraint(SpringLayout.SOUTH, fitSelector, -4,
				SpringLayout.SOUTH, quickBar);
		springLayout.putConstraint(SpringLayout.NORTH, fitSelector, 4,
				SpringLayout.NORTH, quickBar);

		// Constraints on visibleCheck
		springLayout.putConstraint(SpringLayout.EAST, visibleCheckBox, -4,
				SpringLayout.WEST, fitSelector);
		springLayout.putConstraint(SpringLayout.WEST, visibleCheckBox, -80,
				SpringLayout.WEST, fitSelector);
		springLayout.putConstraint(SpringLayout.SOUTH, visibleCheckBox, -4,
				SpringLayout.SOUTH, quickBar);
		springLayout.putConstraint(SpringLayout.NORTH, visibleCheckBox, 4,
				SpringLayout.NORTH, quickBar);
		
		// Constraints on Graph Options
		springLayout.putConstraint(SpringLayout.EAST, graphOptionsButton, 138,
				SpringLayout.WEST, quickBar);
		springLayout.putConstraint(SpringLayout.WEST, graphOptionsButton, 8,
				SpringLayout.WEST, quickBar);
		springLayout.putConstraint(SpringLayout.SOUTH, graphOptionsButton, -4,
				SpringLayout.SOUTH, quickBar);
		springLayout.putConstraint(SpringLayout.NORTH, graphOptionsButton, 4,
				SpringLayout.NORTH, quickBar);	
		
		// Add all components to TopBar
		quickBar.add(visibleCheckBox);
		quickBar.add(columnLabel);
		quickBar.add(columnSelector);
		quickBar.add(dataSetSelector);
		quickBar.add(shapeSelector);
		quickBar.add(colorSelector);
		quickBar.add(fitSelector);
		quickBar.add(graphOptionsButton);
	}
	
	/** Sets up the layout of LineFit and places each panel in the right spot */
	private void setupLayout() 
	{
		SpringLayout layout = new SpringLayout();

		mainDisplayPanel.removeAll();

		mainDisplayPanel.setLayout(layout);

		layout.putConstraint(SpringLayout.EAST, quickBar, 0, SpringLayout.EAST,
				mainDisplayPanel);
		layout.putConstraint(SpringLayout.WEST, quickBar, 0, SpringLayout.WEST,
				mainDisplayPanel);
		layout.putConstraint(SpringLayout.SOUTH, quickBar, QUICK_BAR_HEIGHT,
				SpringLayout.NORTH, mainDisplayPanel);
		layout.putConstraint(SpringLayout.NORTH, quickBar, 0, SpringLayout.NORTH,
				mainDisplayPanel);

		layout.putConstraint(SpringLayout.EAST, rightSideBar, 0, SpringLayout.EAST,
				mainDisplayPanel);
		layout.putConstraint(SpringLayout.WEST, rightSideBar, -dataSetTableWidth,
				SpringLayout.EAST, mainDisplayPanel);
		layout.putConstraint(SpringLayout.SOUTH, rightSideBar, 0,
				SpringLayout.SOUTH, mainDisplayPanel);
		layout.putConstraint(SpringLayout.NORTH, rightSideBar, QUICK_BAR_HEIGHT,
				SpringLayout.NORTH, mainDisplayPanel);

		layout.putConstraint(SpringLayout.EAST, graphingArea, 0, SpringLayout.WEST,
				rightSideBar);
		layout.putConstraint(SpringLayout.WEST, graphingArea, 0, SpringLayout.WEST,
				mainDisplayPanel);
		layout.putConstraint(SpringLayout.SOUTH, graphingArea, 0, SpringLayout.SOUTH,
				mainDisplayPanel);
		layout.putConstraint(SpringLayout.NORTH, graphingArea, QUICK_BAR_HEIGHT,
				SpringLayout.NORTH, mainDisplayPanel);

		mainDisplayPanel.add(quickBar);
		mainDisplayPanel.add(rightSideBar);
		mainDisplayPanel.add(graphingArea);

		this.add(mainDisplayPanel);
		this.setVisible(true);
	}
	
	/** Creates a new DataSet and makes it the currently selected DataSet */
	private void createNewDataSet() 
	{
		setLock = true;
		rightSideBar.removeAll();
		
		//create a new dataset but keep the new DataSet option at the end of the list
		DataSet current = new DataSet(graphingArea, ioHandler.changeTracker);
		graphingArea.registerDataSet(current);
		
		rightSideBar.add(current);
		rightSideBar.add(fitDataPanel);
		current.updateFits();
		//fitSelector.removeActionListener(this);
		fitSelector = current.fitTypeSelector;
		fitSelector.addActionListener(new QuickBarListener());
		setupQuickBar();
		dataSetTableWidth = current.visibleDataColumns.size() * DATA_COLUMN_WIDTH;
		
		Color currentColor = current.getColor();
		Shape currentShape = current.getShape();
		
		colorSelector.setSelectedItem(currentColor);
		shapeSelector.setSelectedItem(currentShape);
		
		visibleCheckBox.setSelected(current.visibleGraph);
		
		columnSelector.setValue(current.visibleDataColumns.size());

		// Add default number of columns to each data set
		createDefaultNumberOfColumns();

		updateCellFormattingInDataSetColumns(current);
		
		setupLayout();
		setLock = false;
	}
	
	/** Creates and displays the default number of columns for a DataSet */
	private void createDefaultNumberOfColumns() 
	{
		setNumberOfVisibleColumns(DataSet.DEFAULT_NUMBER_OF_COLUMNS);
	}

	/** Makes it so that the number of columns in the current DataSet is equal to the given number 
	 * @param desiredColumns The desired number of DataColumns to have in the currently selected DataSet */
	private void setNumberOfVisibleColumns(int desiredColumns)
	{
		rightSideBar.removeAll();
		DataSet current = (DataSet) dataSetSelector.getSelectedItem(); 
		rightSideBar.add(current);
		rightSideBar.add(fitDataPanel);
		current.changeNumVisibleColumns(desiredColumns);
		dataSetTableWidth = current.visibleDataColumns.size() * DATA_COLUMN_WIDTH;
		updateCellFormattingInDataSetColumns(current);
		setupLayout();
	}
	
	/** Updates the DataColumns to make sure they are displaying the correct values in their cells 
	 * @param dataSetToUpdateCellsOf The DataSet to update the DataColumns for
	 */
	private void updateCellFormattingInDataSetColumns(DataSet dataSetToUpdateCellsOf) 
	{ 
		dataSetToUpdateCellsOf.updateCellFormattingInColumns();
	}	
	
	/** Shows the about frame - called by the action listener */
	private void showAboutBox()
	{
		JOptionPane.showMessageDialog(this, About.getAboutString(), "About LineFit", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Opens the given LineFit file in this instance of LineFit using a recursive based opening scheme
	 * @param inputFileReader the BufferedReader that is being used to read in the LineFit file
	 * @param importSettings Whether or not to read in the graph settings/options along with the DataSets from the passed BufferedReader containing the input file's data
	 * @throws IOException throws any IO exceptions to be dealt with at a higher level
	 */
	public void readInLine(String line, boolean importSettings) 
	{
		graphingArea.readInLine(line, importSettings);
	}
	
	public void refreshGraph()
	{
		//this just makes it so that it updates our quickbar
		dataSetSelector.setSelectedIndex(dataSetSelector.getSelectedIndex()); 
		graphingArea.repaint();
	}
	
	/** Recursively saves the LineFit file to a text document by calling GraphArea's recursivelySave function which calls the DataSets' recursivelySave function 
	 * @param outputFormatter The Formatter that is being used to save the LineFit file
	 */
	public void retrieveAllSettingsVariables(ArrayList<String> variableNames, ArrayList<String> variableValues)
	{
		//do a recursive save so we dont have to access grapharea - it can take care of itself
		graphingArea.retrieveAllSettingsVariables(variableNames, variableValues);
	}
	
	public void retrieveAllDataSetVariables(ArrayList<String> variableNames, ArrayList<String> variableValues)
	{
		//do a recursive save so we dont have to access grapharea - it can take care of itself
		graphingArea.retrieveAllDataSetVariables(variableNames, variableValues);
	}

	/**
	 * Centers this instance of LineFit on the screen
	 */
	/** Centers the LineFit window on the screen */
	void centerOnScreen()
	{
		Toolkit tk = getToolkit();
		Dimension size = tk.getScreenSize();
		setLocation(size.width/2 - getWidth()/2, size.height/2 - getHeight()/2);
	}	
	
	/** Centers the passed frame on this window
	 * @param frameToCenter The frame to center on this window */
	private void centerOnThis(JFrame frameToCenter) 
	{
		Dimension size = this.getSize();
		Point pos = this.getLocation();
		frameToCenter.setLocation(size.width/2 - frameToCenter.getWidth()/2 + pos.x, size.height/2 - frameToCenter.getHeight()/2 + pos.y);
	}

	/** Returns the current GraphArea's graph name 
	 * @return The graph name of this LineFit's GraphArea 
	 */
	public String getGraphName() 
	{
		return graphingArea.getGraphName();
	}
	
	//Private Classes
	/** A Listener class that is used for the buttons in the drop down menus from the menu bar at the top of the LineFit window 
	 * @author	Keith Rice
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class MenuBarDropDownButtonsListener implements ActionListener 
	{
		/** Handles the action and determines which of the menu buttons was clicked and then acts accordingly */
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			//file drop down
			switch(e.getActionCommand())
			{
				case menuTitles_NewWindow: ioHandler.newLineFitInstance(); break;
				case menuTitles_OpenFileNewWindow: ioHandler.newLineFitInstancePromptForFile(); break;
				case menuTitles_OpenFile: ioHandler.fileIO.chooseAndOpenLineFitFile(true); break;
				case menuTitles_SaveFile: ioHandler.fileIO.saveLineFitFile(); break;
				case menuTitles_ExportJPG: ioHandler.exportIO.exportJPG(); break;
				case menuTitles_ExportPDF: ioHandler.exportIO.exportPDF(); break;
				case menuTitles_ExportTex: ioHandler.exportIO.exportLaTex(); break;
				case menuTitles_Exit: ioHandler.closeApplication(); break;
				case menuTitles_LineFitHelp: ioHandler.showPDFHelpFile();	break;	
				case menuTitles_AboutLineFit: case "About linefit.LineFit": case "About LineFit": showAboutBox(); break;
			}
		}
	}
	
	/** A Listener class which handles the functionality of all the components on the Quick bar, which is below the Menu Bar and 
	 * above the GraphArea and the DataSet Table, with the exception of the number of columns selector buttons
	 * @author	Unknown
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class QuickBarListener implements ActionListener 
	{
		@Override
		/** Determines which component of the QuickBar was clicked and then behaves accordingly */
		public void actionPerformed(ActionEvent e) 
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
				if (!setLock) 
				{
					DataSet current = (DataSet) dataSetSelector.getSelectedItem();
					if(current.getName().equals("New DataSet")) 
					{
						createNewDataSet();
						current = (DataSet) dataSetSelector.getSelectedItem();
					}
					
					Color currentColor = current.getColor();
					Shape currentShape = current.getShape();
					rightSideBar.removeAll();
					
					rightSideBar.add(current);
					rightSideBar.add(fitDataPanel);
					
					current.updateFits();
					
					//remove the listener from the old set and put it on the new one
					fitSelector.removeActionListener(this);
					fitSelector = current.fitTypeSelector;
					fitSelector.addActionListener(this);
					
					setupQuickBar();
					dataSetTableWidth = current.visibleDataColumns.size() * DATA_COLUMN_WIDTH;
					updateCellFormattingInDataSetColumns(current);

					colorSelector.setSelectedItem(currentColor);
					shapeSelector.setSelectedItem(currentShape); 
					//for whatever reason it does not like setSelectedItem to a Polygon so we have to do this so it gets the triangle shape drop down
					if(currentShape.getClass() == new Polygon().getClass()) 
					{
						shapeSelector.setSelectedIndex(2);
					}
					visibleCheckBox.setSelected(current.visibleGraph);
					

					//update column selector when a new dataset is selected
					columnSelector.setValue(current.visibleDataColumns.size());
					
					setupLayout();
					graphingArea.repaint();
				}
			} 
			else if (e.getSource() == colorSelector) 
			{
				// Event handler for colorSelector
				DataSet current = (DataSet) dataSetSelector.getSelectedItem();
				Color color = (Color) colorSelector.getSelectedItem();
				//if its the reserved color then do some special things
				if(color == ColorBoxRenderer.RESERVED_FOR_CUSTOM_COLOR)
				{
					//see if we already have a customColorMenu and if we dont then we need to center it on the frame
					boolean doNotCenterCustomColorMenu = ((DataSet)dataSetSelector.getSelectedItem()).doesHaveVisibleCustomColorMenu();
					
					//get the custom menu to the front or create it
					CustomColorMenu chooser = ((DataSet)dataSetSelector.getSelectedItem()).createOrFocusOnCustomColorMenu();
						
					//center it only if we just made it
					if(!doNotCenterCustomColorMenu)
					{
						centerOnThis(chooser);
					}
				}
				//only set the color if we are not using the reserved color value
				else
				{
					current.setColor(color);
				}
			} 
			else if (e.getSource() == shapeSelector) 
			{
				DataSet current = (DataSet) dataSetSelector.getSelectedItem();
				Shape shape = (Shape) shapeSelector.getSelectedItem();
				current.setShape(shape);

				graphingArea.repaint();

			} else if (e.getSource() == fitSelector) 
			{
				// Event handler for fitSelector
				DataSet current = (DataSet) dataSetSelector.getSelectedItem();

				if (!current.isFitLocked()) 
				{
					FitType fit = (FitType) fitSelector.getSelectedItem();
					
					if (fit != null) 
					{
						current.setFitType(fit);
						updateCellFormattingInDataSetColumns(current);
					}
				}
			} 
			else if (e.getSource() == graphOptionsButton) 
			{
				DataSet current = (DataSet) dataSetSelector.getSelectedItem();
				centerOnThis(new GraphOptionsMenu(graphingArea, current, ioHandler));
			}
		}
	}
	
	/** A Listener class that handles when the user changes the number of DataColumns in the current DataSet
	 * @author	Unknown
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class changeNumberOfColumnsListener implements ChangeListener 
	{
		/** The event that is called when the Spinner is changed to a different number which handles adding and removing
		 * DataColumns from the DataSet
		 */
		public void stateChanged(ChangeEvent e) 
		{
			JSpinner mySpinner = (JSpinner) (e.getSource());
			SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
			setNumberOfVisibleColumns((Integer) myModel.getValue());
		}
	}
	
	/** A Listener class that handles the key board key combination shortcuts such as F2 bringing up help 
	 * @author	Unknown
	 * @version	1.0
	 * @since 	&lt;0.98.0
	 */
	private class KeyShortcutListener implements KeyEventDispatcher
	{
		/** Allows the use keyboard shortcuts in LineFit */
		public boolean dispatchKeyEvent(KeyEvent e) 
		{
			if(e.getID() == KeyEvent.KEY_PRESSED) 
			{
				//these are the keys that activate only if the ctrl key is currently being pressed
				if(e.isControlDown())
				{
					switch(e.getKeyCode())
					{
						case KeyEvent.VK_S:	ioHandler.fileIO.saveLineFitFile(); break;
						case KeyEvent.VK_G:
								DataSet current = (DataSet) dataSetSelector.getSelectedItem();
								new GraphOptionsMenu(graphingArea, current, ioHandler);
								break; 
						case KeyEvent.VK_D: createNewDataSet(); break; 
						case KeyEvent.VK_N: ioHandler.newLineFitInstance(); break;
						case KeyEvent.VK_O: 
						{
							if(e.isShiftDown())
							{
								ioHandler.newLineFitInstancePromptForFile();								
							}
							else
							{
								ioHandler.fileIO.chooseAndOpenLineFitFile(true);
							}
						} break;
						case KeyEvent.VK_L: ioHandler.exportIO.exportLaTex(); break;
						case KeyEvent.VK_J: ioHandler.exportIO.exportJPG(); break;
						case KeyEvent.VK_P: ioHandler.exportIO.exportPDF(); break; 
						default: return false; //return false if we didnt do anything with it
					}
				}
				else
				{
					//this is for the f1-12 keys
					switch(e.getKeyCode())
					{
						case KeyEvent.VK_F1: ioHandler.showPDFHelpFile(); break;
						case KeyEvent.VK_F2: showAboutBox(); break;
						default: return false; // return false if we didnt do anything
					}  
				}
				return true;
			}
			return false;
		}
	}
}