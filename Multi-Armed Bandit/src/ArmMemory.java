import java.util.Comparator;

public class ArmMemory {

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
     * Constructor that initializes the arm memory.
     * @param cost
     *          the cost to pull the arm
     */
    public ArmMemory(double cost) {
        totalReward = 0;
        pulls = 0;
        meanReward = 0;
        this.cost = cost;
        rewardCostRatio = 0;
    }

    /**
     * Adds a pull to the arm, recalculating values as necessary.
     * @param reward
     *          The reward from the pull
     */
    public void addPull(double reward) //updates everything
    {
        pulls++;
        totalReward += reward;
        meanReward = totalReward / pulls;

        rewardCostRatio = meanReward / cost;
    }

    public double getMeanReward() {
        return meanReward;
    }

    public int getPulls() {
        return pulls;
    }

    public double getRatio() {
        return rewardCostRatio;
    }

    public double getCost() {
        return cost;
    }
}
