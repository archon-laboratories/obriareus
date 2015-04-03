package core;

import java.util.List;

public class Agent
{
    /**
     * The arms that the agent pulls.
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
     * Has minCost been initialized yet? It only needs to happen once. Control flag.
     */
    private static boolean initialized = false;

    /**
     * The random generator for Agent.
     */
    private AlgObject algorithm;

    /**
     * Bandit for this trial, contains all the rewards for this trial.
     */
    private Bandit bandit;

    /**
     * The total reward from pulling arms so far.
     */
    private double totalReward = 0;

    /**
     * The memories that the Agent has of the arm pulls.
     */
    private ArmMemory memories[];

    /**
     * The total current budget of the agent.
     */
    private double budget;

    /**
     * The total number of pulls.
     */
    private int totalPulls = 0;

    /**
     * The constructor for the agent. Pass a budget and the arms array.
     */
    public Agent(int initBudget, Arm armRefs[], AlgObject algorithm_, Bandit trialBandit)
    {
        budget = initBudget;
        arms = armRefs;
        bandit = trialBandit;
        algorithm = algorithm_;

        int count = 0;
        memories = new ArmMemory[armRefs.length];
        for (Arm current : armRefs)
        {
            memories[count++] = new ArmMemory(current.getCost());
        }

        if (!initialized) // You only need to find the minimum cost arm once; cost is constant
        {
            findCosts();
            initialized = true;
        }
    } // end constructor

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
        minCost = min;
    } // end findCosts

    public int getBestFromFeasibles(List<Integer> feasibles)
    {
        int best = -1; // Index of the best arm in terms of mean reward/cost ratio.

        for (int j : feasibles)
        {
            if (best == -1)
                best = j;
            if (memories[j].getRatio() > memories[best].getRatio() && memories[j].getCost() <= budget)
                best = j;
        }
        return best;
    } // end getBestFromFeasibles

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
    } // end pull

    /**
     * @return the budget remaining for the agent
     */
    public double getBudget()
    {
        return budget;
    }

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
     * @return the total reward of the agent.
     */
    public double getTotalReward()
    {
        return totalReward;
    }

    /**
     * Runs the algorithm associated with this agent.
     */
    public void run()
    {
        algorithm.runAlgorithm(this);
    }
}
