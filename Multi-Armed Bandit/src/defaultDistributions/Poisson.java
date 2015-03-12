package defaultDistributions;

import core.IDistribution;

import java.util.Random;

/**
 * Poisson Distribution.
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Poisson implements IDistribution
{
    /**
     * Random generator for the distribution.
     */
    private Random rnd = new Random();

    /**
     * Gets the reward from this distribution, given a mean and standard deviation.
     *
     * @param mean   The mean reward.
     * @param stdDev The standard deviation of rewards.
     * @return A returned reward from the distribution.
     */
    public double getReward(double mean, double stdDev)
    {
        return 0;
    }

    @Override
    public String getName()
    {
        return "Poisson";
    }
}
