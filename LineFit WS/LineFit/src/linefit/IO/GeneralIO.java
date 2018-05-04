package linefit.IO;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import linefit.LineFit;
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
 * @since 	0.98.1
 */
public class GeneralIO 
{
	/** The LineFit instance that this IOHandler is linked to */
	private static LineFit lineFit;
	
	/** The Icon as a BufferedImage that is used for any JFrames created in the lineFit program */
	private static BufferedImage lineFitIcon;

	//Variables that save the user preferences and where they last were in the directory
	/** The user's preferences for LineFit so we can store things like the last opened directory */
	private static Preferences lineFitUserPreferences = Preferences.userRoot().node("LineFit/GeneralPrefs");
	/** Stores the user's Operating System they used */
	private static final String USER_PREFERENCES_DEFAULT_OS = System.getProperty("os.name");
	/** Stores the key for finding the user's operating system so we can access the data */
	private static final String USER_PREFERENCES_DEFAULT_OS_KEY = "UserOperatingSystem";
	/** Stores the user's last used directory so they do not have to navigate from the root directory */
	private static final String USER_PREFERENCES_DEFAULT_DIRECTORY = System.getProperty("user.dir");
	/** Stores the key for finding the user's last used directory so we can access the data */
	private static final String USER_PREFERENCES_DEFAULT_DIRECTORY_KEY = "MostRecentUserDirectory";
	
	//Used for saving and opening files
	/** The returned value from the dialog box that asks the user if they want to save before exiting LineFit
	 * if there are unsaved changed (The dirtyBit is dirty/true) */
	private static int saveBeforeClosingDialogChoice;
	/** The JFileChooser that allows the user to select files to open or import with a GUI */
	private static JFileChooser fileChooser;
	
	//PDF display and exporting variables
	/** The default width in inches when exporting to a PDF image */
	private final static double DEFAULT_PDF_WIDTH = 8.5;
	/** The default height in inches when exporting to a PDF image */
	private final static double DEFAULT_PDF_HEIGHT = 8.5;
	/** The width of the PDF image when exported */
	static double PDFPageWidth = DEFAULT_PDF_WIDTH;
	/** The height of the PDF image when exported */
	static double PDFPageHeight = DEFAULT_PDF_HEIGHT;
	
	/** The LaTex Export spacing in cm of */
	final static double LATEX_EXPORT_SPACING_IN_CM = 0.35;

	//Exporting Variables
	/** The desired width of the graph when it is exported to LaTex */
	static double LaTexGraphWidthInCm = 15;
	/** The desired height of the graph when it is exported to LaTex */
	static double LaTexGraphHeightInCm = 15;
	
	/** Allows the user to change the font size on any exported image of the graph */
	static float exportFontSize = 12;
	
