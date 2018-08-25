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


/** This class contains helper functions that converts numbers as doubles to a string in scientific notation.
 * 
 * Note: In the examples in the functions, the following convention is used:
 * <ul>
 * <li>a = number
 * <li>b = error
 * <li>n = power
 * </ul>
 * Also, the functions round relative to the decimal place. If roundTo is negative it will round to the left of the
 * decimal point
 * 
 * @author Keith Rice
 * @version 1.1
 * @since &lt;0.98.0 */
public class ScientificNotation
{
    /** Converts the number (double) with the given error value (double) into scientific notation, rounded to the given
     * decimal place (relative to the decimal point) using ASCII characters. Returns in the form: (a ± b)×10^n
     * 
     * @param number The number to be converted to scientific notation and rounded
     * @param error The error or uncertainty that is associated with the number to be put in scientific notation
     * @param roundTo The decimal places after the decimal point to round to (negative rounds to the right of the
     *        decimal)
     * @return Returns an ASCII String the number with the given error in scientific notation rounded to the given
     *         decimal place in the following form: (a ± b)×10^n */
    public static String withError(double number, double error, int roundTo)
    {
        return withError(number, error, getPowerOf(number), roundTo);
    }

    /** Converts the number (double) with the given error value (double) into scientific notation with the ten to the
     * given power, rounded to the given decimal place (relative to the decimal point) using ASCII characters. Returns
     * in the form: (a ± b)×10^n
     * 
     * @param number The number to be converted to scientific notation and rounded
     * @param error The error or uncertainty that is associated with the number to be put in scientific notation
     * @param power The power to use for ^n in scientific notation
     * @param roundTo The decimal places after the decimal point to round to (negative rounds to the right of the
     *        decimal)
     * @return Returns an ASCII String of the number with the given error in scientific notation rounded to the given
     *         decimal place in the following form: (a ± b)×10^n */
    public static String withError(double number, double error, int power, int roundTo)
    {
        String inNotation = withoutTimesTen(number, power, roundTo);

        if (error != 0.0d)
        {
            if (power != 0)
            {
                inNotation = "(" + inNotation;
            }
            inNotation += " " + '\u00B1' + " " + withoutTimesTen(error, power, roundTo);
            if (power != 0)
            {
                inNotation += ")";
            }
        }

        inNotation += onlyTimesTen(power);
        return inNotation;
    }

    /** Converts the number (double) with the given error value (double) into scientific notation, rounded to the given
     * decimal place (relative to the decimal point) for use in LaTex. Returns in LaTex in the form: (a ± b)×10^n
     * 
     * @param number The number to be converted to scientific notation and rounded
     * @param error The error or uncertainty that is associated with the number to be put in scientific notation
     * @param roundTo The decimal places after the decimal point to round to (negative rounds to the right of the
     *        decimal)
     * @return Returns a string formatted for use in LaTex of the number with the given error in scientific notation
     *         rounded to the given decimal place in the following form: (a ± b)×10^n */
    public static String laTexWithError(double number, double error, int roundTo)
    {
        return laTexWithError(number, error, getPowerOf(number), roundTo);
    }

    /** Converts the number (double) with the given error value (double) into scientific notation with the ten to the
     * given power, rounded to the given decimal place (relative to the decimal point) for use in LaTex. Returns in
     * LaTex in the form: (a ± b)×10^n
     * 
     * @param number The number to be converted to scientific notation and rounded
     * @param error The error or uncertainty that is associated with the number to be put in scientific notation
     * @param power The power to use for ^n in scientific notation
     * @param roundTo The decimal places after the decimal point to round to (negative rounds to the right of the
     *        decimal)
     * @return Returns a string formatted for use in LaTex of the number with the given error in scientific notation and
     *         the given power rounded to the given decimal place in the following form: (a ± b)×10^n */
    public static String laTexWithError(double number, double error, int power, int roundTo)
    {
        String inNotation = withoutTimesTen(number, power, roundTo);

        if (error != 0.0d)
        {
            if (power != 0)
            {
                inNotation = "(" + inNotation;
            }
            inNotation += " $\\pm$ " + withoutTimesTen(error, power, roundTo);
            if (power != 0)
            {
                inNotation += ")";
            }
        }

        inNotation += laTexOnlyTimesTen(power);
        return inNotation;
    }

    /** Converts the number (double) with no associated error/uncertainty value into scientific notation with the ten to
     * the given power, rounded to the given decimal place (relative to the decimal point) using ASCII characters.
     * Returns in LaTex in the form: a×10^n
     * 
     * @param number The number to be converted to scientific notation and rounded
     * @param power The power to use for ^n in scientific notation
     * @param roundTo The decimal places after the decimal point to round to (negative rounds to the right of the
     *        decimal)
     * @return Returns an ASCII string of the number in scientific notation with the given power rounded to the given
     *         decimal place in the following form: a×10^n */
    public static String withNoError(double number, int power, int roundTo)
    {
        return withoutTimesTen(number, power, roundTo) + onlyTimesTen(power);
    }

