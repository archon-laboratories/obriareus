import java.util.ArrayList;
import java.util.Random;

public class Agent
{

    private int startingArm;
    private double budget;
    private double initBudget;
    private double revenue;
    private double halfRevenue;
    private int totalPulls;
    private ArrayList<ArmMemory> mem;//agent stores knowledge of arms here
    private ArrayList<Integer> armUsage;
    private Bandit myBandit;

    public Agent(double budget, Bandit b)
    {
        this.budget = budget;
        this.initBudget = budget;
        mem = new ArrayList<ArmMemory>();
        revenue = 0;
        halfRevenue = 0;
        totalPulls = 0;
        armUsage = new ArrayList<Integer>();
        myBandit = b;
        startingArm = b.getStartingArm();
    }

    public void storeArm(double c)
    {
        mem.add(new ArmMemory(c)); //stores cost
        armUsage.add(0);
    }

    public ArrayList<ArmMemory> getMemory()
    {
        return mem;
    }

    public ArrayList<Integer> getArmUsage()
    {
        return armUsage;
    }

    public int getArmUsage(int which)
    {
        return armUsage.get(which);
    }

    public double getBudget()
    {
        return budget;
    }

    public int getStartingArm()
    {
        return startingArm;
    }

    public int getTotalPulls()
    {
        return totalPulls;
    }

    public double getRevenue()
    {
        return revenue;
    }

    public double getHalfRevenue()
    {
        return halfRevenue;
    }

    public double getMeanReward()
    {
        double actualMeanReward = 0;
        for (int i = 0; i < mem.size(); i++)
            actualMeanReward += mem.get(i).getPulls() * myBandit.getArms().get(i).getMean();
        return actualMeanReward;
    }

    public double getRegret()
    {
        return myBandit.getOMR(totalPulls) - getMeanReward();
    }

    public double getBudgetRegret()
    {
        return myBandit.getOMR(budget, totalPulls) - getMeanReward();
    }

    public void pullArmNoMemory(Arm a, int i)
    {
        //System.out.println("\t\tCost is " + a.getCost());
        budget -= a.getCost();
        //System.out.println("\t\tTotal budget is now " + budget);
        a.pullArm();
        totalPulls++;
        mem.get(i).addPullNoUpdate();
        armUsage.set(i, armUsage.get(i) + 1);//increment
        revenue += a.getRecentReward(); //updates reward
        if (budget / initBudget < .5)
            halfRevenue += a.getRecentReward();
        //System.out.println("\t\tReward is " + a.getReward());
        //System.out.println("\t\tRevenue is now " + revenue);
    }

    public void pullArm(Arm a, int i)
    {
        //System.out.println("\t\tCost is " + a.getCost());
        budget -= a.getCost();
        //System.out.println("\t\tTotal budget is now " + budget);
        a.pullArm();
        totalPulls++;
        mem.get(i).addPull(a.getRecentReward());
        armUsage.set(i, armUsage.get(i) + 1);//increment
        revenue += a.getRecentReward(); //updates reward
        if (budget / initBudget < .5)
            halfRevenue += a.getRecentReward();
        //System.out.println("\t\tReward is " + a.getReward());
        //System.out.println("\t\tRevenue is now " + revenue);
    }

    public double getEFirstBound(int which, double epsilon)
    {
        double boundVal;
        if (which == 0) //(applies with probability (1-b)^K): (Only applies with sufficient budget to complete exploration, and with mean rewards <= 1, uses Hoeffding inequalities)
        {
            int bestArm = 0;
            int worstArm = 0;
            double sumCosts = mem.get(0).getCost();
            for (int i = 1; i < mem.size(); i++)
            {
                double temp = myBandit.getArms().get(i).getMean();
                if (temp > myBandit.getArms().get(bestArm).getMean())
                    bestArm = i;
                else if (temp < myBandit.getArms().get(worstArm).getMean())
                    worstArm = i;
                sumCosts += mem.get(i).getCost();
            }

            if (initBudget < sumCosts) return -1; //bound does not apply

            double beta = .001; //about 90% chance of the bound applying, with 100 arms
            double dMax = myBandit.getArms().get(bestArm).getRatio() - myBandit.getArms().get(worstArm).getRatio();
            boundVal = 2 + (epsilon * initBudget * dMax) + 2 * Math.sqrt(-initBudget * Math.log(beta / 2) * sumCosts / epsilon);
        } else //Our own alternate calculation
            boundVal = 0;
        return 0;
    }

