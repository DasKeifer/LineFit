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


/** This class renders the square of color instead of a string in the drop down menu for selecting the line's color
 * 
 * @author Unknown
 * @version 1.1
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
    /** Defines the color we are using as reserved for the custom color option. We actually just use null for this so
     * every color is allowed */
    public static final Color RESERVED_FOR_CUSTOM_COLOR = null;
    private Color customColor = Color.BLACK;
    private static final int TOO_LIGHT_VALUE = 210;

    /** The default constructor of the Renderer */
    ColorBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    void setCustomColor(Color color)
    {
        customColor = color;
    }

    /** Makes it paint a square of color instead of a string */
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
            if (customColor != null && (customColor.getRed() > TOO_LIGHT_VALUE || customColor
                    .getGreen() > TOO_LIGHT_VALUE || customColor.getBlue() > TOO_LIGHT_VALUE))
            {
                setBackground(Color.DARK_GRAY);
            }
            else
            {
                setBackground(Color.WHITE);
            }
            setForeground(customColor);
            setText("Custom");

            // just let it do what we normally do so it is text
            super.paint(g);
        }

    }

    /** This method finds the image and text corresponding to the selected value and returns the label, set up to
     * display the text and image */
    @SuppressWarnings("rawtypes")
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        color = (Color) value;
        setText(" ");

        return this;
    }
}
