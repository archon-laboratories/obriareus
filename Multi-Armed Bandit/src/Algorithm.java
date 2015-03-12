import java.util.List;

/**
 * @author Nate Beckemeyer and Sam Beckmann
 */
public interface Algorithm <E>
{
    /**
     * @return the name of the algorithm
     */
    public String getAlgorithm();

    /**
     * Runs the algorithm
     * @param curAgent The agent currently employing the algorithm. Contains vital data, such as the arms, budget,
     *                 memories of the arm pulls,
     * @param inputParameters The input parameter necessary for the algorithm. Can be empty if unused.
     */
    public void run(Agent curAgent, List<E> inputParameters);
}
