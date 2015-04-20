package com.samvbeckmann.obriareus.core;

/**
 * Interface for Distributions that can return rewards.
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public interface IDistribution
{
    /**
     * Generates a value based on this distribution, given a mean and standard deviation.
     *
     * @param mean   The mean reward.
     * @param stdDev The standard deviation of rewards.
     * @return A returned reward from the distribution.
     */
    double generateValue(double mean, double stdDev);

    /**
     * To print the name of the distribution.
     *
     * @return Name of the distribution.
     */
    String getName();
}
