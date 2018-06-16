package linefit;

import java.awt.Container;

public interface HasOptionsGuiElements
{
	public void createOptionsGuiElements(Container contentPane);
	public void resetOptionsGuiElementsToDefaultValues();
	public void applyValuesInOptionsGuiElements();
	
	public void addOptionsGuiElementsToTabs(TabsFocusTraversalPolicy policy);
}
