package linefit.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Formatter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import linefit.LineFit;
import linefit.Version;
import linefit.IO.CloseDialogException;

/**
 * This class Handles all of the IO functionality of LineFit. This is a static class and keeps track of the export and save variables
 * such as the export size of a PDF file. No functionality that reads directly with IO should be handled elsewhere and any IO errors
 * should be thrown up to this class to handle. The saving and exporting done below this should only be on output Strings or Graphics2D
 * and not directly dealing with the file, only formatting and creating what will be outputted to the file here. The Opening of files
 * has to deal with the reader, but should throw errors.
 * 
 * @author	Keith Rice
 * @version	1.0
 * @since 	1.0
 */
public class DataFileIO 
{
	/** The LineFit instance that this IOHandler is linked to */
	private static LineFit lineFit;
	
	//Used for saving and opening files
	/** The returned value from the dialog box that asks the user if they want to save before exiting LineFit
	 * if there are unsaved changed (The dirtyBit is dirty/true) */
	private static int saveBeforeClosingDialogChoice;
	/** the returned value from the dialog box that allows the user to select a file to open or import */
	private static int lastSelectedFileDialogChoice;
	/** The JFileChooser that allows the user to select files to open or import with a GUI */
	private static JFileChooser fileChooser;
	
	/**
	 * Opens up a LineFit file in this instance of LineFit after allowing the user to choose which file to open
	 * @param offerChoiceToNotReadInGraphSettings Whether or not the user can choose not to import the graph settings. This should only be false when opening a file on start up
	 */
	static void chooseAndOpenLineFitFile(boolean offerChoiceToNotReadInGraphSettings)
	{
		File file = promptUserForLineFitFileToOpen();
		
		//Makes sure the user selected something
		if(file != null)
		{
			openLineFitFile(file, offerChoiceToNotReadInGraphSettings);
		}
	}

