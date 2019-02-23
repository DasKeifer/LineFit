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

package linefit.FitAlgorithms;


import linefit.DataDimension;
import linefit.DataSet;
import linefit.ScientificNotation;


/** The Abstract class for the Linear Fit Algorithms where the algorithm is actually implemented. This should only be
 * created as a private class inside a factory class for the fit algorithm
 * 
 * @author Keith Rice
 * @version 2.0
 * @since 0.98.1 */
public abstract class LinearFitStrategy
{
    /** The slope of the fitted line */
    double slope = 0;
    /** The intercept of the fitted line */
    double intercept = 0;
    /** The error value for the slope */
    double slopeError = 0;
    /** The error value for the intercept */
    double interceptError = 0;

    /** Whether or not this algorithm supports fixing the slope of the fit. Set to false by default */
    protected boolean canFixSlope = false;
    /** Whether or not this algorithm supports fixing the intercept of the fit. Set to false by default */
    protected boolean canFixIntercept = false;

    /** What variable is fixed for this fit */
    FixedVariable whatIsFixed = FixedVariable.NONE;
    /** The value of the fixed variable for this fit */
    double fixedValue = 0;

    /** The DataSet that this is a Fit for. Allows us to keep track and not have to pass it around and stuff. Basically
     * makes it "safer" */
    protected DataSet dataForFit;

    /** Recalculates the Fit for the given DataSet - updates the fit for the given values */
    public void refreshFitData()
    {
        if (dataForFit.getFitType() != FitType.NONE)
        {
            calculateLinearFit(dataForFit.getFitType());
        }
    }

    /** Updates the values in the Fit Algorithm's Data based on the current options and values
     * 
     * @param fitTypeToUse The fit Type to use when fitting the line */
    protected abstract void calculateLinearFit(FitType fitTypeToUse);

    /** Calculates the intercept of this fit given the passed slope value with the data points retrieves from this
     * dataset. If the intercept is fixed, it will return the fixed value.
     * 
     * If this function needs to be called often, then the alternate version of this function can be used as an
     * optimization so that it is not constantly re-getting the data each time it is called.
     * 
     * @param inSlope The slope to calculate the intercept for
     * @return The Intercept of the fit line if the passed slope is used for calculating it */
    double calculateIntercept(double inSlope)
    {
        Double[][] data = dataForFit.getAllValidPointsData(true);
        return calculateIntercept(inSlope, data);
    }

    /** Calculates the intercept of this fit given the passed slope value and the passed data. If the intercept is
     * fixed, it will return the fixed value.
     * 
     * If this function needs to be called often, then this version of the function can be used as an optimization so
     * that it is not constantly re-getting the data each time it is called
     * 
     * @param inSlope The slope to calculate the intercept for
     * @param data The points data to calculate the intercept for
     * @return The Intercept of the fit line if the passed slope is used for calculating it */
    double calculateIntercept(double inSlope, Double[][] data)
    {
        // if we can't or don't have the intercept fixed, then we need to calculate it
        if (!canFixIntercept || whatIsFixed != FixedVariable.INTERCEPT)
        {
            double sigmaSquared = 0.0, xSum = 0.0, ySum = 0.0, wSum = 0.0;
            double eX = 0.0, eY = 0.0, x = 0.0, y = 0.0;

            Double[] xData = data[DataDimension.X.getColumnIndex()];
            Double[] yData = data[DataDimension.Y.getColumnIndex()];
            Double[] xErrorData = data[DataDimension.X.getErrorColumnIndex()];
            Double[] yErrorData = data[DataDimension.Y.getErrorColumnIndex()];

            // calculates the intercept with the current slope
            for (int i = 0; i < xData.length; i++)
            {
                x = xData[i];
                y = yData[i];
                eX = xErrorData[i];
                eY = yErrorData[i];

                sigmaSquared = Math.pow(eY, 2) + (Math.pow(inSlope, 2) * Math.pow(eX, 2));
                wSum += 1.0 / sigmaSquared;
                xSum += x / sigmaSquared;
                ySum += y / sigmaSquared;
            }
            return (ySum - inSlope * xSum) / wSum;
        }
        else
        {
            return fixedValue;
        }
    }

