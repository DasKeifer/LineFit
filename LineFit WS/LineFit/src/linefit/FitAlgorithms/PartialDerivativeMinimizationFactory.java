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


/** The Chi Square Fit Factory class that is used to create the Chi Squared Data so that it can be done in a generic way
 * during run time, allowing the user to change the algorithm
 * 
 * @author Keith Rice
 * @version 1.1
 * @since 0.98.1 */
class PartialDerivativeMinimizationFactory extends LinearFitFactory
{
    /** The private instance of this factory so that we only have one object allowing us to populate the user selected
     * algorithm box with the current factory */
    private static PartialDerivativeMinimizationFactory instance = new PartialDerivativeMinimizationFactory();

    /** returns the instance of the factory and makes sure that only one object of it is ever created (singleton) so
     * that we can check what fit is being used easily
     * 
     * @return Returns the object instance of this Algorithm Factory */
    static PartialDerivativeMinimizationFactory getInstance()
    {
        return instance;
    }

    /** The constructor that allows us to create the Chi Square Fit Data without knowing which factory it is and also
     * determines whether or not its child can fix its slope or intercept */
    private PartialDerivativeMinimizationFactory()
    {
        this.canGeneratedFitsFixSlope = true;
        this.canGeneratedFitsFixIntercept = true;
    }

    /** Creates a new instance of the type of FitAlogritm this is This allows us to generically create the fit algorithm
     * and not have to specify before runtime what kind of linear fit we are using
     * 
     * @param dataSet The DataSet that the new FitStrategy will use for data and fit its line to
     * @return Returns a new LinearFitStrategy that is an instance of whatever the instance this is called on subclass
     *         Algorithm */
    public LinearFitStrategy createNewLinearFitStartegy(DataSet dataSet)
    {
        return new PartialDerivativeMinimizationStrategy(dataSet);
    }

    /** Overrides the to String for the Fit Algorithm so that it displays this name for the User to select in the fit
     * algorithm drop down box */
    public String toString()
    {
        return "Partial Derivative Minimization";
    }

    /** The private class for the Chi Square Fit Algorithm that does the calculations and that is made by the Chi
     * Squared Fit Algorithm
     * 
     * @author Keith Rice
     * @version 1.0
     * @since 0.98.1 */
    private class PartialDerivativeMinimizationStrategy extends LinearFitStrategy
    {
        /** Creates the linear fit based on the passed dataset and then calculates the fit
         * 
         * @param dataSet The DataSet that this is the fit for */
        private PartialDerivativeMinimizationStrategy(DataSet dataSet)
        {
            // sets its allowed fixes based of the factory methods allowed fixes
            canFixSlope = canGeneratedFitsFixSlope;
            canFixIntercept = canGeneratedFitsFixIntercept;

            // store our DataSet
            dataForFit = dataSet;

            // keep the fixed values if this DataSet already had fit data
            if (dataSet.linearFitStrategy != null)
            {
                this.setWhatIsFixed(dataSet.linearFitStrategy.getWhatIsFixed(), dataSet.linearFitStrategy
                        .getFixedValue());
            }

            // set the dataSet's linearFit to us
            dataSet.linearFitStrategy = this;
        }

        /** Updates the values in the Fit Algorithm's Data based on the current options and values
         * 
         * @param fitTypeToUse The fit Type to use when fitting the line */
        protected void calculateLinearFit(FitType fitTypeToUse)
        {
            // if we want both errors we need to run it through one way first
            if (fitTypeToUse == FitType.BOTH_ERRORS)
            {
                minimizePartialDerivatesOfErrors();
            }
            else
            {
                defaultChiSquareFitForSingleOrNoErrors(fitTypeToUse);
            }
        }

        /** Fits a line to the graph when the data contains two dimensions of errors (both x and y) */
        private void minimizePartialDerivatesOfErrors()
        {
            double scope;
            double m1, m2, m3, chi1, chi2, chi3, numerator, denominator, weight, delta;
            double sumX = 0.0, sumXX = 0.0, sumW = 0.0;

            // get the data that are valid (have x and y data)
            Double[][] data = dataForFit.getAllValidPointsData(true);
            Double[] xData = data[DataDimension.X.getColumnIndex()];
            Double[] yData = data[DataDimension.Y.getColumnIndex()];
            Double[] xErrorData = data[DataDimension.X.getErrorColumnIndex()];
            Double[] yErrorData = data[DataDimension.Y.getErrorColumnIndex()];

            // This is the minimization equation that is being used
            // ////////////////////////////////////////////////////////////////
            // (b-a)^2 [f(b)-f(c)] - (b-c)^2 [f(b)-f(a)] //
            // x = b - (1/2) * -------------------------------------------- //
            // (b-a) [f(b)-f(c)] - (b-c) [f(b)-f(a)] //
            // ////////////////////////////////////////////////////////////////

            // Run regular linefit first
            calculateLinearFit(FitType.Y_ERROR);

            if (whatIsFixed != FixedVariable.SLOPE)
            {
                scope = 10 * slopeError;

                for (int i = 0; i < 50; i++)
                {
                    m1 = slope - scope;
                    m2 = slope;
                    m3 = slope + scope;

                    // Get the c1 data from chi2
                    double b1 = calculateIntercept(m1, data);
                    chi1 = calculateChiSquared(m1, b1, data);

                    // Get the c2 data from chi2
                    double b2 = calculateIntercept(m2, data);
                    chi2 = calculateChiSquared(m2, b2, data);

                    // Get the c3 data from chi2
                    double b3 = calculateIntercept(m3, data);
                    chi3 = calculateChiSquared(m3, b3, data);

                    numerator = Math.pow(m2 - m1, 2) * (chi2 - chi3) - Math.pow(m2 - m3, 2) * (chi2 - chi1);
                    denominator = (m2 - m1) * (chi2 - chi3) - (m2 - m3) * (chi2 - chi1);

                    slope = m2 - numerator / (2 * denominator);

                    scope = scope / 1.2;
                }
            }

            double sigmaSquared = 0.0, xs = 0.0, ys = 0.0, ws = 0.0;
            if (whatIsFixed != FixedVariable.INTERCEPT)
            {
                // now calculate the final intercept with our minimized error slope
                for (int j = 0; j < xData.length; j++)
                {
                    if (xErrorData[j] != null && yErrorData[j] != null)
                    {
                        sigmaSquared = Math.pow(yErrorData[j], 2) + (Math.pow(slope, 2) * Math.pow(xErrorData[j], 2));
                        ws += 1.0 / sigmaSquared;
                        xs += xData[j] / sigmaSquared;
                        ys += yData[j] / sigmaSquared;
                    }
                }
                intercept = (ys - slope * xs) / ws;
            }

            // After minimization, calculate errors to parameters again
            double x = 0.0, eY = 0.0, eX = 0.0;
            for (int i = 0; i < xData.length; i++)
            {
                if (xErrorData[i] != null && yErrorData[i] != null)
                {
                    x = xData[i];
                    eX = xErrorData[i];
                    eY = yErrorData[i];

                    weight = 1.0 / (eY * eY + slope * slope * eX * eX);

                    sumX += x * weight;
                    sumXX += x * x * weight;
                    sumW += weight;
                }
            }

            delta = sumW * sumXX - sumX * sumX;

            if (whatIsFixed != FixedVariable.SLOPE)
            {
                slopeError = Math.sqrt(sumW / delta);
            } // dont need to set it to zero otherwise because we did that with the starting fit

            if (whatIsFixed != FixedVariable.INTERCEPT)
            {
                interceptError = Math.sqrt(sumXX / delta);
            }
        }
    }
}