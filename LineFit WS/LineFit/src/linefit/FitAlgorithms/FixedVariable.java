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

package linefit.FitAlgorithms;


/** This enumeration is used to keep track of what variable is fixed in the LinearFitData we have for a given DataSet
 * 
 * <ul>
 * <li>0 - Nothing is fixed. Calculate both the slope and the intercept
 * <li>1 - The slope is fixed. Calculate only the intercept
 * <li>2 - The intercept is fixed. Calculate only the slope
 * </ul>
 * 
 * @author Keith Rice
 * @version 1.0
 * @since 0.98.1 */
public enum FixedVariable
{
    NONE("None"), SLOPE("Slope"), INTERCEPT("Intercept");

    /** The String that is displayed when the toString is called on the enumeration so it is displayed this way */
    private final String display;

    /** The constructor of this enum that is used to initialize its members and assign their values
     * 
     * @param displayString The name of the FixedVariables that will be displayed */
    FixedVariable(String displayString)
    {
        this.display = displayString;
    }

    /** Gets the display string of this FixedVariables for display in the GUI
     * 
     * @return Returns a String that contains this FixedVariable's display name */
    public String getDisplayString()
    {
        return display;
    }

    /** This overrides the to String method so that it will show the display string instead of the Enum name */
    @Override
    public String toString()
    {
        return getDisplayString();
    }
}
