package linefit.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import linefit.LineFit;
import linefit.Version;

/**
 * This class Handles all of the IO functionality of LineFit. This is a static class and keeps track of the export and save variables
 * such as the export size of a PDF file. No functionality that reads directly with IO should be handled elsewhere and any IO errors
 * should be thrown up to this class to handle. The saving and exporting done below this should only be on output Strings or Graphics2D
 * and not directly dealing with the file, only formatting and creating what will be outputted to the file here. The Opening of files
 * has to deal with the reader, but should throw errors.
 * 
 * @author	Das Keifer
 * @version	1.0
 * @since 	1.0
 */
public class DataFileIO 
{
	/** The LineFit instance that this IOHandler is linked to */
	private LineFit lineFit;
	private GeneralIO generalIO;
	
	//Used for saving and opening files
	/** the returned value from the dialog box that allows the user to select a file to open or import */
	private int lastSelectedFileDialogChoice;
	/** The JFileChooser that allows the user to select files to open or import with a GUI */
	private JFileChooser fileChooser;
	
	private static final String saveFileExtension = ".txt";


	DataFileIO(GeneralIO parentIO, LineFit lineFitToAssociateWith)
	{
		generalIO = parentIO;
		lineFit = lineFitToAssociateWith;
	}
	
	public File chooseLineFitFile()
	{
		fileChooser = new JFileChooser(generalIO.getMostRecentDirectory());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		lastSelectedFileDialogChoice = fileChooser.showDialog(lineFit, "Open");
		if(lastSelectedFileDialogChoice == JOptionPane.OK_OPTION)
		{
			File fileToOpen = fileChooser.getSelectedFile();
			generalIO.storeDirectoryOfChooser(fileChooser); //TODO: move into GeneralIO?
			if(fileToOpen != null && !fileToOpen.getName().endsWith(saveFileExtension))
			{
				int confirm = JOptionPane.showConfirmDialog(lineFit, "File is not a recognized lineFit " + saveFileExtension + " file. Continue opening?", "Unsupported File Type", JOptionPane.OK_CANCEL_OPTION);
				if (confirm == JOptionPane.OK_OPTION)
				{ 
					return fileToOpen;
				}
			}
			else if (fileToOpen == null || lastSelectedFileDialogChoice == JFileChooser.CANCEL_OPTION)
			{
				System.out.println("Cancelled Opening Unrecognized File");
			} 
			else
			{
				return fileToOpen;
			}
		}
		else
		{
			System.out.println("Cancelled Opening File");
		}
		return null;
	}
	
	/**
	 * Opens up a LineFit file in this instance of LineFit after allowing the user to choose which file to open
	 * @param offerChoiceToNotReadInGraphSettings Whether or not the user can choose not to import the graph settings. This should only be false when opening a file on start up
	 */
	public void chooseAndOpenLineFitFile(boolean offerChoiceToNotReadInGraphSettings)
	{
		File toOpen = chooseLineFitFile();
		if(toOpen != null)
		{
			openLineFitFile(toOpen, offerChoiceToNotReadInGraphSettings);
		}
	}
	
