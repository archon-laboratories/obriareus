package defaultAlgorithms;

import core.Agent;
import core.Arm;
import core.ArmMemory;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * sOAAv algorithm.
 *
 * @author Sam Beckmann, Nate Beckemeyer. Thanks to Anton Ridgeway for original implementations.
 */
public class SOAAv implements core.IAlgorithm
{
    private static final boolean debugSOAAv = false;

    @Override
    public String getName()
    {
        return "sOAAv";
    }

    @Override
    public void run(Agent curAgent, List<Double> inputParameters)
    {
        double xValue = inputParameters.get(0);

        // Initialize variables
        Arm[] arms = curAgent.getArms();
        ArmMemory[] memories = curAgent.getMemories();
        ArrayList<Integer> activeArms = new ArrayList<Integer>();

        int numPullsInPass;
        double passAverageRatio;

        // Add all arms to list to be pulled for first iteration
        for(int i = 0; i < arms.length; i++)
            activeArms.add(i);

        // Loop until budget exhausted
        while (curAgent.getBudget() >= curAgent.getMinCost())
        {
            numPullsInPass = 0;
            passAverageRatio = 0;

            for (int i : activeArms)
            {
                if (arms[i].getCost() <= curAgent.getBudget())
                {
                    curAgent.pull(i);
                    numPullsInPass++;
                    passAverageRatio += memories[i].getRecentReward() / arms[i].getCost();
                    if (debugSOAAv) System.out.println(Utilities.getPullResult(getName(), i, arms[i], memories[i]));
                }
            }

            if (numPullsInPass > 0)
            {
                passAverageRatio = passAverageRatio / numPullsInPass;
                activeArms.clear();

                // Update activeArms for next iteration
                for (int i = 0; i < arms.length; i++)
                {
                    if (arms[i].getCost() <= curAgent.getBudget()
                            && memories[i].getRatio() >= (1 + xValue) * passAverageRatio)
                    {
                        activeArms.add(i);
                    }
                }
                if (activeArms.size() == 0)
                {
                    activeArms.add(curAgent.getBestArm());
                }
            }
        } // TODO: Make random choice of arms though activeArms, to ensure no bias when budget is exhausted. Minor.

        if (debugSOAAv) System.out.println("[" + getName() + "] Budget Exhausted. Trial complete.");
    }
}
