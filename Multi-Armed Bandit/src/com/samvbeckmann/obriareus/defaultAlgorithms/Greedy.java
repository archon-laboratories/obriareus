package com.samvbeckmann.obriareus.defaultAlgorithms;

import com.samvbeckmann.obriareus.core.Bandit;

import java.util.ArrayList;
import java.util.List;

/**
 * Epsilon-greedy algorithm.
 *
 * @author Sam Beckmann, Nate Beckemeyer. Thanks to Anton Ridgeway for original implementations.
 */
public class Greedy implements com.samvbeckmann.obriareus.core.IAlgorithm
{
    @Override
    public String getName()
    {
        return "Greedy";
    }

    /**
     * A special case of the eFirst algorithm when epsilon <= totalCost/totalBudget so that
     * each arm can be pulled a maximum of once.
     *
     * @param curBandit       The current agent employing this algorithm.
     * @param inputParameters Null for this algorithm.
     */
    @Override
    public void run(Bandit curBandit, List<Double> inputParameters)
    {
        EFirst eFirst = new EFirst();
        inputParameters = new ArrayList<Double>(); // add correct epsilon for Greedy.
        inputParameters.add(curBandit.getTotalCost() / curBandit.getBudget());
        eFirst.run(curBandit, inputParameters);
    }
}
