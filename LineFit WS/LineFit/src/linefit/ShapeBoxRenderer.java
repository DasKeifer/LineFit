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
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


/** The class that allows us to display a shape in the shapeSelector instead of its class name in the drop down menu
 * 
 * @author Unknown
 * @version 1.0
 * @since &lt;0.98.0 */
class ShapeBoxRenderer extends JLabel implements ListCellRenderer<Object>
{
    /** The current serial version UID that changes when the interface of the class is changed */
    private final static long serialVersionUID = 42;

    /** The Dimensions of our rendering space in the drop down menu */
    private Dimension ourDimensions;
    /** The graphics we are using to draw the drop down box */
    private Graphics2D g2d;
    /** The Shape that is currently selected in the drop down menu */
    private Shape currentShape;

    /** The Default Constructor */
    ShapeBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    /** Draws the shape on a white background in the drop down list instead of the shapes object string */
    public void paint(Graphics g)
    {
        g2d = (Graphics2D) g;
        ourDimensions = getSize();
        int shapesize = 6;

        Rectangle2D.Double background = new Rectangle2D.Double(0, 0, ourDimensions.width, ourDimensions.height);
        g2d.setColor(Color.white);
        g2d.fill(background);

        Ellipse2D.Double ellipse = new Ellipse2D.Double();
        Rectangle2D.Double rectangle = new Rectangle2D.Double();
        Polygon triangle = new Polygon();

        if (currentShape.getClass() == ellipse.getClass())
        {
            ellipse.setFrame(ourDimensions.width / 2 - shapesize / 2, ourDimensions.height / 2 - shapesize / 2,
                    shapesize, shapesize);
            currentShape = (Shape) ellipse;
        }
        else if (currentShape.getClass() == triangle.getClass())
        {
            triangle.addPoint(ourDimensions.width / 2, ourDimensions.height / 2 - shapesize / 2);
            triangle.addPoint(ourDimensions.width / 2 - shapesize / 2, ourDimensions.height / 2 + shapesize / 2);
            triangle.addPoint(ourDimensions.width / 2 + shapesize / 2, ourDimensions.height / 2 + shapesize / 2);
            currentShape = (Shape) triangle;
        }
        else
        {
            rectangle.setFrame(ourDimensions.width / 2 - shapesize / 2, ourDimensions.height / 2 - shapesize / 2,
                    shapesize, shapesize);
            currentShape = (Shape) rectangle;
        }

        g2d.setColor(Color.black);
        g2d.fill(currentShape);
    }

    /** Gets the selected shape and puts it as our current shape */
    @SuppressWarnings("rawtypes")
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        // Get the selected index. (The index param isn't
        // always valid, so just use the value.)
        currentShape = (Shape) value;
        setText(" ");

        return this;
    }
}