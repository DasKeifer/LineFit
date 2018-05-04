package linefit.IO;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

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
 * @since 	1.0
 */
public class ExportIO 
{
	/** The LineFit instance that this IOHandler is linked to */
	private static LineFit lineFit;
	
	//PDF display and exporting variables
	/** The default width in inches when exporting to a PDF image */
	private final static double DEFAULT_PDF_WIDTH = 8.5;
	/** The default height in inches when exporting to a PDF image */
	private final static double DEFAULT_PDF_HEIGHT = 8.5;
	/** The width of the PDF image when exported */
	public static double PDFPageWidth = DEFAULT_PDF_WIDTH;
	/** The height of the PDF image when exported */
	public static double PDFPageHeight = DEFAULT_PDF_HEIGHT;
	
	/** The LaTex Export spacing in cm of */
	public final static double LATEX_EXPORT_SPACING_IN_CM = 0.35;

	//Exporting Variables
	/** The desired width of the graph when it is exported to LaTex */
	public static double LaTexGraphWidthInCm = 15;
	/** The desired height of the graph when it is exported to LaTex */
	public static double LaTexGraphHeightInCm = 15;
	
	/** Allows the user to change the font size on any exported image of the graph */
	public static float exportFontSize = 12;


	/** Creates the linefit.sty file for the user to use for LaTex exports 
	 * @param destinationFolderPath The File path to create the .sty file at. This must contain the ending "\\" to denote a folder
	 */
	static void createLineFitStyFile(String destinationFolderPath) 
	{
		GeneralIO.copyResourceFileToContainingFolder("linefit.sty", destinationFolderPath);
	}

	/** Saves/exports the current graph area as a PDF image */
	public static void exportPDF()
	{
		File outputFile = promptUserToSelectFileForSaving(".pdf");
		if(outputFile != null)
		{
			Dimension pdfDim = new Dimension(inchesToPixels(PDFPageWidth), inchesToPixels(PDFPageHeight));
			Document document = new Document(new com.itextpdf.text.Rectangle(pdfDim.width, pdfDim.height));
			try 
			{
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
			    document.open();
			    
			    Graphics2D g2 = new PdfGraphics2D(writer.getDirectContent(), pdfDim.width, pdfDim.height);
				
			    lineFit.drawGraphForExport(g2, pdfDim);
				
			    g2.dispose(); 
				JOptionPane.showMessageDialog(lineFit, "File Successfully Exported as a PDF Image", "File Exported", JOptionPane.INFORMATION_MESSAGE);
			} 
			catch (FileNotFoundException e) 
			{
				JOptionPane.showMessageDialog(lineFit, "Could not find specified file or it is already in use by another program : Process aborted",
					    "FNF Error", JOptionPane.ERROR_MESSAGE);
			} 
			catch (DocumentException e) 
			{
				JOptionPane.showMessageDialog(lineFit, "Exception occured while exporting to PDF : Process aborted",
					    "Document Error", JOptionPane.ERROR_MESSAGE);
			} 
		    document.close();
		}
	}

	/** Saves/exports the LineFit graph as a JPG image */
	public static void exportJPG()
	{
		File outputFile = promptUserToSelectFileForSaving(".jpg");
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
			lineFit.drawGraphForExport(g2d, d);
			
			// Graphics context no longer needed so dispose it
			g2d.dispose();
			
			//now save it
			try
			{
				ImageIO.write(bufferedImage, "jpg", outputFile);
				JOptionPane.showMessageDialog(lineFit, "File Successfully Exported as a JPG Image", "File Exported", JOptionPane.INFORMATION_MESSAGE);
			} 
			catch (IOException error2) 
			{
				JOptionPane.showMessageDialog(lineFit, "Exception occured while exporting to JPG : Process aborted", "IO Error", JOptionPane.ERROR_MESSAGE);
			} 
		}
	}

	/** Saves/Exports the current graph area as a LaTex linefit graph */
	public static void exportLaTex()
	{
		File outputFile = promptUserToSelectFileForSaving(".tex");
		if(outputFile != null)
		{		
			try 
			{
				PrintStream outputStream = new PrintStream(outputFile);
				
				StringBuilder output = new StringBuilder();
				
				//kick it off!
				lineFit.initiateLaTexExportStringGeneration(output);
				
				outputStream.print(output.toString());
				outputStream.close();
	
				JOptionPane.showMessageDialog(lineFit, "File Successfully Exported as a LaTex LineFit Graph",
					    "File Exported", JOptionPane.INFORMATION_MESSAGE);
			} 
			catch (IOException error) 
			{
				JOptionPane.showMessageDialog(lineFit, "Exception occured while saving as a LaTex file : Process aborted",
					    "IO Error", JOptionPane.ERROR_MESSAGE);
			}
			
			//create the .sty file
			String pathToFolder = outputFile.getParent();
			createLineFitStyFile(pathToFolder + "\\\\");
		}
	}
	
	/** Opens a prompt that allows users to select a file that is then returned
	 * 
	 * @param extensionToPutOnFile The extension that the file will be saved with
	 * @return The file that the user has selected or null if the operation was canceled
	 */
	private static File promptUserToSelectFileForSaving(String extensionToPutOnFile)
	{
		try
		{
			File outputFile = GeneralIO.showSaveFileDialog(extensionToPutOnFile);
			GeneralIO.storeCurrentDirectory();
			return outputFile;
		}
		catch (NullPointerException npe) 
		{
			JOptionPane.showMessageDialog(lineFit, "An invalid null value occured : Process aborted", "Null Error", JOptionPane.ERROR_MESSAGE);
		} 
		catch (CloseDialogException e) {} //the error we throw if we selected cancel
		return null;
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
	
	/** Sets the LineFit object that this IO Handler is attached to 
	 * @param associatedWith The instance of LineFit to associate the static IOHandler class with */
	static void assocaiteWithLineFit(LineFit associatedWith)
	{
		lineFit = associatedWith;
	}
}