	/** Starts a new instance of LineFit and loads the data from the file into it
	 * @throws NumberFormatException Throws this exception if it expected to find a number while parsing the file and found something else
	 */
	static void openLineFitFileInNewInstance() throws NumberFormatException 
	{
		try 
		{
			File file = promptUserForLineFitFileToOpen();
			
			//make sure we got a file
			if(file != null)
			{
				// Run a java app in a separate system process
				String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
					
				try 
				{
					File currProgram = new File(LineFit.class.getProtectionDomain().getCodeSource().getLocation().toURI());
					if(currProgram.getName().endsWith(".jar"))
					{
						ProcessBuilder newProgram = new ProcessBuilder(javaBin, "-jar", currProgram.getPath(), file.getAbsolutePath());
						newProgram.start();
					}
				} 
				catch (URISyntaxException e) 
				{
					JOptionPane.showMessageDialog(lineFit, "Internal error creating new intance of linefit : Process aborted",
						    "URI Error", JOptionPane.ERROR_MESSAGE);
				}		
			}
		} 
		catch (IOException exception) 
		{
			JOptionPane.showMessageDialog(lineFit, "An error occured while opening the file : Process aborted",
				    "IO Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Opens the lineFit file at the specified path
	 * @param filePath the path to open the file up at
	 * @param offerChoiceToNotReadInGraphSettings Whether or not the user can choose not to import the graph settings. This should only be false when opening a file on start up
	 */
	static void openLineFitFileAtPath(String filePath, boolean offerChoiceToNotReadInGraphSettings)
	{
		File fileToOpen = new File(filePath);
		openLineFitFile(fileToOpen, offerChoiceToNotReadInGraphSettings);
	}

	/** Opens the given LineFit file and prompts the user if they want to read in the graph settings if the flag is set to
	 * 
	 * @param fileToOpen The file to open containing the LineFit graph data
	 * @param offerChoiceToNotReadInGraphSettings Whether of not the user is prompted id they want to read in the graph settings
	 * as well as the datasets. True means that the user is prompted
	 */
	static void openLineFitFile(File fileToOpen, boolean offerChoiceToNotReadInGraphSettings)
	{
		//make sure the file is actually there
		if(fileToOpen != null)
		{
			//try to read in the file
			try	
			{
				//Scanner input = new Scanner(file);
				BufferedReader inputReader = new BufferedReader(new FileReader(fileToOpen));
				
				//see if we need to read in the graph settings too
				boolean importSettings = false;
				if(offerChoiceToNotReadInGraphSettings)
				{
					int importSettingsRes = JOptionPane.showOptionDialog(lineFit, "Import the graph settings as well?", 
								"Import", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Yes","No"}, "No");
						
					if (importSettingsRes == 0)
					{
						importSettings = true;
					}
				}
				else
				{
					importSettings = true;
				}
				try
				{
					//first see what version number it is if it has one - save our spot first
					inputReader.mark(100);
					String versionLine = inputReader.readLine();
					if(versionLine != null)
					{
						if(versionLine.toLowerCase().startsWith("fileformatversion"))
						{
							//read in the version number
							try
							{
								//do some bounds checking here and handle appropriately
								String[] versionParts = versionLine.substring(versionLine.indexOf(' ')).split(".");
								int majorVersion = Integer.parseInt(versionParts[0]);
								int minorVersion = Integer.parseInt(versionParts[1]);
								if(majorVersion > Version.LINEFIT_FILE_FORMAT_MAJOR_VERSION)
								{
									//ask them if they want to still try reading in the file
									int confirm = JOptionPane.showConfirmDialog(lineFit, "This File was created with a newer LineFit file format protocol. Because of this, data could be missed or read in incorrectly. Continue loading data from it?", "Time Traveling File", JOptionPane.OK_CANCEL_OPTION);
									if (confirm != JOptionPane.OK_OPTION)
									{ 
										//if they dont than close us of and return
										inputReader.close();
										return;
									}
								}
							}
							catch (NumberFormatException nfe)
							{
								JOptionPane.showMessageDialog(lineFit, "Error determining file format version number. Continuing but Data may not be complete",
									    "IO Error", JOptionPane.ERROR_MESSAGE);
							}
						}
						else
						{
							//old version - we can just keep going cause we are backwards compatable						
							//reset the line so we dont miss it
							inputReader.reset();
						}
						
						//now keep reading in the file
						lineFit.initiateRecursiveOpen(inputReader, importSettings);
					}	
					inputReader.close();
				}
				catch (IOException ioe)
				{
					JOptionPane.showMessageDialog(lineFit, "IO Error occured during read",
						    "IO Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(lineFit, "Could not find the file: Process aborted",
					    "FileNotFound Error", JOptionPane.ERROR_MESSAGE);
			}
			
			System.out.println("Done Opening File");
		}
	}

	/** Pops up the frame that allows us to select a file for loading and importing
	 * @return The File the user has selected or null if there was a problem or canceled out of selecting
	 */
	static File promptUserForLineFitFileToOpen() 
	{
		fileChooser = new JFileChooser(GeneralIO.getMostRecentDirectory());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		lastSelectedFileDialogChoice = fileChooser.showDialog(lineFit, "Open");
		if(lastSelectedFileDialogChoice == JOptionPane.OK_OPTION)
		{
			File fileToOpen = fileChooser.getSelectedFile();
			GeneralIO.storeCurrentDirectory();
			if(fileToOpen != null && !fileToOpen.getName().endsWith(".txt"))
			{
				int confirm = JOptionPane.showConfirmDialog(lineFit, "File is not a recognized lineFit .txt file. Continue opening?", "Unsupported File Type", JOptionPane.OK_CANCEL_OPTION);
				if (confirm == JOptionPane.OK_OPTION) 
				{ 
					return fileToOpen;
				}
			}
			else if (fileToOpen == null || lastSelectedFileDialogChoice == JFileChooser.CANCEL_OPTION)
			{
				System.err.println("Cancelled");
			} 
			else
			{
				return fileToOpen;
			}
		}
		return null;
	}
	
	/** Opens a blank instance of LineFit in a new separate window
	 * Note: This will only work if it is running from a JAR file */
	static void newLineFitFile()
	{
		// Run a java app in a separate system process
		String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

		try 
		{
			File currProgram = new File(LineFit.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if(currProgram.getName().endsWith(".jar"))
			{
				ProcessBuilder newProgram = new ProcessBuilder(javaBin, "-jar", currProgram.getPath());
				newProgram.start();
			}
		} 
		catch (URISyntaxException e) 
		{
			JOptionPane.showMessageDialog(lineFit, "Internal error creating new intance of linefit : Process aborted",
				    "URI Error", JOptionPane.ERROR_MESSAGE);
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(lineFit, "An error occured while creating a new file : Process aborted",
				    "IO Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** Shows the save file dialog that allows us to specify where to save our data and what to call it
	 * @param extensionToPutOnFile The file extension to save the file with
	 * @return The File that this LineFit's data will be saved to
	 * @throws CloseDialogException throws this error if the save dialog was closed out of
	 */
	private static File showSaveFileDialog(String extensionToPutOnFile) throws CloseDialogException 
	{
		File fileToOpen; 
		fileChooser = new JFileChooser(GeneralIO.getMostRecentDirectory());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		int overwriteCheck = fileChooser.showSaveDialog(lineFit);
		
		//if they hit the save button
		if ( overwriteCheck == JFileChooser.APPROVE_OPTION ) 
		{ 
			fileToOpen = fileChooser.getSelectedFile();
			
			//make sure it has the right extension
			if ( !fileToOpen.exists() ) 
			{ 
				fileToOpen = forceExtension(fileToOpen, extensionToPutOnFile);
				GeneralIO.storeCurrentDirectory();
				return fileToOpen;
			}
			//make sure they want to overwrite the existing file
			else 
			{ 
				int confirm = JOptionPane.showConfirmDialog(lineFit, "Overwrite existing file " + fileToOpen + "?"); 
				
				if ( confirm == JOptionPane.OK_OPTION ) 
				{ 
					fileToOpen = forceExtension(fileToOpen, extensionToPutOnFile);
					GeneralIO.storeCurrentDirectory();
					return fileToOpen;
				}
				else 
				{
					throw new CloseDialogException();
				}
			}
			
		}
		else if(overwriteCheck == JFileChooser.CANCEL_OPTION) 
		{ //thow this error if we cancel it so that we can catch it and do nothing outside
			throw new CloseDialogException(); //make a exception or find one that works
		}
		
		// something went wrong
		return null; 
	}

	/** Makes sure the filename ends with the given extension and if it does not it adds the extension to the end of the file
	 * @param fileToForceExtensionOn The File to force the extension on
	 * @param extensionToPutOnFile The extension to force onto the File
	 * @return The File with the extension forced on it
	 */
	static File forceExtension(File fileToForceExtensionOn, String extensionToPutOnFile) 
	{
		if (fileToForceExtensionOn.getName().endsWith(extensionToPutOnFile)) 
		{
			return fileToForceExtensionOn;
		} 
		else 
		{
			String newFileName = fileToForceExtensionOn.getPath().concat(extensionToPutOnFile);
			return new File(newFileName);
		}
	}
	
	/** Asks the user if they want to save before the quit the LineFit if there are unsaved changes */
	private static void confirmQuitWithoutSave()
	{
		String[] dialogOptions = { "Save", "Don't Save", "Cancel" };
		saveBeforeClosingDialogChoice = JOptionPane.showOptionDialog(lineFit, "Do you want to save the changes you made to the graph \"" + lineFit.getGraphName() + "\"?\nYour changes will be lost if you don\'t save.", "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, dialogOptions, dialogOptions[0]);
		
		switch (saveBeforeClosingDialogChoice) 
		{
			case JOptionPane.YES_OPTION: 
			{
				saveLineFitFile();
				System.out.println("Now Saving!");
				break;
			}
			case JOptionPane.NO_OPTION: 
			{
				DirtyBit.setClean();
				GeneralIO.closeApplication();
				System.out.println("Quitting without saving!");
				break;
			}
			case JOptionPane.CANCEL_OPTION: 
			{
				System.out.println("Cancelling!");
				break; 
			}
			case JOptionPane.CLOSED_OPTION:
			{
				System.out.println("User closed the dialog box!");
				break;
			}
			default: 
			{
				System.out.println("confirmQuitWithoutSave: Unexpected exception");
			}
		}
	}
	
	/** Saves the LineFit file */
	public static void saveLineFitFile()
	{
		File outputFile = GeneralIO.promptUserToSelectFileForSaving(".txt");
		if(outputFile != null)
		{
			try 
			{
				Formatter output = new Formatter(outputFile);

				//save the current linefit file version number
				output.format("%s %s%s", "FileFormatVersion", Version.LINEFIT_FILE_FORMAT_VERSION , System.getProperty("line.separator"));
				
				//get and save all the settings data
				ArrayList<String> settingVarNames = new ArrayList<String>();
				ArrayList<String> settingVarValues = new ArrayList<String>();
				lineFit.retrieveAllSettingsVariables(settingVarNames, settingVarValues);
				for(int i = 0; i < settingVarNames.size(); i++)
				{
					output.format("# %s %s%s", settingVarNames.get(i), settingVarValues.get(i), System.getProperty("line.separator"));
				}
				
				//get and save all the datasets variables
				ArrayList<String> dataSetsVarNames = new ArrayList<String>();
				ArrayList<String> dataSetsVarValues = new ArrayList<String>();
				lineFit.retrieveAllDataSetVariables(dataSetsVarNames, dataSetsVarValues);
				for(int i = 0; i < dataSetsVarNames.size(); i++)
				{
					if(dataSetsVarValues.get(i).equals("newDataSetDefinition"))
					{
						output.format("# %s%s", dataSetsVarNames.get(i), dataSetsVarValues.get(i), System.getProperty("line.separator"));
					}
					else
					{
						output.format("~ %s %s%s", dataSetsVarNames.get(i), dataSetsVarValues.get(i), System.getProperty("line.separator"));
					}
				}
				
				
				//close our formatter
				output.close();
				
				// We have now saved our file! The DirtyBit should be clean!
				DirtyBit.setClean();
		
				//Make sure our file is not empty and if it is warn the user that it might not have saved correctly and to check it
				if(outputFile.length() > 10) 
				{ //just so we have a bit of a buffer in case it has some empty characters. the smallest size for a full one is around 800 bytes
					JOptionPane.showMessageDialog(lineFit, "File Successfully Saved", "File Saved", JOptionPane.INFORMATION_MESSAGE);
				} 
				else 
				{
					JOptionPane.showMessageDialog(lineFit, "File may not have been saved correctly - Make sure the " +
							"file contains data and if it does not try:\n\n\tSaving the file again and if it does not work then try\n\t" +
							"Copying and pasting the data into a new LineFit and saving it",
							"Problem Saving LineFit File", JOptionPane.WARNING_MESSAGE);
				}
			} 
			catch (FileNotFoundException e)
			{
				// this message is redundant 
				JOptionPane.showMessageDialog(lineFit, "Specified file was not found : Process aborted", "FNF Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} 
		}
	}

	/** Sets the LineFit object that this IO Handler is attached to 
	 * @param associatedWith The instance of LineFit to associate the static IOHandler class with */
	static void assocaiteWithLineFit(LineFit associatedWith)
	{
		lineFit = associatedWith;
	}
}
