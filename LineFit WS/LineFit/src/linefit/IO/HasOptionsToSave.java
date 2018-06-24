package linefit.IO;

import java.util.ArrayList;

public interface HasOptionsToSave
{
	public boolean readInOption(String lineRead);
	public void retrieveAllOptions(ArrayList<String> variableNames, ArrayList<String> variableValues);
}
