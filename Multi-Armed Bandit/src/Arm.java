import java.util.Random;

/**
 * The arm that the bandit pulls. Could represent a slot machine or any other item with associated costs and rewards.
 *
 * @author Nate Beckemeyer, Sam Beckmann
 */
public class Arm
{
    /**
     * rnd is the random generator for each arm.
     */
    Random rnd = new Random();

    /**
     * The distribution that this arm is currently using. Gaussian by default.
     */
    Utilities.Distribution currentDistribution = Utilities.Distribution.GAUSSIAN;

    /**
     * Cost to pull the arm.
     */
    private double cost;

    /**
     * Standard deviation of the reward.
     */
    private double stdDev;

    /**
     * Mean reward for the arm.
     */
    private double mean;

    /**
     * Value-assigning Arm constructor.
     *
     * @param price Cost to pull the arm.
     * @param dev   Standard deviation of the reward of the arm.
     * @param avg   Mean reward for the arm.
     */
    public Arm(double price, double dev, double avg, Utilities.Distribution distribution)
    {
        cost = price;
        stdDev = dev;
        mean = avg;
        currentDistribution = distribution;
    }

    /**
     * Gets a Gaussian reward based off of the arm's mean and standard deviation.
     *
     * @return the Gaussian reward
     */
    private double getGaussian()
    {
        return rnd.nextGaussian() * stdDev + mean;
    }

    /**
     * Gets a Poisson reward based off of the arm's mean and standard deviation.
     *
     * @return the Poisson reward
     */
    private double getPoisson()
    {
        // TODO
        return 0;
    }

    /**
     * Gets the reward off the arm based off of the currently used reward distribution.
     *
     * @return the current distribution's reward; -1 if the currentDistribution is missing
     */
    public double getReward()
    {
        switch (currentDistribution)
        {
            case GAUSSIAN:
                return getGaussian();
            case POISSON:
                return getPoisson();

            default:
                return -1;
        }
    }

    public double getStdDev()
    {
        return stdDev;
    }

    /**
     * @return the cost to pull the arm
     */
    public double getCost()
    {
        return cost;
    }

//    /**
//     * Sets the reward distribution of the arm
//     *
//     * @param distName the name of the distribution (does not need to be capitalized)
//     * @return the ordinal value of the distribution in the enum
//     */
//    public int setDistribution(String distName)
//    {
//        currentDistribution = Utilities.Distribution.valueOf(distName.toUpperCase());
//        return currentDistribution.ordinal();
//    }

}