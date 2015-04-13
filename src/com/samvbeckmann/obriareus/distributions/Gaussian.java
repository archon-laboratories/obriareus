package com.samvbeckmann.obriareus.distributions;

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
     * Generates a value based on the Gaussian distribution, given a mean and standard deviation.
     *
     * @param mean   The mean return.
     * @param stdDev The standard deviation of returns.
     * @return The value calculated by the distribution.
     */
    public double generateValue(double mean, double stdDev)
    {
        return rnd.nextGaussian() * stdDev + mean;
    }

    @Override
    public String getName()
    {
        return "Gaussian";
    }
}
