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


import java.util.ArrayList;

import linefit.DataDimension;


/** This enumeration is used to keep track of the FitType and handles its basic functions that allow it to be treated as
 * a string or a number
 * <ul>
 * <li>NONE - Don't show fit
 * <li>REGULAR - Show fit but don't factor errors into the fit
 * <li>X_ERROR - Factor horizontal errors into the fit only
 * <li>Y_ERROR - Factor vertical errors into the fit only
 * <li>BOTH_ERRORS - Factor both horizontal errors and vertical errors into the fit
 * </ul>
 * 
 * @author Keith Rice
 * @version 1.2
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

    /** Appends the appropriate fit types for the passed list of error dimensions to the passed in list
     * 
     * @param allowedFits The list to append all valid fits based on error dimensions to
     * @param dimensions The list of the valid error dimensions to add the fits for */
    public static void appendAllAllowedFitsForErrorDimensions(ArrayList<FitType> allowedFits, ArrayList<
            DataDimension> dimensions)
    {
        if (dimensions.contains(DataDimension.X))
        {
            allowedFits.add(X_ERROR);
            if (dimensions.contains(DataDimension.Y))
            {
                allowedFits.add(Y_ERROR);
                allowedFits.add(BOTH_ERRORS);
            }
        }
        else if (dimensions.contains(DataDimension.Y))
        {
            allowedFits.add(Y_ERROR);
        }
    }

    /** Gets the error dimensions required for the given fit type
     * 
     * @param fitType The fit type to get the required errors of
     * @return An array of the required data dimensions for the passed fit type */
    public static DataDimension[] getRequiredErrorDimsForFitType(FitType fitType)
    {
        DataDimension[] retVal;
        switch (fitType)
        {
            case X_ERROR:
                retVal = new DataDimension[] { DataDimension.X };
                break;

            case Y_ERROR:
                retVal = new DataDimension[] { DataDimension.Y };
                break;

            case BOTH_ERRORS:
                retVal = new DataDimension[] { DataDimension.X, DataDimension.Y };
                break;

            default:
                retVal = new DataDimension[0];
                break;
        }
        return retVal;
    }
}
