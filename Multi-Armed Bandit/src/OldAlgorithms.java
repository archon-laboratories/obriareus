import core.Agent;
import core.Arm;
import core.ArmMemory;
import core.Bandit;

import java.util.ArrayList;
import java.util.Random;

/**
 * @deprecated
 */
public class OldAlgorithms
{
    static final Random rnd = new Random();
    static boolean debug = false;
    static boolean debugKube = false;
    static boolean debugUcbbv = false;
    static boolean debugKSmall = false;
    static boolean debugProg = false;
    static boolean debugEfirst = false;

/////////////////////////////////////////////////////////////////////////////////////////////////////////
// miscellaneous utility algorithms

    private static void sleep(int milliseconds) {

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
        }
    }

    private static double minIndexCost(ArrayList<Integer> a, Bandit b) //returns the integer of a that corresponds to the index arm with the least cost in b
    {
        if (a.isEmpty()) return -1;
        int min = a.get(0);
        for (int i = 0; i < a.size(); i++) {
            if (b.getArms().get(a.get(i)).getCost() < b.getArms().get(min).getCost())
                min = a.get(i);
        }
        return b.getArms().get(min).getCost();
    }

    private static int randomIndex(ArrayList<Integer> numList) {
        return numList.remove(rnd.nextInt(numList.size())); //returns the element that was removed from the list;
    }

    public static double minCost(ArrayList<Arm> a) {
        Arm min = a.get(0);
        for (int i = 0; i < a.size(); i++) {
            if (min.getCost() > a.get(i).getCost())
                min = a.get(i);
        }
        return min.getCost();
    }

    public static double minCost(ArrayList<Integer> indices, ArrayList<Arm> arms) {
        Arm best = arms.get(indices.get(0));
        for (int i = 1; i < indices.size(); i++) {
            if (arms.get(indices.get(i)).getCost() < best.getCost())
                best = arms.get(indices.get(i));
        }
        return best.getCost();
    }

    private static ArmMemory maxRewardDensity(ArrayList<ArmMemory> a, Agent agent) {
        ArmMemory max = null;
        for (int i = 0; i < a.size(); i++) {
            int index = (i + agent.getStartingArm()) % a.size();
            if (max == null) {
                if (a.get(index).getCost() <= agent.getBudget()) {
                    max = a.get(index);
                }
                //else just move on
            } else if (max.getRatio() < a.get(index).getRatio() && a.get(index).getCost() <= agent.getBudget()) {
                max = a.get(index);
                //bestArmCost = agentMemory.get(i).getCost();
            }
        }
        return max;
    }

    private static int maxRewardDensityIndex(ArrayList<ArmMemory> a, Agent agent) {
        int max = -1;
        for (int i = 0; i < a.size(); i++) {
            int index = (i + agent.getStartingArm()) % a.size();
            if (max == -1) {
                if (a.get(index).getCost() <= agent.getBudget()) {
                    max = index;
                }
                //else just move on
            } else if (a.get(max).getRatio() < a.get(index).getRatio() && a.get(index).getCost() <= agent.getBudget()) {
                max = index;
            }
        }
        //bestArmCost = agentMemory.get(i).getCost();
        return max;
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////
//Greedy core.Algorithms

    public static TrialData greedyAlg(Bandit b, Agent a, boolean online) {
        TrialData myTrial = new TrialData();
        int numMaxPulls = 0;
        int maxIndex = b.getBestArmIndex();

        //Exploration
        for (int i = 0; i < b.getNumArms(); i++) {
            if (a.getBudget() > b.getArms().get(i).getCost()) {
                a.pullArm(b.getArms().get(i), i);
                if (i == maxIndex) numMaxPulls++;
                myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), numMaxPulls, b.getOMR(a.getTotalPulls()), b.getNumArms(), false);
            }
        }
        //Exploitation
        ArmMemory bestArm = maxRewardDensity(a.getMemory(), a);

        boolean switchedBest = false;
        while (a.getBudget() >= minCost(b.getArms()))//explorer budget must be greater or equal to min cost
        {
            if (online)
                a.pullArm(b.getArms().get(a.getMemory().indexOf(bestArm)), a.getMemory().indexOf(bestArm));
            else
                a.pullArmNoMemory(b.getArms().get(a.getMemory().indexOf(bestArm)), a.getMemory().indexOf(bestArm));
            if (a.getMemory().indexOf(bestArm) == maxIndex) numMaxPulls++;
            myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), numMaxPulls, b.getOMR(a.getTotalPulls()), 1, switchedBest);
            switchedBest = false;
            if (online || a.getBudget() < bestArm.getCost()) {
                ArmMemory temp = bestArm;
                bestArm = maxRewardDensity(a.getMemory(), a);
                if (a.getMemory().indexOf(temp) != a.getMemory().indexOf(bestArm))
                    switchedBest = true;
            }
        }//end while loop
        return myTrial;
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////
// e-First core.Algorithms

    public static TrialData eFirstAlg(Bandit b, Agent a, double epsilon, boolean dynamic) {
        TrialData myTrial = new TrialData();
        int i = 0;
        ArrayList<Integer> temp = new ArrayList<Integer>();
        int numMaxPulls = 0;
        double eBudget = a.getBudget() * epsilon;
        if (debugEfirst) System.out.println("eBudget = " + eBudget);
        //System.out.println("exploration budget is "+a.getExplrBudget());

        //Exploration
        while (eBudget >= minCost(b.getArms()))//explorer budget must be greater or equal to min cost
        {
            if (temp.size() == 0)//Refill list for multiple passes(refills when all arms have been checked)
            {
                for (int j = 0; j < b.getNumArms(); j++)
                    temp.add(j);
            }
            i = randomIndex(temp); //chooses a random index to pull an arm
            if (eBudget - b.getArms().get(i).getCost() >= 0)//checks if agent can afford the pull
            {
                //System.out.println("\nArm " + i + " pulled");
                a.pullArm(b.getArms().get(i), i); //agent updates memory
                eBudget -= b.getArms().get(i).getCost();
                if (i == b.getBestArmIndex()) numMaxPulls++;
                if (debugEfirst)
                    System.out.println("Budget: " + a.getBudget() + " Pulled arm " + i + "(mean = [" + b.getArms().get(i).getMean() + "], sd = [" + b.getArms().get(i).getSD() + "], est. ratio = [" + a.getMemory().get(i).getRatio() + "]); Got Reward " + b.getArms().get(i).getRecentReward());
                myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), numMaxPulls, b.getOMR(a.getTotalPulls()), b.getNumArms(), false);
            }
        }