    /** Gets the appropriate weight to use for Chi Squared based on the fit type
     * 
     * @param fitTypeToUse The fit type that is being used
     * @param xError The x error value for the point or null if there is no x error
     * @param yError The y error value for the point or null if there is no y error
     * @return The weight to use for calculating Chi Squared */
    public double getChiSquaredWeight(FitType fitTypeToUse, Double xError, Double yError)
    {
        double weight = 1;
        switch (fitTypeToUse)
        {
            case Y_ERROR:
                if (yError != 0.0 && yError != null)
                {
                    weight = (double) (1.0 / (yError * yError));
                }
                else
                {
                    // TODO: add warning?
                    weight = 1;
                }
                break;
            case BOTH_ERRORS:
                System.out.println(
                        "Error: Default Chi Squared Algorithm does not support both x and y error fitting. Defaulting to X only fit");
            case X_ERROR:
                if (xError != 0.0 && xError != null)
                {
                    weight = (double) (1.0 / (xError * xError));
                }
                else
                {
                    // TODO: add warning?
                    weight = 1;
                }
                break;
            case NONE:
                System.out.println("Error: FitType NONE specified! Should not be here!");
            default:
                System.out.println("Error: Bad fit type specified (" + fitTypeToUse.getDisplayString() +
                        "). Defaulting to REGULAR (i.e. no error) fit");
            case REGULAR:
                weight = 1;
                break;
        }

        return weight;
    }

    /** Calculates the Chi Squared(^2) value for the inputed slope and intercept with the data points retrieved from
     * this dataset.The DataSet's Chi Squared measures the average distance of the points away from the fitted line
     * 
     * If this function needs to be called often, then the alternate version of this function can be used as an
     * optimization so that it is not constantly re-getting the data each time it is called
     * 
     * @param inSlope The slope to calculate the Chi Squared value for
     * @param inIntercept The intercept to calculate the Chi Squared value for
     * @return The Chi Squared value of the fit using the passed slope and intercept */
    public double calculateChiSquared(double inSlope, double inIntercept)
    {
        Double[][] data = dataForFit.getAllValidPointsData(true);
        return calculateChiSquared(inSlope, inIntercept, data);
    }

    /** Calculates the Chi Squared(^2) value for the inputed slope and intercept.The DataSet's Chi Squared measures the
     * average distance of the points away from the fitted line
     * 
     * If this function needs to be called often, then this version of the function can be used as an optimization so
     * that it is not constantly re-getting the data each time it is called
     * 
     * @param inSlope The slope to calculate the Chi Squared value for
     * @param inIntercept The intercept to calculate the Chi Squared value for
     * @param data The points data to calculate the Chi Squared value for
     * @return The Chi Squared value of the fit using the passed slope and intercept */
    public double calculateChiSquared(double inSlope, double inIntercept, Double[][] data)
    {
        double x = 0.0, y = 0.0;

        Double[] xData = data[DataDimension.X.getColumnIndex()];
        Double[] yData = data[DataDimension.Y.getColumnIndex()];
        Double[] xErrorData = data[DataDimension.X.getErrorColumnIndex()];
        Double[] yErrorData = data[DataDimension.Y.getErrorColumnIndex()];

        // if we have both fits then calculate the chi squared like this
        if (this.dataForFit.getFitType() == FitType.BOTH_ERRORS)
        {
            double eX = 0.0, eY = 0.0;
            double sigmaSquared = 0.0, chiSquaredSum = 0.0;

            // now finds the chi squared sum using this slope and intercept
            for (int i = 0; i < xData.length; i++)
            {
                x = xData[i];
                y = yData[i];
                eX = xErrorData[i];
                eY = yErrorData[i];

                sigmaSquared = Math.pow(eY, 2) + (Math.pow(inSlope, 2) * Math.pow(eX, 2));

                double distFromSlope = y - (inSlope) * x - inIntercept;
                chiSquaredSum += distFromSlope * distFromSlope / sigmaSquared;
            }

            return chiSquaredSum;
        }
        // if we have either one or none we have to calculate it with weights like this
        else
        {
            double weight = 0.0, sumW = 0.0, sumX = 0.0, sumY = 0.0, sumXX = 0.0, sumXY = 0.0, sumYY = 0.0;
            for (int i = 0; i < xData.length; i++)
            {
                x = xData[i];
                y = yData[i];

                weight = getChiSquaredWeight(dataForFit.getFitType(), xErrorData[i], yErrorData[i]);

                sumX += x * weight;
                sumY += y * weight;
                sumXX += x * x * weight;
                sumXY += x * y * weight;
                sumYY += y * y * weight;
                sumW += weight;
            }

            return sumYY - (2.0 * slope * sumXY) - (2.0 * intercept * sumY) + (Math.pow(slope, 2) * sumXX) + (2.0 *
                    slope * intercept * sumX) + (Math.pow(intercept, 2) * sumW);
        }
    }

