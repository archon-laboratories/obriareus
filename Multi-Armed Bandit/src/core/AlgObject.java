package core;

import java.util.List;

/**
 * Data type that contains the enum value of an algorithm, from <code>Algorithms.AlgorithmNames</code>
 * and the second input parameter for that algorithm (set to zero if the algorithm doesn't use one)
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class AlgObject
{
    /**
     * Special parameter for the algorithm (e, l, x, etc.)
     */
    List<Double> inputParameters;
    /**
     * The algorithm itself
     */
    private IAlgorithm algorithm;

    public AlgObject(IAlgorithm algorithm_, List<Double> inputParameters_)
    {
        algorithm = algorithm_;
        inputParameters = inputParameters_;
    } // end AlgObject

    public void runAlgorithm(Agent curAgent)
    {
        algorithm.run(curAgent, inputParameters);
    }

    public String getAlgorithm()
    {
        return algorithm.getName();
    }

    public List getInputParameters()
    {
        return inputParameters;
    }
}
