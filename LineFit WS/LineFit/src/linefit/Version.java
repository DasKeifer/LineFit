/* Copyright (C) 2013 Covenant College Physics Department This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see http://www.gnu.org/licenses/. */
package linefit;

/** This class serves as a Macro for the current version number of LineFit
 * 
 * @author Unknown
 * @version 1.0
 * @since &lt;0.98.0 */
public class Version
{
    /** The current LineFit's major version number that should only be updated when very significant changes are
     * made&#46; It should be expected that a new major version can break backwards compatibility&#46; It must be
     * updated manually */
    public static final int LINEFIT_MAJOR_VERSION = 0;
    /** The current LineFit's minor version number that should only be updated when new features or important bug fixes
     * are made&#46; These changes should rarely if ever break backwards compatibility&#46; It must be updated
     * manually */
    public static final int LINEFIT_MINOR_VERSION = 99;
    /** The current LineFit's increment version number that should be updated between minor versions when bugfixes or
     * smaller features are added&#46; Mainly used for development and testing reasons&#46; These changes should never
     * break backwards compatibility&#46; It must be updated manually */
    public static final int LINEFIT_INCREMENT_VERSION = 0;

    /** The current LineFit version number */
    public static final String LINEFIT_VERSION = LINEFIT_MAJOR_VERSION + "." + LINEFIT_MINOR_VERSION + "." +
            LINEFIT_INCREMENT_VERSION;

    /** The current LineFit file format's major version number that should only be updated when very significant changes
     * are made or backwards compatibility is broken &#46; It must be updated manually */
    public static final int LINEFIT_FILE_FORMAT_MAJOR_VERSION = 1;
    /** The current LineFit file format's minor version number that should only be updated when new features are added
     * that do not break backwards compatibility&#46; It must be updated manually */
    public static final int LINEFIT_FILE_FORMAT_MINOR_VERSION = 1;

    /** The current LineFit file version */
    public static final String LINEFIT_FILE_FORMAT_VERSION = LINEFIT_FILE_FORMAT_MAJOR_VERSION + "." +
            LINEFIT_FILE_FORMAT_MINOR_VERSION;
}
