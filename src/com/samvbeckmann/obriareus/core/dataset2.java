package com.samvbeckmann.obriareus.core;

import com.google.gson.stream.JsonReader;
import com.samvbeckmann.obriareus.distributions.Constant;
import com.samvbeckmann.obriareus.distributions.Gaussian;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nate Beckemeyer and Sam Beckmann.
 */
public class dataset2
{
    /**
     * If {@code true} prints out the program's interpretation of the file
     */
    private static final boolean printRun = false;

    /**
     * Contains the budgets to run
     */
    private ArrayList<Integer> budgets;

    /**
     * Contains the algorithms to run (Does not check if algorithms exist)
     */
    private ArrayList<AlgObject> algorithms = new ArrayList<AlgObject>();

    private ArrayList<Arm> arms = new ArrayList<>();

    /**
     * Number of trials to be performed
     */
    private int numTrials;

    /**
     * Number of arms (consistent across trials)
     */
    private int numArms;

    private IDistribution defaultRDist;
    private IDistribution defaultCDist;

    /**
     * Array containing the cost to pull each arm
     */
    private double[] armCosts;

    /**
     * Array containing the mean reward of each arm
     */
    private double[] meanRewards;

    /**
     * Array containing the standard deviation of each arm
     */
    private double[] stdDevs;

    /**
     * Name of the file that this Dataset is tied to
     */
    private String fileName;

    public IDistribution getDistribution(String classPath)
    {
        try
        {
            return (IDistribution) Class.forName(classPath).newInstance();
        } catch (Exception e)
        {
            System.out.println("Warning! Could not locate distribution " + classPath);
            return null;
        }
    }

    public IAlgorithm getAlgorithm(String classPath)
    {
        try
        {
            return (IAlgorithm) Class.forName(classPath).newInstance();
        } catch (Exception e)
        {
            System.out.println("Warning! Could not locate distribution " + classPath);
            return null;
        }
    }

    private void implementConfig(JsonReader reader) throws IOException
    {
        reader.beginObject();
        while (reader.hasNext())
        {
            String tag = reader.nextName();
            switch (tag)
            {
                case "defaultRewardDistribution":
                    defaultRDist = getDistribution(reader.nextString());
                    break;

                case "defaultCostDistribution":
                    defaultCDist = getDistribution(reader.nextString());
                    break;

                default:
                    System.out.println("Warning! Item " + tag + " not accounted for!");
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
    }

    public void addAlgorithms(JsonReader reader) throws IOException
    {
        reader.beginArray();
        while (reader.hasNext())
        {
            if (reader.peek() == null)
                reader.skipValue();
            else
            {
                String algName;
                ArrayList<Double> input = new ArrayList<>();
                reader.beginArray();
                algName = reader.nextString();
                while (reader.hasNext())
                {
                    if (reader.peek() == null)
                        reader.skipValue();
                    else
                        input.add(reader.nextDouble());
                }
                reader.endArray();
                algorithms.add(new AlgObject(getAlgorithm(algName), input));
            }
        }
        reader.endArray();
    }

    public void addBudgets(JsonReader reader) throws IOException
    {
        reader.beginArray();

        while (reader.hasNext())
            if (reader.peek() == null)
                reader.skipValue();
            else
                budgets.add(reader.nextInt());

        reader.endArray();
    }

    private void addArms(JsonReader reader) throws IOException
    {
        IDistribution rDist;
        IDistribution cDist;
        double rDev;
        double rMean;
        double cDev;
        double cMean;

        reader.beginArray();
        while (reader.hasNext())
        {
            if (reader.peek() == null)
                reader.skipValue();
            else
            {
                rDist = defaultRDist;
                cDist = defaultCDist;
                rDev = 0;
                rMean = 0;
                cDev = 0;
                cMean = 0;

                reader.beginObject();
                while (reader.hasNext())
                {
                    String tag = reader.nextName();
                    switch (tag)
                    {
                        case "rewardDistribution":
                            rDist = getDistribution(reader.nextString());
                            break;

                        case "rewardMean":
                            rMean = reader.nextDouble();
                            break;

                        case "rewardStdDev":
                            rDev = reader.nextDouble();
                            break;


                        case "costDistribution":
                            cDist = getDistribution(reader.nextString());
                            break;

                        case "costMean":
                            cMean = reader.nextDouble();
                            break;

                        case "costStdDev":
                            cDev = reader.nextDouble();
                            break;

                        default:
                            System.out.println("Warning! Tag " + tag + " in arms array not found!");
                            break;
                    }

                }
                reader.endObject();

                arms.add(new Arm(rDev, rMean, rDist, cDev, cMean, cDist));
            }
        }
    }

    private void initalizeTrial(JsonReader reader) throws IOException
    {
        reader.beginObject();
        while (reader.hasNext())
        {
            String tag = reader.nextName();
            switch (tag)
            {
                case "config":
                    implementConfig(reader);
                    break;

                case "algorithms":
                    addAlgorithms(reader);
                    break;

                case "budgets":
                    addBudgets(reader);
                    break;

                case "arms":
                    addArms(reader);
                    break;

                default:
                    System.out.println("Warning! Main tag " + tag + " not found!");
            }
        }
        reader.endObject();
    }

    public dataset2()
    {
        try
        {
            budgets = new ArrayList<>();
            defaultRDist = new Gaussian();
            defaultCDist = new Constant();

            JsonReader reader = new JsonReader(new FileReader("datasets/3arms.json"));

            initalizeTrial(reader);
        } catch (IOException E)
        {
            System.err.println("Nopeity-nope! There was an IOException error. Try again.");
        }

    }

}