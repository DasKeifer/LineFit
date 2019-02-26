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


import linefit.DataSet;


/** The Quadratic Approximation Factory class that is used to create the Quadratic Approximation Strategy so that it can
 * be done in a generic way during run time, allowing the user to change the algorithm This algorithm makes use of the
 * quadratic equation to calculate the Chi Squared values for the data with given slopes and intercepts Chi Squared (S)
 * is written as: S1 = S0 + alpha(b1 - b0)^2 + beta(m1 - m0)^2 + 2gamma(b1 - b0)(m1 - m0), where S1 is the Chi squared
 * being calculated with the given m1 and b1 and S0 is the Chi Square of the calculated starting point and m0 and b0 are
 * the calculated starting slope and intercept
 * 
 * @author Don Petcher and Keith Rice
 * @version 1.0
 * @since 0.98.1 */
class QuadraticApproximationFactory extends LinearFitFactory
{
    /** The private instance of this factory so that we only have one object allowing us to populate the user selected
     * algorithm box with the current factory */
    private static QuadraticApproximationFactory instance = new QuadraticApproximationFactory();

    /** returns the instance of the factory and makes sure that only one object of it is ever created (singleton) so
     * that we can check what fit is being used easily
     * 
     * @return Returns the object instance of this Algorithm Factory */
    static QuadraticApproximationFactory getInstance()
    {
        return instance;
    }

    /** The constructor that allows us to create the Quadratic Approximation Algorithm Fit Strategy without knowing
     * which factory it is and also determines whether or not its child can fix its slope or intercept */
    private QuadraticApproximationFactory()
    {
        // set whether or not this algorithm can handle fixing the slope or intercept here
        this.canGeneratedFitsFixSlope = false;
        this.canGeneratedFitsFixIntercept = false;
    }

    /** Creates a new instance of the type of FitAlogritm this Factory is This allows us to generically create the fit
     * algorithm and not have to specify before runtime what kind of linear fit we are using
     * 
     * @param dataSet The DataSet that the new FitStrategy will use for data and fit its line to
     * @return Returns a new LinearFitStrategy that is an instance of whatever the instance this is called on subclass
     *         Algorithm */
    public LinearFitStrategy createNewLinearFitStartegy(DataSet dataSet)
    {
        return new QuadraticApproximationStrategy(dataSet);
    }

    /** Overrides the to String for the Fit Algorithm so that it displays this name for the User to select in the fit
     * algorithm drop down box */
    public String toString()
    {
        return "Quadratic Approximation";
    }

    /** The private class for the Quadratic Approximation Algorithm that does the calculations and that is made by the
     * Quadratic Approximation Fit Algorithm
     * 
     * @author Don Petcher and Keith Rice
     * @version 1.0
     * @since 0.98.1 */
    private class QuadraticApproximationStrategy extends LinearFitStrategy
    {
        /** the linear Fit Data we use as a starting point to find the intitial slopes and intercept and errors to
         * use */
        private LinearFitStrategy startingPoint;

        /** Creates the linear fit based on the passed DataSet and then calculates the fit
         * 
         * @param dataSet The DataSet that this is the fit for */
        private QuadraticApproximationStrategy(DataSet dataSet)
        {
            // sets its allowed fixes based of the factory methods allowed fixes
            canFixSlope = canGeneratedFitsFixSlope;
            canFixIntercept = canGeneratedFitsFixIntercept;

            // store our DataSet and then calculate the fit for the first time
            dataForFit = dataSet;

            // create the this linear fit strategy to serve as an initial estimate for the Quadratic Minimization
            // function
            startingPoint = PartialDerivativeMinimizationFactory.getInstance().createNewLinearFitStartegy(dataSet);

            // it will always already have an associated fit because we just set it
            // keep the fixed values if this DataSet already had fit data
            this.setWhatIsFixed(dataSet.linearFitStrategy.getWhatIsFixed(), dataSet.linearFitStrategy.getFixedValue());

            // set the dataSet's linearFit to us
            dataSet.linearFitStrategy = this;
        }

        /** Updates the values in the Fit Algorithm's Data based on the current options and values
         * 
         * @param fitTypeToUse The fit Type to use when fitting the line */
        protected void calculateLinearFit(FitType fitTypeToUse)
        {
            if (fitTypeToUse == FitType.BOTH_ERRORS)
            {
                approximateQuadratically();
            }
            else
            {
                defaultChiSquareFitForSingleOrNoErrors(fitTypeToUse);
            }
        }

