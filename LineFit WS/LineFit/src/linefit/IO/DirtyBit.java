/*
Copyright (C) 2013  Covenant College Physics Department

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see http://www.gnu.org/licenses/.
*/
package linefit.IO;

/**
 * a class that keeps track of whether or not the current graph information has been saved
 * it is dirty when changes have been made after the last time it was saved (unsaved changes)
 * 
 * @author	Don Petcher
 * @version	1.0
 * @since 	&lt;0.98.0
 */
public class DirtyBit 
{	
	/**The boolean that keeps track of whether or not all the changes have been saved&#46; 
	 * Dirty means that there are unsaved changes */
	private static boolean isDirty = false;
	
	/**
	 * Sets the dirty bit to dirty, meaning that there are unsaved changes
	 */
	public static void setDirty() 
	{
		isDirty = true;
	}
	
	/**
	 * Sets the dirty bit to clean, meaning that all changes have been saved
	 */
	static void setClean() 
	{
		isDirty = false;
	}
	
	/**
	 * Returns whether or not LineFit is "dirty" meaning there are unsaved changes
	 * @return Whether or not LineFit is "dirty". True means that there are unsaved changes that have been detected
	 */
	static boolean isDirty()
	{
		return isDirty;
	}
}