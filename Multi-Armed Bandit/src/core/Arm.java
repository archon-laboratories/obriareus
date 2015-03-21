package core;

/**
 * The arm that the bandit pulls. Could represent a slot machine or any other item with associated costs and rewards.
 *
 * @author Nate Beckemeyer, Sam Beckmann
 */
public class Arm
{
    /**
     * The distribution that this arm is currently using. Gaussian by default.
     */
    IDistribution currentDistribution;

    /**
     * Cost to pull the arm.
     */
    private double cost;

    /**
     * Standard deviation of the reward.
     */
    private double stdDev;

    /**
     * Mean reward for the arm.
     */
    private double mean;

    /**
     * Value-assigning core.Arm constructor.
     *
     * @param cost_   Cost to pull the arm.
     * @param stdDev_ Standard deviation of the reward of the arm.
     * @param mean_   Mean reward for the arm.
     */
    public Arm(double cost_, double stdDev_, double mean_, IDistribution distribution)
    {
        cost = cost_;
        stdDev = stdDev_;
        mean = mean_;
        currentDistribution = distribution;
    } // end constructor

    /**
     * Gets the reward off the arm based off of the currently used reward distribution.
     *
     * @return the current distribution's reward; -1 if the currentDistribution is missing
     */
    public double getReward()
    {
        double reward = currentDistribution.getReward(mean, stdDev);

        if (reward < 0)
            return 0;
        else return reward;
    }

    public double getStdDev()
    {
        return stdDev;
    }

    /**
     * @return the cost to pull the arm
     */
    public double getCost()
    {
        return cost;
    }
}
