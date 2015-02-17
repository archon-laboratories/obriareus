import java.util.Random;

public class Agent {
    /**
     * The random generator for Agent.
     */
    private Random rnd;

    /**
     * The arms that the agent pulls.
     */
    private static Arm arms[];

    /**
     * The minimum cost to pull an arm.
     */
    private static double minCost;

    /**
     * The total cost to pull all of the arms.
     */
    private static double totalCost = 0;

    /**
     * Has minCost been initialized yet? It only needs to happen once. Optimization variable.
     */
    private static boolean initialized = false;

    /**
     *  The memories that the Agent has of the arm pulls.
     */
    private ArmMemory memories[];

    /**
     * The index of the current best arm in terms of benefit/cost ratio.
     */
    private int bestArm;

    /**
     * The index of the next best arm in terms of benefit/cost ratio, in case the current base drops behind.
     */
    private int nextBestArm;

    /**
     * The total current budget of the agent.
     */
    private int budget;

    /**
     * Sets the minCost and totalCost variables
     */
    public void findCosts() {
        double min =  arms[0].getCost();
        for (Arm current : arms) {
            double  curCost = current.getCost();
            if (curCost < min)
                min = curCost;
            totalCost += curCost;
        }
    }

    private int partition(ArmMemory[] theArray, int first, int last) {
        // tempItem is used to swap elements in the array
        ArmMemory tempItem;
        ArmMemory pivot = theArray[first]; // reference pivot
        // initially, everything but pivot is in unknown
        int lastS1 = first; // index of last item in S1
        // move one item at a time until unknown region is empty
        for (int firstUnknown = first + 1; firstUnknown <= last; ++firstUnknown) {
            // Invariant: theArray[first+1..lastS1] < pivot
            // theArray[lastS1+1..firstUnknown-1] >= pivot
            // move item from unknown to proper region
            if (theArray[firstUnknown].compareTo(pivot) > 0) {
                // item from unknown belongs in S1
                ++lastS1;
                tempItem = theArray[firstUnknown];
                theArray[firstUnknown] = theArray[lastS1];
                theArray[lastS1] = tempItem;
            } // end if
            // else item from unknown belongs in S2
        } // end for
        // place pivot in proper position and mark its location
        tempItem = theArray[first];
        theArray[first] = theArray[lastS1];
        theArray[lastS1] = tempItem;
        return lastS1;
    } // end partition

    private int getKLarge(int k, ArmMemory[] array, int first, int last) {
        int pI = partition(array, first, last);
        if (pI - first + 1 == k)
            return pI;
        else if (pI - first + 1 > k)
            return getKLarge(k, array, first, pI - 1);
        else
            return getKLarge(k - (pI - first + 1), array, pI + 1, last);
    }

    public int getKLarge(int k) {
        return getKLarge(k, memories, 0, arms.length - 1);
    }


    /**
     * The constructor for the agent. Pass a budget and the arms array.
     */
    public Agent(int initBudget, Arm armRefs[]) {
        budget = initBudget;
        arms = armRefs;
        int count = 0;
        for (Arm current : armRefs)
            memories[count++] = new ArmMemory(current.getCost());

        if (!initialized)
        {
            findCosts();
            initialized = true;
        }
    }

    public void pull(int toPull) {
        Arm current = arms[toPull];
        if (budget >= current.getCost())
        {
            budget -= current.getCost();
            memories[toPull].addPull(current.getReward());
        }
    }

    /**
     * @return
     *      the budget remaining for the agent
     */
    public int getBudget() {
        return budget;
    }

    /**
     * @return
     *      the array of arms of the agent
     */
    public Arm[] getArms() {
        return arms;
    }

    public double getMinCost() {
        return minCost;
    }

    public double getTotalCost() {
        return totalCost;
    }
}
