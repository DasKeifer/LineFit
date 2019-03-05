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


/** This class checks to see if the user is running this program on a Mac so we can fix the copy and paste error from
 * tables as well as other things specific to a Mac such as line endings
 * 
 * @author Unknown
 * @version 1.0
 * @since &lt;0.98.0 */
class CheckOS
{
    // checks if we are using a Mac because we have to do things differently sometimes
    static boolean isMacOSX()
    {
        String osName = System.getProperty("os.name");
        return osName.startsWith("Mac OS");
    }
}
