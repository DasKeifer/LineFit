package linefit;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;

/** 
 * A listener that is called when the user hits the tabs key that sets the order through which the options in the options menu 
 * are focused in
 * 
 * @author Keith Rice
 * @version	1.0
 * @since 	&lt;0.98.0
 */
public class TabsFocusTraversalPolicy extends FocusTraversalPolicy 
{
	/** The list of components that can be focused on in the Graph Options menu */
	private ArrayList<Component> components = new ArrayList<Component>();

	/**
	 * Adds the passed component into the list of components that can be focused on with the tabs key
	 * 
	 * @param component The component to add to the lists of components to be highlighted by tabs 
	 */
	public void addComponentToTabsList(Component component) 
	{
		components.add(component);
	}

	/**
	 * Find the next component in the list of the components that is not disables to be selected by the Tab key
	 */
	public Component getComponentAfter(Container aContainer, Component aComponent) 
	{
		int atIndex = components.indexOf(aComponent);
		int nextIndex = (atIndex + 1) % components.size();
		while(!components.get(nextIndex).isEnabled())
		{
			nextIndex = (nextIndex + 1) % components.size();
		}
		return components.get(nextIndex);
	}

	/**
	 * Finds the component that is before this one that is not disabled in the list to be selected by Shift+Tab
	 */
	public Component getComponentBefore(Container aContainer, Component aComponent) 
	{
		int currentIndex = components.indexOf(aComponent);
		int nextIndex = (currentIndex + components.size() - 1) % components.size();
		while(!components.get(nextIndex).isEnabled())
		{
			nextIndex = (nextIndex - 1) % components.size();
		}
		return components.get(nextIndex);
	}

	/**
	 * Gets the first component in the list of focusable components
	 */
	public Component getFirstComponent(Container aContainer) 
	{
		return components.get(0);
	}

	/**
	 * Returns the default highlighted component, in this case the first one in the list
	 */
	public Component getDefaultComponent(Container arg0) 
	{
		return components.get(0);
	}

	/**
	 * Gets the last component in the list of focusable components
	 */
	public Component getLastComponent(Container arg0) 
	{
		return components.get(components.size() - 1);
	}
}
