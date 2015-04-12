package com.samvbeckmann.obriareus.defaultDistributions;

import com.samvbeckmann.obriareus.core.IDistribution;

import java.util.Random;

/**
 * Gaussian Distribution
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Gaussian implements IDistribution
{
    /**
     * Random generator for the Distribution.
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
        return rnd.nextGaussian() * stdDev + mean;
    }

    @Override
    public String getName()
    {
        return "Gaussian";
    }
}
