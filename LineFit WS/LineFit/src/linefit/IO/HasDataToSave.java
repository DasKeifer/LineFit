package linefit.IO;

import java.util.ArrayList;

public interface HasDataToSave
{
	public boolean readInDataAndDataOptions(String line, boolean newDataSet);
	public void retrieveAllDataAndDataOptions(ArrayList<String> variableNames, ArrayList<String> variableValues);
}
