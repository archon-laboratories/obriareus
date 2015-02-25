import java.util.ArrayList;

/**
 * @deprecated
 */
public class TrialData {
    private ArrayList<Integer> timeVals;
    private ArrayList<Double> rewardVals;
    private ArrayList<Double> regretVals;
    private ArrayList<Double> specialVals;
    private ArrayList<Double> optimalVals;
    private ArrayList<Integer> armsConsidered;
    private ArrayList<Boolean> switchedBest;

    public TrialData() {
        timeVals = new ArrayList<Integer>();
        rewardVals = new ArrayList<Double>();
        regretVals = new ArrayList<Double>();
        specialVals = new ArrayList<Double>();
        optimalVals = new ArrayList<Double>();
        armsConsidered = new ArrayList<Integer>();
        switchedBest = new ArrayList<Boolean>();
    }

    public void addValues(int t, double rew, double reg, double spec, double opt, int aC, boolean sB) {
        timeVals.add(t);
        rewardVals.add(rew);
        regretVals.add(reg);
        specialVals.add(spec);
        optimalVals.add(opt);
        armsConsidered.add(aC);
        switchedBest.add(sB);
        //System.out.println("Added ("+aC+", "+sB+")");
    }

    public int size() {
        return timeVals.size();
    }

    public double getReward(int idx) {
        return rewardVals.get(idx);
    }

    public double getRegret(int idx) {
        return regretVals.get(idx);
    }

    public double getSpecial(int idx) {
        return specialVals.get(idx);
    }

    public double getOptimal(int idx) {
        return optimalVals.get(idx);
    }

    public int getArmsConsidered(int idx) {
        return armsConsidered.get(idx);
    }

    public boolean getSwitchedBest(int idx) {
        return switchedBest.get(idx);
    }

    public boolean hasReward(int idx) {
        if (idx > -1 && idx < rewardVals.size())
            return true;
        return false;
    }

    public boolean hasRegret(int idx) {
        if (idx > -1 && idx < regretVals.size())
            return true;
        return false;
    }

    public boolean hasSpecial(int idx) {
        if (idx > -1 && idx < specialVals.size())
            return true;
        return false;
    }

    public String skipPrint(int skipNum) {
        String myString = "";
        String newLine = System.getProperty("line.separator");
        for (int i = 0; i < timeVals.size(); i += skipNum) {
            myString += timeVals.get(i) + "\t";
            myString += rewardVals.get(i) + "\t";
            myString += regretVals.get(i) + "\t";
            myString += specialVals.get(i) + "\t";
            myString += optimalVals.get(i) + newLine;
        }
        return myString;
    }

    public String toString() {
        String myString = "";
        String newLine = System.getProperty("line.separator");
        for (int i = 0; i < timeVals.size(); i++) {
            myString += timeVals.get(i) + "\t";
            myString += rewardVals.get(i) + "\t";
            myString += regretVals.get(i) + "\t";
            myString += specialVals.get(i) + "\t";
            myString += optimalVals.get(i) + newLine;
        }
        return myString;
    }
}