    /** Converts the number (double) with no associated error/uncertainty and with no times ten to a power. This
     * prevents the conversion to a string from having an 'E' or x10 in it Returns in the form: a
     * 
     * @param number The number (double) to convert into a string
     * @return The number as a string with no power and no error */
    public static String WithNoErrorAndZeroPower(double number)
    {
        boolean isNeg = false; // so we can put the negative sign on at the end and not have it in the middle
        if (number < 0)
        {
            isNeg = true;
        }
        number = Math.abs(number);
        String numStr = Double.toString(number);
        // takes care of if the string has an E in it
        if (numStr.indexOf('E') != -1)
        { // its so small or large it converted it back to E form
            numStr = numStr.substring(0, numStr.indexOf('E'));
            int decPlacesSciNot = numStr.substring(numStr.indexOf('.') + 1).length();
            numStr = numStr.substring(0, numStr.indexOf('.')) + numStr.substring(numStr.indexOf('.') + 1);
            int itsPow = decPlacesSciNot - getPowerOf(number);

            if (itsPow <= 0)
            {
                for (int i = 0; i > itsPow; i--)
                {
                    numStr += "0";
                }
            }
            else
            {
                if (itsPow < numStr.length())
                {
                    int decPlace = numStr.length() - itsPow;
                    numStr = numStr.substring(0, decPlace) + "." + numStr.substring(decPlace);
                }
                else
                {
                    int pastLength = itsPow - numStr.length();
                    for (int i = 0; i < pastLength; i++)
                    {
                        numStr = "0" + numStr;
                    }
                    numStr = "0." + numStr;
                }
            }
        }
        if (isNeg)
        {
            numStr = '-' + numStr;
        }
        return numStr;
    }

    /** Converts the number into scientific notation, but only returns the base number part of it without its power
     * Return in the form: a
     * 
     * @param number The number to be converted to scientific notation and rounded
     * @param roundTo The decimal places after the decimal point to round to (negative rounds to the right of the
     *        decimal)
     * @return Returns the number without the times ten component */
    public static String withoutTimesTen(double number, int roundTo)
    {
        return withoutTimesTen(number, getPowerOf(number), roundTo);
    }

    /** Converts the number into scientific notation with the given power, but only returns the base number part of it
     * without its power Return in the form: a
     * 
     * @param number The number to be converted to scientific notation and rounded
     * @param power The power to use for ^n in scientific notation
     * @param roundTo The decimal places after the decimal point to round to (negative rounds to the right of the
     *        decimal)
     * @return Returns the number without the times ten component */
    public static String withoutTimesTen(double number, int power, int roundTo)
    {
        double roundNum = Math.pow(10, roundTo + 1);
        // do this because when we divide by small number(0.1 or 0.0001) we more often get
        // double math errors (values like 5.1899999995 instead of 5.19) this way we are
        // using bigger numbers which leads to less errors (in my experience at least)
        boolean isNeg = false; // so we can put the negative sign on at the end and not have it in the middle
        if (number < 0)
        {
            isNeg = true;
        }
        number = Math.abs(number);
        String numStr = Double.toString(number);
        // if there is no E then it is simple
        if (numStr.indexOf('E') == -1)
        {
            if (power >= 0)
            {
                number = Math.round(number * roundNum) / roundNum / Math.pow(10, power);
            }
            else if (power < 0)
            {
                number = Math.round(number * roundNum) / roundNum * Math.pow(10, -power);
            }
            numStr = Double.toString(number);
            power = 0; // so if it becomes a e number we don't double count the power
        }
        // otherwise we need to remove it and account for it
        if (numStr.indexOf('E') != -1)
        { // its so small or large it converted it back to E form
            numStr = numStr.substring(0, numStr.indexOf('E'));
            int decPlacesSciNot = numStr.substring(numStr.indexOf('.') + 1).length();
            numStr = numStr.substring(0, numStr.indexOf('.')) + numStr.substring(numStr.indexOf('.') + 1);
            int itsPow = getPowerOf(number) - decPlacesSciNot;
            int powShift = power - itsPow;

            if (powShift <= 0)
            {
                for (int i = 0; i > powShift; i--)
                {
                    numStr += "0";
                }
            }
            else
            {
                if (powShift < numStr.length())
                {
                    int decPlace = numStr.length() - powShift;
                    numStr = numStr.substring(0, decPlace) + "." + numStr.substring(decPlace);
                }
                else
                {
                    int pastLength = powShift - numStr.length();
                    for (int i = 0; i < pastLength; i++)
                    {
                        numStr = "0" + numStr;
                    }
                    numStr = "0." + numStr;
                }
            }
        }
        if (isNeg)
        {
            numStr = '-' + numStr;
        }
        return roundTo(numStr, roundTo);
    }

    /** Returns the given number's times ten to the n power Returns in the form: ×10^n
     * 
     * @param number The number to find the times ten to the n of
     * @return Returns a string containing the times ten to the n power but not the number */
    public static String onlyTimesTen(double number)
    {
        return onlyTimesTen(getPowerOf(number));
    }

