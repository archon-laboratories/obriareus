package defaultAlgorithms;

import core.Agent;
import core.Arm;
import core.ArmMemory;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * fKUBE algorithm.
 *
 * @author Sam Beckmann, Nate Beckemeyer. Thanks to Anton Ridgeway for original implementations.
 */
public class FKUBE implements core.IAlgorithm
{
    @Override
    public String getName()
    {
        return "fKUBE";
    }

    /**
     * The Fractional Knapsack-based Upper Confidence Bound Exploration and Exploitation (fKUBE) algorithm.
     * Pulls the arm with the estimated highest confidence bound:cost ratio (item density).
     *
     * @param curAgent        The agent currently employing this algorithm.
     * @param inputParameters Null for this algorithm.
     */
    @Override
    public void run(Agent curAgent, List<Double> inputParameters)
    {
        //----------------------------------------
        //Localize variables
        Arm[] arms = curAgent.getArms();
        int numArms = arms.length;
        double minCost = curAgent.getMinCost();
        ArmMemory [] memories = curAgent.getMemories();

        // Prepare algorithm
        int bestArm = -1;
        ArrayList<Integer> temp = new ArrayList<Integer>();
        Utilities.generateIndices(temp, numArms);

        int lastBestArm = bestArm;
        int time = 0;

        //Main Fractional KUBE loop
        while (curAgent.getBudget() >= minCost)
        {
            if (temp.size() > 0) // initial phase
            {
                //Make sure we can't go over budget here.
                int x = Utilities.randomIndex(temp);
                if (arms[x].getCost() <= curAgent.getBudget())
                {
                    curAgent.pull(x);
                    time++;
                }
            } else
            { // combined exploration/exploitation phase

                //Find the current best arm, pull it, and re-estimate its value by the result
                bestArm = -1;
                for (int i = 0; i < numArms; i++)
                {
                    if (curAgent.getMemories()[i].getCost() <= curAgent.getBudget() &&
                            (bestArm < 0 || fKubeEst(memories[i], time) >
                                    fKubeEst(memories[curAgent.getBestArm()], time)))
                        bestArm = i;
                }

                if (lastBestArm != bestArm || lastBestArm == -1)
                    lastBestArm = bestArm;

                curAgent.pull(bestArm);
                time++;
            }
        }//end else (which phase are we in)
    }

    /**
     * Estimates the confidence bound:cost ratio of the arm (item density).
     * @param thisArm
     *          the arm whose confidence bound:cost ratio is to be estimated
     * @param time
     *          the timestep of the fKUBE algorithm
     * @return
     *          the confidence bound:cost ratio of the arm
     */
    private static double fKubeEst(ArmMemory thisArm, int time)
    {
        return thisArm.getRatio() + Math.sqrt(2 * Math.log(time) / thisArm.getPulls()) / thisArm.getCost();
    }
}
