/* Copyright (C) 2018 Covenant College Physics Department
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

import linefit.GraphArea;
import linefit.LineFit;
import linefit.Version;


/** This class Handles the general IO operations of LineFit so that the rest of the code does not need to worry about
 * handling these operations
 * 
 * @author Keith Rice
 * @version 1.0
 * @since 0.99.0 */
public class GeneralIO
{
    /** The LineFit instance that this IOHandler is linked to */
    private LineFit lineFit;

    /** The object that handles the exporting functionality of LineFit */
    public ExportIO exportIO;
    /** The object that handles the file IO functionality of LineFit */
    public LineFitFileIO fileIO;
    /** The object that keeps track of if any changes have been made */
    public ChangeTracker changeTracker;

    /** The Icon as a BufferedImage that is used for any JFrames created in the lineFit program */
    private static BufferedImage lineFitIcon;

    // Variables that save the user preferences and where they last were in the directory
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

    // Used for saving and opening files
    /** The returned value from the dialog box that asks the user if they want to save before exiting LineFit if there
     * are unsaved changed (The dirtyBit is dirty/true) */
    private int saveBeforeClosingDialogChoice;
    /** The JFileChooser that allows the user to select files to open or import with a GUI */
    private JFileChooser fileChooser;

    /** The constructor for LineFitFileIO that creates a new instance of ChangeTracker to track if the data has changed
     * and that uses the passed LineFIT instance and Graph
     * 
     * @param lineFitToAssociateWith The LineFit object this General IO helper is associated with/is to help */
    public GeneralIO(LineFit lineFitToAssociateWith)
    {
        changeTracker = new ChangeTracker();
        lineFit = lineFitToAssociateWith;
        fileIO = new LineFitFileIO(this, lineFit);
    }

    /** Initializes the export IO object to use the passed in GraphArea
     * 
     * @param graphToExport The GraphArea that should be exported with the export IO */
    public void InitializeExportIO(GraphArea graphToExport)
    {
        exportIO = new ExportIO(this, lineFit, graphToExport);
    }

