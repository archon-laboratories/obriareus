package core;

import java.util.List;

/**
 * Contains the methods necessary to define an algorithm for the Multi-Armed Bandit program.
 *
 * @author Nate Beckemeyer and Sam Beckmann
 */
public interface IAlgorithm
{
    /**
     * @return the name of the algorithm
     */
    String getName();

    /**
     * Runs the algorithm.
     *
     * @param curAgent        The agent currently employing the algorithm. Contains vital data, such as the arms, budget,
     *                        memories of the arm pulls,
     * @param inputParameters The input parameter necessary for the algorithm. Can be empty if unused.
     */
    void run(Agent curAgent, List<Double> inputParameters);
}
