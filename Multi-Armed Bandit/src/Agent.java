import java.util.List;
import java.util.Random;

public class Agent
{
    /**
     * The random generator for Agent.
     */
    private Random rnd;

    /**
     * The arms that the agent pulls.
     */
    private static Arm arms[];

    /**
     * Bandit for this trial, contains all the rewards for this trial.
     */
    private Bandit bandit;

    /**
     * The minimum cost to pull an arm.
     */
    private static double minCost;

    /**
     * The total reward from pulling arms so far.
     */
    private double totalReward = 0;

    /**
     * The total cost to pull all of the arms.
     */
    private static double totalCost = 0;

    /**
     * Has minCost been initialized yet? It only needs to happen once. Optimization variable.
     */
    private static boolean initialized = false;

    /**
     * The memories that the Agent has of the arm pulls.
     */
    private ArmMemory memories[];

    /**
     * The index of the current best arm in terms of benefit/cost ratio.
     */
    private int bestArm;

    /**
     * The index of the next best arm in terms of benefit/cost ratio, in case the current base drops behind.
     */
    private int nextBestArm;

    /**
     * The total current budget of the agent.
     */
    private double budget;

    /**
     * The total number of pulls.
     */
    private int totalPulls = 0;

    /**
     * Sets the minCost and totalCost variables
     */
    public void findCosts()
    {
        double min = arms[0].getCost();
        for (Arm current : arms)
        {
            double curCost = current.getCost();
            if (curCost < min)
                min = curCost;
            totalCost += curCost;
        }
    }

    /**
     * Gets the best arm Agent knows of, in terms of mean reward/cost ratio.
     *
     * @return the index of the best arm
     */
    public int getBestArm()
    {
        int best = 0; // Index of the best arm in terms of mean reward/cost ratio.

        for (int j = 1; j < memories.length; j++)
        {
            if (memories[best].getCost() > budget && memories[j].getCost() <= budget) // Is it comparably usable?
                best = j;

            if ((memories[j].getRatio() > memories[best].getRatio() && memories[j].getCost() <= budget))
                best = j;
        }
        return best;

    }

    /**
     * Gets the second best arm Agent knows of, in terms of mean reward/cost ratio.
     *
     * @return the index of the second best arm
     */
    public int getSecondBest()
    {
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
            {
                secondBest = j;
            }
        }
        return secondBest;
    }

//    /**
//     * Returns the kth smallest value in the given array between the first and last indexes.
//     *
//     * @param k the element used as the pivot point
//     * @param array array to find the element in
//     * @param first first index to be considered
//     * @param last last index to be considered
//     * @return the kth smallest element in the array
//     */
//    public static <E extends Comparable<? super E>> E kSmall(int k, E[] array, int first, int last)
//    {
//        int pI = Utilities.partition(array, first, last);
//        if (pI - first + 1 == k)
//        {
//            return array[pI];
//        } else if (pI - first + 1 > k)
//        {
//            return kSmall(k, array, first, pI-1);
//        } else
//        {
//            return kSmall(k-(pI-first+1), array, pI+1, last);
//        }
//    }

//    /**
//     * Returns the index of the kth best arm Agent knows of, in terms of mean reward/cost ratio.
//     *
//     * @param k the element used as the pivot point
//     * @return the index of the kth best arm
//     */
//    public int getKthBest(int k)
//    {
//
//
//        return getKthBest(k, 0, memories.length - 1);
//    }

    public int getBestFromFeasibles(List<Integer> feasibles)
    {
        int best = -1; // Index of the best arm in terms of mean reward/cost ratio.

        for (int j : feasibles)
        {
            if (best == -1)
                best = j;
            if (memories[j].getRatio() > memories[best].getRatio()&& memories[j].getCost() <= budget)
                best = j;
        }
        return best;
    }

//    /**
//     * Returns the kth best arm Agent knows of, in terms of mean reward/cost ratio.
//     *
//     * @param k the element used as the pivot point
//     * @param first  first index to be considered
//     * @param last last index to be considered
//     * @return the index of the kth best arm
//     */
//    public int getKthBest(int k, int first, int last)
//    {
//        int pI = Utilities.partition(memories, first, last);
//        if (pI - first + 1 == k)
//        {
//            return pI;
//        } else if (pI - first + 1 < k)
//        {
//            return getKthBest(k, first, pI-1);
//        } else
//        {
//            return getKthBest(k - (pI - first + 1), pI + 1, last);
//        }
//    }

    /**
     * The constructor for the agent. Pass a budget and the arms array.
     */
    public Agent(int initBudget, Arm armRefs[], Bandit trialBandit)
    {
        budget = initBudget;
        arms = armRefs;
        bandit = trialBandit;
        double currMin = Double.MAX_VALUE;
        int count = 0;
        memories = new ArmMemory[armRefs.length];
        for (Arm current : armRefs)
        {
            memories[count++] = new ArmMemory(current.getCost());
            currMin = Math.min(currMin, current.getCost());
        }

        minCost = currMin;

        if (!initialized)
        {
            findCosts();
            initialized = true;
        }
    }


    /**
     * Pulls the arm at index toPull
     *
     * @param toPull the index of the arm to pull
     */
    public void pull(int toPull)
    {
        ArmMemory current = memories[toPull];

        if (budget >= current.getCost())
        {
            budget -= current.getCost();
            memories[toPull].addPull(bandit.pullArm(toPull, this));
            totalPulls++;
            totalReward += memories[toPull].getRecentReward();
        }
    }

    /**
     * @return the budget remaining for the agent
     */
    public double getBudget() { return budget; }

    /**
     * @return the array of arms of the agent
     */
    public Arm[] getArms()
    {
        return arms;
    }

    /**
     * @return the minimum cost to pull an arm
     */
    public double getMinCost()
    {
        return minCost;
    }

    /**
     * @return total cost to pull all arms
     */
    public double getTotalCost()
    {
        return totalCost;
    }

    /**
     * @return the total number of pulls performed by this agent
     */
    public int getTotalPulls() { return totalPulls; }

    /**
     * @return the memories of all of the arms
     */
    public ArmMemory[] getMemories()
    {
        return memories;
    }

    /**
     * @return the total reward of the agent.
     */
    public double getTotalReward()
    {
        return totalReward;
    }

    public void setBudget(double budget)
    {
        this.budget = budget;
    }
}
