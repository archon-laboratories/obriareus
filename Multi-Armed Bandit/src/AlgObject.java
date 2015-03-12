import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Data type that contains the enum value of an algorithm, from <code>Algorithms.AlgorithmNames</code>
 * and the second input parameter for that algorithm (set to zero if the algorithm doesn't use one)
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class AlgObject <E>
{
    /**
     * The algorithm itself
     */
    private Algorithm<E> algorithm;

    /**
     * Special parameter for the algorithm (e, l, x, etc.)
     */
    List <E> inputParameters;

    public AlgObject(Algorithm<E> algorithm_)
    {
        this(algorithm_, new ArrayList<E>());
    }

    public AlgObject(Algorithm<E> algorithm_, List <E> inputParameters_)
    {
        algorithm = algorithm_;
        inputParameters = inputParameters_;
    }

    public void runAlgorithm(Agent curAgent) {
        algorithm.run(curAgent, inputParameters);
    }

    public String getAlgorithm()
    {
        return algorithm.getAlgorithm();
    }

    public List getInputParameters()
    {
        return inputParameters;
    }
}
