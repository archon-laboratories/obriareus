package utilities;

import core.Agent;
import core.Arm;
import core.ArmMemory;

import java.util.List;
import java.util.Random;

/**
 * Utility classes for the Multi-Armed core.Bandit problem
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public final class Utilities
{
    private static Random rnd = new Random();

    /**
     * Gets an array of linear spaced elements of a given size.
     *
     * @param numItems number of items in the array to return
     * @return A double array of linearly spaced items of size numItems
     */
    public static double[] getLinear(int numItems)
    {
        double[] linValues = new double[numItems];
        for (int i = 0; i < linValues.length; i++)
        {
            linValues[i] = (i + 1.) / numItems;
        }
        return linValues;
    }

    /**
     * Gets an array of superLinear spaced elements of a given size.
     *
     * @param numItems number of items in the array to return
     * @return A double array of superlinearly spaced items of size numItems
     */
    public static double[] getSuperlinear(int numItems)
    {
        double[] superlinValues = new double[numItems];
        for (int i = 0; i < superlinValues.length; i++)
        {
            superlinValues[i] = Math.log(((Math.exp(1) - 1) * (i + 1) / numItems) + 1);
        }
        return superlinValues;
    }

    /**
     * Gets an array of subLinear spaced elements of a given size.
     *
     * @param numItems number of items in the array to return
     * @return A double array of sublinearly spaced items of size numItems
     */
    public static double[] getSublinear(int numItems)
    {
        double[] sublinValues = new double[numItems];
        for (int i = 0; i < sublinValues.length; i++)
        {
            sublinValues[i] = Math.pow(10 * ((i + 1.) / numItems), 2) / 100;
        }
        return sublinValues;
    }

    /**
     * Restores the given ArrayList of indices to contain all indices.
     *
     * @param indices The ArrayList of indices to generate
     * @param bound   The bound of indices to be used should remainingIndices need to be regenerated.
     */
    public static void generateIndices(List<Integer> indices, int bound)
    {
        indices.clear();
        for (int i = 0; i < bound; i++)
            indices.add(i);
    }

    /**
     * Generates a random armIndex given an ArrayList of remaining armIndices. Then removes the
     *
     * @param remainingIndices The ArrayList of indices that have yet to be used in the algorithm's random selection.
     * @return the index of the arm to be checked
     */
    public static int randomIndex(List<Integer> remainingIndices)
    {
        if (remainingIndices.size() == 0)
            return -1;

        int indexLocation = rnd.nextInt(remainingIndices.size()); // The location in remainingIndices of the arm's index
        int index = remainingIndices.get(indexLocation); // Location in arms of the arm.

        remainingIndices.remove(indexLocation);
        return index;
    }

    /**
     * Gets the result of a given pull, for use in debugging or logging.
     *
     * @param algName Name of the algorithm doing the pull
     * @param armNum  Number of the arm being pulled
     * @param arm     Arm that is being pulled
     * @param memory  ArmMemory of the arm that is being pulled
     * @return A formatted String of the results of a given pull.
     */
    public static String getPullResult(String algName, int armNum, Arm arm, ArmMemory memory)
    {
        return
                String.format("[%s] Pulled arm %d (mean = [%.3f], sd = [%.3f], est. ratio = [%.3f]); Got Reward %.3f",
                        algName, armNum, memory.getMeanReward(),
                        arm.getStdDev(), memory.getRatio(), memory.getRecentReward());
    }

    /**
     * Gets the best arm Agent knows of, in terms of mean reward/cost ratio.
     *
     * @param caller The agent calling the method
     * @return the index of the best arm
     */
    public static int getBestArm(Agent caller)
    {
        ArmMemory[] memories = caller.getMemories();
        double budget = caller.getBudget();
        int best = 0; // Index of the best arm in terms of mean reward/cost ratio.

        for (int j = 1; j < memories.length; j++)
        {
            if (memories[best].getCost() > budget && memories[j].getCost() <= budget) // Is it comparably usable?
                best = j;

            if ((memories[j].getRatio() > memories[best].getRatio() && memories[j].getCost() <= budget))
                best = j;
        }
        return best;
    } // end getBestArm

    /**
     * Gets the second best arm Agent knows of, in terms of mean reward/cost ratio.
     *
     * @param caller The agent calling the method
     * @return the index of the second best arm
     */
    public static int getSecondBest(Agent caller)
    {
        ArmMemory[] memories = caller.getMemories();
        double budget = caller.getBudget();
        int best = 0;
        int secondBest = 1;

        for (int j = 2; j < memories.length; j++)
        {
            if (memories[best].getCost() > budget && memories[j].getCost() <= budget) // Is it comparably usable?
                best = j;
            else if (memories[secondBest].getCost() > budget && memories[j].getCost() <= budget) // How about this one?
                secondBest = j;

            if ((memories[j].getRatio() > memories[best].getRatio() && memories[j].getCost() <= budget))
            {
                secondBest = best;
                best = j;
            } else if (memories[j].getRatio() > memories[secondBest].getRatio() && memories[j].getCost() <= budget)
                secondBest = j;
        }
        return secondBest;
    } // end getSecondBest

    /**
     * Gets the best arms from a sent set of possible arms.
     *
     * @param caller    Agent that is calling this method.
     * @param feasibles List of the arms that can possibly be chosen as best.
     * @return Index of the best arm.
     */
    public static int getBestFromFeasibles(Agent caller, List<Integer> feasibles)
    {
        int best = -1; // Index of the best arm in terms of mean reward/cost ratio.

        for (int j : feasibles)
        {
            if (best == -1)
                best = j;
            if (caller.getMemories()[j].getRatio() > caller.getMemories()[best].getRatio()
                    && caller.getMemories()[j].getCost() <= caller.getBudget())
                best = j;
        }
        return best;
    } // end getBestFromFeasibles
}
