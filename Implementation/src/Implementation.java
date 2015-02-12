import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Implementation {
	final static Random rnd = new Random();
	static int numTrials = 100; // start magic numbers
	static int numAlgorithms = 10;
	static int initBudget = 300;
	static boolean printRun = false;
	static boolean varCosts = true;
	static double costMean = 0.0;
	static double costStDv = 0.0;

	public static double mathFunctions(int which, double t, double totalTime) {
		switch (which) {
		case 0:
			return t / totalTime; // linear
		case 1:
			return Math.log(((Math.exp(1) - 1) * t / totalTime) + 1); // concave
																		// down,
																		// "exponential"
																		// increase,
																		// 0-1
		case 2:
			return Math.pow(10 * (t / totalTime), 2) / 100; // concave up
		default:
			return rnd.nextDouble(); // random
		}
	}

	public static void clear(boolean[] choices) {
		for (int i = 0; i < choices.length; i++)
			choices[i] = false;
	}

	public static void main(String[] args) {
		final int numIterations = 100;
		double[] linValues = { .1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0 };
		double[] subLinValues = { 0.1, 0.314, 0.466, 0.584, 0.680, 0.761,
				0.832, 0.894, 0.94969, 1.0 };
		double[] superLinValues = { .1, 0.111, 0.144, 0.2, 0.278, 0.378, 0.5,
				0.644, 0.811, 1.0 };
		double[][] means = new double[numAlgorithms][numIterations];
		for (int largeIteration = 0; largeIteration < numIterations; largeIteration++) {
			int dataNum = 0;
			int numArms = 0;
			double bestArmMean = 0;
			double budgetVal = initBudget;
			boolean[] found = { false, false, false, false, false, false,
					false, false, false, false };
			double sumCosts = 0;
			double[] armCosts = new double[0];
			double[] armRewardMeans = new double[0];
			double[] armRewardStdDevs = new double[0];
			ArrayList<Double> tempStorage = new ArrayList<Double>();
			int bestMeanIndex = 0;

			// ==========================================================================
			// Step 1: Load Datasets

			// Load in the number of arms.
			numArms = 10;

			// Load in the cost for each arm.
			for (int j = 0; j < numArms; j++) {
				tempStorage.add(1.0);
			}
			armCosts = new double[tempStorage.size()];
			for (int j = 0; j < tempStorage.size(); j++) {
				armCosts[j] = tempStorage.get(j);

				if (varCosts) {
					double temp = rnd.nextGaussian() * costStDv + costMean;
					armCosts[j] += temp;
					// System.out.println("Arm " + j + "'s cost is now "+
					// armCosts[j]);
				}

				sumCosts += armCosts[j];
			}
			tempStorage.clear();

			// Load in the mean reward for each arm.
			for (int j = 0; j < numArms; j++) {
				int cur = 0;
				do {
					cur = rnd.nextInt(numArms);
				} while (found[cur]);
				
				// HEREEHSETUHSETHOISHTETIHSOETHISEHTUSTHSOEUHT
				
				// linValues, subLinValues, superLinValues
				double temp = linValues[cur]; // HERE HERE HERE! Change this to
												// change the type of data set.
				
				// HERESEOTUHSOETHUSTOEHUSTHOEUTHOESUTHSETOHUST
				found[cur] = true;
				tempStorage.add(temp);
				if (temp > bestArmMean)
					bestArmMean = temp;
			}
			armRewardMeans = new double[tempStorage.size()];
			for (int j = 0; j < tempStorage.size(); j++) {
				armRewardMeans[j] = tempStorage.get(j);
			}
			tempStorage.clear();

			// Load in the standard deviation of arm rewards for each arm.
			for (int j = 0; j < numArms; j++) {
				tempStorage.add(.3);
			}
			armRewardStdDevs = new double[tempStorage.size()];
			for (int j = 0; j < tempStorage.size(); j++) {
				armRewardStdDevs[j] = tempStorage.get(j);
			}
			tempStorage.clear();

			// ==========================================================================
			// Step 2: Perform Trials

			Bandit myBandit;
			Agent myAgent;

			int numSteps = 1;
			// double meanStep = 0;
			// double budgetSteps = 0;
			// double costSteps = 0;
			// double meanSteps = 0;
			// double stdSteps = 0;
			// double numArmsIncrease = 10;
			// double budgetIncrease = 15;
			// double costIncrease = 0;
			// double meanIncrease = 0;
			// double stdIncrease = 0;
			// double meanStep = 0.05;

			double[][][] revenueData = new double[numAlgorithms][numSteps][numTrials];
			double[] bestRevenue = new double[numAlgorithms];
			double[] worstRevenue = new double[numAlgorithms];
			/*
			 * double[][][] netProfitData = new
			 * double[numAlgorithms][numSteps][numTrials]; double[][][]
			 * regretData = new double[numAlgorithms][numSteps][numTrials];
			 * double[][][] timeData = new
			 * double[numAlgorithms][numSteps][numTrials]; double[][][][]
			 * armUsage = new
			 * double[numArms][numAlgorithms][numSteps][numTrials];
			 */
			// TrialData[][][] myTrialData = new
			// TrialData[numAlgorithms][numSteps][numTrials];
			TrialData[] myTrialData = new TrialData[numAlgorithms];

			for (int whichStep = 0; whichStep < numSteps; whichStep++) {
				for (int whichTrial = 0; whichTrial < numTrials; whichTrial++) {
					// Create the bandit for this step (reset between trials),
					// and
					// get the optimal pull combination for it (to get regret)
					myBandit = new Bandit(numArms);
					for (int i = 0; i < numArms; i++) {
						myBandit.createArm(armCosts[i], armRewardMeans[i],
								armRewardStdDevs[i] + whichStep);
					}
					myBandit.genOptPulls(budgetVal);

					if (printRun)
						System.out.println(" Running Trial " + whichTrial
								+ "...");
					for (int whichAlg = 0; whichAlg < numAlgorithms; whichAlg++) {
						if (printRun)
							System.out.println(" Running "
									+ getAlgName(whichAlg) + "...");
						// Generate algorithm parameter values

						int bestArm = 0;
						int secondBestArm = 0;
						int worstArm = 0;
						for (int i = 1; i < numArms; i++) {
							double temp = myBandit.getArms().get(i).getMean();
							if (temp > myBandit.getArms().get(bestArm)
									.getMean()) {
								secondBestArm = bestArm;
								bestArm = i;
							} else if (temp > myBandit.getArms()
									.get(secondBestArm).getMean()) {
								secondBestArm = i;
							} else if (temp < myBandit.getArms().get(worstArm)
									.getMean()) {
								worstArm = i;
							}
						}
						double dMin = myBandit.getArms().get(bestArm)
								.getRatio()
								- myBandit.getArms().get(secondBestArm)
										.getRatio();
						// double dMax =
						// myBandit.getArms().get(bestArm).getRatio()
						// - myBandit.getArms().get(worstArm).getRatio();
						double dVal = dMin * .99;
						// double beta = .001;
						double epsilonValue; // =
												// Math.pow(2*(-Math.log(beta/2)*sumCosts/(initBudget*dMax)),
												// 1/3);

						// Find the best arm, I*
						for (int n = 0; n < armRewardMeans.length; n++) {
							if (armRewardMeans[bestMeanIndex] < armRewardMeans[n])
								bestMeanIndex = n;
						}

						// if(budgetVal > sumCosts*2)
						epsilonValue = sumCosts / budgetVal;
						// else
						// epsilonValue = .5;

						if (whichAlg == 1 && 0.1 >= sumCosts / budgetVal)
							epsilonValue = 0.1;
						if (whichAlg == 2 && 0.2 >= sumCosts / budgetVal)
							epsilonValue = 0.2;

						if (epsilonValue > 1)
							epsilonValue = 1;
						else if (epsilonValue < sumCosts / budgetVal)
							epsilonValue = sumCosts / budgetVal;

						double gammaVal;// = 56*numArms/(3*Math.pow(dVal,2));
										// //minimum gamma value indicated in
										// the
										// text
						if (budgetVal > sumCosts)
							gammaVal = numArms * .4;
						else
							gammaVal = numArms * budgetVal / sumCosts / 3; // for
																			// 1/3
																			// of
																			// the
																			// average
																			// number
																			// of
																			// pulls,
																			// explore

						myAgent = new Agent(budgetVal, myBandit);
						for (int whichArm = 0; whichArm < numArms; whichArm++) {
							myAgent.storeArm(armCosts[whichArm]);
						}

						switch (whichAlg) {
						case 0:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.eFirstAlg(myBandit, myAgent, epsilonValue,
											true);
							// AlgObject.lSplitAlg(myBandit, myAgent, 0.2,
							// false, true);
							// AlgObject.soaavAlg(myBandit, myAgent, 0, true);
							break;
						case 1:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.eFirstAlg(myBandit, myAgent, epsilonValue,
											true);
							// AlgObject.lSplitAlg(myBandit, myAgent, 0.3,
							// false, true);
							// AlgObject.lSplitAlg(myBandit, myAgent, 0.5,
							// false, true);
							break;
						case 2:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.eFirstAlg(myBandit, myAgent, epsilonValue,
											true);
							// AlgObject.lSplitAlg(myBandit, myAgent, 0.4,
							// false, true);
							// AlgObject.eFirstAlg(myBandit, myAgent,
							// epsilonValue, true);
							break;
						case 3:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.FractKubeAlgRandom(myBandit, myAgent,
											true, true);
							// AlgObject.lSplitAlg(myBandit, myAgent, 0.5,
							// false, true);
							// AlgObject.peefAlg(myBandit, myAgent, 0.1);
							break;
						case 4:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.FractKdeAlg(myBandit, myAgent, gammaVal,
											true);
							// AlgObject.lSplitAlg(myBandit, myAgent, 0.6,
							// false, true);
							break;
						case 5:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.UCBBVAlg(myBandit, myAgent, 0, true);
							// AlgObject.lSplitAlg(myBandit, myAgent, 0.7,
							// false, true);
							break;
						case 6:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.lSplitAlg(myBandit, myAgent, 0.5, false,
											true);
							// AlgObject.lSplitAlg(myBandit, myAgent, 0.8,
							// false, true);
							break;
						case 7:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.peefAlg(myBandit, myAgent, 0.1);
							// AlgObject.lSplitAlg(myBandit, myAgent, 0.9,
							// false, true);
							break;
						case 8:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.peefAlg(myBandit, myAgent, 0.2);
							// AlgObject.eFirstAlg(myBandit, myAgent,
							// epsilonValue, true);
						default:
							myTrialData[whichAlg]/* [whichStep][whichTrial] */= AlgObject
									.soaavAlg(myBandit, myAgent, 0, true);
							// AlgObject.soaavAlg(myBandit, myAgent, 0, true);
						}// end switch

						// Error-catch line
						if (myAgent.getBudget() < 0)
							System.out.println("Warning! Algorithm " + whichAlg
									+ " ended over budget (unfair advantage).");
						if (printRun)
							System.out.println("Utility: "
									+ myAgent.getRevenue());
						// Store this trial's data.
						revenueData[whichAlg][whichStep][whichTrial] = myAgent
								.getRevenue(); // stores profit
						// netProfitData[whichAlg][whichStep][whichTrial] =
						// (myAgent.getRevenue() - budgetVal -
						// myAgent.getBudget());
						// regretData[whichAlg][whichStep][whichTrial] =
						// myAgent.getRegret();
						// timeData[whichAlg][whichStep][whichTrial] = endTime -
						// startTime;
						// ArrayList<Integer> tempAU = myAgent.getArmUsage();
						// for(int whichArm = 0; whichArm < numArms; whichArm++)
						// {
						// armUsage[whichArm][whichAlg][whichStep][whichTrial] =
						// tempAU.get(whichArm)/myAgent.getTotalPulls();
						// }
						for (int i = 0; i < numArms; i++)
							myBandit.getArms().get(i).reset(); // reset arms'
																// usage
																// counters
					}// end looping over all algorithms
				}// end trials loop
			}// end steps loop

			// ==========================================================================
			// Step 3: Print to files, graphable in gnuplot

			double avgRevenue[][] = new double[numAlgorithms][numSteps];
			double stdDevRevenue[][] = new double[numAlgorithms][numSteps];
			// double avgRegret[][] = new double[numAlgorithms][numSteps];
			// double stdDevRegret[][] = new double[numAlgorithms][numSteps];
			// double avgTime[] = new double[numAlgorithms];
			// double avgArms[][][] = new
			// double[numAlgorithms][numSteps][numArms];

			for (int i = 0; i < numAlgorithms; i++) {
				bestRevenue[i] = revenueData[i][0][0];
				worstRevenue[i] = revenueData[i][0][0];
				for (int j = 0; j < numSteps; j++) {
					avgRevenue[i][j] = 0;
					for (int k = 0; k < numTrials; k++) {
						avgRevenue[i][j] += revenueData[i][j][k];
						if (revenueData[i][j][k] > bestRevenue[i])
							bestRevenue[i] = revenueData[i][j][k];
						if (revenueData[i][j][k] < worstRevenue[i])
							worstRevenue[i] = revenueData[i][j][k];
						// avgRegret[i][j] += regretData[i][j][k];
						// avgTime[i] += timeData[i][j][k];
						// for(int l = 0; l < numArms; l++)
						// avgArms[i][j][l] += armUsage[l][i][j][k];
					}
					avgRevenue[i][j] /= numTrials;
					// avgRegret[i][j] /= numTrials;
					// avgTime[i] /= numSteps*numTrials;
					// for(int l = 0; l < numArms; l++)
					// avgArms[i][j][l] /= numTrials;

					double stdRevenuePart = 0;
					// double stdRegretPart = 0;
					for (int k = 0; k < numTrials; k++) {
						stdRevenuePart += Math.pow(revenueData[i][j][k]
								- avgRevenue[i][j], 2);
						// stdRegretPart +=
						// Math.pow(regretData[i][j][k]-avgRegret[i][j],2);
					}
					stdDevRevenue[i][j] = Math.sqrt(stdRevenuePart / numTrials);
					// stdDevRegret[i][j] = Math.sqrt(stdRegretPart/numTrials);
				}

				// System.out.printf("%s:\n\t\t\tBest Reward:\t\t%6.3f\n",getAlgName(i),
				// bestRevenue[i]);
				// System.out.printf("\t\t\tWorst Reward:\t\t%6.3f\n",
				// worstRevenue[i]);
				// System.out.printf("\t\t\tAverage Reward:\t\t%6.3f\n",avgRevenue[i][0]);
				// System.out.printf("\t\t\tStd. Dev of Reward:\t%6.3f\n",stdDevRevenue[i][0]);
			}
			// System.out.print("\n" + initBudget + "\n\n");
			double meanRev = 0;
			for (int i = 0; i < numAlgorithms; i++)
				meanRev += avgRevenue[i][0];
			meanRev /= numAlgorithms;
			// System.out.println();
			for (int j = 0; j < numAlgorithms; j++) {
				// System.out.println("\""+getAlgName(j)+"\"");
				/*
				 * System.out.printf("%-25s\t%10.5f\n", getAlgName(j),
				 * avgRevenue[j][0] - meanRev);
				 */
				// System.out.printf("%10.5f\n", avgRevenue[j][0] - meanRev);
				means[j][largeIteration] = avgRevenue[j][0] - meanRev;
			}
			for (int j = 0; j < numAlgorithms; j++) {
				// System.out.printf("%25s\"\t", '"' + getAlgName(j));
				// System.out.println(avgRevenue[j][0] + "\t" +
				// stdDevRevenue[j][0]+ "\t");
			}

			/*
			 * int maxSize = 0; for(int i = 0; i < numAlgorithms; i++) {
			 * if(myTrialData[i].size() > maxSize) maxSize =
			 * myTrialData[i].size(); } for(int i = 0; i < maxSize; i+=3) {
			 * System.out.print(i+"\t"); for(int j = 0; j < numAlgorithms; j++)
			 * { if(myTrialData[j].size() < maxSize) {
			 * System.out.print(myTrialData[j].getArmsConsidered(i)+"\t"); }
			 * else { System.out.print(0+"\t"); } } System.out.println(); }
			 * for(int i = 0; i < maxSize; i++) { System.out.print(i+"\t");
			 * for(int j = 0; j < numAlgorithms; j++) { if(myTrialData[j].size()
			 * < maxSize && myTrialData[j].getSwitchedBest(i)==true) {
			 * System.out.print("1\t"); } else { System.out.print("0\t"); } }
			 * System.out.println(); } //Average TrialData /* TrialData[]
			 * avgTrialData = new TrialData[numAlgorithms]; for(int i = 0; i <
			 * numAlgorithms; i++) { avgTrialData[i] = new TrialData(); int
			 * avgNumTimeSteps = 0; for(int j = 0; j < numTrials; j++) {
			 * avgNumTimeSteps += myTrialData[i][0][j].size(); } avgNumTimeSteps
			 * /= numTrials; double[] avgReward = new double[(int)
			 * avgNumTimeSteps]; double[] avgRegret = new double[(int)
			 * avgNumTimeSteps]; double[] avgMaxNumPulls = new double[(int)
			 * avgNumTimeSteps]; for(int j = 0; j < avgNumTimeSteps; j++) { int
			 * numRewards = 0; int numRegrets = 0; int numMaxNumPulls = 0;
			 * for(int k = 0; k < numTrials; k++) {
			 * if(myTrialData[i][0][k].hasReward(j)) { avgReward[j] +=
			 * myTrialData[i][0][k].getReward(j); numRewards++; }
			 * if(myTrialData[i][0][k].hasRegret(j)) { avgRegret[j] +=
			 * myTrialData[i][0][k].getRegret(j); numRegrets++; }
			 * if(myTrialData[i][0][k].hasSpecial(j)) { avgMaxNumPulls[j] +=
			 * myTrialData[i][0][k].getSpecial(j); numMaxNumPulls++; } }
			 * avgReward[j] /= numRewards; avgRegret[j] /= numRegrets;
			 * avgMaxNumPulls[j] /= numMaxNumPulls;
			 * avgTrialData[i].addValues(j,avgReward
			 * [j],avgRegret[j],avgMaxNumPulls[j],j*bestArmMean); } }
			 */

			try {
				// ======================================
				// Net Profit v. Mean
				// for(int i = 0; i < numAlgorithms; i++)
				// {
				// PrintWriter out = new PrintWriter(new
				// FileWriter("data/meansProfitData_"+dataNum+"_"+i+".txt"));
				// for(int j = 0; j < numSteps; j++)
				// {
				// out.print(armRewardMeans[bestMeanIndex]+
				// /*meanStep*j+*/"\t");
				// out.print(avgRevenue[i][j]+"\t");
				// out.println(stdDevRevenue[i][j]);
				// }
				// out.close();
				// }
				// for(int i = 0; i < numAlgorithms; i++)
				// {
				// PrintWriter out = new PrintWriter(new
				// FileWriter("data/meansProfitDataScatter_"+dataNum+"_"+i+".txt"));
				// for(int j = 0; j < numSteps; j++)
				// {
				// for(int k = 0; k < numTrials; k++)
				// {
				// out.print(armRewardMeans[bestMeanIndex]+/*meanStep*j +
				// */"\t");
				// out.println(revenueData[i][j][k]);
				// }
				// }
				// out.close();
				// }
				//
				// ======================================
				// Regret v. Mean
				// for(int i = 0; i < numAlgorithms; i++)
				// {
				// PrintWriter out = new PrintWriter(new
				// FileWriter("data/meansRegretData_"+dataNum+"_"+i+".txt"));
				// for(int j = 0; j < numSteps; j++)
				// {
				// out.print(armRewardMeans[bestMeanIndex]+meanStep*j+"\t");
				// out.print(avgRegret[i][j]+"\t");
				// out.println(stdDevRegret[i][j]);
				// }
				// out.close();
				// }
				// for(int i = 0; i < numAlgorithms; i++)
				// {
				// PrintWriter out = new PrintWriter(new
				// FileWriter("data/meansRegretDataScatter_"+dataNum+"_"+i+".txt"));
				// for(int j = 0; j < numSteps; j++)
				// {
				// for(int k = 0; k < numTrials; k++)
				// {
				// out.print(armRewardMeans[bestMeanIndex]+meanStep*j+"\t");
				// out.println(regretData[i][j][k]);
				// }
				// }
				// out.close();
				// }
				/*
				 * //====================================== //Arm Usage v. Mean
				 * for(int i = 0; i < numAlgorithms; i++) { PrintWriter out =
				 * new PrintWriter(new
				 * FileWriter("data/meansArmData_"+dataNum+"_"+i+".txt"));
				 * for(int j = 0; j < numSteps; j++) {
				 * out.print(armRewardMeans[1]+meanChangeValues[j]); for(int k =
				 * 0; k < numArms; k++) out.print("\t"+avgArms[i][j][k]);
				 * out.println(); } out.close(); } for(int i = 0; i <
				 * numAlgorithms; i++) { PrintWriter out = new PrintWriter(new
				 * FileWriter
				 * ("data/meansArmDataScatter_"+dataNum+"_"+i+".txt")); for(int
				 * j = 0; j < numSteps; j++) { for(int k = 0; k < numTrials;
				 * k++) { out.print(armRewardMeans[1]+meanChangeValues[j]);
				 * for(int l = 0; l < numArms; l++)
				 * out.print("\t"+armUsage[l][i][j][k]); out.println(); } }
				 * out.close(); }
				 * 
				 * //====================================== // Net Profit v.
				 * Standard Deviation for(int i = 0; i < numAlgorithms; i++) {
				 * PrintWriter out = new PrintWriter(new
				 * FileWriter("data/stdProfitData_"+dataNum+"_"+i+".txt"));
				 * for(int j = 0; j < numSteps; j++) {
				 * out.print(armRewardStdDevs[1]+meanChangeValues[j]+"\t");
				 * out.print(avgProfit[i][j]+"\t");
				 * out.println(stdDevProfit[i][j]); } out.close(); } for(int i =
				 * 0; i < numAlgorithms; i++) { PrintWriter out = new
				 * PrintWriter(new
				 * FileWriter("data/stdProfitDataScatter_"+dataNum
				 * +"_"+i+".txt")); for(int j = 0; j < numSteps; j++) { for(int
				 * k = 0; k < numTrials; k++) {
				 * out.print(armRewardStdDevs[1]+j*varStep+"\t");
				 * out.println(revenueData[i][j][k]); } } out.close(); } /*
				 * //====================================== // Regret v.
				 * Standard Deviation for(int i = 0; i < numAlgorithms; i++) {
				 * PrintWriter out = new PrintWriter(new
				 * FileWriter("data/stdRegretData_"+dataNum+"_"+i+".txt"));
				 * for(int j = 0; j < numSteps; j++) {
				 * out.print(armRewardStdDevs[1]+stdChangeValues[j]+"\t");
				 * out.print(avgRegret[i][j]+"\t");
				 * out.println(stdDevRegret[i][j]); } out.close(); } for(int i =
				 * 0; i < numAlgorithms; i++) { PrintWriter out = new
				 * PrintWriter(new
				 * FileWriter("data/stdRegretDataScatter_"+dataNum
				 * +"_"+i+".txt")); for(int j = 0; j < numSteps; j++) { for(int
				 * k = 0; k < numTrials; k++) {
				 * out.print(armRewardStdDevs[1]+stdChangeValues[j]+"\t");
				 * out.println(regretData[i][j][k]); } } out.close(); }
				 * 
				 * //====================================== //Arm Usage v.
				 * Standard Deviation for(int i = 0; i < numAlgorithms; i++) {
				 * PrintWriter out = new PrintWriter(new
				 * FileWriter("data/stdArmData_"+dataNum+"_"+i+".txt")); for(int
				 * j = 0; j < numSteps; j++) {
				 * out.print(armRewardStdDevs[1]+stdChangeValues[j]); for(int k
				 * = 0; k < numArms; k++) out.print("\t"+avgArms[i][j][k]);
				 * out.println(); } out.close(); } for(int i = 0; i <
				 * numAlgorithms; i++) { PrintWriter out = new PrintWriter(new
				 * FileWriter("data/stdArmDataScatter_"+dataNum+"_"+i+".txt"));
				 * for(int j = 0; j < numSteps; j++) { for(int k = 0; k <
				 * numTrials; k++) {
				 * out.print(armRewardStdDevs[1]+stdChangeValues[j]); for(int l
				 * = 0; l < numArms; l++) out.print("\t"+armUsage[l][i][j][k]);
				 * out.println(); } } out.close(); }
				 * //====================================== //Revenue-per-Pull
				 * v. Budget for(int i = 0; i < numAlgorithms; i++) {
				 * PrintWriter out = new PrintWriter(new
				 * FileWriter("data/budgetReturnData_"+dataNum+"_"+i+".txt"));
				 * for(int j = 0; j < numSteps; j++) { out.print(initBudget +
				 * j*budgetIncrease + "\t");
				 * out.print(avgRevenue[i][j]/(initBudget+j*budgetIncrease));
				 * out.println(); } out.close(); }
				 * //====================================== //Revenue-per-Pull
				 * v. Number of Arms for(int i = 0; i < numAlgorithms; i++) {
				 * PrintWriter out = new PrintWriter(new
				 * FileWriter("data/numArmsReturnData_"+dataNum+"_"+i+".txt"));
				 * for(int j = 0; j < numSteps; j++) {
				 * out.print(numArms+(numArmsIncrease*j) + "\t");
				 * out.print(avgRevenue[i][j]/(initBudget)); out.println(); }
				 * out.close(); }
				 * 
				 * //====================================== //Run-Time v.
				 * Algorithm PrintWriter tOut = new PrintWriter(new
				 * FileWriter("data/timeData_"+dataNum+".txt")); String[]
				 * algNames = {
				 * "\"e-First Static\"","\"e-First Dynamic\"","\"Optimistic KUBE\""
				 * , "\"Pessimistic KUBE\"" ,"\"Optimistic Fractional KUBE\"",
				 * "\"Pessimistic Fractional KUBE\"" ,
				 * "\"KDE\"","\"Fractional KDE\""}; for(int i = 0; i <
				 * numAlgorithms; i++) { tOut.print(i+"\t");
				 * tOut.print(algNames[i]+"\t"); tOut.println(avgTime[i]); }
				 * tOut.close();
				 * 
				 * //====================================== //Arm Usage v.
				 * Algorithm
				 * 
				 * PrintWriter aOut = new PrintWriter(new FileWriter()); for(int
				 * i = 0; i < numAlgorithms; i++) { for(int j = 0; j < numArms;
				 * j++) { aOut.print((i*numArms+j)+"\t");
				 * aOut.println(avgArms[i][0][j]); } } aOut.close();
				 * 
				 * //====================================== //Regret & Error
				 * Bounds v. Time for(int i = 0; i < numAlgorithms; i++) {
				 * PrintWriter trialDataOut = new PrintWriter(new
				 * FileWriter("data/timeRegret_"+i+"_"+dataNum+".txt"));
				 * trialDataOut.print(avgTrialData[i]); trialDataOut.close();
				 * /*trialDataOut = new PrintWriter(new
				 * FileWriter("data/timeRegretScatter_"+i+"_"+dataNum+".txt"));
				 * for(int j = 0; j < numTrials; j++) {
				 * trialDataOut.println(myTrialData[i][0][j].skipPrint(10)); } }
				 * 
				 * for(int i = 0; i < numAlgorithms; i++) { PrintWriter
				 * trialDataOut = new PrintWriter(new
				 * FileWriter("data/timeMaxPulls_"+i+"_"+dataNum+".txt")); int j
				 * = 0; while(avgTrialData[i].hasSpecial(j)) {
				 * trialDataOut.println(j+"\t"+avgTrialData[i].getSpecial(j));
				 * j++; } trialDataOut.close(); }
				 */

				PrintWriter pOut = new PrintWriter(new FileWriter(
						"data/runParameters.txt"));
				pOut.println("Number of Arms: " + numArms);
				pOut.println("Budget: " + initBudget);
				pOut.println("Number of Trials: " + numTrials);
				pOut.println("Number of Steps: " + numSteps);
				pOut.close();

			} catch (IOException e) {

			}
		}
		// Outputting stuff.
		for (int j = 0; j < numAlgorithms; j++) {
			double curMean = 0;
			for (int i = 0; i < numIterations; i++) {
				curMean += means[j][i];
			}
			curMean /= numIterations;
			System.out.println(curMean);
		}

	}// end main

	private void printData(String fileName, int iterations, int[] xArray,
			double[] yArray1, double[] yArray2) {
		try {
			PrintWriter printer = new PrintWriter(new FileWriter(fileName));
			for (int i = 0; i < iterations; i++) {
				printer.println((xArray + "\t" + yArray1[i] + "\t" + yArray2[i]));
			}
			printer.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}

	private static String getAlgName(int i) {
		switch (i) {
		case 0:
			return "Greedy";
		case 1:
			return "e-First, e = 0.1";
		case 2:
			return "e-First, e = 0.2";
		case 3:
			return "Fractional KUBE";
		case 4:
			return "Fractional KDE";
		case 5:
			return "UCB-Bv";
		case 6:
			return "k-Split, k = 0.5";
		case 7:
			return "e-Progressive, e = 0.1";
		case 8:
			return "e-Progressive, e = 0.2";
		default:
			return "Adaptive Split, x = 0";
		}
	}
}// end Implementation
