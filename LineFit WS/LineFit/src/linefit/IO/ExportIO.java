package linefit.IO;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import linefit.LineFit;
import linefit.GraphArea;
import linefit.ScientificNotation;

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
	public double PDFPageWidth = DEFAULT_PDF_WIDTH;
	/** The height of the PDF image when exported */
	public double PDFPageHeight = DEFAULT_PDF_HEIGHT;
	
	/** The LaTex Export spacing in cm of */
	public final static double LATEX_EXPORT_SPACING_IN_CM = 0.35;

	//Exporting Variables
	/** The desired width of the graph when it is exported to LaTex */
	public double LaTexGraphWidthInCm = 15;
	/** The desired height of the graph when it is exported to LaTex */
	public double LaTexGraphHeightInCm = 15;
	
	/** Allows the user to change the font size on any exported image of the graph */
	public float exportFontSize = 12;

	public ExportIO(GeneralIO parentIO, JFrame frameToCenterOn, GraphArea graphToExport)
	{
		generalIO = parentIO;
		toCenterOn = frameToCenterOn;
		graphingArea = graphToExport;
	}

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
			Dimension pdfDim = new Dimension(inchesToPixels(PDFPageWidth), inchesToPixels(PDFPageHeight));
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
				graphingArea.recursivelyGenerateLaTexExportString(output);
				
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
