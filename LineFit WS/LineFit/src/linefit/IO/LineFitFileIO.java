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


/** This class Handles reading and saving LineFit data files so that the rest of the code does not need to know the
 * precise format of the data file. Each class that has data to save must provide functionality for retrieving and
 * setting the data.
 * 
 * @author Keith Rice
 * @version 1.0
 * @since 0.99.0 */
public class LineFitFileIO
{
    /** The LineFit instance that this LineFitFileIO is linked to */
    private LineFit lineFit;
    /** The GeneralIO instance that this LineFitFileIO is linked to that is used to perform the more standard IO
     * operations */
    private GeneralIO generalIO;

    // Used for saving and opening files
    /** the returned value from the dialog box that allows the user to select a file to open or import */
    private int lastSelectedFileDialogChoice;
    /** The JFileChooser that allows the user to select files to open or import with a GUI */
    private JFileChooser fileChooser;

    /** The file extension to use for the saved LineFit files */
    private static final String saveFileExtension = ".txt";

    /** The constructor for LineFitFileIO that uses the passed GeneralIO for common functionality and saves data for the
     * passed LineFit instance
     * 
     * @param parentIO The GeneralIO instance that this LineFitFileIO belongs to and uses for common IO related
     *        functionality
     * @param lineFitToAssociateWith The LineFit instance that this handles the file IO for */
    public LineFitFileIO(GeneralIO parentIO, LineFit lineFitToAssociateWith)
    {
        generalIO = parentIO;
        lineFit = lineFitToAssociateWith;
    }