    /** Gets the x value of a point that goes with the specified y value using our current fit's slope and intercept
     * 
     * @param yPoint the y value to find the x value at on this fit
     * @return The x value for the given y value for the current linear fit */
    public double getXOfYPoint(double yPoint)
    {
        // x = (y-b)/m
        return (yPoint - intercept) / slope;
    }

    /** Gets the y value of a point that goes with the specified x value using our current fit's slope and intercept
     * 
     * @param xPoint the x value for to find the y value at on this fit
     * @return The y value for the given x value for the current linear fit */
    public double getYOfXPoint(double xPoint)
    {
        // y = mx+b
        return getSlope() * xPoint + intercept;
    }

    /** calculates the linear fit for only one set of errors by minimizing the Chi Squared value
     * 
     * @param fitTypeToUse The fit type to fit the DataSet with */
    void defaultChiSquareFitForSingleOrNoErrors(FitType fitTypeToUse)
    {
        double x = 0, y = 0;
        double sumX = 0.0, sumY = 0.0, sumXX = 0.0, sumXY = 0.0, sumW = 0.0, weight = 0.0;

        Double[][] data = dataForFit.getAllValidPointsData(true);
        Double[] xData = data[DataDimension.X.getColumnIndex()];
        Double[] yData = data[DataDimension.Y.getColumnIndex()];
        Double[] xErrorData = data[DataDimension.X.getErrorColumnIndex()];
        Double[] yErrorData = data[DataDimension.Y.getErrorColumnIndex()];

        for (int i = 0; i < xData.length; i++)
        {
            // read the x and y data
            x = xData[i];
            y = yData[i];

            // get the weight
            weight = getChiSquaredWeight(fitTypeToUse, xErrorData[i], yErrorData[i]);

            // sum the values we need later
            sumX += x * weight;
            sumY += y * weight;
            sumXX += x * x * weight;
            sumXY += x * y * weight;
            sumW += weight;
        }

        double delta = 0;
        if (canFixSlope && whatIsFixed == FixedVariable.SLOPE) // check if we have the easy case of a fixed slope
        {
            slope = fixedValue;
            intercept = (sumY - slope * sumX) / sumW;
            delta = sumW * sumXX - sumX * sumX; // we can do it this way because the loss of accuracy wont matter once
                                                // we square root it for finding the errors
        }
        else if (canFixIntercept && whatIsFixed == FixedVariable.INTERCEPT) // or the easy case of a fixed intercept
        {
            intercept = fixedValue;
            slope = (sumXY - (intercept * sumX)) / (sumXX);
            delta = sumW * sumXX - sumX * sumX; // we can do it this way because the loss of accuracy wont matter once
                                                // we square root it for finding the errors
        }
        else if (whatIsFixed == FixedVariable.NONE)
        {
            double xi = 0.0, xj = 0.0, yi = 0.0, yj = 0.0, wi = 0.0, wj = 0.0;
            double slopeSum = 0, interceptSum = 0;

            for (int i = 0; i < xData.length; i++)
            {
                xi = xData[i];
                yi = yData[i];
                wi = getChiSquaredWeight(fitTypeToUse, xErrorData[i], yErrorData[i]);

                double sumj = 0, sumjIntercept = 0;
                for (int j = 0; j < xData.length; j++)
                {
                    xj = xData[j];
                    yj = yData[j];
                    wj = getChiSquaredWeight(fitTypeToUse, xErrorData[j], yErrorData[j]);

                    sumj += (xi - xj) * wj;
                    sumjIntercept += (xi - xj) * wj * yj;
                }
                delta += sumj * xi * wi;
                slopeSum += sumj * yi * wi;
                interceptSum += sumjIntercept * xi * wi;
            }
            slope = slopeSum / delta;
            intercept = interceptSum / delta;

        }
        else
        {
            System.err.println("Trying To set undefined fixed varaible");
        }

        slopeError = Math.sqrt(sumW / delta);
        interceptError = Math.sqrt(sumXX / delta);
        if (fitTypeToUse == FitType.X_ERROR)
        {
            slopeError = slope * slopeError;
            interceptError = slope * interceptError;
        }

        if (whatIsFixed == FixedVariable.SLOPE && canFixSlope)
        {
            slopeError = 0;
        }
        if (whatIsFixed == FixedVariable.INTERCEPT && canFixIntercept)
        {
            interceptError = 0;
        }
    }

