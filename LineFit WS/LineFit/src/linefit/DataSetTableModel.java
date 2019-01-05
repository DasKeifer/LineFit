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


import java.util.Vector;

import javax.swing.table.DefaultTableModel;


/** This class Creates the Table Model for the GraphSet's data and controls the behavior for the GraphSet data table so
 * that we can remove columns and see if it is empty
 * 
 * @author Unknown, Keith Rice
 * @version 1.1
 * @since &lt;0.98.0 */
class DataSetTableModel extends DefaultTableModel
{
    /** The Serial Version UID so that we know what version it is when we are using it. See
     * http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html for full discussion on its uses and purpose */
    private final static long serialVersionUID = 42L;

    /** Removes the last column from the table */
    void removeLastColumn()
    {
        removeColumn(getColumnCount() - 1);
    }

    /** Removes the column with the given index from the table
     * 
     * @param columnIndex The index of the column to remove */
    void removeColumn(int columnIndex)
    {
        for (int r = 0; r < getRowCount(); r++)
        {
            Vector<?> row = (Vector<?>) dataVector.elementAt(r);
            row.removeElementAt(columnIndex);
        }

        columnIdentifiers.removeElementAt(columnIndex);
        fireTableStructureChanged();
    }

    /** Checks to see if we have data in our table. Returns true if there is data in the table
     * 
     * @return Returns a boolean that represents whether or not the table has data */
    boolean hasData()
    {
        for (int i = 0; i < getColumnCount(); i++)
        {
            for (int j = 0; j < getRowCount(); j++)
            {
                if (getValueAt(j, i) != null)
                {
                    return true;
                }
            }
        }

        return false;
    }
}