package com.samvbeckmann.obriareus.core;

import com.google.gson.stream.JsonReader;
import com.samvbeckmann.obriareus.distributions.Constant;
import com.samvbeckmann.obriareus.distributions.Gaussian;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

    /**
     * Contains the arms to be used in the trial.
     */
    private ArrayList<Arm> arms = new ArrayList<>();

    /**
     * Number of arms.
     */
    private int numArms;

    /**
     * Number of trials to be performed
     */
    private int numTrials;

    /**
     * Default distribution for rewards.
     */
    private IDistribution defaultRDist;

    /**
     * Default distribution for costs.
     */
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

    /**
     * Contains instantiations of each distribution, so multiple are not created unnecessarily.
     */
    HashMap<String, IDistribution> distributions = new HashMap<>(8, (float) .75);

    /**
     * Gets a distribution from its classpath; reuses distributions if they have already been instantiated
     * @param classPath The classpath of the distribution
     * @return The corresponding distribution
     */
    private IDistribution getDistribution(String classPath)
    {
        try
        {
            IDistribution dist = (IDistribution) Class.forName(classPath).newInstance();

            if (distributions.get(classPath) != null)
                return distributions.get(classPath);
            distributions.put(classPath, dist);

            return dist;
        } catch (Exception e)
        {
            System.out.println("Warning! Could not locate distribution " + classPath);
            return null;
        }
    }

    /**
     * Gets an algorithm from its classpath
     * @param classPath The classpath of the algorithm
     * @return The corresponding algorithm
     */
    private IAlgorithm getAlgorithm(String classPath)
    {
        try
        {
            return (IAlgorithm) Class.forName(classPath).newInstance();
        } catch (Exception e)
        {
            System.out.println("Warning! Could not locate algorithm " + classPath);
            return null;
        }
    }

    /**
     * Flags that which is manual vs. automated
     * @param reader The JsonReader parsing the file
     * @param manual The array of flags
     * @throws IOException
     */
    private void flagAutomation(JsonReader reader, boolean[] manual) throws IOException
    {
        reader.beginObject(); // Input
        while (reader.hasNext())
        {
            String tag = reader.nextName();
            String value = reader.nextString();

            if (value.equalsIgnoreCase("manual"))
                switch (tag)
                {
                    case "budgets":
                        manual[0] = true;
                        break;

                    case "armRewardMeans":
                        manual[1] = true;
                        break;

                    case "armRewardDevs":
                        manual[2] = true;
                        break;

                    case "armCostMeans":
                        manual[3] = true;
                        break;

                    case "armCostDevs":
                        manual[4] = true;
                        break;

                    case "numArms":
                        manual[5] = true;
                        break;
                }
            else if (tag.equalsIgnoreCase("numArms"))
                numArms = Integer.parseInt(value);
        }
        reader.endObject();
    }

    /**
     * Implements the configuration part of the file
     * @param reader The JsonReader parsing the file
     * @param manual The configuration flags
     * @throws IOException
     */
    private void implementConfig(JsonReader reader, boolean[] manual) throws IOException
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

                case "numberTrials":
                    numTrials = reader.nextInt();
                    break;

                case "input":
                    flagAutomation(reader, manual);
                    break;

                default:
                    System.out.println("Warning! Item " + tag + " not accounted for!");
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
    }

    /**
     * Adds budgets to the dataset
     * @param reader The JsonReader parsing the file
     * @param manual Contains whether or not this process is automated
     * @throws IOException
     */
    private void addBudgets(JsonReader reader, boolean manual[]) throws IOException
    {
        reader.beginArray();

        while (reader.hasNext())
            if (reader.peek() == null)
                reader.skipValue();
            else
                budgets.add(reader.nextInt());

        reader.endArray();

        if (manual[0])
            return;

        int min = budgets.remove(0); // The minimum budget to run
        int max = budgets.remove(0); // The maximum budget to run
        int increment = budgets.remove(0); // The amount by which to increment
        for (int cur = min; cur <= max; cur += increment)
            budgets.add(cur);
    }

    /**
     * Adds algorithms to the dataset
     * @param reader The JsonReader parsing the file
     * @throws IOException
     */
    private void addAlgorithms(JsonReader reader) throws IOException
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

    /**
     * Adds arms to the dataset
     * @param reader The JsonReader parsing the file
     * @param manual The configuration flags
     * @throws IOException
     */
    private void addArms(JsonReader reader, boolean[] manual) throws IOException
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
                            System.out.println("Warning! Tag " + tag + " in arms array not found! Skipping.");
                            reader.skipValue();
                            break;
                    }

                }
                reader.endObject();

                arms.add(new Arm(rDev, rMean, rDist, cDev, cMean, cDist));
            }
        }
        reader.endArray();
    }

    /**
     * Outermost parser of the JSON file
     * @param reader The JsonReader parsing the file
     * @param manual Configuration flags
     * @throws IOException
     */
    private void initalizeTrial(JsonReader reader, boolean[] manual) throws IOException
    {
        reader.beginObject();
        while (reader.hasNext())
        {
            String tag = reader.nextName();
            switch (tag)
            {
                case "config":
                    implementConfig(reader, manual);
                    break;

                case "budgets":
                    addBudgets(reader, manual);
                    break;

                case "algorithms":
                    addAlgorithms(reader);
                    break;

                case "arms":
                    addArms(reader, manual);
                    break;

                default:
                    System.out.println("Warning! Outermost tag " + tag + " not found!");
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
            boolean[] manual = new boolean[6];

            reader.setLenient(true); // Allows for the inclusion of comments
            initalizeTrial(reader, manual);
            reader.close();
        } catch (IOException E)
        {
            E.printStackTrace();
            System.exit(1);
        }

    }

}