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
     * Calculated according to the Knuth algorithm.
     *
     * @param mean   The mean reward (lambda).
     * @param stdDev Does nothing.
     * @return A returned reward from the distribution.
     */
    public double getReward(double mean, double stdDev)
    {
        double l = Math.exp(-mean);
        int k = 0;
        double p = 1;

        do
        {
            k++;
            p *= rnd.nextDouble();
        } while (p > l);

        return k - 1;
    }

    @Override
    public String getName()
    {
        return "Poisson";
    }
}
