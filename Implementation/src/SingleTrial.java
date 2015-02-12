import java.io.*;
import java.util.*;
/**
 * @author sap471
 *
 */
public class SingleTrial {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] armCosts = new double[0];
		double[] armRewardMeans = new double[0];
		double[] armStdDeviations = new double[0];
		double budgetVal = 0;
		int numArms = 0;
		
		Scanner in = new Scanner(System.in);
		System.out.println("Enter dataset number: ");
		int dataNum = in.nextInt();
		in.close();
		
		try
		{
			System.out.println("Getting 'dataset" + dataNum + ".text");
			in = new Scanner(new File("datasets/dataset" + dataNum + ".txt"));
			
			numArms = in.nextInt();
			
			double initBudget = 1000;
			budgetVal = initBudget;
			armCosts = new double[numArms];
			double sumCosts = 0;
			
			for(int j = 0; j < numArms; j++)
			{
				armCosts[j] = in.nextDouble();
				sumCosts += armCosts[j];
			}
			
			armRewardMeans = new double[numArms];
			double bestArmMean = 0;
			
			for(int j = 0; j < numArms; j++)
			{
				armRewardMeans[j] = in.nextDouble();
				if(armRewardMeans[j] > bestArmMean)
					bestArmMean = armRewardMeans[j];
			}
			
			armStdDeviations = new double[numArms];
			for(int j = 0; j < numArms; j++)
			{
				armStdDeviations[j] = in.nextDouble();
			}
			
			in.close();
		}
		catch ( IOException e)
		{
			System.out.println("IOException error: " + e);
		}
		
		//===============================================
		//initialize bandit and agent
		Bandit myBandit;
		Agent myAgent;
		
		int numTrials = 100;
		int numSteps = 1;
		double varStep = 0;
		double decayRate = 0;
		int distIdx = 0;
		
		myBandit = new Bandit(numArms);
		for(int i = 0; i < numArms; i++)
		{
			//myBandit.createArm(distIdx, armCosts[i], armRewardMeans[i], armStdDeviations[i], decayRate); 
		}
		myBandit.genOptPulls(budgetVal);
		
		myAgent = new Agent(budgetVal, myBandit);
		for(int whichArm = 0; whichArm < numArms; whichArm++)
		{
			myAgent.storeArm(armCosts[whichArm]);
		}
		
		AlgObject.HypothesisTestingAlg(myBandit, myAgent, armStdDeviations[0], 1.65);
		}
	}

