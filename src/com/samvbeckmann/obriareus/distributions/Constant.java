package com.samvbeckmann.obriareus.distributions;

import com.samvbeckmann.obriareus.core.IDistribution;

/**
 * Constant Distribution (returns the same every time)
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Constant implements IDistribution
{
    /**
     * Returns the mean value every time because it's constant.
     *
     * @param mean   The return value.
     * @param stdDev Does nothing.
     * @return The value calculated by the distribution.
     */
    public double generateValue(double mean, double stdDev)
    {
        return mean;
    }

    @Override
    public String getName()
    {
        return "Constant";
    }
}