    // setters
    /** Tells the FitData what value is fixed or if no value is fixed and also the value to use for the fixed variable
     * 
     * @param whatFixed The variable to set the fixed value to
     * @param valueOfFixed The value to set the fixed variable to */
    public void setWhatIsFixed(FixedVariable whatFixed, double valueOfFixed)
    {
        if (!canFixIntercept && !canFixSlope)
        {
            System.err.println("Cannot set any fixed values for this Fit Algortihm");
            return;
        }

        switch (whatFixed)
        {
            case NONE:
                whatIsFixed = whatFixed;
                break;
            case SLOPE:
                if (!canFixSlope)
                {
                    System.err.println("Cannot fix the slope for this Fit Algortihm");
                }
                break;
            case INTERCEPT:
                if (!canFixIntercept)
                {
                    System.err.println("Cannot fix the Intercept for this Fit Algortihm");
                }
                break;
            default:
                System.err.println("Trying to fix an Undefined variable");
                return;
        }

        whatIsFixed = whatFixed;
        fixedValue = valueOfFixed;

        refreshFitData();
    }

    // Getters
    /** Gets the slope of this fit
     * 
     * @return The slope value of this fit */
    public double getSlope()
    {
        return (slope);
    }

    /** Gets the intercept of this fit
     * 
     * @return The intercept value of this fit */
    public double getIntercept()
    {
        return (intercept);
    }

    /** Returns the slope error/uncertainty for the fit
     * 
     * @return The slope Error/uncertainty value for the fit */
    public double getSlopeError()
    {
        // if its a regular fit we need to use the chiSquared for the slope error
        if (dataForFit.getFitType() == FitType.REGULAR)
        {
            // we need to make sure that we have enough points so that we do not divide by 0!
            if (dataForFit.getDataSize(DataDimension.X) > 2 && (this.whatIsFixed != FixedVariable.SLOPE ||
                    !canFixSlope))
            {
                return Math.sqrt(Math.abs(calculateChiSquared(this.slope, this.intercept)) / (dataForFit.getDataSize(
                        DataDimension.X) - 2));
            }
            else
            {
                return 0; // Note: if we have two points, it should have and error of 0!
            }
        }
        // otherwise just use what we already put in there
        else
        {
            return Math.abs(slopeError);
        }
    }

    /** Returns the intercept error for the fit
     * 
     * @return The Intercept error/uncertainty value for the fit */
    public double getInterceptError()
    {
        if (dataForFit.getFitType() == FitType.REGULAR)
        {
            return 0;
        }
        return Math.abs(interceptError);
    }

