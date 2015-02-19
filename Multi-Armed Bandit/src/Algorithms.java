import java.util.Random;
import java.util.ArrayList;

public class Algorithms {
    /**
     * The random generator for Algorithms.
     */
    private static Random rnd;

    /**
     * A list of the algorithms involved.
     */
    public static enum AlgorithmNames {
        GREEDY,
        EFIRST,
        FKUBE,
        FKDE,
        UCBBV,
        LPSPLIT,
        EPROGRESSIVE,
        SOAAV
    }

    /**
     * Restores the given ArrayList of indices to contain all indices.
     * @param indices
     *              The ArrayList of indices to generate
     * @param bound
     *                  The bound of indices to be used should remainingIndices need to be regenerated.
     */
    private static void generateIndices(ArrayList<Integer> indices, int bound) {
        for (int i = 0; i < bound; i++)
            indices.set(i, i);
    }

    /**
     * Generates a random armIndex given an ArrayList of remaining armIndices.
     *
     * @param remainingIndices
     *                  The ArrayList of indices that have yet to be used in the algorithm's random selection.
     * @return
     */
    private static int randomIndex(ArrayList<Integer> remainingIndices) {
        if (remainingIndices.size() == 0)
            return -1;

        int indexLocation = rnd.nextInt(remainingIndices.size()); // The location in remainingIndices of the arm's index

        return remainingIndices.get(indexLocation); // Location in arms of the arm.
    }

    /**
     * A special case of the eFirst algorithm when epsilon <= totalCost/totalBudget so that
     * each arm can be pulled a maximum of once.
     *
     * @param curAgent
     *              The current agent employing this algorithm.
     */
    private static void greedy(Agent curAgent) {
        eFirst(curAgent, curAgent.getTotalCost()/curAgent.getBudget());
    }

    /**
     * The eFirst algorithm. Begins with a period of exploration determined by the epsilon value,
     * then chooses the best available arm. Online algorithm.
     *
     * @param curAgent
     *              The current agent employing this algorithm.
     * @param epsilon
     *              The epsilon value <= 1 for the exploration budget (exploration budget = epsilon * budget).
     */
    private static void eFirst(Agent curAgent, double epsilon) { // TODO epsilon

        // Initialize variables
        Arm [] arms = curAgent.getArms();
        ArmMemory [] memories = curAgent.getMemories();
        double budget = curAgent.getBudget();

        double eBudget = budget * epsilon; // Exploration budget for the algorithm
        budget -= eBudget;

        // Declare the arraylist of remaining indices
        ArrayList <Integer> remainingIndices = new ArrayList <Integer> (arms.length); // Stores the location of arms
                                                                                        // that have yet to be pulled
        generateIndices(remainingIndices, arms.length);
        // Exploration
        while (eBudget >= curAgent.getMinCost()) {
            if (remainingIndices.size() == 0)
                generateIndices(remainingIndices, arms.length);

            int armIndex = randomIndex(remainingIndices); // Get a random remaining index
            if (arms[armIndex].getCost() <= eBudget)
            {
                // Pull it!
                curAgent.pull(armIndex);
                eBudget -= arms[armIndex].getCost();
                remainingIndices.remove(armIndex);
            } else
                remainingIndices.remove(armIndex); // Costs too much. Ensure it's not explored again.

        }
        // eBudget has run out. Begin exploitation phase.

        int bestArm = curAgent.getBestArm(); // Get the index of the first largest element
        int secondBestArm = curAgent.getSecondBest(); // Get the index of the second largest element


        while (budget >= curAgent.getMinCost()) {
            curAgent.pull(bestArm);

            if (arms[bestArm].getCost() > budget) // Does the best arm cost too much?
            {
                // Reassign the arms, taking into account budget constraints.
                bestArm = curAgent.getBestArm();
                secondBestArm = curAgent.getSecondBest();
            }

            if (memories[bestArm].getRatio() < memories[secondBestArm].getRatio()) // Did the best arm fall behind?
            {
                // Promote the second best, and find the new second best.
                bestArm = secondBestArm;
                secondBestArm = curAgent.getSecondBest();
            }
        }
    } // End eFirst algorithm