        /** calculates the error and fit for a line with both errors by using quadratic minimization and the use of the
         * ChiSquared (S in the equations) */
        private void approximateQuadratically()
        {
            // calculate and get our starting point
            startingPoint.setWhatIsFixed(FixedVariable.NONE, 0);
            startingPoint.calculateLinearFit(FitType.BOTH_ERRORS);
            double sigmaM = startingPoint.getSlopeError();
            double sigmaB = startingPoint.getInterceptError();
            double startingSlope = startingPoint.getSlope();
            double startingIntercept = startingPoint.getIntercept();
            int epsilon = 1; // TODO: figure this out

            // calculate the chiSquared for six points in the area so we can solve for the 6 unknowns
            Double[][] data = dataForFit.getAllValidPointsData(true);
            double chiSquared1 = calculateChiSquared(startingSlope, startingIntercept, data);
            double chiSquared2 = calculateChiSquared(startingSlope - sigmaM, startingIntercept, data);
            double chiSquared3 = calculateChiSquared(startingSlope + sigmaM, startingIntercept, data);
            double chiSquared4 = calculateChiSquared(startingSlope, startingIntercept - sigmaB, data);
            double chiSquared5 = calculateChiSquared(startingSlope, startingIntercept + sigmaB, data);
            double chiSquared6 = calculateChiSquared(startingSlope + sigmaM, startingIntercept + (sigmaB * epsilon),
                    data);

            // find there difference from our starting ChiSquared
            double delta21 = chiSquared2 - chiSquared1;
            double delta31 = chiSquared3 - chiSquared1;
            double delta41 = chiSquared4 - chiSquared1;
            double delta51 = chiSquared5 - chiSquared1;
            double delta61 = chiSquared6 - chiSquared1;

            // calculate some of the unknowns in the equation for ChiSquared
            double alpha = (delta41 + delta51) / (2 * Math.pow(sigmaB, 2));
            double beta = (delta21 + delta31) / (2 * Math.pow(sigmaM, 2));
            double gamma = (delta61 - delta31 - (0.5 * (1 + epsilon) * delta51) - (0.5 * (1 - epsilon) * delta41)) /
                    (2 * epsilon * sigmaB * sigmaM);

            // extract some of the parts of the next equations to make them simpler
            double oneOverABMinusGSquared = 1 / (alpha * beta - Math.pow(gamma, 2));
            double D41MinusD51Over4Sb = (delta41 - delta51) / (4 * sigmaB);
            double D21MinusD31Over4Sm = (delta21 - delta31) / (4 * sigmaM);

            // TODO: for fixed slope and intercept?
            // //check for divide by 0 and make them 0 instead of NaN
            // if(sigmaB == 0.0)
            // {
            // alpha = 0;
            // gamma = 0;
            // oneOverABMinusGSquared = 0;
            // D41MinusD51Over4Sb = 0;
            // }
            // if(sigmaM == 0.0)
            // {
            // beta = 0;
            // gamma = 0;
            // oneOverABMinusGSquared = 0;
            // D21MinusD31Over4Sm = 0;
            // }

            // calculate our quadratically minimized slopes and intercepts
            double b0 = startingIntercept + oneOverABMinusGSquared * (beta * D41MinusD51Over4Sb - gamma *
                    D21MinusD31Over4Sm);
            double m0 = startingSlope + oneOverABMinusGSquared * (alpha * D21MinusD31Over4Sm - gamma *
                    D41MinusD51Over4Sb);
            double chiSquared0 = chiSquared1 - (alpha * Math.pow(startingIntercept - b0, 2) + beta * Math.pow(
                    startingSlope - m0, 2) + 2 * gamma * (startingIntercept - b0) * (startingSlope - m0));

            // if the old chiSquared was less than the new one, keep the old (starting) m and b
            if (chiSquared1 < chiSquared0)
            {
                System.out.println("old");
                this.slope = startingSlope;
                this.intercept = startingIntercept;
            }
            // otherwise use the one we calculated
            else
            {
                this.slope = m0;
                this.intercept = b0;
            }

            // calculate the errors
            this.slopeError = alpha * oneOverABMinusGSquared;
            this.interceptError = beta * oneOverABMinusGSquared;

            System.out.println(chiSquared1);
            System.out.println(chiSquared0);
            System.out.println(this.calculateChiSquared(m0, b0));
        }
    }
}