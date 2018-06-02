package linefit.IO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
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
import javax.swing.JOptionPane;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import linefit.DataSet;
import linefit.GraphArea;
import linefit.GraphArea.GraphAxesPowers;
import linefit.GraphArea.GraphAxesRanges;
import linefit.GraphArea.GraphMetaData;
import linefit.GraphArea.ResultsDisplayData;
import linefit.ScientificNotation;
import linefit.FitAlgorithms.FitType;

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
public class ExportIO 
{	
	/** The LineFit instance that this ExportIO is linked to */
	private JFrame toCenterOn;
	private GraphArea graphingArea;
	private GeneralIO generalIO;
	
	//PDF display and exporting variables
	/** The default width in inches when exporting to a PDF image */
	public final static double DEFAULT_PDF_WIDTH = 8.5;
	/** The default height in inches when exporting to a PDF image */
	public final static double DEFAULT_PDF_HEIGHT = 8.5;
	/** The width of the PDF image when exported */
	public double pdfPageWidth = DEFAULT_PDF_WIDTH;
	/** The height of the PDF image when exported */
	public double pdfPageHeight = DEFAULT_PDF_HEIGHT;
	
	/** The LaTex Export spacing in cm of */
	public final static double LATEX_EXPORT_SPACING_IN_CM = 0.35;

	//Exporting Variables
	/** The desired width of the graph when it is exported to LaTex */
	public double laTexGraphWidthInCm = 15;
	/** The desired height of the graph when it is exported to LaTex */
	public double laTexGraphHeightInCm = 15;
	
	/** Allows the user to change the font size on any exported image of the graph */
	public float exportFontSize = 12;

	public ExportIO(GeneralIO parentIO, JFrame frameToCenterOn, GraphArea graphToExport)
	{
		generalIO = parentIO;
		toCenterOn = frameToCenterOn;
		graphingArea = graphToExport;
	}

	boolean readInSetting(String lineRead)
	{
		//split the input into the two parts
		//we can't use split because it will mess up on names
		int firstSpaceIndex = lineRead.indexOf(' ');
		String field = lineRead.substring(0, firstSpaceIndex).toLowerCase();
		String valueForField = lineRead.substring(firstSpaceIndex + 1);
		
		//now try and read in the option
		boolean found = true;
		try
		{
			switch(field)
			{
				case "pdfpagewidth": pdfPageWidth = Double.parseDouble(valueForField); break;
				case "pdfpageheight": pdfPageHeight = Double.parseDouble(valueForField); break;
				case "exportfontsize": exportFontSize = Float.parseFloat(valueForField); break;
				default: found = false; break; //if it wasn't an export option return false
			}
		} 
		catch (NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(toCenterOn, "Error reading in number from line: " + lineRead,
				    "NFE Error", JOptionPane.ERROR_MESSAGE);
		}
		
		//return if this was in fact a export setting
		return found;
	}
	
	void retrieveAllSettings(ArrayList<String> variableNames, ArrayList<String> variableValues)
	{
		variableNames.add("PDFPageWidth");
		variableValues.add(Double.toString(pdfPageWidth));
		variableNames.add("PDFPageHeight");
		variableValues.add(Double.toString(pdfPageHeight));
		variableNames.add("ExportFontSize");
		variableValues.add(Float.toString(exportFontSize));
	}
	
	//For tick marks and PDF dimensions
	
	/** Creates the linefit.sty file for the user to use for LaTex exports 
	 * @param destinationFolderPath The File path to create the .sty file at. This must contain the ending "\\" to denote a folder
	 */
	void createLineFitStyFile(String destinationFolderPath) 
	{
		generalIO.copyResourceFileToContainingFolder("linefit.sty", destinationFolderPath);
	}

