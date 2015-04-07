package core;

import java.util.Random;

/**
 * Defines a bandit.
 * A bandit uses an algorithm to pull arms until its
 * budget is expired.
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Bandit
{
    /**
     * The arms that the bandit pulls.
     */
    private static Arm arms[];

    /**
     * The minimum cost to pull an arm.
     */
    private static double minCost;

    /**
     * The total cost to pull all of the arms.
     */
    private static double totalCost = 0;

    /**
     * Random for the Bandit.
     * All algorithm random operations should be
     * done through this random.
     */
    private Random rnd = new Random();

    /**
     * Algorithm that this bandit is running.
     */
    private final AlgObject algorithm;

    /**
     * The total reward from pulling arms so far.
     */
    private double totalReward = 0;

    /**
     * The memories that the Bandit has of the arm pulls.
     */
    private ArmMemory memories[];

    /**
     * The total current budget of the bandit.
     */
    private double budget;

    /**
     * The total number of pulls.
     */
    private int totalPulls = 0;

    /**
     * The constructor for the bandit. Pass a budget and the arms array.
     */
    public Bandit(int initBudget, AlgObject algorithm_)
    {
        budget = initBudget;
        algorithm = algorithm_;

        int count = 0;
        memories = new ArmMemory[arms.length];
        for (Arm current : arms)
            memories[count++] = new ArmMemory(current.getCost());
    } // end constructor

    /**
     * Implements the arms for the Bandit
     * @param armRefs The reference to the arms that were created by Dataset
     */
    public static void implementArms(Arm[] armRefs)
    {
        arms = armRefs;
        findCosts();
    }

    /**
     * Sets the minCost and totalCost variables
     */
    private static void findCosts()
    {
        double min = arms[0].getCost();

        // Implemented as a foreach loop because totalCost needs to be incremented by the cost of the first arm as well
        for (Arm current : arms)
        {
            double curCost = current.getCost();
            if (curCost < min)
                min = curCost;
            totalCost += curCost;
        }
        minCost = min;
    } // end findCosts

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
            memories[toPull].addPull(arms[toPull].getReward());
            totalPulls++;
            totalReward += memories[toPull].getRecentReward();
        }
    } // end pull

    /**
     * @return the budget remaining for the bandit
     */
    public double getBudget()
    {
        return budget;
    }

    /**
     * @return the array of arms of the bandit
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
     * @return the total number of pulls performed by this bandit
     */
    public int getTotalPulls()
    {
        return totalPulls;
    }

    /**
     * @return the memories of all of the arms
     */
    public ArmMemory[] getMemories()
    {
        return memories;
    }

    /**
     * @return the total reward of the bandit.
     */
    public double getTotalReward()
    {
        return totalReward;
    }

    public Random getRnd()
    {
        return rnd;
    }

    /**
     * Runs the algorithm associated with this bandit.
     */
    public void run()
    {
        algorithm.runAlgorithm(this);
    }
}