//        if(debug) System.out.println(explorCount + " pulls for exploration");

        //Exploitation
        ArrayList<ArmMemory> n = a.getMemory();
        ArmMemory bestArm = maxRewardDensity(n, a);
        ArmMemory lastBestArm = bestArm;
        boolean switchedBest = false;
        while (a.getBudget() >= minCost(b.getArms())) {
            if (dynamic) {
                a.pullArm(b.getArms().get(n.indexOf(bestArm)), n.indexOf(bestArm));
                if (n.indexOf(bestArm) == b.getBestArmIndex()) numMaxPulls++;
                if (debugEfirst)
                    System.out.println("Budget: " + a.getBudget() + " Pulled arm " + n.indexOf(bestArm) + "(mean = [" + b.getArms().get(n.indexOf(bestArm)).getMean() + "], sd = [" + b.getArms().get(n.indexOf(bestArm)).getSD() + "], est. ratio = [" + a.getMemory().get(n.indexOf(bestArm)).getRatio() + "]); Got Reward " + b.getArms().get(n.indexOf(bestArm)).getRecentReward());
                myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), numMaxPulls, b.getOMR(a.getTotalPulls()), 1, switchedBest);
            } else //static
            {
                while (a.getBudget() >= bestArm.getCost()) {
                    a.pullArmNoMemory(b.getArms().get(n.indexOf(bestArm)), n.indexOf(bestArm));
                    if (n.indexOf(bestArm) == b.getBestArmIndex()) numMaxPulls++;
                    if (debugEfirst)
                        System.out.println("Budget: " + a.getBudget() + " Pulled arm " + n.indexOf(bestArm) + "(mean = [" + b.getArms().get(n.indexOf(bestArm)).getMean() + "], sd = [" + b.getArms().get(n.indexOf(bestArm)).getSD() + "], est. ratio = [" + a.getMemory().get(n.indexOf(bestArm)).getRatio() + "]); Got Reward " + b.getArms().get(n.indexOf(bestArm)).getRecentReward());
                    myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), numMaxPulls, b.getOMR(a.getTotalPulls()), 1, switchedBest);
                }
            }
            bestArm = maxRewardDensity(n, a);
            if (a.getMemory().indexOf(lastBestArm) != a.getMemory().indexOf(bestArm)) {
                switchedBest = true;
                lastBestArm = bestArm;
            } else
                switchedBest = false;
        }//end while loop
        return myTrial;
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////
// KUBE core.Algorithms

    public static TrialData KubeAlgRandom(Bandit b, Agent a, boolean optimistic) {
        //----------------------------------------
        //Initialize Variables
        TrialData myTrial = new TrialData();
        ArrayList<ArmMemory> agentMemory = a.getMemory();
        int numArms = b.getNumArms();

        //double[] armsEstRD = new double[numArms];
        double minCost = minCost(b.getArms());
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int j = 0; j < b.getNumArms(); j++) //initialize temp
            temp.add(j);

        int armsConsidered = 0;
        int bestArm = -1;
        int lastBestArm = -1;
        boolean switchedArms = false;
        //----------------------------------------
        //Main KUBE loop
        while (a.getBudget() >= minCost) {
            if (temp.size() > 0) // initial phase - stops when all indices in temp have been chosen
            {
                int x = randomIndex(temp); //choose a random index x from temp
                if (b.getArms().get(x).getCost() <= a.getBudget())//check if agent can afford arm x
                {
                    a.pullArm(b.getArms().get(x), x);
                    if (debugKube)
                        System.out.println("Budget: " + a.getBudget() + " Pulled arm " + x + "(mean = [" + b.getArms().get(x).getMean() + "], sd = [" + b.getArms().get(x).getSD() + "], est. ratio = [" + a.getMemory().get(x).getRatio() + "]); Got Reward " + b.getArms().get(x).getRecentReward());
                    //sleep(10);
                    //armsEstRD[x] = KubeConfEst(agentMemory.get(x), optimistic, 1);//t is 1-indexed in the text - estimate confidence bound (reward density) of x
                    myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), 0, b.getOMR(a.getTotalPulls()), b.getNumArms(), false);
                }
            } else // combined exploration/exploitation phase
            {
                bestArm = maxRewardDensityIndex(a.getMemory(), a);
                if (lastBestArm == -1) lastBestArm = bestArm;
                else if (bestArm != lastBestArm) switchedArms = true;
                else switchedArms = false;

                //Get M*, the best combination of arms to pull
                int[] bestComb = KubeDOG(a.getBudget(), minCost, agentMemory, optimistic, a.getTotalPulls());

                //Get the total number of probable pulls stored in M*
                double mSum = 0;
                for (int k = 0; k < numArms; k++) {
                    mSum += bestComb[k];
                }

                //Use M* to determine which arm to pull, with the fractional probability of each (subtracted from a random fraction)
                double[] armProb = new double[numArms]; //probabilities that arm will be pulled
                for (int z = 0; z < numArms; z++) {
                    if (b.getArms().get(z).getCost() <= a.getBudget()) {
                        armProb[z] = ((double) bestComb[z]) / mSum;
                    } else
                        armProb[z] = 0;
                    if (armProb[z] > 0) armsConsidered++;
                }

                double[] cmlProb = new double[numArms];
                cmlProb[0] = armProb[0];

                for (int z = 1; z < numArms; z++)  //calculates cumulative probabilities
                    cmlProb[z] = cmlProb[z - 1] + armProb[z]; //probability of arm z is added to probabilities of all arms before it

                double totalProb = 0; //total probabilities

                for (int z = 0; z < numArms; z++) {
                    totalProb += armProb[z];
                }

                double randomVal = rnd.nextDouble() * totalProb;
                int i = -1;
                for (int z = 0; z < numArms && i < 0; z++) {
                    if (randomVal < cmlProb[z])
                        i = z;
                }

                a.pullArm(b.getArms().get(i), i);
                if (debugKube)
                    System.out.println("Budget: " + a.getBudget() + " Pulled arm " + i + "(mean = [" + b.getArms().get(i).getMean() + "], sd = [" + b.getArms().get(i).getSD() + "], est. ratio = [" + a.getMemory().get(i).getRatio() + "]); Got Reward " + b.getArms().get(i).getRecentReward());
                //System.out.println("exploit: pull arm "+i);
                //sleep(10);
                //armsEstRD[i] = KubeConfEst(agentMemory.get(i), optimistic, a.getTotalPulls()-numArms+1);// reevaluate; t is 1-indexed in the text
                myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), 0, b.getOMR(a.getTotalPulls()), armsConsidered, switchedArms);
                armsConsidered = 0;
            }//end else (which phase we are in)
        }//end while (KUBE algorithm main loop)
        return myTrial;
    }

    public static TrialData FractKubeAlgRandom(Bandit b, Agent a, boolean optimistic, boolean online) {
        //----------------------------------------
        //Initialize Variables
        TrialData myTrial = new TrialData();
        int numArms = b.getNumArms();
        double minCost = minCost(b.getArms());
        boolean doOnce = true;

        int bestArm = -1;
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int j = 0; j < numArms; j++)
            temp.add(new Integer(j));

        int lastBestArm = bestArm;
        boolean switchedArms = false;
        int time = 0;
        //----------------------------------------
        //Main Fractional KUBE loop
        while (a.getBudget() >= minCost) {
            if (temp.size() > 0) // initial phase
            {
                //Make sure we can't go over budget here.
                int x = randomIndex(temp);
                if (b.getArms().get(x).getCost() <= a.getBudget()) {
                    a.pullArm(b.getArms().get(x), x);
                    if (debugKube)
                        System.out.println("Budget: " + a.getBudget() + " Pulled arm " + x + "(mean = [" + b.getArms().get(x).getMean() + "], sd = [" + b.getArms().get(x).getSD() + "], est. ratio = [" + a.getMemory().get(x).getRatio() + "]); Got Reward " + b.getArms().get(x).getRecentReward());
                    myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), 0, b.getOMR(a.getTotalPulls()), b.getNumArms(), false);
                    time++;
                }
            } else // combined exploration/exploitation phase
            {
                //Find the current best arm, pull it, and re-estimate its value by the result
                if (online || doOnce) {
                    doOnce = false;
                    bestArm = -1;
                    for (int i = 0; i < numArms; i++) {
                        if (a.getMemory().get(i).getCost() <= a.getBudget() &&
                                (bestArm < 0 || fKubeEst(a.getMemory().get(i), time) > fKubeEst(a.getMemory().get(bestArm), time)))
                            bestArm = i;
                    }

                    if (lastBestArm == -1)
                        lastBestArm = bestArm;
                    if (lastBestArm != bestArm) {
                        switchedArms = true;
                        lastBestArm = bestArm;
                    } else
                        switchedArms = false;
                }

                a.pullArm(b.getArms().get(bestArm), bestArm);
                if (debugKube)
                    System.out.println("Budget: " + a.getBudget() + " Pulled arm " + bestArm + "(mean = [" + b.getArms().get(bestArm).getMean() + "], sd = [" + b.getArms().get(bestArm).getSD() + "], est. ratio = [" + a.getMemory().get(bestArm).getRatio() + "]); Got Reward " + b.getArms().get(bestArm).getRecentReward());
                myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), 0, b.getOMR(a.getTotalPulls()), 1, switchedArms);
                time++;
            }//end else (which phase are we in)
        }//end while (main Fractional KUBE loop)
        return myTrial;
    }

    /**
     * Use the density-ordered greedy algorithm to find M*, the best
     * combination of arms to be pulled probabilistically.
     */
    private static int[] KubeDOG(double budget, double minCost, ArrayList<ArmMemory> arms, boolean opt, int t) {
        //Get "best" arms combination, M*, to pull from
        int[] bestComb = new int[arms.size()];
        double tempBudget = budget;

        double[] armsRD = new double[arms.size()];
        for (int i = 0; i < arms.size(); i++) {
            armsRD[i] = KubeConfEst(arms.get(i), opt, t);
        }

        //add the best remaining arms that we can fit (greedy) into M*
        while (tempBudget >= minCost) {
            int bestArm = -1;
            for (int i = 0; i < arms.size(); i++) {
                if (bestArm < 0) {
                    if (arms.get(i).getCost() <= tempBudget) {
                        bestArm = i;
                    }
                    //else just move on
                } else if (armsRD[i] > armsRD[bestArm] && arms.get(i).getCost() <= tempBudget) {
                    bestArm = i;
                    //bestArmCost = agentMemory.get(i).getCost();
                }
            }
            int numPullsPossible = (int) (tempBudget / arms.get(bestArm).getCost());
            bestComb[bestArm] += numPullsPossible;
            tempBudget -= numPullsPossible * arms.get(bestArm).getCost();
        }
        return bestComb;
    }

    /**
     * KUBE Confidence Estimation - Applies the confidence interval specified in the paper to
     * re-evaluate projected rewards for a given arm.
     */
    private static double KubeConfEst(ArmMemory a, boolean opt, int t) {
        double estimate = a.getMeanReward();
        if (opt)
            estimate += Math.sqrt(2 * Math.log((double) t) / a.getPulls()); //upper confidence bound
        else
            estimate -= Math.sqrt(2 * Math.log((double) t) / a.getPulls()); //lower confidence bound
        estimate /= a.getCost();
        return estimate;
    }

    private static double fKubeEst(ArmMemory thisArm, int time) {
        return thisArm.getRatio() + Math.sqrt(2 * Math.log(time) / thisArm.getPulls()) / thisArm.getCost();
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////
// KDE core.Algorithms

    /**
     * KDE Unique core.Algorithms: ensures that arms are not pulled more than once during "exploration" phase
     */
    public static TrialData KdeAlgUnique(Bandit b, Agent a, double gamma) {
        if (gamma <= 0) {
            System.out.println("Non-positive gamma value; KDE cannot function.");
            return null;
        }

        int t = 1;
        int numArms = b.getNumArms();
        int numFeasibleArms = numArms;
        ArrayList<ArmMemory> agentMemory = a.getMemory();
        int bestArm;
        int lastBestArm = -1;
        boolean switchedArms = false;
        boolean[] feasibleArms = new boolean[numArms];
        double minCost = minCost(b.getArms());
        TrialData myTrial = new TrialData();

        for (int i = 0; i < numArms; i++) feasibleArms[i] = true;

        while (a.getBudget() >= minCost) {
            //Eliminate arms that exceed our current budget.
            bestArm = -1;
            for (int i = 0; i < numArms; i++) {
                if (agentMemory.get(i).getCost() > a.getBudget() && feasibleArms[i] == true) {
                    feasibleArms[i] = false;
                    numFeasibleArms--;
                }
                if (agentMemory.get(i).getCost() <= a.getBudget() &&
                        (bestArm < 0 || (agentMemory.get(i).getRatio() > agentMemory.get(bestArm).getRatio() && feasibleArms[i])))
                    bestArm = i;
            }
            if (lastBestArm == -1)
                lastBestArm = bestArm;
            else if (lastBestArm != bestArm) {
                switchedArms = true;
                lastBestArm = bestArm;
            } else
                switchedArms = false;

            //Get "best" arms combination M* to pull from
            int[] bestComb = KdeDOG(a.getBudget(), minCost, bestArm, agentMemory);

            //Calculate Epsilon-T
            double epsT = Math.min(1, gamma / ((double) (t))); //t is 1-indexed

            //Determine which arm to pull, using the probability of each
            double mSum = 0;
            for (int k = 0; k < numArms; k++) {
                mSum += bestComb[k];
            }

            //Use fractional probability to find which arm to pull
            double[] armProb = new double[numArms]; //probabilities that arm will be pulled
            for (int z = 0; z < numArms; z++) {
                if (feasibleArms[z] == true) {
                    armProb[z] = (1 - epsT) * ((double) bestComb[z] / mSum) + (epsT / (double) numFeasibleArms);
                } else
                    armProb[z] = 0;
                if (epsT >= 1 && a.getMemory().get(z).getPulls() > a.getTotalPulls() / numArms)  //ensures that arms are not pulled more than once before gamma phase begins
                    armProb[z] = 0;
            }
            double[] cmlProb = new double[numArms];
            cmlProb[0] = armProb[0];

            for (int z = 1; z < numArms; z++)  //calculates cumulative probabilities
                cmlProb[z] = cmlProb[z - 1] + armProb[z]; //probability of arm z is added to probabilities of all arms before it

            double totalProb = 0;

            for (int z = 0; z < numArms; z++) {
                totalProb += armProb[z];
            }
            double randomVal = rnd.nextDouble() * totalProb;

            int i = -1;
            for (int z = 0; z < numArms && i < 0; z++) {
                if (randomVal < cmlProb[z])
                    i = z;
            }
            a.pullArm(b.getArms().get(i), i);
            if (debug)
                System.out.println("Budget: " + a.getBudget() + " / Pulled arm " + i + "(mean = [" + b.getArms().get(i).getMean() + "], sd = [" + b.getArms().get(i).getSD() + "], est. ratio = [" + a.getMemory().get(i).getRatio() + "]); Got Reward " + b.getArms().get(i).getRecentReward());
            myTrial.addValues(t - 1, a.getMeanReward(), a.getRegret(), 0, b.getOMR(a.getTotalPulls()), numFeasibleArms, switchedArms); //t is 1-indexed
            t++;
        }
        return myTrial;
    }

    /**
     * Fractional KDE
     */
    public static TrialData FractKdeAlg(Bandit b, Agent a, double gamma, boolean online) {
        if (gamma <= 0) {
            System.out.println("Non-positive gamma value; KDE cannot function.");
            return null;
        }

        int t = 0;
        int numArms = b.getNumArms();
        ArrayList<ArmMemory> agentMemory = a.getMemory();
        TrialData myTrial = new TrialData();

        boolean[] feasibleArms = new boolean[numArms];
        for (int i = 0; i < numArms; i++) feasibleArms[i] = true;
        int numFeasibleArms = numArms;

        //Get "best" arm, I+
        int bestArm;
        int lastBestArm = -1;
        boolean switchedArms = false;

        while (a.getBudget() >= minCost(b.getArms())) {
            //Eliminate arms that exceed our current budget.
            bestArm = -1;
            for (int i = 0; i < numArms; i++) {
                if (agentMemory.get(i).getCost() > a.getBudget() && feasibleArms[i] == true) {
                    feasibleArms[i] = false;
                    numFeasibleArms--;
                }

                if (bestArm < 0) {
                    if (agentMemory.get(i).getCost() <= a.getBudget())
                        bestArm = i;
                    //else just move on
                } else if (agentMemory.get(i).getRatio() > agentMemory.get(bestArm).getRatio() && feasibleArms[i])
                    bestArm = i;
            }
            if (lastBestArm == -1)
                lastBestArm = bestArm;
            else if (lastBestArm != bestArm) {
                switchedArms = true;
                lastBestArm = bestArm;
            } else
                switchedArms = false;


            double epsT = Math.min(1, gamma / ((double) (t + 1)));
            double[] armProb = new double[numArms]; //probabilities that arm will be pulled
            for (int z = 0; z < numArms; z++) {
                if (feasibleArms[z] == true && !(epsT >= 1 && a.getMemory().get(z).getPulls() > a.getTotalPulls() / numArms))//ensures that arms are not pulled more than once before gamma phase begins
                    armProb[z] = epsT / ((double) numFeasibleArms);
                else
                    armProb[z] = 0;
            }
            armProb[bestArm] += 1 - epsT;
            double[] cmlProb = new double[numArms];
            cmlProb[0] = armProb[0];

            for (int z = 1; z < numArms; z++)  //calculates cumulative probabilities
                cmlProb[z] = cmlProb[z - 1] + armProb[z]; //probability of arm z is added to probabilities of all arms before it

            double totalProb = 0;

            for (int z = 0; z < numArms; z++) {
                totalProb += armProb[z];
            }

            double randomVal = rnd.nextDouble() * totalProb;

            int i = -1;
            for (int z = 0; z < numArms && i < 0; z++) {
                if (randomVal < cmlProb[z])
                    i = z;
            }
            if (i == -1) {
                i = numArms - 1;
                while (feasibleArms[i] == false) i--;
            }

            if (online)
                a.pullArm(b.getArms().get(i), i);
            else
                a.pullArmNoMemory(b.getArms().get(i), i);
            if (debug)
                System.out.println("Budget: " + a.getBudget() + " / Pulled arm " + i + "(mean = [" + b.getArms().get(i).getMean() + "], sd = [" + b.getArms().get(i).getSD() + "], est. ratio = [" + a.getMemory().get(i).getRatio() + "]); Got Reward " + b.getArms().get(i).getRecentReward());
            myTrial.addValues(t, a.getMeanReward(), a.getRegret(), 0, b.getOMR(a.getTotalPulls()), numFeasibleArms, switchedArms);
            t++;
        }
        return myTrial;
    }

    /**
     * KdeDOG is different from KubeDOG in that confidence intervals are not used in sub-optimal arm selection.
     *
     * @param budget
     * @param minCost
     * @param bestArm
     * @param arms
     * @return
     */
    private static int[] KdeDOG(double budget, double minCost, int bestArm, ArrayList<ArmMemory> arms) {
        //Get "best" arms combination, M*, to pull from
        int[] bestComb = new int[arms.size()];
        double tempBudget = budget;

        //add as much of the "best" arm as we can to M*
        int mainChunk = (int) (tempBudget / arms.get(bestArm).getCost());
        bestComb[bestArm] += mainChunk;
        tempBudget -= mainChunk * arms.get(bestArm).getCost();

        //add the best remaining arms that we can fit (greedy) into M*
        while (tempBudget >= minCost) {
            bestArm = 0;
            for (int i = 0; i < arms.size(); i++) {
                if (arms.get(i).getRatio() >= arms.get(bestArm).getRatio() && tempBudget >= arms.get(i).getCost()) {
                    bestArm = i;
                }
            }
            bestComb[bestArm]++;
            tempBudget -= arms.get(bestArm).getCost();
        }
        return bestComb;
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////
//UCB-BV core.Algorithms

    /**
     * performs the UCB-BV algorithm
     * bound 1 assumes known mean of cost defaultDistributions (here, fixed costs, so OK)
     * bound 2 assumes empirically estimated costs (probably not needed here)
     *
     * @param b
     * @param a
     * @param whichBound
     * @return
     */
    public static TrialData UCBBVAlg(Bandit b, Agent a, int whichBound, boolean online) {
        if (debugUcbbv) System.out.println("Starting...");
        TrialData myTrial = new TrialData();
        int numArms = b.getNumArms();
        double[] dValues = new double[numArms]; //assume initialized at 0

        int bestArm = -1;
        int lastBestArm = -1;
        boolean switchedArms = false;

        int t = 0;
        double minCost = minCost(b.getArms());
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int j = 0; j < b.getNumArms(); j++)
            temp.add(j);

        double lambda = minCost(b.getArms());

        boolean doOnce = true;

        while (a.getBudget() >= minCost) {
            if (temp.size() > 0) // initial phase
            {
                int x = randomIndex(temp);
                if (b.getArms().get(x).getCost() <= a.getBudget()) {
                    a.pullArm(b.getArms().get(x), x);
                    myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), 0, b.getOMR(a.getTotalPulls()), b.getNumArms(), false);
                    if (debugUcbbv)
                        System.out.println("Budget: " + a.getBudget() + " / Pulled arm " + x + "(mean = [" + b.getArms().get(x).getMean() + "], sd = [" + b.getArms().get(x).getSD() + "], D-value = [none]); Got Reward " + b.getArms().get(x).getRecentReward());
                    t++;
                }
            } else // exploitation phase
            {
                t++;
                if (online || doOnce) {
                    doOnce = false;
                    bestArm = -1;
                    for (int i = 0; i < numArms; i++) {
                        double tempRoot = Math.sqrt(Math.log(t - 1) / a.getMemory().get(i).getPulls());
                        if (whichBound == 0)
                            dValues[i] = a.getMemory().get(i).getRatio() + (1 + (1 / lambda)) * tempRoot / (lambda - tempRoot);
                        else
                            dValues[i] = a.getMemory().get(i).getRatio() + (tempRoot / lambda) * (1 + 1 / (lambda - tempRoot));

                        if (debugUcbbv) System.out.println("Set D for arm " + i + " at " + dValues[i]);

                        if (a.getMemory().get(i).getCost() <= a.getBudget() && (bestArm < 0 || dValues[i] > dValues[bestArm]))
                            bestArm = i;
                        if (lastBestArm == -1)
                            lastBestArm = bestArm;
                        else if (lastBestArm != bestArm) {
                            switchedArms = true;
                            lastBestArm = bestArm;
                        } else
                            switchedArms = false;
                    }
                }

                if (debugUcbbv)
                    System.out.println("Budget: " + a.getBudget() + " / Pulled arm " + bestArm + "(mean = [" + b.getArms().get(bestArm).getMean() + "], sd = [" + b.getArms().get(bestArm).getSD() + "], D-value = [" + dValues[bestArm] + "]); Got Reward " + b.getArms().get(bestArm).getRecentReward());
                a.pullArm(b.getArms().get(bestArm), bestArm);
                myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), 0, b.getOMR(a.getTotalPulls()), 1, switchedArms);

            }//end else (which phase we are in)
            if (debugUcbbv) sleep(500);
        }//end while (main alg. loop)
        return myTrial;
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////
// z-Test core.Algorithms

    public static TrialData HypothesisTestingAlg(Bandit b, Agent a, double deviation, double zValue) {
        TrialData myTrial = new TrialData();

        ArrayList<Integer> acceptable = new ArrayList<Integer>();
        ArrayList<Integer> comparable = new ArrayList<Integer>(); //acceptable arms already pulled
        for (int i = 0; i < b.getArms().size(); i++) acceptable.add(i); //fill acceptable arraylist with indices of arms

        //System.out.println(a.getBudget()+" vs "+minIndexCost(acceptable, b));
        while (a.getBudget() >= minIndexCost(acceptable, b)) {
            int random = acceptable.get(rnd.nextInt(acceptable.size())); //choose a random arm NOT index from acceptable

            if (b.getArms().get(random).getCost() <= a.getBudget()) {
                a.pullArm(b.getArms().get(random), random);
                if (!comparable.contains(random))
                    comparable.add(random);
                myTrial.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), 0, b.getOMR(a.getTotalPulls()), acceptable.size(), false); //does not account for switched arms

                //Begin z-comparison: I IS AN INDEX IN COMPARABLE, NOT AN ARM NUMBER===========================================================
                for (int i = comparable.size() - 1; i >= 0 && comparable.size() > 1 && acceptable.contains(random); i--) //iterate through comparable, comparing the the random arm to arm i
                {
                    if (random != comparable.get(i)) //do not compare an arm to itself
                    {
                        double sigmaSquared = deviation * deviation;
                        double z = ((a.getMemory().get(random).getRatio() - a.getMemory().get(comparable.get(i)).getRatio())) /
                                Math.sqrt((sigmaSquared / a.getMemory().get(random).getPulls()) + (sigmaSquared / a.getMemory().get(comparable.get(i)).getPulls()));
                        if (z < -zValue) {
                            acceptable.remove(acceptable.indexOf(random)); //arm i is greater than arm random
                            comparable.remove(comparable.indexOf(random)); //remove arm random from each list
                        } else if (z > zValue) {
                            acceptable.remove(acceptable.indexOf(comparable.get(i))); //arm random is greater than arm i
                            comparable.remove(i);
                        }
                    }//end random comparison
                }//end iteration
            }//end if (sufficient budget)
        }//end while
        return myTrial;
    }

    public static TrialData lSplitAlg(Bandit b, Agent a, double dropFract, boolean incScrutiny, boolean online) {
        TrialData myData = new TrialData();
        int pullsPerArm = 1;
        int numMaxPulls = 0;
        int iterations = 0;
        int droppedArms = 0;
        int startArm = 0;
        int explorCount = 0;
        boolean exploring = true;
        ArrayList<Integer> feasibles = new ArrayList<Integer>();

        int bestArm = -1;
        int lastBestArm = -1;
        boolean switchedArms = false;

        for (int i = 0; i < b.getNumArms(); i++) {
            feasibles.add(i);
        }
        //if we reorder these according to expected return per cost, we could improve for cases where we don't reduce to just one arm (low budget)
        while (feasibles.size() > 0 && a.getBudget() >= minCost(feasibles, b.getArms())) {
            if (online)//check if we've switched our "best arm" estimates.
            {
                bestArm = -1;
                for (int i = startArm; i < feasibles.size(); i++) {
                    if (bestArm == -1) {
                        if (a.getMemory().get(feasibles.get(i)).getCost() <= a.getBudget())
                            bestArm = i;
                    } else {
                        if (a.getMemory().get(feasibles.get(i)).getCost() <= a.getBudget()
                                && a.getMemory().get(feasibles.get(i)).getRatio() > a.getMemory().get(bestArm).getRatio())
                            bestArm = i;
                    }
                }
                if (lastBestArm == -1)
                    lastBestArm = bestArm;
                else if (lastBestArm != bestArm) {
                    switchedArms = true;
                    lastBestArm = bestArm;
                } else
                    switchedArms = false;
            }
            for (int i = startArm; i < feasibles.size(); i++) {
                for (int j = 0; j < pullsPerArm; j++) {
                    if (a.getMemory().get(feasibles.get(i)).getCost() <= a.getBudget()) {
                        if (online || exploring)
                            a.pullArm(b.getArms().get(feasibles.get(i)), feasibles.get(i));
                        else
                            a.pullArmNoMemory(b.getArms().get(feasibles.get(i)), feasibles.get(i));
                        if (feasibles.get(i) == b.getBestArmIndex()) numMaxPulls++;
                        if (debug)
                            System.out.println("Budget: " + a.getBudget() + " / Pulled arm " + feasibles.get(i) + "(mean = [" + b.getArms().get(feasibles.get(i)).getMean() + "], sd = [" + b.getArms().get(feasibles.get(i)).getSD() + "], est. ratio = [" + a.getMemory().get(feasibles.get(i)).getRatio() + "]); Got Reward " + b.getArms().get(feasibles.get(i)).getRecentReward());
                        if (startArm + droppedArms < b.getNumArms() - 1)
                            explorCount++;

                        myData.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), numMaxPulls, b.getOMR(a.getTotalPulls()), feasibles.size() - startArm, switchedArms);
                    } else {
                        feasibles.remove(i);
                        droppedArms++;
                        if (startArm > i || startArm >= feasibles.size() - 1)
                            startArm--;
                        i--;
                        j = pullsPerArm; //restart j-loop
                    }
                }
            }

            iterations++;
            if (startArm < feasibles.size() - 1) {
                startArm = (int) (feasibles.size() - b.getNumArms() * (Math.pow(1 - dropFract, iterations)));
                if (startArm < 0)
                    startArm = 0;
                //if(debug) System.out.println("startArm is now "+startArm);
            }

            if (debugKSmall) {
                System.out.print("Before kSmall: ");
                for (int i = 0; i < feasibles.size(); i++) System.out.print(feasibles.get(i));
                System.out.println();
            }
            if (kSmall(feasibles, a.getMemory(), startArm + 1, 0, feasibles.size() - 1) == -1) {
                System.out.println("ARRRRRGH");
                return null;//startArm+1 because 1st arm is 0+1
            }
            if (debugKSmall) {
                System.out.print("After kSmall: ");
                for (int i = 0; i < feasibles.size(); i++) System.out.print(feasibles.get(i));
                System.out.println();
            }


            if (!online) {
                int times = startArm;
                for (int i = 0; i < times; i++) {
                    feasibles.remove(0);
                    droppedArms++;
                }
                startArm = 0;
            }
            //System.out.println("startArm is "+startArm);
            //for(int i = 0; i < feasibles.size(); i++) System.out.println("core.Arm "+feasibles.get(i)+": "+b.getArms().get(feasibles.get(i)).getMean()+" / "+a.getMemory().get(feasibles.get(i)).getAvgReward());
            //increase our scrutiny per pass, if applicable
            if (incScrutiny) pullsPerArm *= 2;
            if (feasibles.size() == 1) exploring = false;
        }
        //System.out.println(explorCount+" pulls spent in exploration");
        return myData;
    }

    static int kSmall(ArrayList<Integer> indices, ArrayList<ArmMemory> arms, int k, int start, int end) {
        if (debugKSmall) System.out.println("start is " + start + "; end is " + end);
        if (start == end) return start;
        else if (start > end)
            return -1;
        int pI = start + rnd.nextInt(end - start + 1); //pick a random arm index from the range
        if (debugKSmall)
            System.out.println("pivot is arm " + indices.get(pI) + " (" + arms.get(indices.get(pI)).getRatio() + ")");
        for (int i = start; i <= end; i++) {
            if (i < pI && arms.get(indices.get(i)).getRatio() >= arms.get(indices.get(pI)).getRatio()) {
                if (debugKSmall)
                    System.out.println("Something to report for i == " + i + " (arm " + indices.get(i) + ")");
                if (debugKSmall)
                    System.out.println("core.Arm " + indices.get(i) + " (" + arms.get(indices.get(i)).getRatio() + ") is larger than the pivot; move to end.");
                indices.add(end, indices.remove(i)); //move to the end
                pI--;
                i--; //do not increment this time
            } else if (i > pI && arms.get(indices.get(i)).getRatio() < arms.get(indices.get(pI)).getRatio()) {
                if (debugKSmall)
                    System.out.println("Something to report for i == " + i + " (arm " + indices.get(i) + ")");
                if (debugKSmall)
                    System.out.println("core.Arm " + indices.get(i) + " (" + arms.get(indices.get(i)).getRatio() + ") is less than the pivot; move to first");
                indices.add(start, indices.remove(i)); //move to the first of this segment of the list
                pI++; //keep pointing to the same index
            } else if (debugKSmall)
                System.out.print("Nothing to report for i = " + i + " (arm " + indices.get(i) + ") | ");
        }
        if (debugKSmall) {
            System.out.print("After this cycle, index order is ");
            for (int j = 0; j < indices.size(); j++) System.out.print(indices.get(j));
            System.out.println();
        }

        if (debugKSmall) System.out.println("Recursion: pI is " + pI + "; k is " + k);
        if (pI > k - 1)
            return kSmall(indices, arms, k, start, pI - 1);
        else if (pI < k - 1)
            return kSmall(indices, arms, k, pI + 1, end);
        else
            return pI;
    }

    //returns the bound size for the given confidence level
    static double HoeffdingBound(double numSamples, double confidence, double lBound, double rBound) {
        double sumSquareDists = 0;
        for (int i = 0; i < numSamples; i++)
            sumSquareDists += Math.pow(rBound - lBound, 2);
        double temp = Math.sqrt(Math.log(1. - confidence) * (-sumSquareDists) / (2 * Math.pow(numSamples, 2)));
        return temp;
    }

    public static TrialData AdaptiveAlg1(Bandit b, Agent a, double confidence, double lBound, double rBound) {
        TrialData myData = new TrialData();
        int numAllowed = b.getNumArms();
        int numMaxPulls = 0;
        boolean[] affordable = new boolean[numAllowed];
        boolean[] dominated = new boolean[numAllowed];

        int bestArm = -1;
        int lastBestArm = -1;
        boolean switchedArms = false;

        for (int i = 0; i < b.getNumArms(); i++) {
            affordable[i] = true;
            dominated[i] = false;
        }
        while (numAllowed > 0) {
            bestArm = maxRewardDensityIndex(a.getMemory(), a);
            if (lastBestArm == -1)
                lastBestArm = bestArm;
            else if (lastBestArm != bestArm) {
                switchedArms = false;
                lastBestArm = bestArm;
            } else
                switchedArms = false;

            //PULL viable arms
            for (int i = 0; i < b.getNumArms(); i++) {
                //ELIMINATE non-affordable arms
                if (affordable[i] && b.getArms().get(i).getCost() > a.getBudget()) {
                    affordable[i] = false;
                    if (dominated[i] == false)
                        numAllowed--;
                }
                if (affordable[i] && !dominated[i]) {
                    a.pullArm(b.getArms().get(i), i);
                    if (i == b.getBestArmIndex()) numMaxPulls++;
                    myData.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), numMaxPulls, b.getOMR(a.getTotalPulls()), numAllowed, switchedArms);
                    if (debug)
                        System.out.println("Budget: " + a.getBudget() + " Pulled arm " + i + "(est reward = [" + a.getMemory().get(i).getMeanReward() + "]); Got Reward " + b.getArms().get(i).getRecentReward());
                }
            }
            if (numAllowed > 0) {
                //ELIMINATE dominated arms
                boolean[] shouldBeDominated = new boolean[b.getNumArms()];
                for (int i = 0; i < b.getNumArms(); i++)
                    shouldBeDominated[i] = false;

                for (int i = 0; i < b.getNumArms(); i++) //check whether i is dominated by any other arm at this time
                {
                    for (int j = 0; j < b.getNumArms(); j++) {
                        if (i != j && a.getMemory().get(i).getRatio() > a.getMemory().get(j).getRatio() && !shouldBeDominated[i] && !shouldBeDominated[j]) //i must be better, and undominated this round
                        {
                            double factor1 = a.getMemory().get(i).getRatio() - HoeffdingBound(a.getMemory().get(i).getPulls(), confidence, lBound, rBound);
                            double factor2 = a.getMemory().get(j).getRatio() + HoeffdingBound(a.getMemory().get(j).getPulls(), confidence, lBound, rBound);
                            //boolean factor1 = a.getMemory().get(feasibles.get(better)).getMean() > a.getMemory().get(feasibles.get(worse)).getMean();
                            //boolean factor2 = a.getMemory().get(feasibles.get(better)).getSD() < a.getMemory().get(feasibles.get(worse)).getSD() )
                            //System.out.println("Is "+factor1 +" greater than or equal to "+factor2+"?");
                            if (factor1 >= factor2) {
                                //worse is dominated; eliminate it
                                shouldBeDominated[j] = true;
                            }
                        }
                    }
                }//end comparisons loop
                for (int i = 0; i < b.getNumArms(); i++) {
                    if (shouldBeDominated[i] && !dominated[i]) {
                        //worse is dominated; eliminate it
                        System.out.println("Eliminated arm " + i);
                        dominated[i] = true;
                        if (affordable[i] == true)
                            numAllowed--;
                    } else if (!shouldBeDominated[i] && dominated[i]) {
                        System.out.println("Resuscitated arm " + i);
                        dominated[i] = false;
                        if (affordable[i] == true)
                            numAllowed++;
                    }
                }
            }

        }//end while loop
        //System.out.println("Ended with budget "+a.getBudget());
        return myData;
    }//end adaptive method


    public static TrialData soaavAlg(Bandit b, Agent a, double xValue, boolean online) {
        TrialData myData = new TrialData();
        int currBest = 0;
        int numMaxPulls = 0;
        double meanRatio = 0;
        int numAllowed = b.getNumArms();
        boolean[] affordable = new boolean[numAllowed];
        boolean[] dominated = new boolean[numAllowed];
        boolean exploring = true;

        int bestArm = -1;
        int lastBestArm = -1;
        boolean switchedArms = false;

        for (int i = 0; i < b.getNumArms(); i++) {
            affordable[i] = true;
            dominated[i] = false;
        }
        while (numAllowed > 0) {
            bestArm = maxRewardDensityIndex(a.getMemory(), a);
            if (lastBestArm == -1)
                lastBestArm = bestArm;
            else if (lastBestArm != bestArm) {
                switchedArms = false;
                lastBestArm = bestArm;
            } else
                switchedArms = false;

            //PULL viable arms
            if (numAllowed == 1) exploring = false;
            meanRatio = 0;
            for (int i = 0; i < b.getNumArms(); i++) {
                //ELIMINATE non-affordable arms
                if (affordable[i] && b.getArms().get(i).getCost() > a.getBudget()) {
                    affordable[i] = false;
                    if (dominated[i] == false)
                        numAllowed--;
                }

                if (affordable[i] && !dominated[i]) {
                    if (online || exploring)
                        a.pullArm(b.getArms().get(i), i);
                    else
                        a.pullArmNoMemory(b.getArms().get(i), i);

                    if (i == b.getBestArmIndex()) numMaxPulls++;
                    if (b.getArms().get(i).getRecentReward() >= b.getArms().get(currBest).getRecentReward())
                        currBest = i;
                    meanRatio += a.getMemory().get(i).getRatio();
                    myData.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), numMaxPulls, b.getOMR(a.getTotalPulls()), numAllowed, switchedArms);
                    if (debug)
                        System.out.println("Budget: " + a.getBudget() + " Pulled arm " + i + "(est reward = [" + a.getMemory().get(i).getMeanReward() + "]); Got Reward " + b.getArms().get(i).getRecentReward());
                }
            }
            if ((online || exploring) && numAllowed > 0) {
                meanRatio /= numAllowed;
                if (debug)
                    System.out.println(meanRatio + " is the mean reward for this round; chop at " + ((1. + xValue) * meanRatio));

                //DETERMINE dominated arms
                for (int i = 0; i < b.getNumArms(); i++) //make comparisons
                {
                    if (!dominated[i] && a.getMemory().get(i).getRatio() < (1 + xValue) * meanRatio) {
                        if (numAllowed > 1) {
                            //if(debug) System.out.println("core.Arm "+i+" is eliminated.");
                            dominated[i] = true;
                            if (affordable[i] == true)
                                numAllowed--;
                        } else {
                            if (debug) System.out.println("Cannot eliminate final arm " + i);
                            //should revert to best known affordable arm
                            int j = -1;
                            for (int k = 0; k < a.getMemory().size(); k++) {
                                if (j == -1) {
                                    if (a.getMemory().get(k).getCost() <= a.getBudget())
                                        j = k;
                                } else if (a.getMemory().get(k).getRatio() > a.getMemory().get(j).getRatio() && a.getMemory().get(k).getCost() <= a.getBudget())
                                    j = k;
                            }
                            if (j > -1 && i != j) {
                                if (debug)
                                    System.out.println("Reverting to best known affordable arm " + i + " (no longer eliminated).");
                                dominated[i] = true;
                                if (affordable[i] == true)
                                    numAllowed--;
                                dominated[j] = false;
                                if (affordable[j] == true)
                                    numAllowed++;
                            }
                        }
                    } else if (dominated[i] && a.getMemory().get(i).getRatio() >= (1 + xValue) * meanRatio) {
                        //if(debug) System.out.println("core.Arm "+i+" is no longer eliminated.");
                        dominated[i] = false;
                        if (affordable[i] == true)
                            numAllowed++;
                    }
                }
            }
        }//end while loop
        //System.out.println("Ended with budget "+a.getBudget());
        return myData;
    }//end adaptive method

    //pick e such that eB is evenly divisible by K
    public static TrialData peefAlg(Bandit b, Agent a, double epsilon) {
        //double oldFract = 1.-1./(getSfromE(epsilon,a.getBudget(),b.getNumArms()));
        //double dropFract = 1.-1./((b.getNumArms()+m-1)/m);

        double dropFract;
        double m = epsilon * a.getBudget() / b.getNumArms();
        if (m <= 1)
            dropFract = (b.getNumArms() - 1.) / b.getNumArms();
        else
            dropFract = 1. - 1. / ((m - (1 / b.getNumArms())) / (m - 1));

        if (debugProg) {
            int predictedPulls = b.getNumArms();
            System.out.println(predictedPulls);
            for (int it = 1; b.getNumArms() - (int) (b.getNumArms() * (1. - Math.pow(1 - dropFract, it))) > 1; it++) {
                predictedPulls += b.getNumArms() - (int) (b.getNumArms() * (1. - Math.pow(1 - dropFract, it)));
                System.out.println(predictedPulls);
            }
            System.out.println("For a system with budget " + a.getBudget() + " and epsilon " + epsilon + ", we should have " + epsilon * a.getBudget() + " pulls."
                    + "\nIn fact, we drop " + dropFract + " of the arms for each pass, for a total of " + predictedPulls + " exploration pulls. m = " + m);
        }
        TrialData myData = new TrialData();
        int pullsPerArm = 1;
        int numMaxPulls = 0;
        int iterations = 0;
        int droppedArms = 0;
        int startArm = 0;
        ArrayList<Integer> feasibles = new ArrayList<Integer>();
        for (int i = 0; i < b.getNumArms(); i++) {
            feasibles.add(i);
        }
        //if we reorder these according to expected return per cost, we could improve for cases where we don't reduce to just one arm (low budget)

        int bestArm = -1;
        int lastBestArm = -1;
        boolean switchedArms = false;

        while (feasibles.size() > 0 && a.getBudget() >= minCost(feasibles, b.getArms())) {
            bestArm = maxRewardDensityIndex(a.getMemory(), a);
            if (lastBestArm == -1)
                lastBestArm = bestArm;
            else if (lastBestArm != bestArm) {
                switchedArms = false;
                lastBestArm = bestArm;
            } else
                switchedArms = false;

            for (int i = startArm; i < feasibles.size(); i++) {
                for (int j = 0; j < pullsPerArm; j++) {
                    if (a.getMemory().get(feasibles.get(i)).getCost() <= a.getBudget()) {
                        a.pullArm(b.getArms().get(feasibles.get(i)), feasibles.get(i));
                        if (feasibles.get(i) == b.getBestArmIndex()) numMaxPulls++;
                        if (debugProg)
                            System.out.println("Budget: " + a.getBudget() + " / Pulled arm " + feasibles.get(i) + "(mean = [" + b.getArms().get(feasibles.get(i)).getMean() + "], sd = [" + b.getArms().get(feasibles.get(i)).getSD() + "], est. ratio = [" + a.getMemory().get(feasibles.get(i)).getRatio() + "]); Got Reward " + b.getArms().get(feasibles.get(i)).getRecentReward());
                        myData.addValues(a.getTotalPulls() - 1, a.getMeanReward(), a.getRegret(), numMaxPulls, b.getOMR(a.getTotalPulls()), feasibles.size() - startArm, switchedArms);
                    } else {
                        feasibles.remove(i);
                        droppedArms++;
                        if (startArm >= i)
                            startArm--;
                        i--;
                        j = pullsPerArm; //restart j-loop
                    }
                }
            }
            iterations++;
            if (startArm < feasibles.size() - 1) {
                startArm = (int) (feasibles.size() - b.getNumArms() * (Math.pow(1 - dropFract, iterations)));
            } else if (debugProg)
                System.out.print("e");
            if (debugKSmall) {
                System.out.print("Before kSmall: ");
                for (int i = 0; i < feasibles.size(); i++) System.out.print(feasibles.get(i));
                System.out.println();
            }
            if (kSmall(feasibles, a.getMemory(), startArm + 1, 0, feasibles.size() - 1) == -1)
                System.out.println("eProgressive error");
            if (debugKSmall) {
                System.out.print("After kSmall: ");
                for (int i = 0; i < feasibles.size(); i++) System.out.print(feasibles.get(i));
                System.out.println();
            }
        }
        return myData;
    }//end adaptive method

    //S is the inverse of the fraction of arms we'd like to pull each round
    private static double getSfromE(double epsilon, double budget, int numArms) {
        double mGoal = epsilon * budget / numArms;
        double sValue = 1;
        double test = sToM(sValue + 1, numArms);
        while (test > mGoal) {
            sValue++;
            test = sToM(sValue + 1, numArms);
        }
        return findS(mGoal, sValue, sValue + 1, 1E-99, numArms);
    }

    private static double sToM(double sValue, int numArms)//the function strictly decreases from infinity at s = 1 to 0 as s -> infinity
    {
        return (Math.pow(sValue, (Math.log(numArms) / Math.log(sValue))) - 1) / (sValue - 1);
    }

    //findS uses a bisection method on the decreasing interval between lSB and rSB to find the s that gives the m closest to mGoal
    private static double findS(double mGoal, double lSB, double rSB, double tolerance, int numArms) {
        double midS = (lSB + rSB) / 2;
        double midM = sToM(midS, numArms);
        if (Math.abs(mGoal - midM) < tolerance)
            return midS;
        else if (midM < mGoal)
            return findS(mGoal, lSB, midS, tolerance, numArms);
        else
            return findS(mGoal, midS, rSB, tolerance, numArms);
    }

}//end core.Algorithms

