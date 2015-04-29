package com.samvbeckmann.obriareus.algorithms;

import com.samvbeckmann.obriareus.core.Arm;
import com.samvbeckmann.obriareus.core.Bandit;
import com.samvbeckmann.obriareus.utilities.Utilities;

import java.util.List;

/**
 * fKDE (Fractional Knapsack Based Decreasing ε–greedy)
 *
 * @author Sam Beckmann, Nate Beckemeyer. Thanks to Anton Ridgeway for original implementations.
 */
public class FKDE implements com.samvbeckmann.obriareus.core.IAlgorithm
{
    private static final boolean debugFKDE = false;

    @Override
    public String getName()
    {
        return "fKDE";
    }

    /**
     * Fractional Knapsack Based Decreasing ε–greedy (fKDE)
     *
     * @param curBandit       The agent currently employing the algorithm
     * @param inputParameters [0] --> gamma, The tuning factor > 0 associated with fKDE
     */
    @Override
    public void run(Bandit curBandit, List<Double> inputParameters)
    {
        double gamma = inputParameters.get(0);

        if (gamma <= 0)
        {
            System.out.println("Non-positive gamma value; KDE cannot function.");
            return;
        }

        int t = 0;
        Arm[] arms = curBandit.getArms();
        int numArms = arms.length;

        int bestArm; //The best arm to pull: I+

        while (curBandit.getBudget() >= curBandit.getMinCost())
        {
            bestArm = Utilities.getBestArm(curBandit);
            double epsT = Math.min(1, gamma / (t + 1));
            if (debugFKDE)
                System.out.println("Epsilon T: " + epsT);
            int numFeasibleArms = 0; // The number of feasible arms

            double[] armProb = new double[numArms]; // Probability of pulling the corresponding arms
            for (int z = 0; z < numArms; z++)
            {
                if (arms[z].getCostMean() <= curBandit.getBudget())
                {
                    armProb[z] = epsT; // Set it up
                    numFeasibleArms++;
                } else
                    armProb[z] = 0;
            }

            // Assign probabilities
            // ε/K, where K is the number of arms that can be pulled minus one (to account for the separate probability
            // of 1 - ε for the best arm)
            for (int z = 0; z < numArms; z++)
                if (armProb[z] > 0)
                {
                    armProb[z] /= numFeasibleArms - 1;
                    if (debugFKDE)
                        System.out.println("Arm " + z + " probability: " + armProb[z]);
                }

            // Assign the best arm the probability of 1 - ε
            armProb[bestArm] = 1 - epsT;
            if (debugFKDE)
                System.out.println("Best arm probability: " + (1 - epsT));

            // Calculate cumulative probabilities; the last arm should have a cumulative probability of 1
            double[] cmlProb = new double[numArms];
            cmlProb[0] = armProb[0];

            for (int z = 1; z < numArms; z++)
                cmlProb[z] = cmlProb[z - 1] + armProb[z];

            double randomVal = curBandit.getRnd().nextDouble();

            int pullIndex = -1; // The index of the arm to pull

            for (int z = 0; z < numArms && pullIndex < 0; z++)
            {
                // Randomly select an arm from amongst the arms that can be pulled
                if (randomVal < cmlProb[z] && armProb[z] > 0)
                    pullIndex = z;
            }

            if (debugFKDE)
                Utilities.getPullResult(getName(), pullIndex, arms[pullIndex], curBandit.getMemories()[pullIndex]);
            curBandit.pull(pullIndex);
            t++;
        }
    }
}
