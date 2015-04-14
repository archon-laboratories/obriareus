package com.samvbeckmann.obriareus.distributions;

import com.samvbeckmann.obriareus.core.IDistribution;

import java.util.Random;

/**
 * Binary Distribution
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Binary implements IDistribution
{
    /**
     * Random generator for this distribution
     */
    Random rnd = new Random();
    /**
     * Returns 1 if random > stdDev, and 0 otherwise.
     * (0 <= stdDev <= 1)
     *
     * @param mean   The return value.
     * @param stdDev Does nothing.
     * @return The value calculated by the distribution.
     */
    public double generateValue(double mean, double stdDev)
    {
        return ((rnd.nextDouble() > stdDev) ? 1 : 0)*mean;
    }

    @Override
    public String getName()
    {
        return "Binary";
    }
}