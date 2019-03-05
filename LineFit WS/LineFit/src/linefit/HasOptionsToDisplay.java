/* Copyright (C) 2018 Covenant College Physics Department
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


import java.awt.Container;
import java.awt.Insets;


/** This interface should be used for classes and objects that have options that should be displayed in the GUI so that
 * the user can change them. It provides a set of standardized functions to create, position, display, apply changes,
 * etc.
 * 
 * @author Keith Rice
 * @version 1.0
 * @since 0.99.0 */
public interface HasOptionsToDisplay
{
    /** Creates the GUI elements associated with the options of this class to the default values
     * 
     * @param contentPane The Container to add the elements to */
    public void createOptionsGuiElements(Container contentPane);

    /** Positions the GUI elements appropriately using the passed offsets to position it so it fits well with any other
     * classes that are displaying GUI elements
     * 
     * @param insets The Insets of the Container the GUI elements reside in
     * @param xOffset The x offset to apply to the GUI elements so that they fit with any other GUI elements being
     *        displayed by other classes
     * @param yOffset The y offset to apply to the GUI elements so that they fit with any other GUI elements being
     *        displayed by other classes */
    public void positionOptionsGuiElements(Insets insets, int xOffset, int yOffset);

    /** Resets the GUI elements associated with the options of this class to the default values */
    public void resetOptionsGuiElementsToDefaultValues();

    /** Applies the current values in the GUI options */
    public void applyValuesInOptionsGuiElements();

    /** Adds the GUI options to the traversal policy for tabs for the Container
     * 
     * @param policy The tab policy to add the GUI options to */
    public void addOptionsGuiElementsToTabs(TabsFocusTraversalPolicy policy);
}
