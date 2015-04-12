package com.samvbeckmann.obriareus.defaultAlgorithms;

import com.samvbeckmann.obriareus.core.Arm;
import com.samvbeckmann.obriareus.core.ArmMemory;
import com.samvbeckmann.obriareus.core.Bandit;
import com.samvbeckmann.obriareus.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Survival of the Above Average (SOAAv) algorithm.
 *
 * @author Sam Beckmann, Nate Beckemeyer. Thanks to Anton Ridgeway for original implementations.
 */
public class SOAAv implements com.samvbeckmann.obriareus.core.IAlgorithm
{
    private static final boolean debugSOAAv = false;

    public String getName()
    {
        return "SOAAv";
    }

    public void run(Bandit curBandit, List<Double> inputParameters)
    {
        double xValue = inputParameters.get(0);

        // Initialize variables
        Arm[] arms = curBandit.getArms();
        ArmMemory[] memories = curBandit.getMemories();
        ArrayList<Integer> activeArms = new ArrayList<Integer>();
        double passAverageRatio;

        // Add all arms to list to be pulled for first iteration
        for (int i = 0; i < arms.length; i++)
            activeArms.add(i);

        // Loop until budget exhausted
        while (curBandit.getBudget() >= curBandit.getMinCost())
        {
            passAverageRatio = 0;

            List<Integer> indices = new ArrayList<Integer>();
            Utilities.generateIndices(indices, activeArms.size());

            while (!indices.isEmpty())
            {
                int armToPull = activeArms.get(Utilities.randomIndex(indices, curBandit.getRnd()));

                if (arms[armToPull].getCost() <= curBandit.getBudget())
                {
                    curBandit.pull(armToPull);
                    if (debugSOAAv) System.out.println(Utilities.getPullResult(
                            getName(), armToPull, arms[armToPull], memories[armToPull]));

                }
            }

            for (Integer activeArm : activeArms)
                passAverageRatio += memories[activeArm].getRatio() / arms[activeArm].getCost();

            passAverageRatio = passAverageRatio / activeArms.size();
            activeArms.clear();

            // Update activeArms for next iteration
            for (int i = 0; i < arms.length; i++)
            {
                if (arms[i].getCost() <= curBandit.getBudget()
                        && memories[i].getRatio() >= (1 + xValue) * passAverageRatio)
                {
                    activeArms.add(i);
                }
            }
            if (activeArms.size() == 0)
            {
                activeArms.add(Utilities.getBestArm(curBandit));
            }
        }
        if (debugSOAAv) System.out.println("[" + getName() + "] Budget Exhausted. Trial complete.");
    }
}
