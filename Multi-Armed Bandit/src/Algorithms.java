import java.util.Random;
import java.util.ArrayList;

public class Algorithms
{
    /**
     * The random generator for Algorithms.
     */
    private static Random rnd;

    /**
     * If true, prints debug statements for the l-split algorithm.
     */
    static boolean debugLSplit = true;

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
     * @param curAgent
     *              The agent currently employing this algorithm.
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

    private static void uCBBv(Agent curAgent)
    {
        //TODO
    }

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
                            "]); Got Reward " + "WHERE DO WE STORE REWARDS?");
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

    private static void eProgressive(Agent curAgent)
    {
        //TODO
    }


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
                uCBBv(curAgent);
                break;

            case LPSPLIT:
                lSplit(curAgent, .5);
                break;

            case EPROGRESSIVE:
                eProgressive(curAgent);
                break;

            default:
                System.err.println("Algorithm " + algorithm.toString() + " not found!");
                break;
        }
    }
}