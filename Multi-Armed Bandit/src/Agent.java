import java.util.Random;

public class Agent {
    /**
     * The random generator for Agent.
     */
    private Random rnd;

    /**
     * The arms that the agent pulls.
     */
    public static Arm arms[];

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

    private void changeBestArm() {
        if (memories[bestArm] == memories[nextBestArm])
            if (rnd.nextDouble() > .5)
            {
                int temp = bestArm;
                bestArm = nextBestArm;
                nextBestArm = temp;
            }
        else if (memories[bestArm])
    }
}
