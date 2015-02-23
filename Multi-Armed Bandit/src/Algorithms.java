import java.util.Random;
import java.util.ArrayList;

public class Algorithms
{
    /**
     * The random generator for Algorithms.
     */
    private static Random rnd;

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
        LPSPLIT,
        EPROGRESSIVE,
        SOAAV
    }

    /**
     * Restores the given ArrayList of indices to contain all indices.
     *
     * @param indices The ArrayList of indices to generate
     * @param bound   The bound of indices to be used should remainingIndices need to be regenerated.
     */
    private static void generateIndices(ArrayList<Integer> indices, int bound)
    {
        for (int i = 0; i < bound; i++)
            indices.set(i, i);
    }

    /**
     * Generates a random armIndex given an ArrayList of remaining armIndices. Then removes the
     *
     * @param remainingIndices The ArrayList of indices that have yet to be used in the algorithm's random selection.
     * @return the index of the arm to be checked
     */
    private static int randomIndex(ArrayList<Integer> remainingIndices)
    {
        if (remainingIndices.size() == 0)
            return -1;

        int indexLocation = rnd.nextInt(remainingIndices.size()); // The location in remainingIndices of the arm's index
        int index = remainingIndices.get(indexLocation); // Location in arms of the arm.
        remainingIndices.remove(indexLocation);
        return index;

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
    { // TODO epsilon

        // Initialize variables
        Arm[] arms = curAgent.getArms();
        ArmMemory[] memories = curAgent.getMemories();
        double budget = curAgent.getBudget();

        double eBudget = budget * epsilon; // Exploration budget for the algorithm
        budget -= eBudget;

        // Declare the arraylist of remaining indices
        ArrayList<Integer> remainingIndices = new ArrayList<Integer>(arms.length); // Stores the location of arms
        // that have yet to be pulled

        generateIndices(remainingIndices, arms.length);
        // Exploration
        while (eBudget >= curAgent.getMinCost())
        {
            if (remainingIndices.size() == 0)
                generateIndices(remainingIndices, arms.length);

            int armIndex = randomIndex(remainingIndices); // Get a random remaining index
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


        while (budget >= curAgent.getMinCost())
        {
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
     * The fKUBE algorithm. Pulls the arm with the estimated highest confidence bound:cost ratio (item density).
     * @param curAgent The agent currently employing this algorithm.
     */
    private static void fKUBE(Agent curAgent)
    {
        //----------------------------------------
        //Localize variables
        Arm[] arms = curAgent.getArms();
        int numArms = arms.length;
        double minCost = curAgent.getMinCost();
        double budget = curAgent.getBudget();
        ArmMemory [] memories = curAgent.getMemories();

        // Prepare algorithm
        int bestArm = -1;
        ArrayList<Integer> temp = new ArrayList<Integer>();
        generateIndices(temp, numArms);

        int lastBestArm = bestArm;
        int time = 0;

        //Main Fractional KUBE loop
        while (budget >= minCost)
        {
            if (temp.size() > 0) // initial phase
            {
                //Make sure we can't go over budget here.
                int x = randomIndex(temp);
                if (arms[x].getCost() <= budget)
                {
                    curAgent.pull(x);
                    time++;
                }
            } else { // combined exploration/exploitation phase

                //Find the current best arm, pull it, and re-estimate its value by the result
                bestArm = -1;
                for (int i = 0; i < numArms; i++)
                {
                    if (curAgent.getMemories()[i].getCost() <= budget &&
                            (bestArm < 0 || fKubeEst(memories[i], time) >
                                    fKubeEst(memories[curAgent.getBestArm()], time)))
                        bestArm = i;
                }

                if (lastBestArm != bestArm || lastBestArm == -1)
                    lastBestArm = bestArm;
            }

            // TODO: Should these two lines be down here or in the else?
            curAgent.pull(bestArm);
            time++;
        }//end else (which phase are we in)
    }
    // End fKUBE


    private static void fKDE(Agent curAgent)
    {
        //TODO
    }

    /**
     * THE UCB-BV algorithm. (Specifically UCB-BV 1) Iterates through all the arms once, then dulls the arm with
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
        generateIndices(indices, arms.length);

        while(!indices.isEmpty())
        {
            int i = randomIndex(indices);
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

            generateIndices(indices, arms.length);
            while(!indices.isEmpty())
            {
                int testArm = randomIndex(indices);
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
    { //TODO: l-value

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
            for (int i = 0; i < numToPull; i++)
            {
                remainingArms.add(curAgent.getKthBest(i+1));
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
    {   //TODO: epsilon

        double lValue;
        double eBudget = epsilon * curAgent.getBudget();

        if (eBudget / curAgent.getArms().length <=1)
            lValue = (curAgent.getArms().length - 1) / curAgent.getArms().length;
        else
            lValue = (eBudget - 1) / (eBudget - curAgent.getArms().length);

        lSplit(curAgent, lValue);
    }

    /**
     * Survival of the Above Average algorithm. For each iteration, only pulls arms greater than
     * x distance from the average. At <code>x = 0</code>, arms better than the average result are pulled.
     *
     * @param curAgent The agent currently employing this algorithm.
     * @param xValue the distance from the average that determines the drop point.
     */
    private static void sOAAv(Agent curAgent, double xValue)
    { // TODO: xValue
        // Initialize variables
        Arm [] arms = curAgent.getArms();
        ArmMemory [] memories = curAgent.getMemories();
        ArrayList<Integer> activeArms = new ArrayList<Integer>();

        int numPullsInPass;
        int passAverageRatio;

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
                    passAverageRatio += memories[i].getRecentReward();
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
            }
        } // TODO: Make random choice of arms though activeArms, to ensure no bias when budget is exhausted. Minor.
    } // end sOAAv


    public static void run(AlgorithmNames algorithm, Agent curAgent)
    {
        switch (algorithm)
        {
            case GREEDY:
                greedy(curAgent);
                break;

            case EFIRST:
                eFirst(curAgent, .1); // TODO
                break;

            case FKUBE:
                fKUBE(curAgent);
                break;

            case FKDE:
                fKDE(curAgent);
                break;

            case UCBBV:
                UCBBV(curAgent);
                break;

            case LPSPLIT:
                lSplit(curAgent, .5); // TODO
                break;

            case EPROGRESSIVE:
                eProgressive(curAgent, .1); // TODO
                break;

            case SOAAV:
                sOAAv(curAgent, 0); // TODO


            default:
                System.err.println("Algorithm " + algorithm.toString() + " not found!");
                break;
        }
    }
}