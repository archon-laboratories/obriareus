package defaultAlgorithms;

import core.Agent;
import core.Arm;
import core.ArmMemory;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * e-first algorithm.
 *
 * @author Sam Beckmann, Nate Beckemeyer. Thanks to Anton Ridgeway for original implementations.
 */
public class EFirst implements core.IAlgorithm
{
    @Override
    public String getName()
    {
        return "e-First";
    }

    /**
     * The eFirst algorithm. Begins with a period of exploration determined by the epsilon value,
     * then chooses the best available arm. Online algorithm.
     *
     * @param curAgent        The current agent employing this algorithm.
     * @param inputParameters [0] --> The epsilon value <= 1 for the exploration budget
     *                        (exploration budget = epsilon * budget).
     */
    @Override
    public void run(Agent curAgent, List<Double> inputParameters)
    {
        // Initialize variables
        Arm[] arms = curAgent.getArms();
        ArmMemory[] memories = curAgent.getMemories();
        double epsilon = inputParameters.get(0);

        double eBudget = curAgent.getBudget() * epsilon; // Exploration budget for the algorithm
        if (eBudget <= curAgent.getTotalCost())
            eBudget = curAgent.getTotalCost();

        if ((curAgent.getBudget() - eBudget) < 0)
            System.out.println("Budget too low.");

        // Declare the arraylist of remaining indices
        ArrayList<Integer> remainingIndices = new ArrayList<Integer>(arms.length); // Stores the location of arms
        // that have yet to be pulled

        Utilities.generateIndices(remainingIndices, arms.length);

        // Exploration
        while (eBudget >= curAgent.getMinCost())
        {
            if (remainingIndices.size() == 0)
                Utilities.generateIndices(remainingIndices, arms.length);

            int armIndex = Utilities.randomIndex(remainingIndices); // Get a random remaining index
            if (arms[armIndex].getCost() <= eBudget)
            {
                // Pull it!
                curAgent.pull(armIndex);
                eBudget -= arms[armIndex].getCost();
            }
        }
        // eBudget has run out. Begin exploitation phase.

        int bestArm = curAgent.getBestArm(); // Get the index of the first largest element
        int secondBestArm = curAgent.getSecondBest(); // Get the index of the second largest element

        while (curAgent.getBudget() >= curAgent.getMinCost())
        {
            curAgent.pull(bestArm);

            if (arms[bestArm].getCost() > curAgent.getBudget()) // Does the best arm cost too much?
            {
                // Reassign the arms, taking into account budget constraints.
                bestArm = curAgent.getBestArm();
                secondBestArm = curAgent.getSecondBest();
            }

            if (memories[bestArm].getRatio() < memories[secondBestArm].getRatio()) // Did the best arm fall behind?
            {
                // Promote the second best, and find the new second best.
                bestArm = secondBestArm;
                secondBestArm = curAgent.getSecondBest();
            }
        }
    }
}
