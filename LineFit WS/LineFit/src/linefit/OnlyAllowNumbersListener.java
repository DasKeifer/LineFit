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


import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;


/** A private Listener class that implements FocusListener so that we can save the value when the field is first focused
 * on so that we can return it to the field's preselected value if the input was not a valid number String.
 * 
 * @author Keith Rice
 * @version 1.0
 * @since &lt;0.98.0 */
public class OnlyAllowNumbersListener implements FocusListener
{
    /** This String keeps track of what value was in the currently selected field at the moment it was first selected.
     * This lets us revert the field to its previous value if an invalid number string is inputed. This is only used for
     * places where only numbers are allowed */
    private String inputFieldValueOnFocused;

    @Override
    /** Saves the current value of the field in another location so we can revert back to it if we want later
     * 
     * @param passedFocusEvent The Focus Event that is passed to this function by the system when the field is focused
     *        on */
    public void focusGained(FocusEvent passedFocusEvent)
    {
        try
        {
            JTextField focusedField = (JTextField) passedFocusEvent.getSource();
            inputFieldValueOnFocused = focusedField.getText();
        }
        catch (ClassCastException cce)
        {
            inputFieldValueOnFocused = "0";
        }
    }

    @Override
    /** Checks when we lose the focus to see if the input was a valid number String ans if not then it will revert it
     * back to the original String
     * 
     * @param passedFocusEvent The FocusEvent that is passed to this funtion by the system when the field loses the
     *        focus */
    public void focusLost(FocusEvent passedFocusEvent)
    {
        try
        {
            JTextField focusedField = (JTextField) passedFocusEvent.getSource();
            if (!doesFieldContainValidDoubleString(focusedField))
            {
                focusedField.setText(inputFieldValueOnFocused);
            }
        }
        catch (ClassCastException cce)
        {
        }
    }

    /** Checks the String value in the given field to see if it contains a valid double as a String
     * 
     * @param fieldToCheckForNumber The text field that will be checked to see whether or not it contains a double
     *        number as a String
     * @return A boolean of whether or not the passed TextField has a valid double as a String in it */
    private boolean doesFieldContainValidDoubleString(JTextField fieldToCheckForNumber)
    {
        try
        {
            Double.parseDouble(fieldToCheckForNumber.getText());
            // also check for d and f at the end since it will parse it as doubles and floats respectively
            if (fieldToCheckForNumber.getText().toLowerCase().endsWith("d") || fieldToCheckForNumber.getText()
                    .toLowerCase().endsWith("f"))
            {
                return false;
            }
            return true;
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
    }
}
