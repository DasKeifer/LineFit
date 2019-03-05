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


import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;


/** This class contains some functionality related to other LineFit classes that either are used in multiple classes or
 * potentially could be.
 * 
 * @author Keith Rice
 * @version 1.0
 * @since 0.99.0 */
public class Utils
{
    /** Creates a menu item, adds the keystroke shortcut to it, adds it to the menu, and finally adds the action
     * listener to it. If either the keystroke or action listener are null, then it will skip adding them.
     * 
     * @param menuToAddTo The JMenu to add the menu item to
     * @param itemText The text that will appear on the menu item
     * @param itemActionListener The action listener that is called when the menu item is clicked
     * @param itemShortcut The keyboard shortcut to use for this menu item or null to have no shortcut. It will show up
     *        on the right side of the menu item in a different font. */
    public static void createAndAddMenuItem(JMenu menuToAddTo, String itemText, ActionListener itemActionListener,
            String itemShortcut)
    {
        JMenuItem menuItem = new JMenuItem(itemText);

        if (itemShortcut != null)
        {
            KeyStroke menuKS = KeyStroke.getKeyStroke(itemShortcut);
            menuItem.setAccelerator(menuKS);
        }

        menuToAddTo.add(menuItem);
        menuItem.addActionListener(itemActionListener);
    }

    /** Inlays the component in another GUI item, positioning it vertically based on that item and horizontally based on
     * the passed item which can be the same as the item it is inlayed in.
     * 
     * @param springLayout The SpringLayout that is being used to layout the component
     * @param panelToInlayIn The JPanel to inlay the component in
     * @param toInlay The Component to inlay
     * @param alignToLeftSide True if the component is aligned based on the left side of the passed object to align
     *        horizontally off of
     * @param leftRightObjToAlignTo The component to align horizontally to
     * @param leftOffset The number of pixels to offset the inlayed component's left side to the component that it is
     *        being horizontally aligned to
     * @param rightOffset The number of pixels to offset the inlayed component's right side to the component that it is
     *        being horizontally aligned to
     * @param topOffset The number of pixels to offset the inlayed component's top from the panel it is being inlayed in
     * @param bottomOffset The number of pixels to offset the inlayed component's bottom from the panel it is being
     *        inlayed in */
    public static void inlayGuiItem(SpringLayout springLayout, JPanel panelToInlayIn, Component toInlay,
            boolean alignToLeftSide, Component leftRightObjToAlignTo, int leftOffset, int rightOffset, int topOffset,
            int bottomOffset)
    {
        String rightLeftSideAlign = SpringLayout.WEST;
        if (!alignToLeftSide)
        {
            rightLeftSideAlign = SpringLayout.EAST;
        }

        springLayout.putConstraint(SpringLayout.WEST, toInlay, leftOffset, rightLeftSideAlign, leftRightObjToAlignTo);
        springLayout.putConstraint(SpringLayout.EAST, toInlay, rightOffset, rightLeftSideAlign, leftRightObjToAlignTo);
        springLayout.putConstraint(SpringLayout.NORTH, toInlay, topOffset, SpringLayout.NORTH, panelToInlayIn);
        springLayout.putConstraint(SpringLayout.SOUTH, toInlay, bottomOffset, SpringLayout.SOUTH, panelToInlayIn);

        panelToInlayIn.add(toInlay);
    }
}
