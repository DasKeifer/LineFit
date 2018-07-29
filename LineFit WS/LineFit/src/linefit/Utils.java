package linefit;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;


public class Utils
{
    public static void createAndAddMenuItem(JMenu menuToAddTo, String menuTitle, String menuTitleShortcut,
            ActionListener menuActionListener)
    {
        JMenuItem menuItem = new JMenuItem(menuTitle);
        KeyStroke menuKS = KeyStroke.getKeyStroke(menuTitleShortcut);
        menuItem.setAccelerator(menuKS);

        menuToAddTo.add(menuItem);

        menuItem.addActionListener(menuActionListener);
    }

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
