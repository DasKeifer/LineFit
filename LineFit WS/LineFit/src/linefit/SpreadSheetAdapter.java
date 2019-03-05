/* Copyright (C) 2013 Covenant College Physics Department
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

package linefit;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;


// Maybe add mouse functionality like in:
// http://stackoverflow.com/questions/22622973/jtable-copy-and-paste-using-clipboard-and-abstractaction

/** ExcelAdapter enables Copy-Paste Clipboard functionality on JTables. The clipboard data format used by the adapter is
 * compatible with the clipboard format used by Excel. This provides for clipboard interoperability between enabled
 * JTables and Excel.
 * 
 * This code is based on the ExcelAdapter from JavaWorld and is used per their (IDG) copyright policy as of 2015 and has
 * since been modified by Covenant College Students to work with other platforms
 * 
 * http://www.javaworld.com/article/2077579/learn-java/java-tip-77--enable-copy-and-paste-functionality-between-swing-s-jtables-and-excel.html
 * 
 * Allowed use of source code from http://www.javaworld.com/about/copyright.html on 2/16/2015 copied below for
 * convenience: Permissions Content derived from JavaWorld may be reproduced in print or displayed online and
 * distributed, for free, in limited quantities for nonprofit, educational purposes with proper attribution. IDG retains
 * the copyright for any such use. Any use of whole or partial JavaWorld content intended to endorse a product or for
 * other commercial use must be approved by a JavaWorld editor. Requests will be acted on quickly. For any commercial
 * reproduction of JavaWorld content, print or online, you must purchase a reprint.
 * 
 * @author Ashok Banerjee, Jignesh Mehta and Keith Rice
 * @version 1.1
 * @since &lt;0.98.0 */
public class SpreadSheetAdapter implements ActionListener
{
    private Clipboard clipboard;
    private JTable tableActedOn;

    /** Creates the Adapter based on the JTable that it listens to in order to add copy, paste, etc. functionalities too
     * 
     * @param tableToListenOn The table that this will listen to */
    public SpreadSheetAdapter(JTable tableToListenOn)
    {
        tableActedOn = tableToListenOn;

        // Set up the keystrokes to use for the commands so it activates the listener
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),
                false);
        KeyStroke cut = KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),
                false);
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),
                false);
        KeyStroke selectAll = KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask(), false);
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);

        // Now register them to the listener
        tableActedOn.registerKeyboardAction(this, "copy", copy, JComponent.WHEN_FOCUSED);
        tableActedOn.registerKeyboardAction(this, "cut", cut, JComponent.WHEN_FOCUSED);
        tableActedOn.registerKeyboardAction(this, "paste", paste, JComponent.WHEN_FOCUSED);
        tableActedOn.registerKeyboardAction(this, "all", selectAll, JComponent.WHEN_FOCUSED);
        tableActedOn.registerKeyboardAction(this, "delete", delete, JComponent.WHEN_FOCUSED);

        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /** Copies the data so that it is in the standard spreadsheet format */
    public void copy()
    {
        // Check to ensure we have selected only a contiguous block of cells
        int numberOfColumns = tableActedOn.getSelectedColumnCount();
        int numberOfRows = tableActedOn.getSelectedRowCount();
        int[] rowsselected = tableActedOn.getSelectedRows();
        int[] colsselected = tableActedOn.getSelectedColumns();

        if (!((numberOfRows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] &&
                numberOfRows == rowsselected.length) && (numberOfColumns - 1 == colsselected[colsselected.length - 1] -
                        colsselected[0] && numberOfColumns == colsselected.length)))
        {
            JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuffer copiedCellsAsStrings = new StringBuffer();
        for (int currentRow = 0; currentRow < numberOfRows; currentRow++)
        {
            for (int currentColumn = 0; currentColumn < numberOfColumns; currentColumn++)
            {
                copiedCellsAsStrings.append(tableActedOn.getValueAt(rowsselected[currentRow],
                        colsselected[currentColumn]));
                if (currentColumn < numberOfColumns - 1)
                {
                    copiedCellsAsStrings.append("\t");
                }
            }
            copiedCellsAsStrings.append("\n");
        }
        StringSelection selectedCellsAsString = new StringSelection(copiedCellsAsStrings.toString());
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selectedCellsAsString, selectedCellsAsString);
    }

    /** Pastes the data from our clipboard into our table */
    public void paste()
    {
        int startRow = (tableActedOn.getSelectedRows())[0];
        int startCol = (tableActedOn.getSelectedColumns())[0];

        try
        {
            String trstring = (String) (clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
            System.out.println("String is:\n" + trstring);

            String newLine;
            if (CheckOS.isMacOSX())
            {
                newLine = "\r\n";
            }
            else
            {
                newLine = "\n";
            }
            StringTokenizer st1 = new StringTokenizer(trstring, newLine);
            boolean firstTime = true;
            for (int i = 0; st1.hasMoreTokens(); i++)
            {
                String rowstring = st1.nextToken();
                System.out.println("String at row " + i + " is " + rowstring);
                StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
                for (int j = 0; st2.hasMoreTokens(); j++)
                {
                    String value = (String) st2.nextToken();
                    int sRi = startRow + i, sCj = startCol + j; // debugging
                    System.out.println("String at row " + sRi + " and column " + sCj + " is " + value);
                    // we keep track of the first value because it does wonky things on macs and wont take it
                    if (firstTime)
                    {
                        if (startRow != 0 && startCol != 0)
                        {
                            tableActedOn.editCellAt(0, 0);
                        }
                        else
                        {
                            tableActedOn.editCellAt(1, 1);
                        }
                    }
                    if (startRow + i < tableActedOn.getRowCount() && startCol + j < tableActedOn.getColumnCount())
                    {
                        tableActedOn.setValueAt(value, startRow + i, startCol + j);
                    }
                    if (firstTime)
                    {
                        tableActedOn.editCellAt(startRow, startCol);
                        firstTime = false;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Deletes all the selected data and sets it to null so we don't read it as data */
    public void delete()
    {
        int rows[] = tableActedOn.getSelectedRows();
        int columns[] = tableActedOn.getSelectedColumns();
        for (int i = 0; i < rows.length; i++)
        {
            for (int j = 0; j < columns.length; j++)
            {
                tableActedOn.setValueAt(null, rows[i], columns[j]);
            }
        }
    }

    /** The action listener that is triggered when one of our defined keystrokes are pressed which then calls an
     * appropriate function to do the work */
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case "copy":
                copy();
                break;
            case "cut":
            {
                copy();
                delete();
                break;
            }
            case "paste":
                paste();
                break;
            case "all":
                tableActedOn.selectAll();
                break;
            case "delete":
                delete();
                break;
        }
    }
}

