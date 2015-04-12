package com.samvbeckmann.obriareus.defaultAlgorithms;

import com.samvbeckmann.obriareus.core.Arm;
import com.samvbeckmann.obriareus.core.ArmMemory;
import com.samvbeckmann.obriareus.core.Bandit;
import com.samvbeckmann.obriareus.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Epsilon-first algorithm.
 *
 * @author Sam Beckmann, Nate Beckemeyer. Thanks to Anton Ridgeway for original implementations.
 */
public class EFirst implements com.samvbeckmann.obriareus.core.IAlgorithm
{
    private static final boolean debugEFirst = false;

    @Override
    public String getName()
    {
        return "e-First";
    }

    /**
     * The eFirst algorithm. Begins with a period of exploration determined by the epsilon value,
     * then chooses the best available arm. Online algorithm.
     *
     * @param curBandit       The current agent employing this algorithm.
     * @param inputParameters [0] --> The epsilon value <= 1 for the exploration budget
     *                        (exploration budget = epsilon * budget).
     */
    @Override
    public void run(Bandit curBandit, List<Double> inputParameters)
    {
        // Initialize variables
        Arm[] arms = curBandit.getArms();
        ArmMemory[] memories = curBandit.getMemories();
        double epsilon = inputParameters.get(0);

        double eBudget = curBandit.getBudget() * epsilon; // Exploration budget for the algorithm
        if (eBudget <= curBandit.getTotalCost())
            eBudget = curBandit.getTotalCost();

        if ((curBandit.getBudget() - eBudget) < 0)
            System.out.println("Budget too low.");

        // Declare the arraylist of remaining indices
        ArrayList<Integer> remainingIndices = new ArrayList<Integer>(arms.length); // Stores the location of arms
        // that have yet to be pulled

        Utilities.generateIndices(remainingIndices, arms.length);

        // Exploration
        while (eBudget >= curBandit.getMinCost())
        {
            if (remainingIndices.size() == 0)
                Utilities.generateIndices(remainingIndices, arms.length);

            int armIndex = Utilities.randomIndex(remainingIndices, curBandit.getRnd()); // Get a random remaining index
            if (arms[armIndex].getCost() <= eBudget)
            {
                // Pull it!
                curBandit.pull(armIndex);
                if (debugEFirst)
                    System.out.println(Utilities.getPullResult(
                            getName(), armIndex, arms[armIndex], memories[armIndex]));

                eBudget -= arms[armIndex].getCost();
            }
        }
        // eBudget has run out. Begin exploitation phase.
        if (debugEFirst) System.out.println("[" + getName() + "] Exploration budget exhausted.");

        int bestArm = Utilities.getBestArm(curBandit); // Get the index of the first largest element
        int secondBestArm = Utilities.getSecondBest(curBandit, bestArm); // Get the index of the second largest element

        while (curBandit.getBudget() >= curBandit.getMinCost())
        {
            curBandit.pull(bestArm);
            if (debugEFirst)
                System.out.println(Utilities.getPullResult(getName(), bestArm, arms[bestArm], memories[bestArm]));

            if (arms[bestArm].getCost() > curBandit.getBudget()) // Does the best arm cost too much?
            {
                // Reassign the arms, taking into account budget constraints.
                bestArm = Utilities.getBestArm(curBandit);
                secondBestArm = Utilities.getSecondBest(curBandit, bestArm);
            }

            if (memories[bestArm].getRatio() < memories[secondBestArm].getRatio()) // Did the best arm fall behind?
            {
                // Promote the second best, and find the new second best.
                bestArm = secondBestArm;
                secondBestArm = Utilities.getSecondBest(curBandit, bestArm);
            }
        }

        if (debugEFirst) System.out.println("[" + getName() + "] Budget Exhausted. Trial complete.");
    }
}
