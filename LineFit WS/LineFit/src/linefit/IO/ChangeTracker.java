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

package linefit.IO;


/** A class that keeps track of whether or not the file has been modified in such a way that the save file would be
 * different then when it was last saved (i.e. are there unsaved changes)
 * 
 * @author Don Petcher and Keith Rice
 * @version 2.0
 * @since &lt;0.98.0 */
public class ChangeTracker
{
    /** The boolean that keeps track of whether or not all the changes have been saved */
    private boolean modifiedBit = false;

    /** Tells the tracker that the file has been modified such that the saved file no longer matches what is
     * displayed/set */
    public void setFileModified()
    {
        modifiedBit = true;
    }

    /** Tells the tracker to disregard any previous modifications which is generally only done when the file is saved */
    public void clearFileModified()
    {
        modifiedBit = false;
    }

    /** Returns whether or not the file has unsaved changes
     * 
     * @return True means that there are unsaved changes */
    public boolean unsavedModifications()
    {
        return modifiedBit;
    }
}