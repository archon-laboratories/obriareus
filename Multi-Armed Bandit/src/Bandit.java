import java.util.ArrayList;
import java.util.List;

public class Bandit
{

    /**
     * The rewards for each arm after n pulls, to ensure consistent rewards across algorithms (removing the chance one
     * algorithm outperforms another based purely on luck).
     */
    private List<List<Double>> rewards;

    /**
     * Returns the pre-calculated reward for an arm if one exists. If one does not exist, creates one to be used across
     * all algorithms for the nth pull.
     *
     * @param armIndex     index of the arm to be pulled
     * @param pullingAgent the agent attempting to pull the arm
     * @return the reward of the arm
     */
    public double pullArm(int armIndex, Agent pullingAgent)
    {
        // The number of times the arm has been pulled
        int count = pullingAgent.getMemories()[armIndex].getPulls();

        if (count == rewards.get(armIndex).size() || rewards.get(armIndex).isEmpty()) // Calculate the new reward
            rewards.get(armIndex).add(pullingAgent.getArms()[armIndex].getReward());

        return rewards.get(armIndex).get(count);
    }

    /**
     * Generates bandit given number of arms for each trial.
     *
     * @param numArms The number of arms in the dataset used by the bandit.
     */
    public Bandit(int numArms)
    {
        rewards = new ArrayList<List<Double>>(numArms);
        for (int i = 0; i < numArms; i++)
        {
            rewards.add(new ArrayList<Double>());
        }
    }
}