	/** Saves/exports the current graph area as a PDF image */
	public void exportPDF()
	{
		File outputFile = generalIO.promptUserToSelectFileForSaving(".pdf");
		if(outputFile != null)
		{
			Dimension pdfDim = new Dimension(inchesToPixels(pdfPageWidth), inchesToPixels(pdfPageHeight));
			Document document = new Document(new com.itextpdf.text.Rectangle(pdfDim.width, pdfDim.height));
			try 
			{
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
			    document.open();
			    
			    Graphics2D g2 = new PdfGraphics2D(writer.getDirectContent(), pdfDim.width, pdfDim.height);

				Font fontBase = new Font(g2.getFont().getName(), Font.PLAIN, 12);//12 just because we have to give it some height
				Font exportFont = fontBase.deriveFont(exportFontSize);
				
			    graphingArea.makeGraph(g2, pdfDim, true, exportFont);
				
			    g2.dispose(); 
				JOptionPane.showMessageDialog(toCenterOn, "File Successfully Exported as a PDF Image", "File Exported", JOptionPane.INFORMATION_MESSAGE);
			} 
			catch (FileNotFoundException e) 
			{
				JOptionPane.showMessageDialog(toCenterOn, "Could not find specified file or it is already in use by another program : Process aborted",
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

	/** Saves/exports the LineFit graph as a JPG image */
	public void exportJPG()
	{
		File outputFile = generalIO.promptUserToSelectFileForSaving(".jpg");
		if(outputFile != null)
		{
			// Create an image to save
			int width = 1000;
			int height = 900;
			
			Dimension d = new Dimension(width, height);

			// Create a buffered image in which to draw
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// Create a graphics contents on the buffered image
			Graphics2D g2d = bufferedImage.createGraphics();
			//g2d.setColor(Color.BLACK);

			// Draw graphics
			Font fontBase = new Font(g2d.getFont().getName(), Font.PLAIN, 12);//12 just because we have to give it some height
			Font exportFont = fontBase.deriveFont(exportFontSize);
			
		    graphingArea.makeGraph(g2d, d, true, exportFont);
			
			// Graphics context no longer needed so dispose it
			g2d.dispose();
			
			//now save it
			try
			{
				ImageIO.write(bufferedImage, "jpg", outputFile);
				JOptionPane.showMessageDialog(toCenterOn, "File Successfully Exported as a JPG Image", "File Exported", JOptionPane.INFORMATION_MESSAGE);
			} 
			catch (IOException error2) 
			{
				JOptionPane.showMessageDialog(toCenterOn, "Exception occured while exporting to JPG : Process aborted", "IO Error", JOptionPane.ERROR_MESSAGE);
			} 
		}
	}

	/** Saves/Exports the current graph area as a LaTex linefit graph */
	public void exportLaTex()
	{
		File outputFile = generalIO.promptUserToSelectFileForSaving(".tex");
		if(outputFile != null)
		{		
			try 
			{
				PrintStream outputStream = new PrintStream(outputFile);
				
				StringBuilder output = new StringBuilder();
				
				//kick it off!
				recursivelyGenerateLaTexExportString(output);
				
				outputStream.print(output.toString());
				outputStream.close();
	
				JOptionPane.showMessageDialog(toCenterOn, "File Successfully Exported as a LaTex LineFit Graph",
					    "File Exported", JOptionPane.INFORMATION_MESSAGE);
			} 
			catch (IOException error) 
			{
				JOptionPane.showMessageDialog(toCenterOn, "Exception occured while saving as a LaTex file : Process aborted",
					    "IO Error", JOptionPane.ERROR_MESSAGE);
			}
			
			//create the .sty file
			String pathToFolder = outputFile.getParent();
			createLineFitStyFile(pathToFolder + "\\\\");
		}
	}
	
	/**
	 * Continues to recursively export to a LaTEx File. This saves the Graph Area and below
	 * @param output The StringBuilder that is building the output for the LaTex files
	 */
	void recursivelyGenerateLaTexExportString(StringBuilder output)
	{				
		//if our values are too small than LaTex will handle them poorly so we adjust them so it draws them like they are bigger
		//and label them so they are small again;
		GraphAxesRanges origAxesRanges = graphingArea.GetGraphAxesRanges();
		GraphAxesPowers origAxesPowers = graphingArea.GetGraphAxesPowers();
		GraphAxesRanges axesRanges = graphingArea.GetGraphAxesRanges();
		GraphAxesPowers axesPowers = graphingArea.GetGraphAxesPowers();
		FontMetrics currentFontMeasurements = graphingArea.GetGraphFontMetrics();

		double xAdjForSmall = 0;
		if(Math.abs(axesRanges.xAxisMaximumValue - axesRanges.xAxisMinimumValue) / Math.pow(10, origAxesPowers.xAxisPower) < 0.1) 
		{
			xAdjForSmall = axesRanges.xAxisMinimumValue;
			axesRanges.xAxisMaximumValue -= axesRanges.xAxisMinimumValue;
			axesRanges.xAxisMinimumValue = 0.0;
			axesPowers.xAxisPower = 0;
			while(Math.abs(axesRanges.xAxisMaximumValue - axesRanges.xAxisMinimumValue) / Math.pow(10, axesPowers.xAxisPower) < 0.1) 
			{
				axesPowers.xAxisPower--;
			}
		}

		double yAdjForSmall = 0;
		if(Math.abs(axesRanges.yAxisMaximumValue - axesRanges.yAxisMinimumValue) / Math.pow(10, origAxesPowers.yAxisPower) < 0.1)
		{
			yAdjForSmall = axesRanges.yAxisMinimumValue;
			axesRanges.yAxisMaximumValue -= axesRanges.yAxisMinimumValue;
			axesRanges.yAxisMinimumValue = 0.0;
			axesPowers.yAxisPower = 0;				
			while(Math.abs(axesRanges.yAxisMaximumValue - axesRanges.yAxisMinimumValue) / Math.pow(10, axesPowers.yAxisPower) < 0.1)
			{
				axesPowers.yAxisPower--;
			}
		}
		
		//calculate some of our needed values for spacing our graph
		double xAxisPowerMultiplier = Math.pow(10, axesPowers.xAxisPower);
		double yAxisPowerMultiplier = Math.pow(10, axesPowers.yAxisPower);
		double xAxisSpan = axesRanges.xAxisMaximumValue - axesRanges.xAxisMinimumValue;
		double yAxisSpan = axesRanges.yAxisMaximumValue - axesRanges.yAxisMinimumValue;

		//comments and directions for the top
		output.append("% Important: You must put \\RequirePackage{linefit} in the beging of the file to use graphs\n");
		output.append("% Note: You can change the horizontal location on the graph by editing the value in the brackets in this line in the graph code: \\leftshift{value}\n");
		output.append("% Note: changing this will NOT change the word spacings. In order to change spacing for a different test size, re-export the graph with a different export font size selected in the Graph Options\n\n");
		
		output.append("\\setbox0=\\hbox{\n");
		output.append("\t\\shiftleft{");
		output.append(laTexGraphWidthInCm / 5 + 2 * (exportFontSize - 12.0) / 22.0); //just to give it a reasonable starting point
		output.append("}\n");
		output.append("\t\\settextsize{");
		output.append(exportFontSize);
		output.append("}\n");

		//begin the graph and tell it how numbers on our graph relate to cm
		output.append("\t\\linefitgraphbegin{");
		output.append(ScientificNotation.WithNoErrorAndZeroPower(laTexGraphWidthInCm / (xAxisSpan / xAxisPowerMultiplier)));
		output.append("}{");
		output.append(ScientificNotation.WithNoErrorAndZeroPower(laTexGraphHeightInCm / (yAxisSpan / yAxisPowerMultiplier)));
		output.append("}\n"); 
		
		//export our datasets and fits
		for(int k = 0; k < graphingArea.dataSetSelector.getItemCount(); k++) 
		{
			DataSet current = graphingArea.dataSetSelector.getItemAt(k);
			//if its not the empty set we save it
			if(!current.getName().equals("New DataSet")) 
			{
				if (current.visibleGraph && current.hasData()) 
				{	
					if (current.getColor() == Color.BLACK) {
						output.append("\n\t\\color{black}\n");
					} else if (current.getColor() == Color.YELLOW) {
						output.append("\n\t\\color{yellow}\n");
					} else if (current.getColor() == Color.BLUE) {
						output.append("\n\t\\color{blue}\n");
					} else if (current.getColor() == Color.GREEN) {
						output.append("\n\t\\color{green}\n");
					} else if(current.getColor() == Color.ORANGE) {
						output.append("\n\t\\color{orange}\n");
					} else if (current.getColor() == Color.RED) {
						output.append("\n\t\\color{red}\n");
					} else {
						output.append("\n\t\\color[RGB]{" + current.getColor().getRed() + "," + current.getColor().getGreen() + "," + current.getColor().getBlue() + "}\n");
					}
						
					if (current.xData != null && current.yData != null) 
					{	
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
						
						if(current.xData.getNonNullDataSize() > 0 && current.yData.getNonNullDataSize() > 0) 
						{
							for (int l = 0; l < Math.max(current.xData.getData().size(), current.yData.getData().size()); l++) 
							{
								if(!current.xData.isNull(l) && !current.yData.isNull(l)) 
								{
									output.append("\t\\putpoint");
									if (current.xErrorData != null && !current.xErrorData.isNull(l) && current.xErrorData.readDouble(l) != 0.0)
									{
										if (current.yErrorData != null && !current.yErrorData.isNull(l) && current.yErrorData.readDouble(l) != 0.0)
										{
											output.append("xyerr");
										} 
										else
										{
											output.append("xerr");
										}
									} 
									else if (current.yErrorData != null && !current.yErrorData.isNull(l) && current.yErrorData.readDouble(l) != 0.0) 
									{
										output.append("yerr");
									}
									
									output.append(symbol);
									output.append("{");
									output.append(ScientificNotation.WithNoErrorAndZeroPower((current.xData.readDouble(l) - xAdjForSmall) / Math.pow(10, axesPowers.xAxisPower)));
									output.append("}{");
									output.append(ScientificNotation.WithNoErrorAndZeroPower((current.yData.readDouble(l) - yAdjForSmall) / Math.pow(10, axesPowers.yAxisPower)));
									output.append("}");							
									
									if (current.xErrorData != null && !current.xErrorData.isNull(l) && current.xErrorData.readDouble(l) != 0.0) 
									{
										output.append("{");
										output.append(ScientificNotation.WithNoErrorAndZeroPower(current.xErrorData.readDouble(l) / Math.pow(10, axesPowers.xAxisPower)));
										output.append("}");
									}
									
									if (current.yErrorData != null && !current.yErrorData.isNull(l) && current.yErrorData.readDouble(l) != 0.0) {
										output.append("{");
										output.append(ScientificNotation.WithNoErrorAndZeroPower(current.yErrorData.readDouble(l) / Math.pow(10, axesPowers.yAxisPower)));
										output.append("}");
									}
									output.append("\n");									
								}
							}
						}
						
						//put the line on the graph if we have a fit
						if(current.getFitType() != FitType.NONE) 
						{
							//finds out which axis the line starts and ends on and draws it off of those otherwise we would get lines that go outside the graph
							double xStart = (origAxesRanges.xAxisMinimumValue) / Math.pow(10, axesPowers.xAxisPower);
							double yStart = (current.linearFitStrategy.getYOfXPoint(origAxesRanges.xAxisMinimumValue)) /
									Math.pow(10, axesPowers.yAxisPower);

							if(yStart < origAxesRanges.yAxisMinimumValue / Math.pow(10, axesPowers.yAxisPower) || 
									yStart > origAxesRanges.yAxisMaximumValue / Math.pow(10, axesPowers.yAxisPower)) 
							{ //if its not on the graph then the other must be
								yStart = origAxesRanges.yAxisMinimumValue / Math.pow(10, axesPowers.yAxisPower);
								xStart = current.linearFitStrategy.getXOfYPoint(origAxesRanges.yAxisMinimumValue) / 
										Math.pow(10, axesPowers.xAxisPower);
							}
								
							double xEnd = (origAxesRanges.xAxisMaximumValue) / Math.pow(10, axesPowers.xAxisPower);
							double yEnd = (current.linearFitStrategy.getYOfXPoint(origAxesRanges.xAxisMaximumValue)) /
									Math.pow(10, axesPowers.yAxisPower);
							if(yEnd > origAxesRanges.yAxisMaximumValue / Math.pow(10, axesPowers.yAxisPower) ||
									yEnd < origAxesRanges.yAxisMinimumValue / Math.pow(10, axesPowers.yAxisPower))
							{ //if its not on the graph then the other must be
								yEnd = origAxesRanges.yAxisMaximumValue / Math.pow(10, axesPowers.yAxisPower);
								xEnd = current.linearFitStrategy.getXOfYPoint(origAxesRanges.yAxisMaximumValue) /
										Math.pow(10, axesPowers.xAxisPower);
								if(xEnd > (origAxesRanges.xAxisMaximumValue) / Math.pow(10, axesPowers.xAxisPower) ||
										xEnd < (origAxesRanges.xAxisMinimumValue) / Math.pow(10, axesPowers.xAxisPower)) 
								{
									yEnd = origAxesRanges.yAxisMinimumValue / Math.pow(10, axesPowers.yAxisPower);
									xEnd = current.linearFitStrategy.getXOfYPoint(origAxesRanges.yAxisMinimumValue) /
											Math.pow(10, axesPowers.xAxisPower);
								}
							}
							
							if(Double.isNaN(xStart) || Double.isNaN(xEnd)) 
							{
								xStart = origAxesRanges.xAxisMaximumValue;
								xEnd = origAxesRanges.xAxisMaximumValue;
							}
							
							if(Double.isNaN(yStart) || Double.isNaN(yEnd))
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
						
							//do the result on graphs part if they are selected to be displayed
							if(graphingArea.resultsAreDisplayedOnGraph) 
							{
								ResultsDisplayData resultsDisplay = graphingArea.GetResultsDisplayData();
								
								String kStr = "";
								if(graphingArea.dataSetSelector.getItemCount() > 1)
								{
									kStr = "$_{" + (k + 1) + "}$";
								}
								//String xPosStr = ScientificNotation.WithNoErrorAndZeroPower(getXLaTexResultsPos());
//
//								BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
//								Graphics2D graphGraphics = bufferedImage.createGraphics();
//								Font fontBase = new Font(graphGraphics.getFont().getName(), Font.PLAIN, 12);//12 just because we have to give it some height
//								Font newFont = fontBase.deriveFont(exportFontSize);
//								graphGraphics.setFont(newFont);
//								currentFontMeasurements = graphGraphics.getFontMetrics();
								
								double defaultResultsLength = currentFontMeasurements.stringWidth("m = 0.0000");
								double currentResultsLength = graphingArea.getLongestResultsLength();
								String xPosStr = "" + (laTexGraphWidthInCm - (1 + exportFontSize * 0.1) * //this is y = mx + b from plotting desired spacing at 3 different font sizes and then fitting a line to it
										(currentResultsLength / defaultResultsLength) - 
										(double)resultsDisplay.resultsPositionX / 
										resultsDisplay.graphWidthAfterPadding * laTexGraphWidthInCm); 
								
								double yFontSizeInGraphUnits = 0.35 * (exportFontSize / 12.0);
								double yResPos = -yFontSizeInGraphUnits / 2 + (double)resultsDisplay.resultsPositionY / 
										resultsDisplay.graphHeightAfterPadding * laTexGraphHeightInCm;								
								
								String resultsSuffixString = "}{" + axesRanges.xAxisMinimumValue / 
										xAxisPowerMultiplier + "}{" + axesRanges.yAxisMinimumValue / 
										yAxisPowerMultiplier + "}\n";
								output.append("\t\\putresults{y");
								output.append(kStr);
								output.append(" = m");
								output.append(kStr);
								output.append("x + b");
								output.append(kStr);
								output.append("}{");
								output.append(xPosStr);
								output.append("}{");
								output.append(ScientificNotation.WithNoErrorAndZeroPower(yResPos + 
										yFontSizeInGraphUnits * 3));
								output.append(resultsSuffixString);
								output.append("\t\\putresults{m");
								output.append(kStr);
								output.append(" = ");
								output.append(current.linearFitStrategy.getSlopeAsString(
										resultsDisplay.resultsDecimalPlaces, 
										resultsDisplay.resultsUseScientificNotation, true));
								output.append("}{");
								output.append(xPosStr); 
								output.append("}{");
								output.append(ScientificNotation.WithNoErrorAndZeroPower(yResPos + 
										yFontSizeInGraphUnits * 2));
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
		}
		
		//set the color back to black
		output.append("\n\t\\color{black}\n");		
		
		//Moved the axes down to here so that we dont have to worry as much about the plots going through the axes

		String graphName = "", xAxisDescription = "", yAxisDescription = "";
		GraphMetaData metaData = graphingArea.GetGraphMetaData();
		
		//put the begining part
		if(Math.abs(xAxisSpan) / Math.pow(10, origAxesPowers.xAxisPower) >= 0.1) 
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
			//if we are a small axis then we need to put the adjusted positions for on the document as well as the labeled positions
			if(metaData.xAxisNumberOfTickMarks > 0 && metaData.xAxisHasTickMarks) 
			{
				for(int i = 0; i < metaData.xAxisNumberOfTickMarks; i++)
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
			
		//add the label positions for the x axis
		if(metaData.xAxisNumberOfTickMarks > 0 && metaData.xAxisHasTickMarks) 
		{
			for(int i = 0; i < metaData.xAxisNumberOfTickMarks; i++)
			{
				Double tickSpot = xAxisSpan * i / metaData.xAxisNumberOfTickMarks + axesRanges.xAxisMinimumValue;
				output.append(ScientificNotation.withoutTimesTen(tickSpot, axesPowers.xAxisPower, 
						metaData.xAxisDecimalPlaces));
				output.append(" ");
			}
			output.append(ScientificNotation.withoutTimesTen(axesRanges.xAxisMaximumValue, axesPowers.xAxisPower,
					metaData.xAxisDecimalPlaces))
			;
			output.append("}{");
		} 

		output.append(ScientificNotation.WithNoErrorAndZeroPower(axesRanges.yAxisMinimumValue / yAxisPowerMultiplier));
		output.append("}{");
		output.append(ScientificNotation.WithNoErrorAndZeroPower(LATEX_EXPORT_SPACING_IN_CM + exportFontSize / 
				12 * 0.35 )); //0.35 is the spacing from the axes to the tick labels, 0.35 * fontsize is for the labels width and axes name label
		output.append("}\n");
		
		if(origAxesPowers.xAxisPower != 0) 
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
		
		//now the same for the y
		if(Math.abs(yAxisSpan) / Math.pow(10, origAxesPowers.yAxisPower) >= 0.1) 
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
			for(int i = 0; i < metaData.yAxisNumberOfTickMarks; i++)
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
		
		if(metaData.yAxisNumberOfTickMarks > 0 && metaData.yAxisHasTickMarks)
		{
			for(int i = 0; i < metaData.yAxisNumberOfTickMarks; i++) 
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
		
		double defaultLongestYStringLength =  currentFontMeasurements.stringWidth("0.00");
		double longestYStringLength =  currentFontMeasurements.stringWidth(ScientificNotation.withoutTimesTen(
				axesRanges.yAxisMaximumValue, axesPowers.yAxisPower, metaData.yAxisDecimalPlaces));
		double yMinStringLength =  currentFontMeasurements.stringWidth(ScientificNotation.withoutTimesTen(
				axesRanges.yAxisMinimumValue, axesPowers.yAxisPower, metaData.yAxisDecimalPlaces));
		if(yMinStringLength > longestYStringLength)
		{
			longestYStringLength = yMinStringLength;
		}
		
		output.append(ScientificNotation.WithNoErrorAndZeroPower(LATEX_EXPORT_SPACING_IN_CM + exportFontSize / 12.0 * 0.10 + (exportFontSize / 12.0) * 
				(longestYStringLength / defaultLongestYStringLength) * 0.65 )); //this spacing was just figured out by trial and error
		output.append("}\n");
		
		if(origAxesPowers.yAxisPower != 0) 
		{
			output.append("\t\\putypower{");
			output.append("$\\times10^{");
			output.append(origAxesPowers.yAxisPower);
			output.append("}$}{");
			output.append(ScientificNotation.WithNoErrorAndZeroPower(axesRanges.xAxisMinimumValue / xAxisPowerMultiplier));
			output.append("}{");
			output.append(ScientificNotation.WithNoErrorAndZeroPower(axesRanges.yAxisMaximumValue / yAxisPowerMultiplier));
			output.append("}{");
			output.append(LATEX_EXPORT_SPACING_IN_CM);
			output.append("}\n");
		}
		output.append("\t\\linefitgraphend\n}");
		
		//moved down here so we dont have to import but can just copy the whole thing
		output.append("\n\\begin{figure}\n\\begin{center}\n");			
		output.append("\\scalebox{1}{\\box0}\n\\caption{");
		output.append(graphName);
		output.append("}\n\\end{center}\n\\end{figure}\n");
	}
		
	
	/** Converts the given length in inches to pixel length - used to determine the size of PDF when we export it
	 * @param inchesToConvert The number of inches to convert to equivalent amount of pixels
	 * @return The number of pixels that are equivalent to the inputed number of inches */
	public static int inchesToPixels(double inchesToConvert) 
	{
		double converter = 72;
		return (int)(converter * inchesToConvert);
	}
	
	/** Converts the given length in centimeters to pixel length - used to determine the size of PDF when we export it
	 * @param centimetersToConvert The number of centimeters to convert to equivalent amount of pixels
	 * @return The number of pixels that are equivalent to the inputed number of centimeters */
	public static int cmToPixels(double centimetersToConvert)
	{
		return inchesToPixels(centimetersToConvert / 2.54);
	}
}