	/**
	 * Opens the lineFit file at the specified path
	 * @param filePath the path to open the file up at
	 * @param offerChoiceToNotReadInGraphSettings Whether or not the user can choose not to import the graph settings. This should only be false when opening a file on start up
	 */
	public void openLineFitFile(String filePath, boolean offerChoiceToNotReadInGraphSettings)
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
	public void openLineFitFile(File fileToOpen, boolean offerChoiceToNotReadInGraphSettings)
	{
		//make sure the file is actually there
		if(fileToOpen != null)
		{
			//try to read in the file
			try	
			{
				//Scanner input = new Scanner(file);
				BufferedReader inputReader = new BufferedReader(new FileReader(fileToOpen));
				
				//see if we need to read in the graph settings too - if we don't
				//offer a choice than that means its a new LineFit so we want to
				//read in the graph settings
				boolean importSettings = true;
				if(offerChoiceToNotReadInGraphSettings)
				{
					int importSettingsRes = JOptionPane.showOptionDialog(lineFit, "Import the graph settings as well?", 
								"Import", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Yes","No"}, "No");
						
					if (importSettingsRes != 0)
					{
						importSettings = false;
					}
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
								String[] versionParts = versionLine.substring(versionLine.indexOf(' ') + 1).split("\\.");
								int majorVersion = Integer.parseInt(versionParts[0].trim());
								int minorVersion = Integer.parseInt(versionParts[1].trim());
								if(majorVersion > Version.LINEFIT_FILE_FORMAT_MAJOR_VERSION || 
										(majorVersion == Version.LINEFIT_FILE_FORMAT_MAJOR_VERSION &&
										minorVersion > Version.LINEFIT_FILE_FORMAT_MINOR_VERSION))
								{
									//ask them if they want to still try reading in the file
									int confirm = JOptionPane.showConfirmDialog(lineFit, "This File was created with a newer LineFit file format protocol. Because of this, data could be missed or read in incorrectly. Continue loading data from it?", "Time Traveling File", JOptionPane.OK_CANCEL_OPTION);
									if (confirm != JOptionPane.OK_OPTION)
									{ 
										//if they don't than close us of and return
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
							//first version - we can just keep going cause we are backwards compatible						
							//reset the line so we don't miss it
							inputReader.reset();
						}
						
						//now keep reading in the file
				        String lineRead = "";
				        boolean readingDataSet = false;
				        boolean newDataSet = false;
				        while((lineRead = inputReader.readLine()) != null)
				        {
				        	//skip empty lines
				            if (lineRead.isEmpty())
				            {
				            	continue;
				            }
				            
				        	//trim any whitespaces
				        	lineRead = lineRead.trim();
				        	
				        	//If its a graph level line
				            if (lineRead.startsWith("#"))
				            {				  
				            	readingDataSet = false;
				            	
				            	//trim off the #
				            	lineRead = lineRead.substring(1).trim();
				            	
				            	//if it is signalling the start of a dataset then set our state
				            	//variables and continue - the next line will be the first line 
				            	//with actual dataset content - this line is just a switch
				            	if (lineRead.toLowerCase().startsWith("dataset"))
				            	{
				            		readingDataSet = true;
				            		newDataSet = true;
				            		continue;
				            	}
				            	//otherwise it is a "graph" setting and only import it if they
				            	//selected to read in the graph settings
				            	else if (importSettings)
				            	{
				            		//first see if it is an export parameter and if it wasn't check
				            		//if it was a graph setting
				            		boolean found = generalIO.exportIO.readInExportSetting(lineRead);
				            		
				            		//if it wasn't an export setting try loading it as a graph setting
				            		if (!found)
				            		{
				            			found = lineFit.readInGraphSetting(lineRead);
				            		}
				            		
				            		//if it wasn't either then print a warning and continue - it may 
				            		//just be an unsupported setting (either an old one or a future one)
				            		if (!found)
				            		{
				            			System.err.println("Non Fatal Error reading in Setting - Continuing: " + lineRead);
				            		}
				            	}
				            }
				            //if its a dataset level line and we are expecting dataset data
				            else if (lineRead.startsWith("~") && readingDataSet)
				            {
				            	//trim off the ~
				            	lineRead = lineRead.substring(1).trim();
				            	
				            	//if we didn't find it as valid setting give a warning an continue -
				            	//it may just be a currently unsupported setting
				            	if(!lineFit.readInDataSetLine(lineRead, newDataSet))
				            	{
				            		System.err.println("Error reading in DataSet - Continuing: " + lineRead);
				            	}
				            	//make sure it will only show up as new for the first successful parameter 
				            	//passed or else it will split the dataset. If we failed to read in the line
				            	//in the previous call it will not create the dataset so we need leave it set
				            	else if (newDataSet)
				            	{
				            		newDataSet = false;
				            	}
				            }
				            // we shouldn't ever get here - if we do it was an error
				            else
				            {
				            	System.err.println("Unexpected line start or dataset line");
				            }
				        }
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

	
	/** Saves the LineFit file */
	public void saveLineFitFile()
	{
		File outputFile = generalIO.promptUserToSelectFileForSaving(saveFileExtension);
		if(outputFile != null)
		{
			try 
			{
				Formatter output = new Formatter(outputFile);

				//save the current linefit file version number
				output.format("FileFormatVersion %s%s", Version.LINEFIT_FILE_FORMAT_VERSION , System.getProperty("line.separator"));
				
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
					if(dataSetsVarNames.get(i).equals("DataSet"))
					{
						output.format("%s# %s %s%s", System.getProperty("line.separator"), dataSetsVarNames.get(i), 
								dataSetsVarValues.get(i), System.getProperty("line.separator"));
					}
					else
					{
						output.format("~ %s %s%s", dataSetsVarNames.get(i), dataSetsVarValues.get(i), System.getProperty("line.separator"));
					}
				}
				
				
				//close our formatter
				output.close();
				
				// We have now saved our file! The DirtyBit should be clean!
				generalIO.changeTracker.clearFileModified();
		
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
}
