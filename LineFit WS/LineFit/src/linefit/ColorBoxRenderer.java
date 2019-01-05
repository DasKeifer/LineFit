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


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


/** This class renders the square of color instead of a string in the drop down menu for selecting the line's color and
 * renders a custom color option that users can select if none of the defined colors are suitable or more color options
 * are needed
 * 
 * @author Keith Rice
 * @version 2.0
 * @since &lt;0.98.0 */
class ColorBoxRenderer extends JLabel implements ListCellRenderer<Object>
{
    /** The current serial version UID that changes when the interface of the class is changed */
    private final static long serialVersionUID = 42;

    /** The Dimensions of our rendering space in the drop down menu */
    private Dimension ourDimensions;
    /** The graphics we are using to draw the drop down box */
    private Graphics2D g2d;
    /** The Color that is current selected in the drop down menu */
    private Color color;
    /** Defines the color we are using as reserved for the custom color option. Note that this just prevents the color
     * from appearing in the drop down and does not actually prevent the color from being selected */
    public static final Color RESERVED_FOR_CUSTOM_COLOR = new Color(1, 2, 3);
    /** The custom color to render the "custom" text with */
    private Color customColor = Color.BLACK;

    /** A somewhat arbitrary number that is used to determine whether to give the "custom" field a light or dark
     * background */
    private static final int TOO_LIGHT_VALUE = 210;

    /** The default constructor of the Renderer */
    ColorBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    /** Sets the custom color to use for the "custom" text to the passed value
     * 
     * @param color The color to render the text with */
    void setCustomColor(Color color)
    {
        customColor = color;
    }

    /** Makes it paint a square of color instead of a string or render the custom text with the current custom color
     * 
     * @param g The graphics that are being used for the painting */
    public void paint(Graphics g)
    {
        if (color != RESERVED_FOR_CUSTOM_COLOR)
        {
            g2d = (Graphics2D) g;
            ourDimensions = getSize();
            Rectangle2D.Double background = new Rectangle2D.Double(0, 0, ourDimensions.width, ourDimensions.height);
            g2d.setColor(color);
            g2d.fill(background);
        }
        else
        {
            // determine whether to use a light or dark background
            if ((customColor.getRed() > TOO_LIGHT_VALUE || customColor.getGreen() > TOO_LIGHT_VALUE || customColor
                    .getBlue() > TOO_LIGHT_VALUE))
            {
                setBackground(Color.DARK_GRAY);
            }
            else
            {
                setBackground(Color.WHITE);
            }

            // set the text color and the text
            setForeground(customColor);
            setText("Custom");

            // Call the parent paint so the text is drawn
            super.paint(g);
        }

    }

    /** This method finds the image and text corresponding to the selected value and returns the label, set up to
     * display the text and image
     * 
     * @param list unused
     * @param value The selected color from the menu used to set the renderer's color
     * @param index unused
     * @param isSelected unused
     * @param cellHasFocus unused */
    @SuppressWarnings("rawtypes")
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        // set the current color to the selected color and the text to empty or else it will render incorrectly
        color = (Color) value;
        setText(" ");

        return this;
    }
}
