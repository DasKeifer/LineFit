package linefit;

import java.awt.Container;
import java.awt.Insets;


/** This interface should be used for classes and objects that have options that should be displayed in the GUI so that
 * the user can change them. It provides a set of standardized functions to create, position, display, apply changes,
 * etc.
 * 
 * @author Das Keifer
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
     * displayed by other classes
     * @param yOffset The y offset to apply to the GUI elements so that they fit with any other GUI elements being
     * displayed by other classes */
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
