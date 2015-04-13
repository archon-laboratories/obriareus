package com.samvbeckmann.obriareus.core;

/**
 * Defines an arm, of which a bandit can pull
 *
 * @author Nate Beckemeyer, Sam Beckmann
 */
public class Arm
{
    /**
     * The distribution that this arm is currently using. Gaussian by default.
     */
    private IDistribution currentDistribution;

    /**
     * Cost to pull the arm.
     */
    private final double cost;

    /**
     * Standard deviation of the reward.
     */
    private final double stdDev;

    /**
     * Mean reward for the arm.
     */
    private final double mean;

    /**
     * Value and distribution assigning Arm constructor.
     *
     * @param cost_        Cost to pull the arm.
     * @param stdDev_      Standard deviation of the reward of the arm.
     * @param mean_        Mean reward for the arm.
     * @param distribution The distribution used by the arm
     */
    public Arm(double cost_, double stdDev_, double mean_, IDistribution distribution)
    {
        cost = cost_;
        stdDev = stdDev_;
        mean = mean_;
        currentDistribution = distribution;
    } // end constructor

    /**
     * Value-assigning Arm constructor.
     *
     * @param cost_   Cost to pull the arm.
     * @param stdDev_ Standard deviation of the reward of the arm.
     * @param mean_   Mean reward for the arm.
     */
    public Arm(double cost_, double stdDev_, double mean_)
    {
        this(cost_, stdDev_, mean_, null);
    } // end constructor

    /**
     * Gets the reward off the arm based off of the currently used reward distribution.
     *
     * @return the current distribution's reward; -1 if the currentDistribution is missing
     */
    public double getReward()
    {
        double reward = currentDistribution.generateValue(mean, stdDev);

        if (reward < 0)
            return 0;
        else return reward;
    }

    /**
     * @return The standard deviation of the arm
     */
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

    /**
     * @param distribution IDistribution for this arms distribution to be set to.
     */
    public void setCurrentDistribution(IDistribution distribution)
    {
        currentDistribution = distribution;
    }
}
