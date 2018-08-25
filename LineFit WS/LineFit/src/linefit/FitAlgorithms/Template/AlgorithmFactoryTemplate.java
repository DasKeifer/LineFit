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

package linefit.FitAlgorithms.Template; // TODO: Remove the ".template" part and move the created class to the higher
                                        // package, FitAlgorithms


import linefit.DataSet;
import linefit.FitAlgorithms.FitType;
import linefit.FitAlgorithms.LinearFitFactory;
import linefit.FitAlgorithms.LinearFitStrategy;

// TODO: after you are finished with this coding, make sure you add your algorithmFactory to the appropriate array in
// LinearFitFactory or else it wont show up in LineFit!


// TODO: fill in the class information
/** The *algorithm name* Factory class that is used to create the *algorithm name* Strategy so that it can be done in a
 * generic way during run time, allowing the user to change the algorithm Feel free to explain high level aspects of
 * your algorithm here
 * 
 * @author Your Name
 * @version 1.0
 * @since "current LineFit version number" */
class AlgorithmFactoryTemplate extends LinearFitFactory // TODO: Rename this using the format *algorithm name*Factory,
                                                        // i.e. if it was named Chi Squared Fit, it would be
                                                        // ChiSquaredFitFactory
{
    /** The private instance of this factory so that we only have one object allowing us to populate the user selected
     * algorithm box with the current factory */
    private static AlgorithmFactoryTemplate instance = new AlgorithmFactoryTemplate();

    /** returns the instance of the factory and makes sure that only one object of it is ever created (singleton) so
     * that we can check what fit is being used easily
     * 
     * @return Returns the object instance of this Algorithm Factory */
    static AlgorithmFactoryTemplate getInstance()
    {
        return instance;
    }

    /** The constructor that allows us to create the *name* Fit Data without knowing which factory it is and also
     * determines whether or not its child can fix its slope or intercept */
    private AlgorithmFactoryTemplate()
    {
        // TODO:set whether or not this algorithm can handle fixing the slope or intercept here
        this.canGeneratedFitsFixSlope = false;
        this.canGeneratedFitsFixIntercept = false;
    }

    /** Creates a new instance of the type of FitAlogritm this Factory is This allows us to generically create the fit
     * algorithm and not have to specify before runtime what kind of linear fit we are using */
    public LinearFitStrategy createNewLinearFitStartegy(DataSet dataSet)
    {
        return new AlgorithmStrategyTemplate(dataSet);
    }

    /** Overrides the to String for the Fit Algorithm so that it displays this name for the User to select in the fit
     * algorithm drop down box */
    public String toString()
    {
        return "Name Of Your Algorithm"; // TODO: put your algorithm name in
    }

    // TODO: fill in class metadata
    /** The private class for the *algorithm name* Algorithm that does the calculations and that is made by the
     * *algorithm name* Fit Algorithm
     * 
     * @author your name
     * @version 1.0
     * @since *current LineFit version Number* */
    private class AlgorithmStrategyTemplate extends LinearFitStrategy // TODO:Rename this using the format *algorithm
                                                                      // name*Strategy, i.e. if it was named Chi Squared
                                                                      // Fit, it would be ChiSquaredFitStrategy
    {
        // TODO: declare any private variables necessary for fit here


        /** Creates the linear fit based on the passed DataSet and then calculates the fit
         * 
         * @param dataSet The DataSet that this is the fit for */
        private AlgorithmStrategyTemplate(DataSet dataSet)
        {
            // This should not be changed!, expect for the name of the constructor to reflect the class name of course

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

        // TODO: This is where your algorithm is actually implemented - you can use as many private helper functions as
        // needed
        /** Updates the values in the Fit Algorithm's Data based on the current options and values
         * 
         * @param fitTypeToUse The fit Type to use when fitting the line */
        protected void calculateLinearFit(FitType fitTypeToUse)
        {
            // algorithm stuff
            /* the ChiSquare fit for one error is the correct way to graph for one error so this should look like this
             * in most circumstances if(fitTypeToUse == FitType.BOTH_ERRORS) { //your fit algorithm for fitting both
             * errors } else { defaultChiSquareFitForSingleError(fitTypeToUse); } */
        }


        // TODO: place any needed private methods here


        // TODO: place any needed override functions here

    }
}