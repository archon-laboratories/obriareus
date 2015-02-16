import java.util.Random;

/**
 * The arm that the bandit pulls. Could represent a slot machine or any other item with associated costs and rewards.
 * @author Nate Beckmeyer, Sam Beckmann
 */
public class Arm {
    /**
     * rnd is the random generator for each arm.
     */
    Random rnd = new Random();

    /**
     * The possible reward distributions.
     */
    private static enum Distribution {
        /**
         * The Gaussian-based reward distribution.
         */
        GAUSSIAN,

        /**
         * The Poisson-based reward distribution.
         */
        POISSON
    }

    /**
     * The distribution that this arm is currently using. Gaussian by default.
     */
    Distribution currentDistribution = Distribution.GAUSSIAN;

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
     * @param price
     *          Cost to pull the arm.
     * @param dev
     *          Standard deviation of the reward of the arm.
     * @param avg
     *          Mean reward for the arm.
     */
    public Arm(double price, double dev, double avg) {
        cost = price;
        stdDev = dev;
        mean = avg;
    }

    /**
     * Gets a Gaussian reward based off of the arm's mean and standard deviation.
     * @return the Gaussian reward
     */
    private double getGaussian() {
        return rnd.nextGaussian()*stdDev + mean;
    }

    /**
     * Gets a Poisson reward based off of the arm's mean and standard deviation.
     * @return the Poisson reward
     */
    private double getPoisson() {
        // TODO
        return 0;
    }

    /**
     * Gets the reward off the arm based off of the currently used reward distribution.
     * @return the current distribution's reward; -1 if the currentDistribution is missing
     */
    public double getReward() {
        switch (currentDistribution) {
            case GAUSSIAN: return getGaussian();

            case POISSON: return getPoisson();
            default: return -1;
        }
    }

    /**
     * Returns the cost to pull the arm.
     * @return the cost to pull the arm.
     */
    public double getCost() {
        return cost;
    }

    /**
     * Sets the reward distribution of the arm
     * @param distName
     *          the name of the distribution (does not need to be capitalized)
     * @return
     *          the ordinal value of the distribution in the enum
     */
    public int setDistribution(String distName) {
        currentDistribution = Distribution.valueOf(distName.toUpperCase());
        return currentDistribution.ordinal();
    }

}

/*
public class Arm {
    Random rnd = new Random();
    private double mean;
    private double stdDev;
    private double cost;
    private double recentReward;
    private double decayAmt;
    private int distributionIndex;
    private int numPulls;
    private ArrayList<Double> storedRewards;

    public Arm(double cost, double mean, double std) {
        this.cost = cost;
        this.mean = mean;
        this.stdDev = std;
        storedRewards = new ArrayList<Double>();
    }

    public void pullArm() //updates reward
    {
        if (storedRewards.size() <= numPulls) {
            storedRewards.add(rnd.nextGaussian() * stdDev + mean);
            //storedRewards.add(((double)RandomNums.getPoisson(mean*10))/10.);
            //storedRewards.add((double) RandomNums.getPoisson(mean));
        }
        recentReward = storedRewards.get(numPulls);
        numPulls++;
        if (recentReward < 0) recentReward = 0;
        else if (recentReward > 2.0) recentReward = 2.0;
    }

    public void reset() {
        recentReward = 0;
        numPulls = 0;
    }

    public double getCost() {
        return cost;
    }

    public double getMean() {
        return mean;
    }

    public double getSD() {
        return stdDev;
    }

    public double getRecentReward() {
        if (recentReward < 0)
            System.out.println(recentReward);
        return recentReward;
    }

    public double getRatio() {
        return mean / cost;
    }

    public String toString() {
        return "Cost: " + cost + " \tReward: " + recentReward;
    }
}
*/