    /** Opens a dialog in the most recent directory and prompts the user for LineFit file to open.
     * 
     * @return The file that was selected or null if the selection was aborted */
    public File chooseLineFitFile()
    {
        fileChooser = new JFileChooser(generalIO.getMostRecentDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        lastSelectedFileDialogChoice = fileChooser.showDialog(lineFit, "Open");
        if (lastSelectedFileDialogChoice == JOptionPane.OK_OPTION)
        {
            File fileToOpen = fileChooser.getSelectedFile();
            generalIO.storeDirectoryOfChooser(fileChooser);
            if (fileToOpen != null && !fileToOpen.getName().endsWith(saveFileExtension))
            {
                int confirm = JOptionPane.showConfirmDialog(lineFit, "File is not a recognized lineFit " +
                        saveFileExtension + " file. Continue opening?", "Unsupported File Type",
                        JOptionPane.OK_CANCEL_OPTION);
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

    /** Opens up a LineFit file in this instance of LineFit after allowing the user to choose which file to open
     * 
     * @param offerChoiceToNotReadInGraphSettings Whether or not the user can choose not to import the graph settings.
     *        This should only be false when opening a file on start up */
    public void chooseAndOpenLineFitFile(boolean offerChoiceToNotReadInGraphSettings)
    {
        File toOpen = chooseLineFitFile();
        if (toOpen != null)
        {
            openLineFitFile(toOpen, offerChoiceToNotReadInGraphSettings);
        }
    }

    /** Opens the lineFit file at the specified path
     * 
     * @param filePath the path to open the file up at
     * @param offerChoiceToNotReadInGraphSettings Whether or not the user can choose not to import the graph settings.
     *        This should only be false when opening a file on start up */
    public void openLineFitFile(String filePath, boolean offerChoiceToNotReadInGraphSettings)
    {
        File fileToOpen = new File(filePath);
        openLineFitFile(fileToOpen, offerChoiceToNotReadInGraphSettings);
    }

    /** Opens the given LineFit file and prompts the user if they want to read in the graph settings if the flag is set
     * to
     * 
     * @param fileToOpen The file to open containing the LineFit graph data
     * @param offerChoiceToNotReadInGraphSettings Whether of not the user is prompted id they want to read in the graph
     *        settings as well as the datasets. True means that the user is prompted */
    public void openLineFitFile(File fileToOpen, boolean offerChoiceToNotReadInGraphSettings)
    {
        // make sure the file is actually there
        if (fileToOpen != null)
        {
            // try to read in the file
            try
            {
                BufferedReader inputReader = new BufferedReader(new FileReader(fileToOpen));

                // see if we need to read in the graph settings too - if we don't
                // offer a choice than that means its a new LineFit so we want to
                // read in the graph settings
                boolean importSettings = true;
                if (offerChoiceToNotReadInGraphSettings)
                {
                    int importSettingsRes = JOptionPane.showOptionDialog(lineFit, "Import the graph settings as well?",
                            "Import", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[] {
                                    "Yes", "No" }, "No");

                    if (importSettingsRes != 0)
                    {
                        importSettings = false;
                    }
                }

                // now actually read in the data from the file now that we are all set up
                try
                {
                    // read and determine which version of file we are using - we don't really do much with because
                    // right now the versions are similar enough we handle all of them with the same read methods
                    readAndCheckFileVersion(inputReader);

                    // now keep reading in the file
                    String lineRead = "";
                    boolean readingDataSet = false;
                    boolean newDataSet = false;
                    while ((lineRead = inputReader.readLine()) != null)
                    {
                        // skip empty lines
                        if (lineRead.isEmpty())
                        {
                            continue;
                        }

                        // trim any white spaces
                        lineRead = lineRead.trim().toLowerCase();

                        // if its a graph level line
                        if (lineRead.startsWith("#"))
                        {
                            readingDataSet = false;

                            // trim off the #
                            String trimmedLine = lineRead.substring(1).trim();

                            // if it is signaling the start of a dataset then set our state
                            // variables and continue - the next line will be the first line
                            // with actual dataset content - this line is just a switch
                            if (trimmedLine.startsWith("dataset"))
                            {
                                readingDataSet = true;
                                newDataSet = true;
                            }
                            // otherwise it is a "graph" setting and only import it if they selected to read in the
                            // graph settings
                            else if (importSettings)
                            {
                                // first see if it is an export parameter and if it wasn't check
                                // if it was a graph setting
                                boolean found = generalIO.exportIO.readInOption(trimmedLine);

                                // if it wasn't an export setting try loading it as a graph setting
                                if (!found)
                                {
                                    found = lineFit.readInOption(trimmedLine);
                                }

                                // if it wasn't either then print a warning and continue - it may
                                // just be an unsupported setting (either an old one or a future one)
                                if (!found)
                                {
                                    System.err.println("Non Fatal Error reading in Setting - Continuing: " + lineRead);
                                }
                            }
                        }
                        // if its a dataset level line and we are expecting dataset data
                        else if (lineRead.startsWith("~") && readingDataSet)
                        {
                            // trim off the ~
                            String trimmedLine = lineRead.substring(1).trim();

                            // if we didn't find it as valid setting give a warning an continue -
                            // it may just be a currently unsupported setting
                            if (!lineFit.readInDataAndDataOptions(trimmedLine, newDataSet))
                            {
                                System.err.println("Error reading in DataSet - Continuing: " + lineRead);
                            }
                            // make sure it will only show up as new for the first successful parameter
                            // passed or else it will split the dataset. If we failed to read in the line
                            // in the previous call it will not create the dataset so we need leave it set
                            else if (newDataSet)
                            {
                                newDataSet = false;
                            }
                        }
                        // we shouldn't ever get here - if we do it was an error
                        else
                        {
                            System.err.println("Unexpected line start or dataset line: " + lineRead);
                        }
                    }
                }
                catch (IOException ioe)
                {
                    JOptionPane.showMessageDialog(lineFit, "IO Error occured during read", "IO Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                finally
                {
                    // ensure the file was closed
                    try
                    {
                        inputReader.close();
                    }
                    catch (IOException e)
                    {
                    } // just because close may throw an exception
                }
            }
            catch (FileNotFoundException e)
            {
                JOptionPane.showMessageDialog(lineFit, "Could not find the file: Process aborted", "FileNotFound Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            lineFit.finishedReadingInData();

            System.out.println("Done Opening File");
        }
    }

    /** Attempts to read in the LineFit file version number and resets the position if it was not found (i.e. its the
     * original file format). If finds the version line then it will check the version in the file to see if this
     * LineFit knows how to read in that version and will warn the user if it is an unsupported version.
     * 
     * @param inputReader The BufferefReader for the LineFit file being read in
     * @throws IOException If the BufferedReader encounters and error */
    private void readAndCheckFileVersion(BufferedReader inputReader) throws IOException
    {
        // save our spot first - the original format doesn't have a version
        inputReader.mark(100);
        String versionLine = inputReader.readLine();
        if (versionLine != null)
        {
            // if we found the version line then read it in
            if (versionLine.toLowerCase().startsWith("fileformatversion"))
            {
                String versionString = versionLine.substring(versionLine.indexOf(' ') + 1);
                Version.VersionComparisonResult relationship = Version.checkLineFitFileFormatVersionString(
                        versionString);
                // if the version in the file is a newer version than LineFit
                if (relationship.isNewerVersion())
                {
                    // ask them if they want to still try reading in the file
                    int confirm = JOptionPane.showConfirmDialog(lineFit, "This file was created with a newer LineFit " +
                            "file format. Because of this, data could be missed or read in incorrectly. Continue " +
                            "loading data from it?", "Time Traveling File", JOptionPane.OK_CANCEL_OPTION);
                    if (confirm != JOptionPane.OK_OPTION)
                    {
                        // if they don't than close us of and return
                        inputReader.close();
                        return;
                    }
                }
                else if (relationship.isOlderVersion())
                {
                    if (Version.isLineFitFileVersionBefore(versionString, 2, 0))
                    {
                        JOptionPane.showMessageDialog(lineFit,
                                "The file was created with an older LineFit file format that is not fully supported." +
                                        " The error data columns may be loaded into the wrong columns",
                                "Partially Supported File Version", JOptionPane.INFORMATION_MESSAGE);
                    }

                    JOptionPane.showMessageDialog(lineFit, "The file was created with an older LineFit file format." +
                            " When the file is saved, the file format will be updated.", "Old File Version",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else if (relationship.isBadComparison())
                {
                    JOptionPane.showMessageDialog(lineFit,
                            "Error determining file format version number. Continuing but Data may not be complete",
                            "IO Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            // otherwise reset to before the line - it the old format and is a setting and not the file
            // version
            // so we need to read it in as a setting
            else
            {
                inputReader.reset();
            }
        }
        else
        {
            JOptionPane.showMessageDialog(lineFit, "Warning: Specified file is empty!", "IO Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Prompts the user for where to save the LineFit file and saves the data in the LineFit instance this is linked to
     * to the selected file */
    public void saveLineFitFile()
    {
        File outputFile = generalIO.promptUserToSelectFileForSaving(saveFileExtension);
        if (outputFile != null)
        {
            try
            {
                // create the file, output the data, and close the file
                Formatter output = new Formatter(outputFile);
                outputDataToFile(output);
                output.close();

                // make sure our file is not empty and if it is warn the user that it might not have saved
                // correctly and
                // to check it
                if (outputFile.length() > 10)
                { // just so we have a bit of a buffer in case it has some empty characters. the smallest size
                  // for a
                  // full one is around 800 bytes
                    JOptionPane.showMessageDialog(lineFit, "File Successfully Saved", "File Saved",
                            JOptionPane.INFORMATION_MESSAGE);

                    // we have now saved our file! The DirtyBit should be clean!
                    // only set this if we detected the file was saved successfully
                    generalIO.changeTracker.clearFileModified();
                }
                else
                {
                    JOptionPane.showMessageDialog(lineFit, "File may not have been saved correctly - Make sure the " +
                            "file contains data and if it does not try:\n\n\tSaving the file again " +
                            "and if it does not work then try\n\t" +
                            "Copying and pasting the data into a new LineFit and saving it",
                            "Problem Saving LineFit File", JOptionPane.WARNING_MESSAGE);
                }
            }
            catch (FileNotFoundException e)
            {
                // this message is redundant
                JOptionPane.showMessageDialog(lineFit, "Specified file was not found : Process aborted", "FNF Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /** Retrieves the information in the LineFit file and writes it to the passed formatter in the correct LineFit file
     * format
     * 
     * @param output the Formatter for the file being saved to write the LineFit file data to */
    private void outputDataToFile(Formatter output)
    {
        // save the current linefit file version number and then an additional space
        output.format("FileFormatVersion %s%s%s", Version.LINEFIT_FILE_FORMAT_VERSION, System.getProperty(
                "line.separator"), System.getProperty("line.separator"));

        // get and save all the graph settings data
        ArrayList<String> lineNames = new ArrayList<String>();
        ArrayList<String> lineValues = new ArrayList<String>();
        lineFit.retrieveAllOptions(lineNames, lineValues);
        for (int i = 0; i < lineNames.size(); i++)
        {
            output.format("# %s %s%s", lineNames.get(i), lineValues.get(i), System.getProperty("line.separator"));
        }

        // get and save all the export settings data
        // add a space between the settings groups
        output.format(System.getProperty("line.separator"));
        lineNames.clear();
        lineValues.clear();
        generalIO.exportIO.retrieveAllOptions(lineNames, lineValues);
        for (int i = 0; i < lineNames.size(); i++)
        {
            output.format("# %s %s%s", lineNames.get(i), lineValues.get(i), System.getProperty("line.separator"));
        }

        // get and save all the datasets variables
        lineNames.clear();
        lineValues.clear();
        lineFit.retrieveAllDataAndDataOptions(lineNames, lineValues);
        for (int i = 0; i < lineNames.size(); i++)
        {
            // use the graph level line starter for new datasets and add a empty line for ease of readability
            if (lineNames.get(i).equals("DataSet"))
            {
                output.format("%s# %s %s%s", System.getProperty("line.separator"), lineNames.get(i), lineValues.get(i),
                        System.getProperty("line.separator"));
            }
            else
            {
                output.format("~ %s %s%s", lineNames.get(i), lineValues.get(i), System.getProperty("line.separator"));
            }
        }
    }
}
