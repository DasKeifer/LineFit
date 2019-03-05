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


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.FocusListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import linefit.DataDimension;
import linefit.DataSet;
import linefit.GraphArea;
import linefit.GraphArea.GraphAxesPowers;
import linefit.GraphArea.GraphAxesRanges;
import linefit.GraphArea.GraphMetaData;
import linefit.GraphArea.ResultsDisplayData;
import linefit.GraphOptionsMenu;
import linefit.HasOptionsToDisplay;
import linefit.OnlyAllowNumbersListener;
import linefit.ScientificNotation;
import linefit.TabsFocusTraversalPolicy;
import linefit.FitAlgorithms.FitType;


/** This class Handles all of the export functionality of LineFit including JPG, PDF, and LaTex. The PNG export is
 * WYSWYG, basing its dimensions on the current LineFit window but the other exports are done based on the export
 * settings in the options panel.
 * 
 * @author Keith Rice
 * @version 1.0
 * @since 0.99.0 */
public class ExportIO implements HasOptionsToSave, HasOptionsToDisplay
{
    /** The JFrame to center any dialogs on or null to center them on the screen */
    private JFrame toCenterOn;
    /** The GraphArea to export */
    private GraphArea graphingArea;
    /** The GeneralIO instance that this LineFitFileIO is linked to that is used to perform the more standard IO
     * operations */
    private GeneralIO generalIO;

    // PDF display and exporting variables
    /** The number of pixels in an inch used for scaling the saved files to match the dimensions of what is displayed */
    private static int INCH_TO_PIXELS = 72;
    /** The default width in inches when exporting to a PDF image */
    public final static double DEFAULT_PDF_WIDTH = 8.5;
    /** The default height in inches when exporting to a PDF image */
    public final static double DEFAULT_PDF_HEIGHT = 8.5;
    /** The width of the PDF image when exported */
    private double pdfPageWidth = DEFAULT_PDF_WIDTH;
    /** The height of the PDF image when exported */
    private double pdfPageHeight = DEFAULT_PDF_HEIGHT;

    /** The LaTex Export spacing in cm of */
    public final static double LATEX_EXPORT_SPACING_IN_CM = 0.35;

    // Exporting Variables
    /** The desired width of the graph when it is exported to LaTex */
    private double laTexGraphWidthInCm = 15;
    /** The desired height of the graph when it is exported to LaTex */
    private double laTexGraphHeightInCm = 15;

    /** Allows the user to change the font size on any exported image of the graph */
    private float exportFontSize = 12;

    // Variables for displaying export options in options panel
    // PDF sizing variables
    /** The label for the PDF export size TextFields */
    private JLabel pdfSizeLabel;
    /** The label for the multiplication mark between the PDF export size textFields */
    private JLabel pdfTimesLabel;
    /** The TextField for the PDF export width in inches */
    private JTextField pdfWidthField;
    /** The TextField for the PDF export height in inches */
    private JTextField pdfHeightField;

    // LaTex export option variables
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
    private JSpinner exportFontSizeSpinner;

    /** The constructor for ExportIO that associates this instance with the passed instances
     * 
     * @param parentIO The GeneralIO instance that this LineFitFileIO belongs to and uses for common IO related
     *        functionality
     * @param frameToCenterOn The JFrame to center dialogs on or null to center them on the screen
     * @param graphToExport The GraphArea that this ExportIO instance will export */
    public ExportIO(GeneralIO parentIO, JFrame frameToCenterOn, GraphArea graphToExport)
    {
        generalIO = parentIO;
        toCenterOn = frameToCenterOn;
        graphingArea = graphToExport;
    }

