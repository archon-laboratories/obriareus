package com.samvbeckmann.obriareus.core;

import java.util.List;

/**
 * Data type that contains the Algorithm to be used,
 * and a list of the input parameters for that algorithm (empty if the algorithm doesn't use one)
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class AlgObject
{
    /**
     * Special parameter for the algorithm (e, l, x, etc.)
     */
    private final List<Double> inputParameters;

    /**
     * The algorithm itself
     */
    private final IAlgorithm algorithm;

    /**
     * Constructor
     */
    public AlgObject(IAlgorithm algorithm_, List<Double> inputParameters_)
    {
        algorithm = algorithm_;
        inputParameters = inputParameters_;
    } // end AlgObject

    /**
     * Runs the algorithm inside agent.
     *
     * @param curBandit The agent containing the algorithm.
     */
    public void runAlgorithm(Bandit curBandit)
    {
        algorithm.run(curBandit, inputParameters);
    }

    /**
     * @return The name of the algorithm
     */
    public String getAlgorithm()
    {
        return algorithm.getName();
    }

    /**
     * @return The list of input parameters.
     */
    public List getInputParameters()
    {
        return inputParameters;
    }
}
