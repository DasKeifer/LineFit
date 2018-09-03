package linefit;

public enum DataDimension
{
    X("x"), Y("y");

    private static final class StaticFields
    {
        static int numDimensions = 0;
    }

    /** The String that will be displayed when toString is called and the String that shows up in the drop down to
     * select the FitType */
    private final String display;

    private final char errorChar = '\u03B4';

    private final int index;

    /** The constructor of this enum that is used to initialize its members and assign their values
     * 
     * @param displayString The name of the FitType that will be displayed in the GUI */
    DataDimension(String displayString)
    {
        this.display = displayString;
        index = StaticFields.numDimensions++;
    }

    public static int getNumberOfDimensions()
    {
        return StaticFields.numDimensions;
    }

    /** Gets the display string of this FitType for display in the GUI
     * 
     * @return Returns a String that contains this FitData's display name */
    public String getDisplayString()
    {
        return display;
    }

    /** Gets the display string of this FitType for display in the GUI
     * 
     * @return Returns a String that contains this FitData's display name */
    public String getErrorDisplayString()
    {
        return errorChar + display;
    }

    /** Gets the display string of this FitType for display in the GUI
     * 
     * @return Returns a String that contains this FitData's display name */
    public int getColumnIndex()
    {
        return index;
    }
    
    /** Gets the display string of this FitType for display in the GUI
     * 
     * @return Returns a String that contains this FitData's display name */
    public int getErrorColumnIndex()
    {
        return index + StaticFields.numDimensions;
    }
}