	public static void isUpdateAvailable()
	{
		try {
			URL url = new URL("https://sourceforge.net/projects/linefit/files/LatestVersion.txt");
			Scanner s = new Scanner(url.openStream());
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens up a LineFit file in this instance of LineFit after allowing the user to choose which file to open
	 * @param offerChoiceToNotReadInGraphSettings Whether or not the user can choose not to import the graph settings. This should only be false when opening a file on start up
	 */
	public static void chooseAndOpenLineFitFile(boolean offerChoiceToNotReadInGraphSettings)
	{
		File file = DataFileIO.promptUserForLineFitFileToOpen();
		
		//Makes sure the user selected something
		if(file != null)
		{
			DataFileIO.openLineFitFile(file, offerChoiceToNotReadInGraphSettings);
		}
	}

	/** Starts a new instance of LineFit and loads the data from the file into it
	 * @throws NumberFormatException Throws this exception if it expected to find a number while parsing the file and found something else
	 */
	public static void openLineFitFileInNewInstance() throws NumberFormatException 
	{
		try 
		{
			File file = DataFileIO.promptUserForLineFitFileToOpen();
			
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

	/** Opens a blank instance of LineFit in a new separate window
	 * Note: This will only work if it is running from a JAR file */
	public static void newLineFitInstance()
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

	/** Creates and displays or just displays the LineFit help PDF file */
	public static void showPDFHelpFile() 
	{
		//copy our help file
		copyResourceFileToContainingFolder("LineFitHelp.pdf", "");
		try
		{
			//open our file
			File helpPDF = new File("LineFitHelp.pdf");
	        Desktop.getDesktop().open(helpPDF);
		} 
		catch(IOException e2) 
		{
			JOptionPane.showMessageDialog(lineFit, "Error opening the help file. Make sure you have a PDF reader and try again",
				    "IO Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** Creates the linefit.sty file for the user to use for LaTex exports 
	 * @param destinationFolderPath The File path to create the .sty file at. This must contain the ending "\\" to denote a folder
	 */
	static void createLineFitStyFile(String destinationFolderPath) 
	{
		copyResourceFileToContainingFolder("linefit.sty", destinationFolderPath);
	}
	
	/**
	 * Creates a copy of the passed file in the resources folder into the given directory
	 * @param fileName The name of the resource to copy to the folder that the jar is in
	 * @param destinationFolderPath The path to put the created resource at. This must contain the ending "\\" to denote a folder
	 */
	static void copyResourceFileToContainingFolder(String fileName, String destinationFolderPath)
	{
		File file = new File(destinationFolderPath + fileName);
		try
		{
			//if we havent already created the help file we need to copy it from inside the jar to outside so we can open it
			if(!file.exists())
			{
				file.createNewFile();
				InputStream fileIn = GeneralIO.class.getResourceAsStream("/resources/" + fileName);
				OutputStream fileOut = new FileOutputStream(file);

				byte[] readBuffer = new byte[1024];
				int lengthToRead = 0;
				while ((lengthToRead = fileIn.read(readBuffer)) != -1) 
				{
					fileOut.write(readBuffer, 0, lengthToRead);
				}
				
				fileIn.close();
				fileOut.close();
			}
		} 
		catch(IOException e2) 
		{
			JOptionPane.showMessageDialog(lineFit, "Error creating the " + fileName + " file",
				    "IO Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** Shows the save file dialog that allows us to specify where to save our data and what to call it
	 * @param extensionToPutOnFile The file extension to save the file with
	 * @return The File that this LineFit's data will be saved to
	 * @throws CloseDialogException throws this error if the save dialog was closed out of
	 */
	static File showSaveFileDialog(String extensionToPutOnFile) throws CloseDialogException 
	{
		File fileToOpen; 
		fileChooser = new JFileChooser(getMostRecentDirectory());
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
				storeCurrentDirectory();
				return fileToOpen;
			}
			//make sure they want to overwrite the existing file
			else 
			{ 
				int confirm = JOptionPane.showConfirmDialog(lineFit, "Overwrite existing file " + fileToOpen + "?"); 
				
				if ( confirm == JOptionPane.OK_OPTION ) 
				{ 
					fileToOpen = forceExtension(fileToOpen, extensionToPutOnFile);
					storeCurrentDirectory();
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
				DataFileIO.saveLineFitFile();
				System.out.println("Now Saving!");
				break;
			}
			case JOptionPane.NO_OPTION: 
			{
				DirtyBit.setClean();
				closeApplication();
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

	/** Closes ourself in a safe way that asks the user if they want to save if there are unsaved changes */
	public static void closeApplication() 
	{
		if (DirtyBit.isDirty())
		{
			// Ask user if data should be saved
			confirmQuitWithoutSave();

		} 
		else
		{
			savePreferences();
			lineFit.dispose();
			System.exit(0);
		}
	}
	
	/** Stores the current directory the user is in for the file chooser so we do not have to navigate from scratch each time */
	static void storeCurrentDirectory() 
	{
		String currentDirectory = null;
		try 
		{
			currentDirectory = fileChooser.getCurrentDirectory().getCanonicalPath();
		} 
		catch (IOException e) 
		{
			System.err.println("Error: Could not resolve the current filesystem directory.");
			System.err.println("Resolution: setting preferences to defaults.");
			e.printStackTrace();
			JOptionPane.showMessageDialog(lineFit, "Exception occured while saving file : Process aborted",
				    "IO Error", JOptionPane.ERROR_MESSAGE);
		}
		//storeMostRecentDirectory(currentDirectory);
		storeOperatingSystem(USER_PREFERENCES_DEFAULT_OS);
		lineFitUserPreferences.put(USER_PREFERENCES_DEFAULT_DIRECTORY_KEY, currentDirectory);
	}

	/** Gets the folder that the user was last in so we can start our file chooser there
	 * @return A String containing the last used directory's file path */
	static String getMostRecentDirectory() 
	{
		String mostRecentDirectory;
		if (storedOSisCurrentOS()) 
		{
			// Since the Operating Systems are the same, the stored
			// directory should be okay to use.
			mostRecentDirectory = lineFitUserPreferences.get(USER_PREFERENCES_DEFAULT_DIRECTORY_KEY, USER_PREFERENCES_DEFAULT_DIRECTORY);
		} 
		else 
		{
			// The operating systems are different, so we can't use the stored preference
			mostRecentDirectory = System.getProperty("user.dir");
		}
		return mostRecentDirectory;
	}
	
	/** Stores our current operating system
	 * @param currentOS The operating system String to Store */
	private static void storeOperatingSystem(String currentOS) 
	{
		lineFitUserPreferences.put(USER_PREFERENCES_DEFAULT_OS_KEY, currentOS);
	}

	/** gets our stored operating system
	 * @return A String that represents what operating system is stored for the user */
	private static String getStoredOperatingSystem() 
	{
		return lineFitUserPreferences.get(USER_PREFERENCES_DEFAULT_OS_KEY, USER_PREFERENCES_DEFAULT_OS);
	}

	/** Checks to make sure the OS we have stored is the one we are using
	 * @return True if the stored operating system is the same as the one we are using and false otherwise */
	private static boolean storedOSisCurrentOS() 
	{
		return ((!getStoredOperatingSystem().equals(USER_PREFERENCES_DEFAULT_OS)) ? false : true);
	}
	
	/** Saves our preferences for LineFit such as the last folder the user was in */
	private static void savePreferences() 
	{
		try 
		{
			lineFitUserPreferences.flush();
		}
		catch (BackingStoreException e)
		{
			System.err.println("Unable to access the backing store. Preferences may not have been saved.");
			JOptionPane.showMessageDialog(lineFit, "Warning: Exception occured while saving preferences",
				    "BackingStore Error", JOptionPane.WARNING_MESSAGE);
		}
	}

	/** Opens a prompt that allows users to select a file that is then returned
	 * 
	 * @param extensionToPutOnFile The extension that the file will be saved with
	 * @return The file that the user has selected or null if the operation was canceled
	 */
	static File promptUserToSelectFileForSaving(String extensionToPutOnFile)
	{
		try
		{
			File outputFile = showSaveFileDialog(extensionToPutOnFile);
			storeCurrentDirectory();
			return outputFile;
		}
		catch (NullPointerException npe) 
		{
			JOptionPane.showMessageDialog(lineFit, "An invalid null value occured : Process aborted", "Null Error", JOptionPane.ERROR_MESSAGE);
		} 
		catch (CloseDialogException e) {} //the error we throw if we selected cancel
		return null;
	}

	/** Sets the LineFit object that this IO Handler is attached to 
	 * @param associatedWith The instance of LineFit to associate the static IOHandler class with */
	public static void assocaiteWithLineFit(LineFit associatedWith)
	{
		lineFit = associatedWith;
	}
	
	/** Gets the LineFit Icon Image to be used throughout the program and loads it into memory if it already isn't 
	 * @return Returns the LineFit's Icon as a BufferedImage
	 */
	public static BufferedImage getLineFitIcon()
	{
		if(lineFitIcon == null)
		{
			initializeIconImage();
		}
		return lineFitIcon;
	}
	
	/** Loads the Icon Image file into memory for use in the rest of the program */
	private static void initializeIconImage()
	{
		//try to set our icon
		InputStream iconStream = GeneralIO.class.getResourceAsStream("/resources/LineFitIcon.png");
		try 
		{
			GeneralIO.lineFitIcon = ImageIO.read(iconStream);
		} 
		catch (IOException e) 
		{
			System.err.println("Error creating Icon. Program continuing");
		}
	}

	public static void openLineFitFileAtPath(String filePathOfFileToLoad, boolean b) {
		// TODO Auto-generated method stub
		
	}
}