    /** Returns a string of times ten to the given power Returns in the form: ×10^n
     * 
     * @param power The power to use for the times ten
     * @return Returns a string of the times ten to the given power */
    public static String onlyTimesTen(int power)
    {
        String powerString = "";
        if (power != 0)
        {
            powerString += '\u00D7' + "10";
            if (power < 0)
            {
                powerString += '\u207B';
            }
            String tmpPow = Integer.toString(power);
            for (int j = 0; j < tmpPow.length(); j++)
            {
                switch (tmpPow.charAt(j))
                {
                    case '0':
                        powerString += '\u2070';
                        break;
                    case '1':
                        powerString += '\u00B9';
                        break;
                    case '2':
                        powerString += '\u00B2';
                        break;
                    case '3':
                        powerString += '\u00B3';
                        break;
                    case '4':
                        powerString += '\u2074';
                        break;
                    case '5':
                        powerString += '\u2075';
                        break;
                    case '6':
                        powerString += '\u2076';
                        break;
                    case '7':
                        powerString += '\u2077';
                        break;
                    case '8':
                        powerString += '\u2078';
                        break;
                    case '9':
                        powerString += '\u2079';
                        break;
                }
            }
        }
        return powerString;
    }

    /** Returns a string of times ten to the given power Returns in Latex in the form: ×10^n
     * 
     * @param power The power to use for the times ten
     * @return Returns a string of the times ten to the given power */
    public static String laTexOnlyTimesTen(int power)
    {
        String powStr = "";
        if (power != 0)
        {
            powStr += "$\\times10^{" + power + "}$";
        }
        return powStr;
    }


    /** Finds the power of the number when it is in scientific notation
     * 
     * @param number The number to find the power of when in scientific notation
     * @return Returns the int which is the power of the number when i standard scientific notation */
    public static int getPowerOf(double number)
    {
        int power = 0;
        String numStr = Double.toString(number);
        // if there is an E we can find the power easily
        if (numStr.indexOf('E') != -1)
        {
            power = Integer.parseInt(numStr.substring(numStr.indexOf('E') + 1));
        }
        // otherwise we must find it by looping until we arrive at standard form
        else
        {
            if (number >= 10.0d || number <= -10.0d)
            {
                // well have positive power
                String xBeforeDec = numStr.substring(0, numStr.indexOf('.'));
                power = xBeforeDec.length() - 1;

                // if there's a negative sign in front it will add one to our power so we need to decrement it by 1
                if (numStr.indexOf('-') != -1)
                {
                    power--;
                }
            }
            else
            {
                if (number != 0.0d && (number < 1.0d && number > -1.0d))
                {
                    // we have neg power
                    power--;
                    numStr = numStr.substring(2); // get rid of before the decimal
                    while (numStr.startsWith("0"))
                    {
                        power--;
                        numStr = numStr.substring(1);
                    }
                }
            }
        }
        return power;
    }

    /** Rounds a number (in string form) to a specified number of places, which is centered on the decimal place e.g. 1
     * = tenths place, 3 = thousandths place e.g. -1 = tens place, -3 = thousands place
     * 
     * @param numStr The number to round in string form
     * @param decimalsToRoundTo The place to round the number to relative to the decimal point
     * @return Returns the number in string format that has been rounded */
    public static String roundTo(String numStr, int decimalsToRoundTo)
    {
        int periodSpot = numStr.indexOf('.');
        int decPlaces = numStr.length() - periodSpot - 1;
        // if we are rounding after the decimal place
        if (decimalsToRoundTo > 0)
        {
            // if there is a decimal place we can just tack on 0
            if (periodSpot != -1)
            {
                if (decPlaces < decimalsToRoundTo)
                {
                    while (decPlaces < decimalsToRoundTo)
                    {
                        numStr += '0';
                        decPlaces++;
                    }
                }
                else
                {
                    numStr = numStr.substring(0, numStr.length() - decPlaces + decimalsToRoundTo);
                }
            }
            else
            { // otherwise we must first add the decimal place
                numStr += ".0";
                for (int i = 1; i < decimalsToRoundTo; i++)
                {
                    numStr += "0";
                }
            }
        }
        else if (decimalsToRoundTo < 0)
        { // if we are rounding before the decimal place we must remove it if it exists then add 0s until it matches up
          // correctly
            int nonDec = numStr.length() - decPlaces - 1;
            if (periodSpot == -1)
            {
                nonDec = numStr.length();
            }
            if (nonDec + decimalsToRoundTo >= 1)
            {
                numStr = numStr.substring(0, nonDec + decimalsToRoundTo);// remember roundTo is neg in this part
            }
            for (int i = 0; i > decimalsToRoundTo; i--)
            {
                numStr += 0;
            }
        }
        else if (periodSpot != -1)
        { // roundTo is always == 0 if it makes it to this point
            numStr = numStr.substring(0, periodSpot);
        }
        return numStr;
    }
}
