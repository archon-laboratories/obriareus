package com.samvbeckmann.obriareus.core;

import com.samvbeckmann.obriareus.distributions.Constant;
import com.samvbeckmann.obriareus.distributions.Gaussian;

/**
 * Defines an arm, of which a bandit can pull
 *
 * @author Nate Beckemeyer, Sam Beckmann
 */
public class Arm
{
    /**
     * The distribution that this arm's reward method is currently using. Gaussian by default.
     */
    private IDistribution rewardDistribution;

    /**
     * Standard deviation of the reward.
     */
    private final double rewardDev;

    /**
     * Mean reward for the arm.
     */
    private final double rewardMean;

    /**
     * The distribution that this arm's costMean method is currently using. Constant by default.
     */
    private IDistribution costDistribution;

    /**
     * Cost to pull the arm.
     */
    private final double costMean;

    /**
     * Standard deviation of arm cost
     */
    private final double costDev;

    /**
     * Value and distribution assigning Arm constructor.
     *  @param costMean_        Cost to pull the arm.
     * @param rewardDev_      Standard deviation of the reward of the arm.
     * @param rewardMean_        Mean reward for the arm.
     * @param rDist The distribution used by the arm
     * @param costDev_ Standard deviation of the cost of the arm
     */
    public Arm(double rewardDev_, double rewardMean_, IDistribution rDist,
               double costDev_, double costMean_, IDistribution cDist)
    {
        rewardDev = rewardDev_;
        rewardMean = rewardMean_;
        rewardDistribution = rDist;
        costDev = costDev_;
        costMean = costMean_;
        costDistribution = cDist;
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
        this(stdDev_, mean_, new Gaussian(), 0, cost_, new Constant());
    } // end constructor

    /**
     * Gets the reward off the arm based off of the currently used reward distribution.
     *
     * @return the current distribution's reward; -1 if the rewardDistribution is missing
     */
    public double getReward()
    {
        double reward = rewardDistribution.generateValue(rewardMean, rewardDev);

        if (reward < 0)
            return 0;
        else return reward;
    }

    /**
     * @return The standard deviation of the arm
     */
    public double getRewardDev()
    {
        return rewardDev;
    }

    /**
     * @return The mean cost to pull the arm
     */
    public double getCostMean()
    {
        return costMean;
    }

    /**
     * @param distribution IDistribution for this arms distribution to be set to.
     */
    public void setRewardDistribution(IDistribution distribution)
    {
        rewardDistribution = distribution;
    }

    public void setCostDistribution(IDistribution costDistribution)
    {
        this.costDistribution = costDistribution;
    }

}
