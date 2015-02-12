import java.util.ArrayList;
import java.util.Random;

public class Bandit {

    private int startingArm;
	private int numberArms;
	private int bestArmIndex;
	private ArrayList<Arm> arms = new ArrayList<Arm>();
	private ArrayList<Integer> optimalPulls;

	public Bandit(int numArms)
	{
		numberArms = numArms;
		bestArmIndex = -1;
		arms = new ArrayList<Arm>(numberArms);
        startingArm = new Random().nextInt(numberArms);
	}
	public void createArm(double cost, double mean, double variance)
	{
		arms.add(new Arm(cost, mean, variance));
		if (bestArmIndex == -1 || arms.get(bestArmIndex).getMean() < mean) bestArmIndex = arms.size()-1;
	}

    public int getStartingArm()
    {
        return startingArm;
    }

	public void genOptPulls( double bdgt )
	{
		optimalPulls = regretDOG(bdgt, arms);
	}
	public double getOMR( int steps )
	{
		double bestMean = 0;
		for(int i = 0; i < arms.size(); i++)
		{
			if(arms.get(i).getMean() > bestMean)
			{
				bestMean = arms.get(i).getMean();
			}
		}
		return bestMean * steps;
	}
	public double getOMR( double bdgt, int steps) //actually total pulls should be passed in)
	{
		double rewSum = 0;
		for(int i = 0; i < optimalPulls.size() && i < steps; i++)
			rewSum += arms.get(optimalPulls.get(i)).getMean();
		return rewSum;
	}

	public String toString()
	{
		String out = "";
		for(int i = 0; i<arms.size(); i++)
			out += "\n Arm " + (i+1) + ": " + arms.get(i);
		return out;
	}

	public ArrayList<Arm> getArms()
	{
		return arms;
	}

	public int getNumArms()
	{
		return arms.size();
	}
	public int getBestArmIndex()
	{
		return bestArmIndex;
	}

	private ArrayList<Integer> regretDOG(double budget, ArrayList<Arm> arms)
	{
		ArrayList<Integer> pullIndices = new ArrayList<Integer>();
		double tempBudget = budget;
		double minCost = AlgObject.minCost(arms);
		int bestArm;

		//add the best remaining arms that we can fit (greedy) into M*
		while(tempBudget >= minCost)
		{
			bestArm = 0;
			for(int i = 0; i < arms.size(); i++)
			{
				if(arms.get(i).getRatio() >= arms.get(bestArm).getRatio() && tempBudget >= arms.get(i).getCost())
				{
					bestArm = i;
				}
			}
			pullIndices.add(bestArm);
			tempBudget -= arms.get(bestArm).getCost();
		}
		return pullIndices;
	}

}
