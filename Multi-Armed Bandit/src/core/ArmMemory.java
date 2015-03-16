package core;

public class ArmMemory implements Comparable<ArmMemory>
{
    /**
     * The total reward received from the arm
     */
    private double totalReward;

    /**
     * The number of pulls on the arm.
     */
    private int pulls;

    /**
     * The mean reward from all pulls.
     */
    private double meanReward;

    /**
     * The cost to pull the arm
     */
    private double cost;

    /**
     * The reward:cost ratio.
     */
    private double rewardCostRatio;

    /**
     * The most recent reward when this arm was pulled
     */
    private double recentReward;

    /**
     * Constructor that initializes the arm memory.
     *
     * @param cost the cost to pull the arm
     */
    public ArmMemory(double cost)
    {
        totalReward = 0;
        pulls = 0;
        meanReward = 0;
        this.cost = cost;
        rewardCostRatio = 0;
    } // end constructor

    /**
     * Adds a pull to the arm, recalculating values as necessary.
     *
     * @param reward The reward from the pull
     */
    public void addPull(double reward)
    {
        pulls++;
        totalReward += reward;
        recentReward = reward;

        meanReward = totalReward / pulls;

        rewardCostRatio = meanReward / cost;
    } // end appPull

    /**
     * @return the mean reward for this arm
     */
    public double getMeanReward()
    {
        return meanReward;
    }

    /**
     * @return the number of times this arm has been pulled
     */
    public int getPulls()
    {
        return pulls;
    }

    /**
     * @return the ratio of mean reward to cost
     */
    public double getRatio()
    {
        return rewardCostRatio;
    }

    /**
     * @return the cost to pull the arm
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * @return the recent reward of the arm.
     */
    public double getRecentReward()
    {
        return recentReward;
    }

    /**
     * Compares two ArmMemories against each other.
     *
     * @param memory the armMemory to be compared.
     * @return <code>1</code> if the first item is greater than the item it's being compared to.
     * <code>0</code> if the items are equivalent.
     * <code>-1</code> if the first item is less than the item it's being compared to.
     */
    @Override
    public int compareTo(ArmMemory memory)
    {
        if (memory.getRatio() < getRatio())
            return -1;
        else if (memory.getRatio() == getRatio())
            return 0;
        else
            return 1;
    } // end compareTo
}
