import java.util.ArrayList;
import java.util.Random;

public class Bandit {

    //private int startingArm;
    //private int numberArms;
    //private int bestArmIndex;
    //private ArrayList<Arm> arms = new ArrayList<Arm>();
    //private ArrayList<Integer> optimalPulls;

    /**
     * The arms that the bandit may pull.
     */
    private static Arm [] arms;

    /**
     * The rewards for each arm after n pulls, to ensure consistent rewards across algorithms (removing the
     * chance one algorithm outperforms another based purely on luck).
     */
    private static ArrayList<Double> [] rewards;

    /**
     * Returns the precalculated reward for an arm if one exists. If one does not exist, creates one to be used across
     * all algorithms for the nth pull.
     * @param armIndex
     *          index of the arm to be pulled
     * @param pullingAgent
     *          the agent attempting to pull the arm
     * @return
     *          the reward of the arm
     */
    public static double pullArm(int armIndex, Agent pullingAgent) {
        // The number of times the arm has been pulled
        int count = pullingAgent.getMemories()[armIndex].getPulls();

        if (count == (rewards[armIndex].size() + 1)) // Calculate the new reward
            rewards[armIndex].add(arms[armIndex].getReward());

        return rewards[armIndex].get(count);
    }

    /**
     * Generates bandit given arms for each trial.
     *
     * @param newArms
     *          The arms of the bandit (with associated means, standard deviations, and costs).
     */
    public static void generateBandit(Arm[] newArms) {
        arms = newArms;
        int numArms = arms.length;

        // Java throws either a generic array exception or an unchecked exception warning if instantiated another way.
        for (int i = 0; i < numArms; i++)
            rewards[i] = new ArrayList<Double>();
    }

    /*
    public void createArm(double cost, double mean, double variance) {
        arms.add(new Arm(cost, mean, variance));
        if (bestArmIndex == -1 || arms.get(bestArmIndex).getMean() < mean) bestArmIndex = arms.size() - 1;
    }

    public int getStartingArm() {
        return startingArm;
    }

    public void genOptPulls(double bdgt) {
        optimalPulls = regretDOG(bdgt, arms);
    }

    public double getOMR(int steps) {
        double bestMean = 0;
        for (int i = 0; i < arms.size(); i++) {
            if (arms.get(i).getMean() > bestMean) {
                bestMean = arms.get(i).getMean();
            }
        }
        return bestMean * steps;
    }

    public double getOMR(double bdgt, int steps) //actually total pulls should be passed in)
    {
        double rewSum = 0;
        for (int i = 0; i < optimalPulls.size() && i < steps; i++)
            rewSum += arms.get(optimalPulls.get(i)).getMean();
        return rewSum;
    }

    public String toString() {
        String out = "";
        for (int i = 0; i < arms.size(); i++)
            out += "\n Arm " + (i + 1) + ": " + arms.get(i);
        return out;
    }

    public ArrayList<Arm> getArms() {
        return arms;
    }


    public int getNumArms() {
        return arms.size();
    }

    public int getBestArmIndex() {
        return bestArmIndex;
    }

    private ArrayList<Integer> regretDOG(double budget, ArrayList<Arm> arms) {
        ArrayList<Integer> pullIndices = new ArrayList<Integer>();
        double tempBudget = budget;
        double minCost = Algorithms.minCost(arms);
        int bestArm;

        //add the best remaining arms that we can fit (greedy) into M*
        while (tempBudget >= minCost) {
            bestArm = 0;
            for (int i = 0; i < arms.size(); i++) {
                if (arms.get(i).getRatio() >= arms.get(bestArm).getRatio() && tempBudget >= arms.get(i).getCost()) {
                    bestArm = i;
                }
            }
            pullIndices.add(bestArm);
            tempBudget -= arms.get(bestArm).getCost();
        }
        return pullIndices;
    }
    */

}
