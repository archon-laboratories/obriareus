package com.samvbeckmann.obriareus.distributions;

import com.samvbeckmann.obriareus.core.IDistribution;

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
     * Given a mean and standard deviation, generates a return based on the Poisson distribution.
     * Calculated according to the Knuth algorithm.
     *
     * @param mean   The mean return (lambda).
     * @param stdDev Does nothing.
     * @return A returned reward from the distribution.
     */
    public double generateValue(double mean, double stdDev)
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
