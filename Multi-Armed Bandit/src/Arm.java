import java.util.Random;
import java.util.ArrayList;
//import math3.distribution.BetaDistribution;
//import uncommons-maths-1.2.3.org.uncommons.maths.random.*;

public class Arm
{
    Random rnd = new Random();
    private double mean;
    private double stdDev;
    private double cost;
    private double recentReward;
    private double decayAmt;
    private int distributionIndex;
    private int numPulls;
    private ArrayList<Double> storedRewards;

    public Arm(double cost, double mean, double std)
    {
        this.cost = cost;
        this.mean = mean;
        this.stdDev = std;
        storedRewards = new ArrayList<Double>();
    }

    public void pullArm() //updates reward
    {
        if (storedRewards.size() <= numPulls)
        {
            storedRewards.add(rnd.nextGaussian()*stdDev+mean);
            //storedRewards.add(((double)RandomNums.getPoisson(mean*10))/10.);
            //storedRewards.add((double) RandomNums.getPoisson(mean));
        }
        recentReward = storedRewards.get(numPulls);
        numPulls++;
        if (recentReward < 0) recentReward = 0;
        else if (recentReward > 2.0) recentReward = 2.0;
    }

    public void reset()
    {
        recentReward = 0;
        numPulls = 0;
    }

    public double getCost()
    {
        return cost;
    }

    public double getMean()
    {
        return mean;
    }

    public double getSD()
    {
        return stdDev;
    }

    public double getRecentReward()
    {
        if (recentReward < 0)
            System.out.println(recentReward);
        return recentReward;
    }

    public double getRatio()
    {
        return mean / cost;
    }

    public String toString()
    {
        return "Cost: " + cost + " \tReward: " + recentReward;
    }
}