import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


/**
 * @author sap471
 */
public class Experimentation
{

    final static Random rnd = new Random();

    public static double mathFunctions(int which, double t, double totalTime)
    {
        switch (which)
        {
            case 0:
                return t / totalTime; //linear
            case 1:
                return Math.log(((Math.exp(1) - 1) * t / totalTime) + 1); //concave down, "exponential" increase, 0-1
            case 2:
                return Math.pow(10 * (t / totalTime), 2) / 100; //concave up
            default:
                return rnd.nextDouble(); //random
        }
    }

    public static void profitPerPullVsArms(double budget/*starting budget*/, int startingArms, int totalArms, int whichFunction)
    {
        int stepNumber = 0;
        int numTrials = 1000;
        int numAlgorithms = 8;
        int numSteps = totalArms / 10;
        double meanStep = 0.05;
        double decayRate = 0;
        double epsilonValue = 0.045; //ARBITRARY EPSILON VALUE
        int distIdx = 1;

        int[][][] pullData = new int[numAlgorithms][numSteps][numTrials];
        double[][][] revenueData = new double[numAlgorithms][numSteps][numTrials];
        double[][][] netProfitData = new double[numAlgorithms][numSteps][numTrials];
        double[][][] regretData = new double[numAlgorithms][numSteps][numTrials];
//		double[][][] timeData = new double[numAlgorithms][numSteps][numTrials];
//		double[][][][] armUsage = new double[numArms][numAlgorithms][numSteps][numTrials];
//		TrialData[][][] myTrialData = new TrialData[numAlgorithms][numSteps][numTrials];

        for (stepNumber = startingArms; stepNumber < numSteps; stepNumber++)
        {
            int armsAdded = stepNumber * 10; //adds ten more arms each time
            Bandit myBandit = new Bandit(armsAdded);
            for (int arm = 0; arm < armsAdded; arm++)//initializes arms in bandit
            {
                myBandit.createArm(1, mathFunctions(whichFunction, arm, armsAdded), 0.5);
            }
            System.out.println("Done creating arms for step " + stepNumber);
            myBandit.genOptPulls(budget);
            for (int whichAlg = 0; whichAlg < numAlgorithms; whichAlg++)//starts algorithms loop
            {
                for (int whichTrial = 0; whichTrial < numTrials; whichTrial++)
                { //starts  trials loop

                    int bestArm = 0;
                    int secondBestArm = 0;
                    for (int i = 1; i < myBandit.getNumArms(); i++)
                    {
                        double temp = myBandit.getArms().get(i).getMean();
                        if (temp > myBandit.getArms().get(bestArm).getMean())
                        {
                            secondBestArm = bestArm;
                            bestArm = i;
                        } else if (temp > myBandit.getArms().get(secondBestArm).getMean()) //finds d for gamma
                        {
                            secondBestArm = i;
                        }
                    }
                    double dMin = myBandit.getArms().get(bestArm).getRatio() - myBandit.getArms().get(secondBestArm).getRatio();
                    double gammaVal = 56 * myBandit.getNumArms() / (3 * Math.pow(dMin, 2)); //minimum gamma value indicated in the text

                    Agent myAgent = new Agent(budget, myBandit); //initialize agent each trial
                    for (int whichArm = 0; whichArm < myBandit.getNumArms(); whichArm++)//store arm costs in agent memory
                    {
                        myAgent.storeArm(myBandit.getArms().get(whichArm).getCost());
                    }
                    switch (whichAlg)
                    {
                        case 0:
                            AlgObject.eFirstAlg(myBandit, myAgent, epsilonValue, false);
                            break;
                        case 1:
                            AlgObject.eFirstAlg(myBandit, myAgent, epsilonValue, true);
                            break;
                        case 2:
                            AlgObject.KubeAlgRandom(myBandit, myAgent, true);
                            break;
                        case 3:
                            AlgObject.KubeAlgRandom(myBandit, myAgent, false);
                            break;
                        case 4:
                            AlgObject.FractKubeAlgRandom(myBandit, myAgent, true, true);
                            break;
                        case 5:
                            AlgObject.FractKubeAlgRandom(myBandit, myAgent, false, true);
                            break;
                        case 6:
                            AlgObject.KdeAlgUnique(myBandit, myAgent, gammaVal);
                            break;
                        default:
                            AlgObject.FractKdeAlg(myBandit, myAgent, gammaVal, true);
                    }//end switch
                    //Error-catch line
                    if (myAgent.getBudget() < 0)
                        System.out.println("Warning! Algorithm " + whichAlg + " ended over budget (unfair advantage).");

                    //Store this trial's data.
                    revenueData[whichAlg][stepNumber][whichTrial] = myAgent.getRevenue() / myAgent.getTotalPulls();  //stores profit/pull
                    netProfitData[whichAlg][stepNumber][whichTrial] = (myAgent.getRevenue() - budget - myAgent.getBudget());
                    regretData[whichAlg][stepNumber][whichTrial] = myAgent.getRegret();
                    pullData[whichAlg][stepNumber][whichTrial] = (int) myAgent.getTotalPulls();
                    System.out.println("Data stored for trial " + whichTrial);
                    //	timeData[whichAlg][whichStep][whichTrial] = endTime - startTime;
//			ArrayList<Integer> tempAU = myAgent.getArmUsage();
//			for(int whichArm = 0; whichArm < numArms; whichArm++)
//			{
//				armUsage[whichArm][whichAlg][whichStep][whichTrial] = tempAU.get(whichArm)/myAgent.getTotalPulls();
//			}
                }
            }
        }


        double avgRevenue[][] = new double[numAlgorithms][numSteps];
        double stdDevRevenue[][] = new double[numAlgorithms][numSteps];
        double avgPulls[][] = new double[numAlgorithms][numSteps];
//		double avgRegret[][] = new double[numAlgorithms][numSteps];
//		double stdDevRegret[][] = new double[numAlgorithms][numSteps];
//		double avgTime[] = new double[numAlgorithms];
//		double avgArms[][][] = new double[numAlgorithms][numSteps][numArms];

        for (int i = 0; i < numAlgorithms; i++)
        {
            for (int j = 0; j < numSteps; j++)
            {
                avgRevenue[i][j] = 0;
                for (int k = 0; k < numTrials; k++)
                {
                    avgRevenue[i][j] += revenueData[i][j][k];
                    avgPulls[i][j] += pullData[i][j][k];
//					avgRegret[i][j] += regretData[i][j][k];
//					avgTime[i] += timeData[i][j][k];
//					for(int l = 0; l < numArms; l++)
//						avgArms[i][j][l] += armUsage[l][i][j][k]; 
                }
                avgRevenue[i][j] /= numTrials;
                avgPulls[i][j] /= numTrials;
//				avgRegret[i][j] /= numTrials;
//				avgTime[i] /= numSteps*numTrials;
//				for(int l = 0; l < numArms; l++)
//					avgArms[i][j][l] /= numTrials;

                double stdRevenuePart = 0;
//				double stdRegretPart = 0;
                for (int k = 0; k < numTrials; k++)
                {
                    stdRevenuePart += Math.pow(revenueData[i][j][k] - avgRevenue[i][j], 2);
//					stdRegretPart += Math.pow(regretData[i][j][k]-avgRegret[i][j],2);
                }
                stdDevRevenue[i][j] = Math.sqrt(stdRevenuePart / numTrials);
//				stdDevRegret[i][j] = Math.sqrt(stdRegretPart/numTrials);
            }
        }

        try
        {
            //======================================
            //Revenue-per-Pull v. Number of Arms
            for (int i = 0; i < numAlgorithms; i++)
            {
                PrintWriter out = new PrintWriter(new FileWriter("data/numArmsReturnData_" + i + "_.txt"));
                for (int j = 0; j < numSteps; j++)
                {
                    out.print((10 * j) + "\t");
                    out.print(avgRevenue[i][j] / (avgPulls[i][j]) + "\t");
                    out.print(stdDevRevenue[i][j]);
                    out.println();
                }
                out.close();
            }

//		for(int i = 0; i < numAlgorithms; i++)
//		{
//			PrintWriter trialDataOut = new PrintWriter(new FileWriter("data/timeRegret_"+i+".txt"));
//			trialDataOut.print(myTrialData[i][0][0]);
//			trialDataOut.close();
//		}

            PrintWriter pOut = new PrintWriter(new FileWriter("data/runParameters.txt"));
            pOut.println("Number of Arms: " + totalArms);
            pOut.println("Budget: " + budget);
            pOut.println("Number of Trials: " + numTrials);
            pOut.println("Number of Steps: " + numSteps);
            pOut.println("Arm Decay Rate: " + decayRate);
            pOut.close();
        } catch (IOException e)
        {
            System.out.println("IOException: " + e);
        }


    }

    public static void main(String[] args)
    {
        profitPerPullVsArms(1000, 1, 100, 0);
    }


}

