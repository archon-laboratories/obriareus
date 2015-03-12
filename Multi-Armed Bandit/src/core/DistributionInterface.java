package core;

/**
 * Interface for defaultDistributions that can return rewards.
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public interface DistributionInterface
{
    /**
     * Gets the reward from this distribution, given a mean and standard deviation.
     *
     * @param mean   The mean reward.
     * @param stdDev The standard deviation of rewards.
     * @return A returned reward from the distribution.
     */
    public double getReward(double mean, double stdDev);
}
