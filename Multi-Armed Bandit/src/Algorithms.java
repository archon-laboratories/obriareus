import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Algorithms
{
    /**
     * The random generator for Algorithms.
     */
    private static Random rnd = new Random();

    /**
     * Prints debug statements for the l-split algorithm.
     */
    static boolean debugLSplit = false;

    /**
     * Prints debug statements for the SOAAv algorithm.
     */
    static boolean debugSOAAv = false;

    /**
     * Prints debug statements for the UCB-BV algorithm
     */
    static boolean debugUCBBV = false;

    /**
     * A list of the algorithms involved.
     */
    public static enum AlgorithmNames
    {
        GREEDY,
        EFIRST,
        FKUBE,
        FKDE,
        UCBBV,
        LSPLIT,
        EPROGRESSIVE,
        SOAAV
    }

    /**
     * A special case of the eFirst algorithm when epsilon <= totalCost/totalBudget so that
     * each arm can be pulled a maximum of once.
     *
     * @param curAgent The current agent employing this algorithm.
     */
    private static void greedy(Agent curAgent)
    {
        eFirst(curAgent, curAgent.getTotalCost() / curAgent.getBudget());
    }

    /**
     * The eFirst algorithm. Begins with a period of exploration determined by the epsilon value,
     * then chooses the best available arm. Online algorithm.
     *
     * @param curAgent The current agent employing this algorithm.
     * @param epsilon  The epsilon value <= 1 for the exploration budget (exploration budget = epsilon * budget).
     */
    private static void eFirst(Agent curAgent, double epsilon)
    {
        // Initialize variables
        Arm[] arms = curAgent.getArms();
        ArmMemory[] memories = curAgent.getMemories();

        double eBudget = curAgent.getBudget() * epsilon; // Exploration budget for the algorithm
        if (eBudget <= curAgent.getTotalCost())
            eBudget = curAgent.getTotalCost();

        curAgent.setBudget(curAgent.getBudget() - eBudget);

        // Declare the arraylist of remaining indices
        ArrayList<Integer> remainingIndices = new ArrayList<Integer>(arms.length); // Stores the location of arms
        // that have yet to be pulled

        Utilities.generateIndices(remainingIndices, arms.length);
        // Exploration
        while (eBudget >= curAgent.getMinCost())
        {
            if (remainingIndices.size() == 0)
                Utilities.generateIndices(remainingIndices, arms.length);

            int armIndex = Utilities.randomIndex(remainingIndices, rnd); // Get a random remaining index
            if (arms[armIndex].getCost() <= eBudget)
            {
                // Pull it!
                curAgent.pull(armIndex);
                eBudget -= arms[armIndex].getCost();
            }

        }
        // eBudget has run out. Begin exploitation phase.

        int bestArm = curAgent.getBestArm(); // Get the index of the first largest element
        int secondBestArm = curAgent.getSecondBest(); // Get the index of the second largest element


        while (curAgent.getBudget() >= curAgent.getMinCost())
        {
            curAgent.pull(bestArm);

            if (arms[bestArm].getCost() > curAgent.getBudget()) // Does the best arm cost too much?
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


    // fKUBE
    /**
     * Estimates the confidence bound:cost ratio of the arm (item density).
     * @param thisArm
     *          the arm whose confidence bound:cost ratio is to be estimated
     * @param time
     *          the timestep of the fKUBE algorithm
     * @return
     *          the confidence bound:cost ratio of the arm
     */
    private static double fKubeEst(ArmMemory thisArm, int time)
    {
        return thisArm.getRatio() + Math.sqrt(2 * Math.log(time) / thisArm.getPulls()) / thisArm.getCost();
    }

    /**
     * The Fractional Knapsack-based Upper Confidence Bound Exploration and Exploitation (fKUBE) algorithm.
     * Pulls the arm with the estimated highest confidence bound:cost ratio (item density).
     * @param curAgent The agent currently employing this algorithm.
     */
    private static void fKUBE(Agent curAgent)
    {
        //----------------------------------------
        //Localize variables
        Arm[] arms = curAgent.getArms();
        int numArms = arms.length;
        double minCost = curAgent.getMinCost();
        ArmMemory [] memories = curAgent.getMemories();

        // Prepare algorithm
        int bestArm = -1;
        ArrayList<Integer> temp = new ArrayList<Integer>();
        Utilities.generateIndices(temp, numArms);

        int lastBestArm = bestArm;
        int time = 0;

        //Main Fractional KUBE loop
        while (curAgent.getBudget() >= minCost)
        {
            if (temp.size() > 0) // initial phase
            {
                //Make sure we can't go over budget here.
                int x = Utilities.randomIndex(temp, rnd);
                if (arms[x].getCost() <= curAgent.getBudget())
                {
                    curAgent.pull(x);
                    time++;
                }
            } else
            { // combined exploration/exploitation phase

                //Find the current best arm, pull it, and re-estimate its value by the result
                bestArm = -1;
                for (int i = 0; i < numArms; i++)
                {
                    if (curAgent.getMemories()[i].getCost() <= curAgent.getBudget() &&
                            (bestArm < 0 || fKubeEst(memories[i], time) >
                                    fKubeEst(memories[curAgent.getBestArm()], time)))
                        bestArm = i;
                }

                if (lastBestArm != bestArm || lastBestArm == -1)
                    lastBestArm = bestArm;

                curAgent.pull(bestArm);
                time++;
            }
        }//end else (which phase are we in)
    }
    // End fKUBE


    /** Fractional Knapsack Based Decreasing ε–greedy (fKDE)
     *
     * @param curAgent The agent currently employing the algorithm
     * @param gamma The tuning factor > 0 associated with fKDE
     */
    private static void fKDE(Agent curAgent, double gamma)
    {
        if (gamma <= 0) {
            System.out.println("Non-positive gamma value; KDE cannot function.");
            return;
        }

        int t = 0;
        Arm[] arms = curAgent.getArms();
        int numArms = arms.length;

        int bestArm; //The best arm to pull: I+
        int numFeasibleArms = 0; // The number of feasible terms

        while (curAgent.getBudget() >= curAgent.getMinCost())
        {
            bestArm = curAgent.getBestArm();
            double epsT = Math.min(1, gamma / (t + 1));

            double[] armProb = new double[numArms]; // Probability of pulling the corresponding arms
            for (int z = 0; z < numArms; z++)
            {
                if (arms[z].getCost() <= curAgent.getBudget())
                {
                    armProb[z] = epsT; // Set it up
                    numFeasibleArms++;
                }
                else
                    armProb[z] = 0;
            }

            // Assign probabilities
            // ε/K, where K is the number of arms that can be pulled minus one (to account for the separate probability
            // of 1 - ε for the best arm)
            for (int z = 0; z < numArms; z++)
                if (armProb[z] > 0)
                    armProb[z] /= numFeasibleArms - 1;

            // Assign the best arm the probability of 1 - ε
            armProb[bestArm] = 1 - epsT;

            // Calculate cumulative probabilities; the last arm should have a cumulative probability of 1
            double[] cmlProb = new double[numArms];
            cmlProb[0] = armProb[0];

            for (int z = 1; z < numArms; z++)
                cmlProb[z] = cmlProb[z - 1] + armProb[z];

            double randomVal = rnd.nextDouble();

            // TODO BROKEN
            int pullIndex = -1; // The index of the arm to pull

            for (int z = 0; z < numArms && pullIndex < 0; z++)
            {
                // Randomly select an arm from amongst the arms that can be pulled
                if (randomVal < cmlProb[z] && armProb[z] > 0)
                    pullIndex = z;
            }

            curAgent.pull(pullIndex);
            t++;
        }
    } // End fKDE algorithm

    /**
     * THE UCB-BV (specifically, UCB-BV 1) algorithm. Iterates through all the arms once, then dulls the arm with
     * the greatest D-value.
     *
     * @param curAgent The agent currently employing this algorithm.
     */
    private static void UCBBV(Agent curAgent)
    {
        // Initialize variables
        Arm arms [] = curAgent.getArms();
        ArmMemory [] memories = curAgent.getMemories();
        double [] dValues = new double[arms.length];

        double lambda = curAgent.getMinCost();
        int currentBest = -1;
        int totalPulls = 0;

        // Initial phase: Pull each arm once.
        ArrayList<Integer> indices = new ArrayList<Integer>();
        Utilities.generateIndices(indices, arms.length);

        while (!indices.isEmpty())
        {
            int i = Utilities.randomIndex(indices, rnd);
            curAgent.pull(i);
            totalPulls++;
            if (debugUCBBV) System.out.println("[UCB-BV] Pulled arm " + i +
                                                "(mean = [" + memories[i].getMeanReward() +
                                                "], sd = [" + arms[i].getStdDev() +
                                                "], est. ratio = [" + memories[i].getRatio() +
                                                "]); Got Reward " + memories[i].getRecentReward());
        }

        // Exploitation phase
        while (curAgent.getBudget() >= curAgent.getMinCost())
        {
            totalPulls++;
            for (int i = 0; i < arms.length; i++)
            {
                dValues[i] = getDValue(memories[i], lambda, totalPulls);
                if (debugUCBBV) System.out.println("[UCB-BV] D for arm " + i + " set to: " + dValues[i]);
            }

            Utilities.generateIndices(indices, arms.length);
            while(!indices.isEmpty())
            {
                int testArm = Utilities.randomIndex(indices, rnd);
                if (arms[testArm].getCost() <= curAgent.getBudget()
                        && (currentBest < 0 || dValues[testArm] > dValues[currentBest]))
                {
                    currentBest = testArm;
                }
            }

            curAgent.pull(currentBest);

            if (debugUCBBV) System.out.println("[UCB-BV] Pulled arm " + currentBest +
                    "(mean = [" + memories[currentBest].getMeanReward() +
                    "], sd = [" + arms[currentBest].getStdDev() +
                    "], est. ratio = [" + memories[currentBest].getRatio() +
                    "]); Got Reward " + memories[currentBest].getRecentReward());
        }
    }

    /**
     * Returns the dValue for a given arm. Used for UCB-BV algorithms.
     *
     * @param thisArm Arm that D will be caclulated for.
     * @param lambda Minimum arm cost. (Or best guess)
     * @param totalpulls Total number of arms that have been pulled so far.
     * @return The dValue for the given arm, with the given boundType.
     */
    private static double getDValue(ArmMemory thisArm, double lambda, int totalpulls)
    {
        double root = Math.sqrt(Math.log(totalpulls - 1) / thisArm.getPulls());
        return thisArm.getRatio() + (1 + (1 / lambda)) * root / (lambda - root);
    }

    /**
     * The l-split algorithm retains <code>lValue^(iteration)</code> arms after each iteration.
     *
     * @param curAgent The agent currently employing this algorithm.
     * @param lValue Value that determines how quickly arms are dropped. <code>lValue * 100%</code>
     *               will be dropped in the first iteration.
     */
    private static void lSplit(Agent curAgent, double lValue)
    {

        // Initialize variables
        Arm [] arms = curAgent.getArms();
        ArmMemory [] memories = curAgent.getMemories();
        ArrayList<Integer> remainingArms = new ArrayList<Integer>();

        double budget = curAgent.getBudget();
        int numToPull = arms.length;
        int iterations = 0;

        // Add all the arms to the list to be pulled in the first iteration
        for (int i = 0; i < curAgent.getArms().length; i++)
            remainingArms.add(i);

        // Loop the runs throughout the algorithm
        while(remainingArms.size() > 0 && budget >= curAgent.getMinCost())
        {
            for (int i = 0; i < remainingArms.size(); i++)
            {
                if (arms[i].getCost() <= budget)
                {
                    curAgent.pull(i);
                    if (debugLSplit) System.out.println("[l-split] Pulled arm " + i +
                            "(mean = [" + memories[i].getMeanReward() +
                            "], sd = [" + arms[i].getStdDev() +
                            "], est. ratio = [" + memories[i].getRatio() +
                            "]); Got Reward " + memories[i].getRecentReward());
                }

            }

            iterations++;

            // Clears the current list of arms, to be repopulated for the next trial
            remainingArms.clear();

            // Updates the number of arms to be pulled in the next iteration.
            if (numToPull > 1)
            {
                numToPull = (int) (arms.length * (Math.pow(1 - lValue, iterations)));
                if (numToPull < 1)
                    numToPull = 1;
                if (debugLSplit) System.out.println("[l-split] Number of Arms to pull on next iteration: " + numToPull);
            }

            // Picks the best arms, which will be pulled on the next iteration
            List<Integer> feasibles = new ArrayList<Integer>(arms.length);
            Utilities.generateIndices(feasibles, arms.length);
            for (int i = 0; i < numToPull; i++)
            {
                if (!feasibles.isEmpty())
                {
                    int currentBest = curAgent.getBestFromFeasibles(feasibles);
                    if (arms[currentBest].getCost() <= curAgent.getBudget())
                    {
                        remainingArms.add(currentBest);
                    } else
                    {
                        i--;
                    }
                    feasibles.remove(new Integer(currentBest));
                }
            }
        }
    } // End l-split

    /**
     * e-Progressive algorithm. Special case of the l-split algorithm, designed to have an l-values
     * that gives an exploration phase of epsilon, before the number of arms is reduced to one.
     *
     * @param curAgent The agent currently employing this algorithm.
     * @param epsilon <code>epsilon * 100%</code> is the percent of budget used on exploration.
     */
    private static void eProgressive(Agent curAgent, double epsilon)
    {

        double lValue;
        double eBudget = epsilon * curAgent.getBudget();
        int numArms = curAgent.getArms().length;

        if (eBudget / numArms <=1)
            lValue = (numArms - 1) / numArms;
        else
            lValue = (eBudget - 1) / (eBudget - numArms);

        lSplit(curAgent, lValue);
    } // End eProgressive

    /**
     * Survival of the Above Average algorithm. For each iteration, only pulls arms greater than
     * x distance from the average. At <code>x = 0</code>, arms better than the average result are pulled.
     *
     * @param curAgent The agent currently employing this algorithm.
     * @param xValue the distance from the average that determines the drop point.
     */
    private static void sOAAv(Agent curAgent, double xValue)
    {
        // Initialize variables
        Arm [] arms = curAgent.getArms();
        ArmMemory [] memories = curAgent.getMemories();
        ArrayList<Integer> activeArms = new ArrayList<Integer>();

        int numPullsInPass;
        double passAverageRatio;

        // Add all arms to list to be pulled for first iteration
        for(int i = 0; i < arms.length; i++)
            activeArms.add(i);

        // Loop until budget exhausted
        while (curAgent.getBudget() >= curAgent.getMinCost())
        {
            numPullsInPass = 0;
            passAverageRatio = 0;

            for (int i : activeArms)
            {
                if (arms[i].getCost() <= curAgent.getBudget())
                {
                    curAgent.pull(i);
                    numPullsInPass++;
                    passAverageRatio += memories[i].getRecentReward() / arms[i].getCost();
                    if (debugSOAAv) System.out.println("[SOAAv] Pulled arm " + i +
                            "(mean = [" + memories[i].getMeanReward() +
                            "], sd = [" + arms[i].getStdDev() +
                            "], est. ratio = [" + memories[i].getRatio() +
                            "]); Got Reward " + memories[i].getRecentReward());
                }
            }

            if (numPullsInPass > 0)
            {
                passAverageRatio = passAverageRatio / numPullsInPass;
                activeArms.clear();

                // Update activeArms for next iteration
                for (int i = 0; i < arms.length; i++)
                {
                    if (arms[i].getCost() <= curAgent.getBudget()
                            && memories[i].getRatio() >= (1 + xValue) * passAverageRatio)
                    {
                        activeArms.add(i);
                    }
                }
                if (activeArms.size() == 0)
                {
                    activeArms.add(curAgent.getBestArm());
                }
            }
        } // TODO: Make random choice of arms though activeArms, to ensure no bias when budget is exhausted. Minor.
    } // end sOAAv


    public static void run(AlgObject algorithm, Agent curAgent)
    {
        switch (algorithm.getAlgorithm())
        {
            case GREEDY:
                greedy(curAgent);
                break;

            case EFIRST:
                eFirst(curAgent, algorithm.getInputParameter());
                break;

            case FKUBE:
                fKUBE(curAgent);
                break;

            case FKDE:
                fKDE(curAgent, algorithm.getInputParameter());
                break;

            case UCBBV:
                UCBBV(curAgent);
                break;

            case LSPLIT:
                lSplit(curAgent, algorithm.getInputParameter());
                break;

            case EPROGRESSIVE:
                eProgressive(curAgent, algorithm.getInputParameter());
                break;

            case SOAAV:
                sOAAv(curAgent, algorithm.getInputParameter());
                break;


            default:
                System.err.println("Algorithm " + algorithm.getAlgorithm().toString() + " not found!");
                break;
        }
    }
}