    public double getKubeBound(int which)//Always applies
    {
        double boundVal;
        if (which == 0)
        {
            int bestArm = 0;
            int secondBestArm = 0;
            int worstArm = 0;
            double minCost = Double.MAX_VALUE;
            for (int i = 1; i < mem.size(); i++)
            {
                double temp = myBandit.getArms().get(i).getMean();
                if (temp > myBandit.getArms().get(bestArm).getMean())
                {
                    secondBestArm = bestArm;
                    bestArm = i;
                } else if (temp > myBandit.getArms().get(secondBestArm).getMean())
                {
                    secondBestArm = i;
                } else if (temp < myBandit.getArms().get(worstArm).getMean())
                {
                    worstArm = i;
                }
                if (mem.get(i).getCost() < minCost)
                    minCost = mem.get(i).getCost();
            }

            double dMin = myBandit.getArms().get(bestArm).getRatio() - myBandit.getArms().get(secondBestArm).getRatio();
            double sumBigDeltas = 0;
            double sumRatios = 0;
            for (int i = 0; i < mem.size(); i++) //for each, when for arm j it is > 0
            {
                double temp = myBandit.getArms().get(bestArm).getMean() - myBandit.getArms().get(i).getMean();
                double delta = myBandit.getArms().get(bestArm).getCost() - myBandit.getArms().get(i).getCost();
                if (temp > 0)
                    sumBigDeltas += temp;
                if (delta > 0)
                {
                    sumRatios += delta / mem.get(bestArm).getCost();
                }
            }
            boundVal = 8 / Math.pow(dMin, 2) * (sumBigDeltas + sumRatios) * Math.log(initBudget / minCost) + ((sumBigDeltas + sumRatios) * (Math.pow(Math.PI, 2) / 3. + 1)) + 1;
        } else
            boundVal = 0;
        return 0;
    }

    public double getKdeBound(int which, double gamma, double dVal)
    {
        double boundVal;
        if (which == 0) //(mean rewards <= 1; uses Hoeffding, Bernoulli inequalities)
        {
            int bestArm = 0;
            double minCost = Double.MAX_VALUE;
            double sumCosts = mem.get(0).getCost();
            double sumDeltas = 0.5 * mem.size(); //some kind of bound, summed across the arms where it is positive
            double sumRatios = myBandit.getArms().get(0).getMean();
            for (int i = 1; i < mem.size(); i++)
            {
                double temp = myBandit.getArms().get(i).getMean();
                if (temp > myBandit.getArms().get(bestArm).getMean())
                {
                    bestArm = i;
                }
                sumCosts += mem.get(i).getCost();
                if (mem.get(i).getCost() < minCost)
                    minCost = mem.get(i).getCost();
            }
            double CVal = (gamma + Math.pow(gamma, 2)) / mem.size() + 4 * gamma * Math.exp(.5) / Math.pow(dVal, 2);
            boundVal = ((CVal * sumDeltas + CVal * sumRatios + gamma / mem.size() * sumDeltas) * Math.log(initBudget / minCost)) + (gamma * (sumCosts / (mem.size() * mem.get(bestArm).getCost()) + sumDeltas / mem.size())) + (CVal * sumDeltas) + 1;
        } else
            boundVal = 0;
        return 0;
    }
}
