

public class ArmMemory {
    private double totalReward;
    private int pulls;
    private double meanReward;
    private double cost;
    private double rewardCostRatio;

    public ArmMemory(double cost) {
        totalReward = 0;
        pulls = 0;
        meanReward = 0;
        this.cost = cost;
        rewardCostRatio = 0;
    }

    public void addPullNoUpdate() {
        pulls++;
    }

    public void addPull(double r) //updates everything
    {
        pulls++;
        totalReward += r;
        //System.out.println("\tTotal reward of arm is now " + totalReward);
        meanReward = totalReward / pulls;
        //System.out.println("\tAverage reward of arm is now " + avgReward);
        rewardCostRatio = meanReward / cost;
        //System.out.println("\tReward Density is now " + rewardCostRatio);
    }

    public double getMeanReward() {
        return meanReward;
    }

    public int getPulls() {
        return pulls;
    }

    public double getRatio() {
        return rewardCostRatio;
    }

    public double getCost() {
        return cost;
    }

}
