/**
 * Data type that contains the enum value of an algorithm, from <code>Algorithms.AlgorithmNames</code>
 * and the second input parameter for that algorithm (set to zero if the algorithm doesn't use one)
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class AlgObject
{
    /**
     * Name of the algorithm to be run.
     */
    private Algorithms.AlgorithmNames algorithm;

    /**
     * Special parameter for the algorithm (e, l, x, etc.)
     */
    private double inputParameter;

    public AlgObject(String algorithm_)
    {
        this(algorithm_, 0);
    }

    public AlgObject(String algorithm_, double inputParameter_)
    {
        algorithm = Algorithms.AlgorithmNames.valueOf(algorithm_);
        inputParameter = inputParameter_;
    }

    public Algorithms.AlgorithmNames getAlgorithm()
    {
        return algorithm;
    }

    public double getInputParameter()
    {
        return inputParameter;
    }
}
