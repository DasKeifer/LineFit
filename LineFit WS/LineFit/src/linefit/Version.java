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


/** This class serves as a Macro for the current version number of LineFit
 * 
 * @author Keith Rice
 * @version 2.0
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
    public static final int LINEFIT_FILE_FORMAT_MAJOR_VERSION = 2;
    /** The current LineFit file format's minor version number that should only be updated when new features are added
     * that do not break backwards compatibility&#46; It must be updated manually */
    public static final int LINEFIT_FILE_FORMAT_MINOR_VERSION = 0;

    /** The current LineFit file version */
    public static final String LINEFIT_FILE_FORMAT_VERSION = LINEFIT_FILE_FORMAT_MAJOR_VERSION + "." +
            LINEFIT_FILE_FORMAT_MINOR_VERSION;


    /** Enum class for the return value of version comparisons */
    public enum VersionComparisonResult
    {
        // Having these separate allow for finer granularity response if desired
        INCREMENT_OLDER(-3), MINOR_OLDER(-2), MAJOR_OLDER(-1), SAME(0), MAJOR_NEWER(1), MINOR_NEWER(2), INCREMENT_NEWER(
                3), BAD_VERSION(999);

        /** The internal value of the enum */
        private final int value;

        /** Constructor for constructing the enum values
         * 
         * @param value value of the enum */
        private VersionComparisonResult(int value)
        {
            this.value = value;
        }

        /** returns true if the result specifies the versions are the same
         * 
         * @return true if the versions were the same */
        public boolean isSameVersion()
        {
            return value == SAME.value;
        }

        /** returns true if the result specifies the version compared to this LineFit's version was newer
         * 
         * @return true if the version compared to this LineFit's version was newer */
        public boolean isNewerVersion()
        {
            return value > 0 && !isBadComparison();
        }

        /** returns true if the result specifies the version compared to this LineFit's version was older
         * 
         * @return true if the version compared to this LineFit's version was older */
        public boolean isOlderVersion()
        {
            // currently bad are positive but its good practice in case another enum value is ever added or the bad
            // values are changed to be negative
            return value < 0 && !isBadComparison();
        }

        /** returns true if the result specifies the version compared to this LineFit's version was not a properly
         * formatted version
         * 
         * @return true if the version compared to this LineFit's version was not a properly formatted version */
        public boolean isBadComparison()
        {
            return value == BAD_VERSION.value;
        }
    }

    /** Checks the passed LineFit version number string against the current LineFit version to see if it is the same,
     * newer, or older.
     * 
     * @param version The string containing the version number as text
     * @return int representing the relationship with negative meaning it is an older version, positive meaning a newer
     *         version, and 0 meaning the same version. If the versions are not equal it will return the number that
     *         does not match up (i.e. 2 (or -2) if the minor versions do not match) */
    public static VersionComparisonResult checkLineFitVersionString(String version)
    {
        String[] versionParts = version.split("\\.");
        try
        {
            int majorVersion = Integer.parseInt(versionParts[0].trim());
            int minorVersion = Integer.parseInt(versionParts[1].trim());
            int incrementVersion = Integer.parseInt(versionParts[2].trim());

            if (majorVersion == LINEFIT_MAJOR_VERSION)
            {
                if (minorVersion == LINEFIT_MINOR_VERSION)
                {
                    if (incrementVersion == LINEFIT_INCREMENT_VERSION)
                    {
                        return VersionComparisonResult.SAME;
                    }
                    else if (incrementVersion < LINEFIT_INCREMENT_VERSION)
                    {
                        return VersionComparisonResult.INCREMENT_OLDER;
                    }
                    else
                    {
                        return VersionComparisonResult.INCREMENT_NEWER;
                    }
                }
                else if (minorVersion < LINEFIT_MINOR_VERSION)
                {
                    return VersionComparisonResult.MINOR_OLDER;
                }
                else
                {
                    return VersionComparisonResult.MAJOR_NEWER;
                }
            }
            else if (majorVersion < LINEFIT_MAJOR_VERSION)
            {
                return VersionComparisonResult.MAJOR_OLDER;
            }
            else
            {
                return VersionComparisonResult.MAJOR_NEWER;
            }
        }
        catch (NumberFormatException nfe)
        {
            return VersionComparisonResult.BAD_VERSION;
        }
        catch (IndexOutOfBoundsException iobe)
        {
            return VersionComparisonResult.BAD_VERSION;
        }
    }

    /** Checks the passed LineFit FileFormat version number string against the current LineFit File Format version to
     * see if it is the same, newer, or older.
     * 
     * @param fileVersion The string containing the File Format version number as text
     * @return int representing the relationship with negative meaning it is an older version, positive meaning a newer
     *         version, and 0 meaning the same version. If the versions are not equal it will return the number that
     *         does not match up (i.e. 2 (or -2) if the minor versions do not match) */
    public static VersionComparisonResult checkLineFitFileFormatVersionString(String fileVersion)
    {
        String[] versionParts = fileVersion.split("\\.");
        try
        {
            int majorVersion = Integer.parseInt(versionParts[0].trim());
            int minorVersion = Integer.parseInt(versionParts[1].trim());

            if (majorVersion == LINEFIT_FILE_FORMAT_MAJOR_VERSION)
            {
                if (minorVersion == LINEFIT_FILE_FORMAT_MINOR_VERSION)
                {
                    return VersionComparisonResult.SAME;
                }
                else if (minorVersion < LINEFIT_FILE_FORMAT_MINOR_VERSION)
                {
                    return VersionComparisonResult.MINOR_OLDER;
                }
                else
                {
                    return VersionComparisonResult.MINOR_NEWER;
                }
            }
            else if (majorVersion < LINEFIT_FILE_FORMAT_MAJOR_VERSION)
            {
                return VersionComparisonResult.MAJOR_OLDER;
            }
            else
            {
                return VersionComparisonResult.MAJOR_NEWER;
            }
        }
        catch (NumberFormatException nfe)
        {
            return VersionComparisonResult.BAD_VERSION;
        }
        catch (IndexOutOfBoundsException iobe)
        {
            return VersionComparisonResult.BAD_VERSION;
        }
    }

    /** Checks if the passed LineFit file version string is before the version passed in
     * 
     * @param toCheck The version string to check against the passed values
     * @param majorVersion The major portion of the version of the version to check if the passed value is before
     * @param minorVersion The major portion of the version of the version to check if the passed value is before
     * @return True if the version in the string is a earlier version than the one passed in. False if it is the same or
     *         a newer version */
    public static boolean isLineFitFileVersionBefore(String toCheck, int majorVersion, int minorVersion)
    {
        String[] versionParts1 = toCheck.split("\\.");
        int toCheckMajor = Integer.parseInt(versionParts1[0].trim());
        int toCheckMinor = Integer.parseInt(versionParts1[1].trim());

        return toCheckMajor < majorVersion || (toCheckMajor == majorVersion && toCheckMinor < minorVersion);
    }
}
