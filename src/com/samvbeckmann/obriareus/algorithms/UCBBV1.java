package com.samvbeckmann.obriareus.algorithms;

import com.samvbeckmann.obriareus.core.Arm;
import com.samvbeckmann.obriareus.core.ArmMemory;
import com.samvbeckmann.obriareus.core.Bandit;
import com.samvbeckmann.obriareus.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * UCB-BV 1 algorithm.
 *
 * @author Sam Beckmann, Nate Beckemeyer. Thanks to Anton Ridgeway for original implementations.
 */
public class UCBBV1 implements com.samvbeckmann.obriareus.core.IAlgorithm
{
    private static final boolean debugUCBBV = false;

    @Override
    public String getName()
    {
        return "UCB-BV1";
    }

    /**
     * Iterates through all the arms once, then pulls the arm with the greatest D-value.
     *
     * @param curBandit       The agent currently employing this algorithm.
     * @param inputParameters Null for this algorithm.
     */
    @Override
    public void run(Bandit curBandit, List<Double> inputParameters)
    {
        // Initialize variables
        Arm arms[] = curBandit.getArms();
        ArmMemory[] memories = curBandit.getMemories();
        double[] dValues = new double[arms.length];

        double lambda = curBandit.getMinCost();
        int currentBest = -1;
        int totalPulls = 0;

        // Initial phase: Pull each arm once.
        ArrayList<Integer> indices = new ArrayList<Integer>();
        Utilities.generateIndices(indices, arms.length);

        while (!indices.isEmpty())
        {
            int i = Utilities.randomIndex(indices, curBandit.getRnd());
            curBandit.pull(i);
            totalPulls++;
            if (debugUCBBV) System.out.println(Utilities.getPullResult(getName(), i, arms[i], memories[i]));
        }

        if (debugUCBBV) System.out.println("[" + getName() + "] Initial phase complete.");

        // Exploitation phase
        while (curBandit.getBudget() >= curBandit.getMinCost())
        {
            totalPulls++;
            for (int i = 0; i < arms.length; i++)
            {
                dValues[i] = getDValue(memories[i], lambda, totalPulls);
                if (debugUCBBV) System.out.println("[" + getName() + "] D for arm " + i + " set to: " + dValues[i]);
            }

            int start = curBandit.getRnd().nextInt(arms.length);
            for (int i = 0; i < arms.length; i++)
            {
                int index = (i + start) % arms.length;
                if (arms[index].getCost() <= curBandit.getBudget()
                        && (currentBest < 0 || dValues[index] > dValues[currentBest]))
                {
                    currentBest = index;
                }
            }

            curBandit.pull(currentBest);

            if (debugUCBBV) System.out.println(Utilities.getPullResult(
                    getName(), currentBest, arms[currentBest], memories[currentBest]));

        }
        if (debugUCBBV) System.out.println("[" + getName() + "] Budget Exhausted. Trial Complete.");
    }

    /**
     * Returns the dValue for a given arm. Used for UCB-BV algorithms.
     *
     * @param thisArm    Arm that D will be calculated for.
     * @param lambda     Minimum arm cost. (Or best guess)
     * @param totalPulls Total number of arms that have been pulled so far.
     * @return The dValue for the given arm, with the given boundType.
     */
    private static double getDValue(ArmMemory thisArm, double lambda, int totalPulls)
    {
        double root = Math.sqrt(Math.log(totalPulls - 1) / thisArm.getPulls());
        return thisArm.getRatio() + (1 + (1 / lambda)) * root / (lambda - root);
    }
}
