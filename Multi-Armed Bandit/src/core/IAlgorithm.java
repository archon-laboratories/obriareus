package core;

import java.util.List;
import java.util.Random;

/**
 * Contains the methods necessary to define an algorithm for the Multi-Armed Bandit program.
 *
 * @author Nate Beckemeyer and Sam Beckmann
 */
public interface IAlgorithm
{
    Random rnd = new Random();

    /**
     * @return the name of the algorithm
     */
    String getName();

    /**
     * Runs the algorithm.
     *
     * @param curBandit       The agent currently employing the algorithm. Contains vital data, such as the arms, budget,
     *                        memories of the arm pulls,
     * @param inputParameters The input parameter necessary for the algorithm. Can be empty if unused.
     */
    void run(Bandit curBandit, List<Double> inputParameters);
}