    private static void fKUBE(Agent curAgent) {
        //TODO
    }
    public static void KubeAlgRandom(Agent curAgent, boolean optimistic) {

        //Initialize Variables
        TrialData myTrial = new TrialData();
        ArmMemory [] agentMemory = curAgent.getMemories();
        Arm [] arms = curAgent.getArms();
        int numArms = arms.length;

        double minCost = curAgent.getMinCost();
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int j = 0; j < arms.length; j++) //initialize temp
            temp.add(j);

        int armsConsidered = 0;
        int bestArm = -1;
        int lastBestArm = -1;
        boolean switchedArms = false;

        //Main KUBE loop
        while (curAgent.getBudget() >= minCost) {
            if (temp.size() > 0) // initial phase - stops when all indices in temp have been chosen
            {
                int x = randomIndex(temp); //choose a random index x from temp
                if (arms[x].getCost() <= curAgent.getBudget())//check if agent can afford arm x
                {
                    curAgent.pull(x);
                }
            } else // combined exploration/exploitation phase
            {
                bestArm = curAgent.getBestArm();
                if (lastBestArm == -1)
                    lastBestArm = bestArm;
                else switchedArms = (bestArm != lastBestArm);

                //Get M*, the best combination of arms to pull
                int[] bestComb = KubeDOG(curAgent.getBudget(), minCost, agentMemory, optimistic, curAgent.getTotalPulls());

                //Get the total number of probable pulls stored in M*
                double mSum = 0;
                for (int k = 0; k < numArms; k++) {
                    mSum += bestComb[k];
                }

                //Use M* to determine which arm to pull, with the fractional probability of each (subtracted from a random fraction)
                double[] armProb = new double[numArms]; //probabilities that arm will be pulled
                for (int z = 0; z < numArms; z++) {
                    if (arms[z].getCost() <= curAgent.getBudget()) {
                        armProb[z] = ((double) bestComb[z]) / mSum;
                    } else
                        armProb[z] = 0;
                    if (armProb[z] > 0) armsConsidered++;
                }

                double[] cmlProb = new double[numArms];
                cmlProb[0] = armProb[0];

                for (int z = 1; z < numArms; z++)  //calculates cumulative probabilities
                    cmlProb[z] = cmlProb[z - 1] + armProb[z]; //probability of arm z is added to probabilities of all arms before it

                double totalProb = 0; //total probabilities

                for (int z = 0; z < numArms; z++) {
                    totalProb += armProb[z];
                }

                double randomVal = rnd.nextDouble() * totalProb;
                int i = -1;
                for (int z = 0; z < numArms && i < 0; z++) {
                    if (randomVal < cmlProb[z])
                        i = z;
                }

                curAgent.pull(i);
                armsConsidered = 0;
            }//end else (which phase we are in)
        }//end while (KUBE algorithm main loop)
    }

    public static void FractKubeAlgRandom(Agent curAgent, boolean optimistic, boolean online) {
        //----------------------------------------
        //Initialize Variables
        Arm [] arms = curAgent.getArms();
        int numArms = arms.length;
        double minCost = curAgent.getMinCost();
        boolean doOnce = true;

        int bestArm = -1;
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int j = 0; j < numArms; j++)
            temp.add(j);

        int lastBestArm = bestArm;
        boolean switchedArms = false;
        int time = 0;

        //Main Fractional KUBE loop
        while (curAgent.getBudget() >= minCost) {
            if (temp.size() > 0) // initial phase
            {
                //Make sure we can't go over budget here.
                int x = randomIndex(temp);
                if (arms[x].getCost() <= curAgent.getBudget()) {
                    curAgent.pull(x);

                    time++;
                }
            } else // combined exploration/exploitation phase
            {
                //Find the current best arm, pull it, and re-estimate its value by the result
                if (online || doOnce) {
                    doOnce = false;
                    bestArm = -1;
                    for (int i = 0; i < numArms; i++) {
                        if (curAgent.getMemories()[i].getCost() <= curAgent.getBudget() &&
                                (bestArm < 0 || fKubeEst(curAgent.getMemories()[i], time) >
                                        fKubeEst(curAgent.getMemories()[curAgent.getBestArm()], time)))
                            bestArm = i;
                    }

                    if (lastBestArm == -1)
                        lastBestArm = bestArm;
                    if (lastBestArm != bestArm) {
                        switchedArms = true;
                        lastBestArm = bestArm;
                    } else
                        switchedArms = false;
                }

                curAgent.pull(bestArm);
                time++;
            }//end else (which phase are we in)
        }//end while (main Fractional KUBE loop)
    }

    /**
     * Use the density-ordered greedy algorithm to find M*, the best
     * combination of arms to be pulled probabilistically.
     */
    private static int[] KubeDOG(double budget, double minCost, ArmMemory[] arms, boolean opt, int time) {
        //Get "best" arms combination, M*, to pull from
        int[] bestComb = new int[arms.length];
        double tempBudget = budget;

        double[] armsRD = new double[arms.length];
        for (int i = 0; i < arms.length; i++) {
            armsRD[i] = KubeConfEst(arms[i], opt, time);
        }

        //add the best remaining arms that we can fit (greedy) into M*
        while (tempBudget >= minCost) {
            int bestArm = -1;
            for (int i = 0; i < arms.length; i++) {
                if (bestArm < 0) {
                    if (arms[i].getCost() <= tempBudget) {
                        bestArm = i;
                    }
                    //else just move on
                } else if (armsRD[i] > armsRD[bestArm] && arms[i].getCost() <= tempBudget) {
                    bestArm = i;
                    //bestArmCost = agentMemory.get(i).getCost();
                }
            }
            int numPullsPossible = (int) (tempBudget / arms[bestArm].getCost());
            bestComb[bestArm] += numPullsPossible;
            tempBudget -= numPullsPossible * arms[bestArm].getCost();
        }
        return bestComb;
    }

    /**
     * KUBE Confidence Estimation - Applies the confidence interval specified in the paper to
     * re-evaluate projected rewards for a given arm.
     */
    private static double KubeConfEst(ArmMemory a, boolean opt, int t) {
        double estimate = a.getMeanReward();
        if (opt)
            estimate += Math.sqrt(2 * Math.log((double) t) / a.getPulls()); //upper confidence bound
        else
            estimate -= Math.sqrt(2 * Math.log((double) t) / a.getPulls()); //lower confidence bound
        estimate /= a.getCost();
        return estimate;
    }

    private static double fKubeEst(ArmMemory thisArm, int time) {
        return thisArm.getRatio() + Math.sqrt(2 * Math.log(time) / thisArm.getPulls()) / thisArm.getCost();
    }


    private static void fKDE(Agent curAgent) {
        //TODO
    }

    private static void uCBBv(Agent curAgent) {
        //TODO
    }

    private static void lSplit(Agent curAgent) {
        //TODO
    }

    private static void eProgressive(Agent curAgent) {
        //TODO
    }



    public static void run(AlgorithmNames algorithm, Agent curAgent) {
        switch (algorithm) {
            case GREEDY:        greedy(curAgent);
                                break;

            case EFIRST:        eFirst(curAgent, .1); // TODO
                                break;

            case FKUBE:         fKUBE(curAgent);
                                break;

            case FKDE:          fKDE(curAgent);
                                break;

            case UCBBV:         uCBBv(curAgent);
                                break;

            case LPSPLIT:       lSplit(curAgent);
                                break;

            case EPROGRESSIVE:  eProgressive(curAgent);
                                break;

            default:            System.err.println("Algorithm " + algorithm.toString() + " not found!");
                                break;
        }
    }
}