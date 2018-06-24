package linefit;

import java.awt.Container;

public interface HasOptionsToDisplay
{
	public void createOptionsGuiElements(Container contentPane);
	public void resetOptionsGuiElementsToDefaultValues();
	public void applyValuesInOptionsGuiElements();
	
	public void addOptionsGuiElementsToTabs(TabsFocusTraversalPolicy policy);
}