    /** Checks to see if a newer version of LineFit is available and if so, prompts the user to download it
     * 
     * @param alwaysPopUpResult True if the result of the check should always be displayed (i.e. if the user initiated
     *        it) */
    public void isUpdateAvailable(boolean alwaysPopUpResult)
    {
        String versionText = "";
        try
        {
            URL url = new URL("https://sourceforge.net/projects/linefit/files/LatestVersion.txt");
            try (Scanner s = new Scanner(url.openStream()))
            {
                versionText = s.nextLine();
            }
            catch (IOException e)
            {
                // message handled later
            }

            // if we found the file at the url and successfully read it, then compare the version
            if (!versionText.isEmpty())
            {
                Version.VersionComparisonResult relationship = Version.checkLineFitVersionString(versionText);
                if (relationship.isNewerVersion())
                {
                    JOptionPane.showMessageDialog(lineFit, "A newer version of LineFit is available!",
                            "LineFit Update Check", JOptionPane.WARNING_MESSAGE);
                }
                else if (relationship.isBadComparison())
                {
                    if (alwaysPopUpResult)
                    {
                        JOptionPane.showMessageDialog(lineFit,
                                "LineFit failed to determine the most recent version - Bad most recent version",
                                "LineFit Update Check", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else if (relationship.isSameVersion())
                {
                    if (alwaysPopUpResult)
                    {
                        JOptionPane.showMessageDialog(lineFit, "LineFit is up to date!", "LineFit Update Check",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else // older version???
                {
                    if (alwaysPopUpResult)
                    {
                        JOptionPane.showMessageDialog(lineFit,
                                "This version of LineFit has time travelled - It is newer than the most recent version!",
                                "LineFit Update Check", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            else
            {
                if (alwaysPopUpResult)
                {
                    JOptionPane.showMessageDialog(lineFit,
                            "LineFit failed to determine the most recent version - Couldn't open or read most recent file from URL",
                            "LineFit Update Check", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        catch (MalformedURLException e)
        {
            if (alwaysPopUpResult)
            {
                JOptionPane.showMessageDialog(lineFit,
                        "LineFit failed to determine the most recent version - Malformed URL", "LineFit Update Check",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /** Prompts the user for a LineFit file to open and then attempts to open it in a new LineFit instance
     * 
     * Note: This will only work if it is running from a JAR file */
    public void newLineFitInstancePromptForFile()
    {
        File file = fileIO.chooseLineFitFile();
        if (file != null)
        {
            newLineFitInstance(file.getAbsolutePath());
        }
    }

    /** Starts a new instance of LineFit and attempts to loads the data from the file path passed
     * 
     * Note: This will only work if it is running from a JAR file
     * 
     * @param pathToFile The path of the LineFit file to open in the new instance of LineFit
     * 
     * @throws NumberFormatException Throws this exception if it expected to find a number while parsing the file and
     *         found something else */
    public void newLineFitInstance(String pathToFile)
    {
        // make sure we got a file
        if (pathToFile != null)
        {
            // Run a java app in a separate system process
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

            try
            {
                File currProgram = new File(LineFit.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                if (currProgram.getName().endsWith(".jar"))
                {
                    ProcessBuilder newProgram = new ProcessBuilder(javaBin, "-jar", currProgram.getPath(), pathToFile);
                    newProgram.start();
                }
            }
            catch (URISyntaxException e)
            {
                JOptionPane.showMessageDialog(lineFit,
                        "Internal error creating new intance of linefit : Process aborted", "URI Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(lineFit, "Error starting a new intance of linefit : Process aborted",
                        "IO Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Opens a blank instance of LineFit in a new separate window.
     * 
     * Note: This will only work if it is running from a JAR file */
    public void newLineFitInstance()
    {
        newLineFitInstance(null);
    }

    /** Creates and displays or just displays the LineFit help PDF file */
    public void showPDFHelpFile()
    {
        // copy our help file
        copyResourceFileToContainingFolder("LineFitHelp.pdf", "");
        try
        {
            // open our file
            File helpPDF = new File("LineFitHelp.pdf");
            Desktop.getDesktop().open(helpPDF);
        }
        catch (IOException e2)
        {
            JOptionPane.showMessageDialog(lineFit,
                    "Error opening the help file. Make sure you have a PDF reader and try again", "IO Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Creates a copy of the passed file in the resources folder into the given directory
     * 
     * @param fileName The name of the resource to copy to the folder that the jar is in
     * @param destinationFolderPath The path to put the created resource at. This must contain the ending "\\" to denote
     *        a folder */
    public void copyResourceFileToContainingFolder(String fileName, String destinationFolderPath)
    {
        File file = new File(destinationFolderPath + fileName);
        try
        {
            // if we havent already created the help file we need to copy it from inside the jar to outside so we can
            // open it
            if (!file.exists())
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
        catch (IOException e2)
        {
            JOptionPane.showMessageDialog(lineFit, "Error creating the " + fileName + " file", "IO Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Shows the save file dialog that allows us to specify where to save our data and what to call it
     * 
     * @param extensionToPutOnFile The file extension to save the file with
     * @return The File that this LineFit's data will be saved to
     * @throws CloseDialogException throws this error if the save dialog was closed out of */
    public File showSaveFileDialog(String extensionToPutOnFile) throws CloseDialogException
    {
        File fileToOpen;
        fileChooser = new JFileChooser(getMostRecentDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int overwriteCheck = fileChooser.showSaveDialog(lineFit);

        // if they hit the save button
        if (overwriteCheck == JFileChooser.APPROVE_OPTION)
        {
            fileToOpen = fileChooser.getSelectedFile();

            // make sure it has the right extension
            if (!fileToOpen.exists())
            {
                fileToOpen = forceExtension(fileToOpen, extensionToPutOnFile);
                storeDirectoryOfChooser(fileChooser);
                return fileToOpen;
            }
            // make sure they want to overwrite the existing file
            else
            {
                int confirm = JOptionPane.showConfirmDialog(lineFit, "Overwrite existing file " + fileToOpen + "?");

                if (confirm == JOptionPane.OK_OPTION)
                {
                    fileToOpen = forceExtension(fileToOpen, extensionToPutOnFile);
                    storeDirectoryOfChooser(fileChooser);
                    return fileToOpen;
                }
                else
                {
                    throw new CloseDialogException();
                }
            }

        }
        else if (overwriteCheck == JFileChooser.CANCEL_OPTION)
        { // thow this error if we cancel it so that we can catch it and do nothing outside
            throw new CloseDialogException(); // make a exception or find one that works
        }

        // something went wrong
        return null;
    }

    /** Makes sure the filename ends with the given extension and if it does not it adds the extension to the end of the
     * file
     * 
     * @param fileToForceExtensionOn The File to force the extension on
     * @param extensionToPutOnFile The extension to force onto the File
     * @return The File with the extension forced on it */
    public File forceExtension(File fileToForceExtensionOn, String extensionToPutOnFile)
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
    private void confirmQuitWithoutSave()
    {
        String[] dialogOptions = { "Save", "Don't Save", "Cancel" };
        saveBeforeClosingDialogChoice = JOptionPane.showOptionDialog(lineFit,
                "Do you want to save the changes you made to the graph \"" + lineFit.getGraphName() +
                        "\"?\nYour changes will be lost if you don\'t save.", "Save changes?",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, dialogOptions, dialogOptions[0]);

        switch (saveBeforeClosingDialogChoice)
        {
            case JOptionPane.YES_OPTION:
            {
                fileIO.saveLineFitFile();
                System.out.println("Now Saving!");
                break;
            }
            case JOptionPane.NO_OPTION:
            {
                changeTracker.clearFileModified();
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
    public void closeApplication()
    {
        if (changeTracker.unsavedModifications())
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

    /** Stores the current directory the user is in for the file chooser so we do not have to navigate from scratch each
     * time
     * 
     * @param chooser The File Chooser to save the directory of */
    public void storeDirectoryOfChooser(JFileChooser chooser)
    {
        String currentDirectory = null;
        try
        {
            currentDirectory = chooser.getCurrentDirectory().getCanonicalPath();
        }
        catch (IOException e)
        {
            System.err.println("Error: Could not resolve the current filesystem directory.");
            System.err.println("Resolution: setting preferences to defaults.");
            e.printStackTrace();
            JOptionPane.showMessageDialog(lineFit, "Exception occured while saving file : Process aborted", "IO Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        // storeMostRecentDirectory(currentDirectory);
        storeOperatingSystem(USER_PREFERENCES_DEFAULT_OS);
        lineFitUserPreferences.put(USER_PREFERENCES_DEFAULT_DIRECTORY_KEY, currentDirectory);
    }

    /** Gets the folder that the user was last in so we can start our file chooser there
     * 
     * @return A String containing the last used directory's file path */
    public String getMostRecentDirectory()
    {
        String mostRecentDirectory;
        if (storedOSisCurrentOS())
        {
            // Since the Operating Systems are the same, the stored
            // directory should be okay to use.
            mostRecentDirectory = lineFitUserPreferences.get(USER_PREFERENCES_DEFAULT_DIRECTORY_KEY,
                    USER_PREFERENCES_DEFAULT_DIRECTORY);
        }
        else
        {
            // The operating systems are different, so we can't use the stored preference
            mostRecentDirectory = System.getProperty("user.dir");
        }
        return mostRecentDirectory;
    }

    /** Stores our current operating system
     * 
     * @param currentOS The operating system String to Store */
    private void storeOperatingSystem(String currentOS)
    {
        lineFitUserPreferences.put(USER_PREFERENCES_DEFAULT_OS_KEY, currentOS);
    }

    /** gets our stored operating system
     * 
     * @return A String that represents what operating system is stored for the user */
    private String getStoredOperatingSystem()
    {
        return lineFitUserPreferences.get(USER_PREFERENCES_DEFAULT_OS_KEY, USER_PREFERENCES_DEFAULT_OS);
    }

    /** Checks to make sure the OS we have stored is the one we are using
     * 
     * @return True if the stored operating system is the same as the one we are using and false otherwise */
    private boolean storedOSisCurrentOS()
    {
        return ((!getStoredOperatingSystem().equals(USER_PREFERENCES_DEFAULT_OS)) ? false : true);
    }

    /** Saves our preferences for LineFit such as the last folder the user was in */
    private void savePreferences()
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
     * @return The file that the user has selected or null if the operation was canceled */
    public File promptUserToSelectFileForSaving(String extensionToPutOnFile)
    {
        try
        {
            File outputFile = showSaveFileDialog(extensionToPutOnFile);
            storeDirectoryOfChooser(fileChooser);
            return outputFile;
        }
        catch (NullPointerException npe)
        {
            JOptionPane.showMessageDialog(lineFit, "An invalid null value occured : Process aborted", "Null Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        catch (CloseDialogException e)
        {
        } // the error we throw if we selected cancel
        return null;
    }

    /** Gets the LineFit Icon Image to be used throughout the program and loads it into memory if it already isn't
     * 
     * @return Returns the LineFit's Icon as a BufferedImage */
    public BufferedImage getLineFitIcon()
    {
        if (lineFitIcon == null)
        {
            initializeIconImage();
        }
        return lineFitIcon;
    }

    /** Loads the Icon Image file into memory for use in the rest of the program */
    private void initializeIconImage()
    {
        // try to set our icon
        InputStream iconStream = GeneralIO.class.getResourceAsStream("/resources/LineFitIcon.png");
        try
        {
            lineFitIcon = ImageIO.read(iconStream);
        }
        catch (IOException e)
        {
            System.err.println("Error creating Icon. Program continuing");
        }
    }
}
