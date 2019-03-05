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


/** This class creates the string that is used in the about window
 * 
 * @author Unknown
 * @version 1.0
 * @since &lt;0.98.0 */
class About
{
    /** The String that is returned when the */
    static String about = "LineFit " + Version.LINEFIT_VERSION + "\n" + "Built with Java 1.7    \n" +
            "---------------\n" + " Contributors: \n\n" + " Don Petcher \n" + " Ben Hubbard\n" + " Nick Jenkins \n" +
            " Zach McElrath \n" + " Josiah Lewis \n" + " Paul McLain \n" + " Will Loderhose\n" + " Jeffrey Cox\n" +
            " Harrison Hicks\n" + " Keith Rice\n" +
            "\nLineFit makes use of iText in order to export PDF files and in compliance with\nthe iText license, http://itextpdf.com/agpl, has a GNU AGPL license by extension.";

    /** Gets the about information in a string
     * 
     * @return Returns the about information as a string to be displayed in the window */
    static String getAboutString()
    {
        return (about);
    }
}
