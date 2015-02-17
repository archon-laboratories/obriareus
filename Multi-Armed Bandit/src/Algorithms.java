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
     *              The epsilon value for the exploration budget (exploration budget = epsilon * budget).
     */
    private static void eFirst(Agent curAgent, double epsilon) {

        // Initialize variables
        Arm [] arms = curAgent.getArms();
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
        // eBudget has run out. Time to begin the real work.

        int bestArm = curAgent.getKLarge(1);
        int secondBestArm;
        while (budget >= curAgent.getMinCost()) {

        }
    } // End eFirst algorithm


    private static void fKUBE(Agent curAgent) {
        //TODO
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