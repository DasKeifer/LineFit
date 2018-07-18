/* Copyright (C) 2013 Covenant College Physics Department This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see http://www.gnu.org/licenses/. */
package linefit.FitAlgorithms;

/** This enumeration is used to keep track of the FitType and handles its basic functions that allow it to be treated as
 * a string or a number
 * 
 * <ul>
 * <li>0 - Don't show fit
 * <li>1 - Show fit but don't factor errors into the fit
 * <li>2 - Factor horizontal errors into the fit only
 * <li>3 - Factor vertical errors into the fit only
 * <li>4 - Factor both horizontal errors and vertical errors into the fit
 * </ul>
 * 
 * @author Keith Rice
 * @version 1.1.0
 * @since &lt;0.98.0 */
public enum FitType
{
    NONE("No Fit"), REGULAR("Regular Fit"), X_ERROR("X Error Fit"), Y_ERROR("Y Error Fit"), BOTH_ERRORS(
            "X&Y Error Fit");

    /** The String that will be displayed when toString is called and the String that shows up in the drop down to
     * select the FitType */
    private final String display;

    /** The constructor of this enum that is used to initialize its members and assign their values
     * 
     * @param displayString The name of the FitType that will be displayed in the GUI */
    FitType(String displayString)
    {
        this.display = displayString;
    }

    /** Gets the display string of this FitType for display in the GUI
     * 
     * @return Returns a String that contains this FitData's display name */
    public String getDisplayString()
    {
        return display;
    }

    /** This overrides the to String method so that it will show the display string in the JComboBox instead of teh Enum
     * name */
    @Override
    public String toString()
    {
        return getDisplayString();
    }
}