    /** Gets whether or not the slope can be fixed in this slope
     * 
     * @return Whether or not the slope can be fixed for this slope */
    public boolean canFixSlope()
    {
        return canFixSlope;
    }

    /** Gets whether or not the intercept can be fixed in this slope
     * 
     * @return Whether or not the intercept can be fixed for this algorithm */
    public boolean canFixIntercept()
    {
        return canFixIntercept;
    }

    /** Gets what variable is currently fixed for this algorithm
     * 
     * @return The variable that is currently set to be fixed */
    public FixedVariable getWhatIsFixed()
    {
        return whatIsFixed;
    }

    /** Gets the value of the fixed variable
     * 
     * @return The value of the fixed variable */
    public double getFixedValue()
    {
        if (!canFixIntercept && !canFixSlope)
        {
            System.err.println("this strategy does not support fixing variables");
        }
        return fixedValue;
    }

    /** Returns the slope and its error as a string in scientific notation using ASCII characters
     * 
     * @param resultDecPlaces The number of decimal places to round the slope value to. If negative values are inputed,
     *        it rounds that many digits to the left of the decimal place
     * @param useScientificNotation Whether or not to use scientific notation for formatting the slope String. True
     *        means the results use scientific notation
     * @param returnAsLaTexString Whether or not to return the String in the correct form to be displayed in LaTex
     *        (which is the incorrect form for other purposes). True returns it as a LaTex String
     * @return Returns the slope of this particular fit in the format specified by the parameters */
    public String getSlopeAsString(int resultDecPlaces, boolean useScientificNotation, boolean returnAsLaTexString)
    {
        if (useScientificNotation)
        {
            if (!returnAsLaTexString)
            {
                return ScientificNotation.withError(getSlope(), getSlopeError(), resultDecPlaces);
            }
            else
            {
                return ScientificNotation.laTexWithError(getSlope(), getSlopeError(), resultDecPlaces);
            }
        }
        else
        {
            if (!returnAsLaTexString)
            {
                return ScientificNotation.withError(getSlope(), getSlopeError(), 0, resultDecPlaces);
            }
            else
            {
                return ScientificNotation.laTexWithError(getSlope(), getSlopeError(), 0, resultDecPlaces);
            }
        }
    }

    /** Returns the intercept and its error as a string in scientific notation using ASCII characters
     * 
     * @param resultDecPlaces The number of decimal places to round the intercept value to. If negative values are
     *        inputed, it rounds that many digits to the left of the decimal place
     * @param yAxisPower The power on the Y Axis of the graph so that the intercept's power will match the power on the
     *        Y Axis if put into scientific notation
     * @param useScientificNotation Whether or not to use scientific notation for formatting the intercept String. True
     *        means the results use scientific notation
     * @param returnAsLaTexString Whether or not to return the String in the correct form to be displayed in LaTex
     *        (which is the incorrect form for other purposes). True returns it as a LaTex String
     * @return Returns the intercept of this particular fit in the format specified by the parameters */
    public String getInterceptAsString(int resultDecPlaces, int yAxisPower, boolean useScientificNotation,
            boolean returnAsLaTexString)
    {
        // if we use scientific notation we want the intercept with the same power as the y axis
        if (useScientificNotation)
        {
            if (!returnAsLaTexString)
            {
                return ScientificNotation.withError(getIntercept(), getInterceptError(), yAxisPower, resultDecPlaces);
            }
            else
            {
                return ScientificNotation.laTexWithError(getIntercept(), getInterceptError(), yAxisPower,
                        resultDecPlaces);
            }
        }
        // otherwise we want the power to be 0
        else
        {
            if (!returnAsLaTexString)
            {
                return ScientificNotation.withError(getIntercept(), getInterceptError(), 0, resultDecPlaces);
            }
            else
            {
                return ScientificNotation.laTexWithError(getIntercept(), getInterceptError(), 0, resultDecPlaces);
            }
        }
    }
}