    /** Reads in the options associated with exporting in from the LineFit data file
     * 
     * @param lineRead The line to attempt to read a setting from
     * 
     * @return True if an export option was found in the passed line and False if the line did not contain an export
     *         option */
    public boolean readInOption(String lineRead)
    {
        // split the input into the two parts
        // we can't use split because it will mess up on names
        int firstSpaceIndex = lineRead.indexOf(' ');
        String field = lineRead.substring(0, firstSpaceIndex).toLowerCase();
        String valueForField = lineRead.substring(firstSpaceIndex + 1);

        // now try and read in the option
        boolean found = true;
        try
        {
            switch (field)
            {
                case "pdfpagewidth":
                    pdfPageWidth = Double.parseDouble(valueForField);
                    break;
                case "pdfpageheight":
                    pdfPageHeight = Double.parseDouble(valueForField);
                    break;
                case "exportfontsize":
                    exportFontSize = Float.parseFloat(valueForField);
                    break;
                default:
                    found = false;
                    break; // if it wasn't an export option return false
            }
        }
        catch (NumberFormatException nfe)
        {
            JOptionPane.showMessageDialog(toCenterOn, "Error reading in number from line: " + lineRead, "NFE Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // return if this was in fact a export setting
        return found;
    }

    /** Adds the names of the options as saved in the LineFit file and the values associated with them to the respective
     * passed ArrayLists
     * 
     * @param variableNames The ArrayList of the names of the options
     * @param variableValues The ArrayList of the values of the options (indexed matched to the names) */
    public void retrieveAllOptions(ArrayList<String> variableNames, ArrayList<String> variableValues)
    {
        variableNames.add("PDFPageWidth");
        variableValues.add(Double.toString(pdfPageWidth));
        variableNames.add("PDFPageHeight");
        variableValues.add(Double.toString(pdfPageHeight));
        variableNames.add("ExportFontSize");
        variableValues.add(Float.toString(exportFontSize));
    }

    /** Resets the GUI elements associated with the options of this class to the default values */
    public void resetOptionsGuiElementsToDefaultValues()
    {
        pdfWidthField.setText("8.50");
        pdfHeightField.setText("8.50");

        LaTexWidthField.setText("15.0");
        LaTexHeightField.setText("15.0");
        exportFontSizeSpinner.setValue(12.0);
    }

    /** Creates the GUI elements associated with the options of this class to the default values
     * 
     * @param contentPane The Container to add the elements to */
    public void createOptionsGuiElements(Container contentPane)
    {
        FocusListener onlyNumbers = new OnlyAllowNumbersListener();

        pdfSizeLabel = new JLabel("PDF Export Size (in inches)");
        pdfSizeLabel.setFont(new Font("Verdana", Font.BOLD, 12));
        contentPane.add(pdfSizeLabel);
        pdfWidthField = new JTextField("");
        pdfWidthField.setText("" + pdfPageWidth);
        contentPane.add(pdfWidthField);
        pdfWidthField.addFocusListener(onlyNumbers);
        pdfTimesLabel = new JLabel("x");
        pdfTimesLabel.setFont(new Font("Verdana", Font.BOLD, 12));
        contentPane.add(pdfTimesLabel);
        pdfHeightField = new JTextField("");
        pdfHeightField.setText("" + pdfPageHeight);
        contentPane.add(pdfHeightField);
        pdfHeightField.addFocusListener(onlyNumbers);

        LaTexSizeLabel = new JLabel("LaTex Export Size (in cm)");
        LaTexSizeLabel.setFont(new Font("Verdana", Font.BOLD, 12));
        contentPane.add(LaTexSizeLabel);
        LaTexWidthField = new JTextField();
        LaTexWidthField.setText("" + laTexGraphWidthInCm);
        contentPane.add(LaTexWidthField);
        LaTexWidthField.addFocusListener(onlyNumbers);
        LaTexTimesSymbol = new JLabel("x");
        LaTexTimesSymbol.setFont(new Font("Verdana", Font.BOLD, 12));
        contentPane.add(LaTexTimesSymbol);
        LaTexHeightField = new JTextField();
        LaTexHeightField.setText("" + laTexGraphHeightInCm);
        contentPane.add(LaTexHeightField);
        LaTexHeightField.addFocusListener(onlyNumbers);
        exportFontSizeLabel = new JLabel("Exporting Font Size");
        contentPane.add(exportFontSizeLabel);
        SpinnerNumberModel laTexSpinnerModel = new SpinnerNumberModel(exportFontSize, 4.0, 32.0, 0.5);
        exportFontSizeSpinner = new JSpinner(laTexSpinnerModel);
        JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(exportFontSizeSpinner, "0.0");
        exportFontSizeSpinner.setEditor(numberEditor);
        contentPane.add(exportFontSizeSpinner);
    }

    /** Positions the GUI elements appropriately using the passed offsets to position it so it fits well with any other
     * classes that are displaying GUI elements
     * 
     * @param insets The Insets of the Container the GUI elements reside in
     * @param xOffset The x offset to apply to the GUI elements so that they fit with any other GUI elements being
     *        displayed by other classes
     * @param yOffset The y offset to apply to the GUI elements so that they fit with any other GUI elements being
     *        displayed by other classes */
    public void positionOptionsGuiElements(Insets insets, int xOffset, int yOffset)
    {
        // PDF Sizes
        GraphOptionsMenu.setElementBoundsIfVisible(pdfSizeLabel, insets, 0, 0, 208);
        GraphOptionsMenu.setElementBoundsIfVisible(pdfWidthField, insets, 0, 35, 60);
        GraphOptionsMenu.setElementBoundsIfVisible(pdfTimesLabel, insets, 70, 35, 20);
        GraphOptionsMenu.setElementBoundsIfVisible(pdfHeightField, insets, 89, 35, 60);

        ArrayList<Component> elementsGroup = new ArrayList<Component>();
        elementsGroup.add(pdfSizeLabel);
        elementsGroup.add(pdfWidthField);
        elementsGroup.add(pdfTimesLabel);
        elementsGroup.add(pdfHeightField);

        // Export sizes for LaTex
        GraphOptionsMenu.setElementBoundsIfVisible(LaTexSizeLabel, insets, 0, 0, 200);
        GraphOptionsMenu.setElementBoundsIfVisible(LaTexWidthField, insets, 4, 30, 60);
        GraphOptionsMenu.setElementBoundsIfVisible(LaTexTimesSymbol, insets, 74, 30, 20);
        GraphOptionsMenu.setElementBoundsIfVisible(LaTexHeightField, insets, 94, 30, 60);
        GraphOptionsMenu.setElementBoundsIfVisible(exportFontSizeLabel, insets, 4, 62, 142);
        GraphOptionsMenu.setElementBoundsIfVisible(exportFontSizeSpinner, insets, 155, 62, 50);

        ArrayList<Component> LaTexExportSizeGroup = new ArrayList<Component>();
        LaTexExportSizeGroup.add(LaTexSizeLabel);
        LaTexExportSizeGroup.add(LaTexWidthField);
        LaTexExportSizeGroup.add(LaTexTimesSymbol);
        LaTexExportSizeGroup.add(LaTexHeightField);
        LaTexExportSizeGroup.add(exportFontSizeLabel);
        LaTexExportSizeGroup.add(exportFontSizeSpinner);

        GraphOptionsMenu.shiftElementGroup(LaTexExportSizeGroup, 0, 65);
        elementsGroup.addAll(LaTexExportSizeGroup);

        GraphOptionsMenu.shiftElementGroup(elementsGroup, xOffset, yOffset);
    }

    /** Adds the GUI options to the traversal policy for tabs for the Container
     * 
     * @param policy The tab policy to add the GUI options to */
    public void addOptionsGuiElementsToTabs(TabsFocusTraversalPolicy policy)
    {
        policy.addComponentToTabsList(pdfWidthField);
        policy.addComponentToTabsList(pdfHeightField);
        policy.addComponentToTabsList(LaTexWidthField);
        policy.addComponentToTabsList(LaTexHeightField);
        policy.addComponentToTabsList(exportFontSizeSpinner);
    }

    /** Applies the current values in the GUI options */
    public void applyValuesInOptionsGuiElements()
    {
        pdfPageWidth = Double.parseDouble(pdfWidthField.getText());
        pdfPageHeight = Double.parseDouble(pdfHeightField.getText());
        // TODO: remove or explain this
        pdfWidthField.setText("" + pdfPageWidth);
        pdfHeightField.setText("" + pdfPageHeight);

        laTexGraphWidthInCm = Double.parseDouble(LaTexWidthField.getText());
        laTexGraphHeightInCm = Double.parseDouble(LaTexHeightField.getText());
        exportFontSize = ((Double) exportFontSizeSpinner.getValue()).floatValue();
    }

    /** Creates the linefit.sty file for the user to use for LaTex exports
     * 
     * @param destinationFolderPath The File path to create the .sty file at. This must contain the ending "\" to denote
     *        a folder */
    public void createLineFitStyFile(String destinationFolderPath)
    {
        generalIO.copyResourceFileToContainingFolder("linefit.sty", destinationFolderPath);
    }

    /** Saves/exports the current graph area as a PDF image using the dimensions set in the options panel */
    public void exportPDF()
    {
        File outputFile = generalIO.promptUserToSelectFileForSaving(".pdf");
        if (outputFile != null)
        {
            Dimension pdfDim = new Dimension(inchesToPixels(pdfPageWidth), inchesToPixels(pdfPageHeight));
            Document document = new Document(new com.itextpdf.text.Rectangle(pdfDim.width, pdfDim.height));
            try
            {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
                document.open();

                Graphics2D g2 = new PdfGraphics2D(writer.getDirectContent(), pdfDim.width, pdfDim.height);

                Font fontBase = new Font(g2.getFont().getName(), Font.PLAIN, 12);// 12 just because we have to
                                                                                 // give it some height
                Font exportFont = fontBase.deriveFont(exportFontSize);

                graphingArea.makeGraph(g2, pdfDim, true, exportFont);

                g2.dispose();
                JOptionPane.showMessageDialog(toCenterOn, "File Successfully Exported as a PDF Image", "File Exported",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            catch (FileNotFoundException e)
            {
                JOptionPane.showMessageDialog(toCenterOn,
                        "Could not find specified file or it is already in use by another program : Process aborted",
                        "FNF Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (DocumentException e)
            {
                JOptionPane.showMessageDialog(toCenterOn, "Exception occured while exporting to PDF : Process aborted",
                        "Document Error", JOptionPane.ERROR_MESSAGE);
            }
            document.close();
        }
    }

    /** Saves/exports the LineFit graph as a JPG image using dimensions matching LineFits current dimensions to give a
     * semi WYSWYG export */
    public void exportJPG()
    {
        File outputFile = generalIO.promptUserToSelectFileForSaving(".jpg");
        if (outputFile != null)
        {
            // Create an image to save
            int width = 1000;
            int height = 900;

            Dimension d = new Dimension(width, height);

            // Create a buffered image in which to draw
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Create a graphics contents on the buffered image
            Graphics2D g2d = bufferedImage.createGraphics();
            // g2d.setColor(Color.BLACK);

            // Draw graphics
            Font fontBase = new Font(g2d.getFont().getName(), Font.PLAIN, 12);// 12 just because we have to
                                                                              // give it some height
            Font exportFont = fontBase.deriveFont(exportFontSize);

            graphingArea.makeGraph(g2d, d, true, exportFont);

            // Graphics context no longer needed so dispose it
            g2d.dispose();

            // now save it
            try
            {
                ImageIO.write(bufferedImage, "jpg", outputFile);
                JOptionPane.showMessageDialog(toCenterOn, "File Successfully Exported as a JPG Image", "File Exported",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException error2)
            {
                JOptionPane.showMessageDialog(toCenterOn, "Exception occured while exporting to JPG : Process aborted",
                        "IO Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Saves/Exports the current graph area as a LaTex linefit graph using the set dimensions and font size as
     * specified in the options menu */
    public void exportLaTex()
    {
        File outputFile = generalIO.promptUserToSelectFileForSaving(".tex");
        if (outputFile != null)
        {
            try
            {
                PrintStream outputStream = new PrintStream(outputFile);

                StringBuilder output = new StringBuilder();

                // kick it off!
                generateLaTexExportString(output);

                outputStream.print(output.toString());
                outputStream.close();

                JOptionPane.showMessageDialog(toCenterOn, "File Successfully Exported as a LaTex LineFit Graph",
                        "File Exported", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException error)
            {
                JOptionPane.showMessageDialog(toCenterOn,
                        "Exception occured while saving as a LaTex file : Process aborted", "IO Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // create the .sty file
            String pathToFolder = outputFile.getParent();
            createLineFitStyFile(pathToFolder + "\\\\");
        }
    }

    /** Formats the needed information for the LaTex export into the StringBuilder.
     * 
     * @param output The StringBuilder that is building the output for the LaTex files */
    private void generateLaTexExportString(StringBuilder output)
    {
        // if our values are too small than LaTex will handle them poorly so we adjust them so it draws them
        // like they are bigger
        // and label them so they are small again;
        GraphAxesRanges origAxesRanges = graphingArea.getGraphAxesRanges();
        GraphAxesPowers origAxesPowers = graphingArea.getGraphAxesPowers();
        GraphAxesRanges axesRanges = graphingArea.getGraphAxesRanges();
        GraphAxesPowers axesPowers = graphingArea.getGraphAxesPowers();
        FontMetrics currentFontMeasurements = graphingArea.GetGraphFontMetrics();

        double xAdjForSmall = 0;
        if (Math.abs(axesRanges.xAxisMaximumValue - axesRanges.xAxisMinimumValue) / Math.pow(10,
                origAxesPowers.xAxisPower) < 0.1)
        {
            xAdjForSmall = axesRanges.xAxisMinimumValue;
            axesRanges.xAxisMaximumValue -= axesRanges.xAxisMinimumValue;
            axesRanges.xAxisMinimumValue = 0.0;
            axesPowers.xAxisPower = 0;
            while (Math.abs(axesRanges.xAxisMaximumValue - axesRanges.xAxisMinimumValue) / Math.pow(10,
                    axesPowers.xAxisPower) < 0.1)
            {
                axesPowers.xAxisPower--;
            }
        }

        double yAdjForSmall = 0;
        if (Math.abs(axesRanges.yAxisMaximumValue - axesRanges.yAxisMinimumValue) / Math.pow(10,
                origAxesPowers.yAxisPower) < 0.1)
        {
            yAdjForSmall = axesRanges.yAxisMinimumValue;
            axesRanges.yAxisMaximumValue -= axesRanges.yAxisMinimumValue;
            axesRanges.yAxisMinimumValue = 0.0;
            axesPowers.yAxisPower = 0;
            while (Math.abs(axesRanges.yAxisMaximumValue - axesRanges.yAxisMinimumValue) / Math.pow(10,
                    axesPowers.yAxisPower) < 0.1)
            {
                axesPowers.yAxisPower--;
            }
        }

        // calculate some of our needed values for spacing our graph
        double xAxisPowerMultiplier = Math.pow(10, axesPowers.xAxisPower);
        double yAxisPowerMultiplier = Math.pow(10, axesPowers.yAxisPower);
        double xAxisSpan = axesRanges.xAxisMaximumValue - axesRanges.xAxisMinimumValue;
        double yAxisSpan = axesRanges.yAxisMaximumValue - axesRanges.yAxisMinimumValue;

        // comments and directions for the top
        output.append("% Important: You must put \\RequirePackage{linefit} in the beging of the file to use graphs\n");
        output.append(
                "% Note: You can change the horizontal location on the graph by editing the value in the brackets in this line in the graph code: \\leftshift{value}\n");
        output.append(
                "% Note: changing this will NOT change the word spacings. In order to change spacing for a different test size, re-export the graph with a different export font size selected in the Graph Options\n\n");

        output.append("\\setbox0=\\hbox{\n");
        output.append("\t\\shiftleft{");
        output.append(laTexGraphWidthInCm / 5 + 2 * (exportFontSize - 12.0) / 22.0); // just to give it a
                                                                                     // reasonable starting
                                                                                     // point
        output.append("}\n");
        output.append("\t\\settextsize{");
        output.append(exportFontSize);
        output.append("}\n");

        // begin the graph and tell it how numbers on our graph relate to cm
        output.append("\t\\linefitgraphbegin{");
        output.append(ScientificNotation.WithNoErrorAndZeroPower(laTexGraphWidthInCm / (xAxisSpan /
                xAxisPowerMultiplier)));
        output.append("}{");
        output.append(ScientificNotation.WithNoErrorAndZeroPower(laTexGraphHeightInCm / (yAxisSpan /
                yAxisPowerMultiplier)));
        output.append("}\n");

        // export our datasets and fits
        for (int dataSetIdx = 0; dataSetIdx < graphingArea.dataSetSelector.getItemCount(); dataSetIdx++)
        {
            DataSet current = graphingArea.dataSetSelector.getItemAt(dataSetIdx);
            // if its not the empty set we save it
            if (!current.getName().equals("New DataSet"))
            {
                if (current.visibleGraph && current.hasData())
                {
                    if (current.getColor() == Color.BLACK)
                    {
                        output.append("\n\t\\color{black}\n");
                    }
                    else if (current.getColor() == Color.YELLOW)
                    {
                        output.append("\n\t\\color{yellow}\n");
                    }
                    else if (current.getColor() == Color.BLUE)
                    {
                        output.append("\n\t\\color{blue}\n");
                    }
                    else if (current.getColor() == Color.GREEN)
                    {
                        output.append("\n\t\\color{green}\n");
                    }
                    else if (current.getColor() == Color.ORANGE)
                    {
                        output.append("\n\t\\color{orange}\n");
                    }
                    else if (current.getColor() == Color.RED)
                    {
                        output.append("\n\t\\color{red}\n");
                    }
                    else
                    {
                        output.append("\n\t\\color[RGB]{" + current.getColor().getRed() + "," + current.getColor()
                                .getGreen() + "," + current.getColor().getBlue() + "}\n");
                    }

                    String symbol = "{\\symFilledSquare}";
                    Ellipse2D.Double ellipse = new Ellipse2D.Double();
                    Polygon triangle = new Polygon();

                    if (current.getShape().getClass() == ellipse.getClass())
                    {
                        symbol = "{\\symFilledCircle}";
                    }
                    else if (current.getShape().getClass() == triangle.getClass())
                    {
                        symbol = "{\\symFilledTriangle}";
                    }

                    // get the data with valid points (X and Y)
                    Double[][] data = current.getAllValidPointsData(true);
                    Double[] xData = data[DataDimension.X.getColumnIndex()];
                    Double[] yData = data[DataDimension.Y.getColumnIndex()];
                    Double[] xErrorData = data[DataDimension.X.getErrorColumnIndex()];
                    Double[] yErrorData = data[DataDimension.Y.getErrorColumnIndex()];

                    for (int pointIdx = 0; pointIdx < xData.length; pointIdx++)
                    {
                        output.append("\t\\putpoint");
                        if (xErrorData[pointIdx] != null)
                        {
                            if (yErrorData[pointIdx] != null)
                            {
                                output.append("xyerr");
                            }
                            else
                            {
                                output.append("xerr");
                            }
                        }
                        else if (yErrorData[pointIdx] != null)
                        {
                            output.append("yerr");
                        }

                        output.append(symbol);
                        output.append("{");
                        output.append(ScientificNotation.WithNoErrorAndZeroPower((xData[pointIdx] - xAdjForSmall) / Math
                                .pow(10, axesPowers.xAxisPower)));
                        output.append("}{");
                        output.append(ScientificNotation.WithNoErrorAndZeroPower((yData[pointIdx] - yAdjForSmall) / Math
                                .pow(10, axesPowers.yAxisPower)));
                        output.append("}");

                        if (xErrorData[pointIdx] != null)
                        {
                            output.append("{");
                            output.append(ScientificNotation.WithNoErrorAndZeroPower(xErrorData[pointIdx] / Math.pow(10,
                                    axesPowers.xAxisPower)));
                            output.append("}");
                        }

                        if (yErrorData[pointIdx] != null)
                        {
                            output.append("{");
                            output.append(ScientificNotation.WithNoErrorAndZeroPower(yErrorData[pointIdx] / Math.pow(10,
                                    axesPowers.yAxisPower)));
                            output.append("}");
                        }
                        output.append("\n");
                    }

                    // put the line on the graph if we have a fit
                    if (current.getFitType() != FitType.NONE)
                    {
                        // finds out which axis the line starts and ends on and draws it off of those
                        // otherwise we would get lines that go outside the graph
                        double xStart = (origAxesRanges.xAxisMinimumValue) / Math.pow(10, axesPowers.xAxisPower);
                        double yStart = (current.linearFitStrategy.getYOfXPoint(origAxesRanges.xAxisMinimumValue)) /
                                Math.pow(10, axesPowers.yAxisPower);

                        if (yStart < origAxesRanges.yAxisMinimumValue / Math.pow(10, axesPowers.yAxisPower) ||
                                yStart > origAxesRanges.yAxisMaximumValue / Math.pow(10, axesPowers.yAxisPower))
                        { // if its not on the graph then the other must be
                            yStart = origAxesRanges.yAxisMinimumValue / Math.pow(10, axesPowers.yAxisPower);
                            xStart = current.linearFitStrategy.getXOfYPoint(origAxesRanges.yAxisMinimumValue) / Math
                                    .pow(10, axesPowers.xAxisPower);
                        }

                        double xEnd = (origAxesRanges.xAxisMaximumValue) / Math.pow(10, axesPowers.xAxisPower);
                        double yEnd = (current.linearFitStrategy.getYOfXPoint(origAxesRanges.xAxisMaximumValue)) / Math
                                .pow(10, axesPowers.yAxisPower);
                        if (yEnd > origAxesRanges.yAxisMaximumValue / Math.pow(10, axesPowers.yAxisPower) ||
                                yEnd < origAxesRanges.yAxisMinimumValue / Math.pow(10, axesPowers.yAxisPower))
                        { // if its not on the graph then the other must be
                            yEnd = origAxesRanges.yAxisMaximumValue / Math.pow(10, axesPowers.yAxisPower);
                            xEnd = current.linearFitStrategy.getXOfYPoint(origAxesRanges.yAxisMaximumValue) / Math.pow(
                                    10, axesPowers.xAxisPower);
                            if (xEnd > (origAxesRanges.xAxisMaximumValue) / Math.pow(10, axesPowers.xAxisPower) ||
                                    xEnd < (origAxesRanges.xAxisMinimumValue) / Math.pow(10, axesPowers.xAxisPower))
                            {
                                yEnd = origAxesRanges.yAxisMinimumValue / Math.pow(10, axesPowers.yAxisPower);
                                xEnd = current.linearFitStrategy.getXOfYPoint(origAxesRanges.yAxisMinimumValue) / Math
                                        .pow(10, axesPowers.xAxisPower);
                            }
                        }

                        if (Double.isNaN(xStart) || Double.isNaN(xEnd))
                        {
                            xStart = origAxesRanges.xAxisMaximumValue;
                            xEnd = origAxesRanges.xAxisMaximumValue;
                        }

                        if (Double.isNaN(yStart) || Double.isNaN(yEnd))
                        {
                            yStart = origAxesRanges.yAxisMaximumValue;
                            yEnd = origAxesRanges.yAxisMaximumValue;
                        }

                        output.append("\t\\putline{");
                        output.append(ScientificNotation.WithNoErrorAndZeroPower(xStart - xAdjForSmall /
                                yAxisPowerMultiplier));
                        output.append("}{");
                        output.append(ScientificNotation.WithNoErrorAndZeroPower(yStart - yAdjForSmall /
                                yAxisPowerMultiplier));
                        output.append("}{");
                        output.append(ScientificNotation.WithNoErrorAndZeroPower(xEnd - xAdjForSmall /
                                xAxisPowerMultiplier));
                        output.append("}{");
                        output.append(ScientificNotation.WithNoErrorAndZeroPower(yEnd - yAdjForSmall /
                                xAxisPowerMultiplier));
                        output.append("}\n");

                        // do the result on graphs part if they are selected to be displayed
                        if (graphingArea.resultsAreDisplayedOnGraph)
                        {
                            ResultsDisplayData resultsDisplay = graphingArea.GetResultsDisplayData();

                            String kStr = "";
                            if (graphingArea.dataSetSelector.getItemCount() > 1)
                            {
                                kStr = "$_{" + (dataSetIdx + 1) + "}$";
                            }

                            double defaultResultsLength = currentFontMeasurements.stringWidth("m = 0.0000");
                            double currentResultsLength = graphingArea.getLongestResultsLength();
                            // this is y = mx + b from plotting desired spacing at 3 different font sizes and then
                            // fitting a line to it
                            String xPosStr = "" + (laTexGraphWidthInCm - (1 + exportFontSize * 0.1) *
                                    (currentResultsLength / defaultResultsLength) -
                                    (double) resultsDisplay.resultsPositionX / resultsDisplay.graphWidthAfterPadding *
                                            laTexGraphWidthInCm);

                            double yFontSizeInGraphUnits = 0.35 * (exportFontSize / 12.0);
                            double yResPos = -yFontSizeInGraphUnits / 2 + (double) resultsDisplay.resultsPositionY /
                                    resultsDisplay.graphHeightAfterPadding * laTexGraphHeightInCm;

                            String resultsSuffixString = "}{" + axesRanges.xAxisMinimumValue / xAxisPowerMultiplier +
                                    "}{" + axesRanges.yAxisMinimumValue / yAxisPowerMultiplier + "}\n";
                            output.append("\t\\putresults{y");
                            output.append(kStr);
                            output.append(" = m");
                            output.append(kStr);
                            output.append("x + b");
                            output.append(kStr);
                            output.append("}{");
                            output.append(xPosStr);
                            output.append("}{");
                            output.append(ScientificNotation.WithNoErrorAndZeroPower(yResPos + yFontSizeInGraphUnits *
                                    3));
                            output.append(resultsSuffixString);
                            output.append("\t\\putresults{m");
                            output.append(kStr);
                            output.append(" = ");
                            output.append(current.linearFitStrategy.getSlopeAsString(
                                    resultsDisplay.resultsDecimalPlaces, resultsDisplay.resultsUseScientificNotation,
                                    true));
                            output.append("}{");
                            output.append(xPosStr);
                            output.append("}{");
                            output.append(ScientificNotation.WithNoErrorAndZeroPower(yResPos + yFontSizeInGraphUnits *
                                    2));
                            output.append(resultsSuffixString);
                            output.append("\t\\putresults{b");
                            output.append(kStr);
                            output.append(" = ");
                            output.append(current.linearFitStrategy.getInterceptAsString(
                                    resultsDisplay.resultsDecimalPlaces, axesPowers.yAxisPower,
                                    resultsDisplay.resultsUseScientificNotation, true));
                            output.append("}{");
                            output.append(xPosStr);
                            output.append("}{");
                            output.append(ScientificNotation.WithNoErrorAndZeroPower(yResPos + yFontSizeInGraphUnits));
                            output.append(resultsSuffixString);
                        }
                    }
                }
            }
        }

        // set the color back to black
        output.append("\n\t\\color{black}\n");

        // Moved the axes down to here so that we dont have to worry as much about the plots going through the
        // axes

        String graphName = "", xAxisDescription = "", yAxisDescription = "";
        GraphMetaData metaData = graphingArea.getGraphMetaData();

        // put the begining part
        if (Math.abs(xAxisSpan) / Math.pow(10, origAxesPowers.xAxisPower) >= 0.1)
        {
            output.append("\t\\drawxaxis{");
            output.append(xAxisDescription);
            output.append("}{");
        }
        else
        {
            output.append("\t\\drawxaxisforsmallvals{");
            output.append(xAxisDescription);
            output.append("}{");
            // if we are a small axis then we need to put the adjusted positions for on the document as well
            // as the labeled positions
            if (metaData.xAxisNumberOfTickMarks > 0 && metaData.xAxisHasTickMarks)
            {
                for (int i = 0; i < metaData.xAxisNumberOfTickMarks; i++)
                {
                    Double labelSpot = (origAxesRanges.xAxisMaximumValue - origAxesRanges.xAxisMinimumValue) * i /
                            metaData.xAxisNumberOfTickMarks + origAxesRanges.xAxisMinimumValue;
                    output.append(ScientificNotation.withoutTimesTen(labelSpot, origAxesPowers.xAxisPower,
                            metaData.xAxisDecimalPlaces));
                    output.append(" ");
                }
            }
            output.append(ScientificNotation.withoutTimesTen(origAxesRanges.xAxisMaximumValue,
                    origAxesPowers.xAxisPower, metaData.xAxisDecimalPlaces));
            output.append("}{");
        }

        // add the label positions for the x axis
        if (metaData.xAxisNumberOfTickMarks > 0 && metaData.xAxisHasTickMarks)
        {
            for (int i = 0; i < metaData.xAxisNumberOfTickMarks; i++)
            {
                Double tickSpot = xAxisSpan * i / metaData.xAxisNumberOfTickMarks + axesRanges.xAxisMinimumValue;
                output.append(ScientificNotation.withoutTimesTen(tickSpot, axesPowers.xAxisPower,
                        metaData.xAxisDecimalPlaces));
                output.append(" ");
            }
            output.append(ScientificNotation.withoutTimesTen(axesRanges.xAxisMaximumValue, axesPowers.xAxisPower,
                    metaData.xAxisDecimalPlaces));
            output.append("}{");
        }

        output.append(ScientificNotation.WithNoErrorAndZeroPower(axesRanges.yAxisMinimumValue / yAxisPowerMultiplier));
        output.append("}{");
        output.append(ScientificNotation.WithNoErrorAndZeroPower(LATEX_EXPORT_SPACING_IN_CM + exportFontSize / 12 *
                0.35)); // 0.35
                        // is
                        // the
                        // spacing
                        // from
                        // the
                        // axes
                        // to
                        // the
                        // tick
                        // labels,
                        // 0.35
                        // *
                        // fontsize
                        // is
                        // for
                        // the
                        // labels
                        // width
                        // and
                        // axes
                        // name
                        // label
        output.append("}\n");

        if (origAxesPowers.xAxisPower != 0)
        {
            output.append("\t\\putxpower{");
            output.append("$\\times10^{");
            output.append(origAxesPowers.xAxisPower);
            output.append("}$}{");
            output.append(ScientificNotation.WithNoErrorAndZeroPower(axesRanges.xAxisMaximumValue /
                    xAxisPowerMultiplier));
            output.append("}{");
            output.append(ScientificNotation.WithNoErrorAndZeroPower(axesRanges.yAxisMinimumValue /
                    yAxisPowerMultiplier));
            output.append("}{");
            output.append(LATEX_EXPORT_SPACING_IN_CM);
            output.append("}\n");
        }

        // now the same for the y
        if (Math.abs(yAxisSpan) / Math.pow(10, origAxesPowers.yAxisPower) >= 0.1)
        {
            output.append("\t\\drawyaxis{");
            output.append(yAxisDescription);
            output.append("}{");
        }
        else
        {
            output.append("\t\\drawyaxisforsmallvals{");
            output.append(yAxisDescription);
            output.append("}{");
            for (int i = 0; i < metaData.yAxisNumberOfTickMarks; i++)
            {
                Double labelSpot = yAxisSpan * i / metaData.yAxisNumberOfTickMarks + origAxesRanges.yAxisMinimumValue;
                output.append(ScientificNotation.withoutTimesTen(labelSpot, origAxesPowers.yAxisPower,
                        metaData.yAxisDecimalPlaces));
                output.append(" ");
            }
            output.append(ScientificNotation.withoutTimesTen(origAxesRanges.yAxisMaximumValue,
                    origAxesPowers.yAxisPower, metaData.yAxisDecimalPlaces));
            output.append("}{");
        }

        if (metaData.yAxisNumberOfTickMarks > 0 && metaData.yAxisHasTickMarks)
        {
            for (int i = 0; i < metaData.yAxisNumberOfTickMarks; i++)
            {
                Double tickSpot = yAxisSpan * i / metaData.yAxisNumberOfTickMarks + axesRanges.yAxisMinimumValue;
                output.append(ScientificNotation.withoutTimesTen(tickSpot, axesPowers.yAxisPower,
                        metaData.yAxisDecimalPlaces));
                output.append(" ");
            }
            output.append(ScientificNotation.withoutTimesTen(axesRanges.yAxisMaximumValue, axesPowers.yAxisPower,
                    metaData.yAxisDecimalPlaces));
            output.append("}{");
        }

        output.append(ScientificNotation.WithNoErrorAndZeroPower(axesRanges.xAxisMinimumValue / xAxisPowerMultiplier));
        output.append("}{");

        double defaultLongestYStringLength = currentFontMeasurements.stringWidth("0.00");
        double longestYStringLength = currentFontMeasurements.stringWidth(ScientificNotation.withoutTimesTen(
                axesRanges.yAxisMaximumValue, axesPowers.yAxisPower, metaData.yAxisDecimalPlaces));
        double yMinStringLength = currentFontMeasurements.stringWidth(ScientificNotation.withoutTimesTen(
                axesRanges.yAxisMinimumValue, axesPowers.yAxisPower, metaData.yAxisDecimalPlaces));
        if (yMinStringLength > longestYStringLength)
        {
            longestYStringLength = yMinStringLength;
        }

        output.append(ScientificNotation.WithNoErrorAndZeroPower(LATEX_EXPORT_SPACING_IN_CM + exportFontSize / 12.0 *
                0.10 + (exportFontSize / 12.0) * (longestYStringLength / defaultLongestYStringLength) * 0.65)); // this
                                                                                                                // spacing
                                                                                                                // was
                                                                                                                // just
                                                                                                                // figured
                                                                                                                // out
                                                                                                                // by
                                                                                                                // trial
                                                                                                                // and
                                                                                                                // error
        output.append("}\n");

        if (origAxesPowers.yAxisPower != 0)
        {
            output.append("\t\\putypower{");
            output.append("$\\times10^{");
            output.append(origAxesPowers.yAxisPower);
            output.append("}$}{");
            output.append(ScientificNotation.WithNoErrorAndZeroPower(axesRanges.xAxisMinimumValue /
                    xAxisPowerMultiplier));
            output.append("}{");
            output.append(ScientificNotation.WithNoErrorAndZeroPower(axesRanges.yAxisMaximumValue /
                    yAxisPowerMultiplier));
            output.append("}{");
            output.append(LATEX_EXPORT_SPACING_IN_CM);
            output.append("}\n");
        }
        output.append("\t\\linefitgraphend\n}");

        // moved down here so we dont have to import but can just copy the whole thing
        output.append("\n\\begin{figure}\n\\begin{center}\n");
        output.append("\\scalebox{1}{\\box0}\n\\caption{");
        output.append(graphName);
        output.append("}\n\\end{center}\n\\end{figure}\n");
    }

    /** Converts the given length in inches to pixel length - used to determine the size of PDF when we export it
     * 
     * @param inchesToConvert The number of inches to convert to equivalent amount of pixels
     * @return The number of pixels that are equivalent to the inputed number of inches */
    private static int inchesToPixels(double inchesToConvert)
    {
        return (int) (INCH_TO_PIXELS * inchesToConvert);
    }
}
