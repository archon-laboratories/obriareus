import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Contains all the information for one dataset
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Dataset
{
    /**
     * If {@code true} prints out the program's interpretation of the file
     */
    static boolean printRun = true;

    /**
     * Contains the distributions to be run (Does not check if they are applicable)
     */
    private ArrayList<String> distributions = new ArrayList<String>();

    /**
     * Contains the budgets to run
     */
    private ArrayList<Integer> budgets = new ArrayList<Integer>();

    /**
     * Contains the algorithms to run (Does not check if algorithms exist)
     */
    private ArrayList<AlgObject> algorithms = new ArrayList<AlgObject>();

    /**
     * Number of trials to be performed
     */
    private int numTrials;

    /**
     * Number of arms (consistent across trials)
     */
    private int numArms;

    /**
     * Array containing the cost to pull each arm
     */
    private double [] armCosts;

    /**
     * Array containing the mean reward of each arm
     */
    private double [] meanRewards;

    /**
     * Array containing the standard deviation of each arm
     */
    private double [] stdDevs;

    /**
     * Name of the file that this Dataset is tied to
     */
    private String fileName;

    /**
     * Constructs the data for a dataset from a given file.
     *
     * @param file Formatted File to construct the dataset from.
     * @throws java.io.IOException If the file is not formatted correctly.
     */
    public Dataset(File file, String name) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        fileName = name;

        reader.readLine(); // # Distributions to Run

        String distro = reader.readLine();
        do
        {
            distributions.add(distro);
            if (printRun) System.out.println("Added Distribution: " + distro);
            distro = reader.readLine();
        } while (!distro.isEmpty());

        reader.readLine(); // # Budgets to Run

        String budget = reader.readLine();
        if (budget.equalsIgnoreCase("*"))
        {
            int start = Integer.parseInt(reader.readLine());
            int end = Integer.parseInt(reader.readLine());
            int increment = Integer.parseInt(reader.readLine());
            for (int i = start; i <= end; i += increment)
            {
                if (i >= 0)
                    budgets.add(i);
                else
                {
                    System.out.println("ERROR: Attempted to add budget " + i + ", which is less than zero.");
                    System.exit(3);
                }
                if (printRun) System.out.println("Added Budget: " +  i);
            }
            reader.readLine();
        } else
        {
            do
            {
                int toAdd = Integer.parseInt(budget);
                if (toAdd >= 0)
                    budgets.add(toAdd);
                else
                {
                    System.out.println("ERROR: Attempted to add budget " + toAdd + ", which is less than zero.");
                    System.exit(3);
                }
                if (printRun) System.out.println("Added Budget: " +  budget);
                budget = reader.readLine();
            } while (!budget.isEmpty());
        }

        reader.readLine(); // # Number of Trials

        numTrials = Integer.parseInt(reader.readLine());
        if (numTrials < 0)
        {
            System.out.println("ERROR: Number of trials, " + numTrials + ", is less than zero.");
            System.exit(4);
        }
        if (printRun) System.out.println("Number of Trials: " + numTrials);

        reader.readLine();
        reader.readLine(); // # Number of Arms

        numArms = Integer.parseInt(reader.readLine());
        if (numArms <= 0)
        {
            System.out.println("ERROR: Number of trials, " + numTrials + ", is less than or equal to zero.");
            System.exit(5);
        }
        if (printRun) System.out.println("Number of Arms: " + numArms);

        armCosts = new double[numArms];
        meanRewards = new double[numArms];
        stdDevs = new double[numArms];

        reader.readLine();
        reader.readLine(); // # Arm Costs

        String stringCost = reader.readLine();
        if (stringCost.equalsIgnoreCase("*"))
        {
            double flatCost = Double.parseDouble(reader.readLine());
            if (flatCost <= 0)
            {
                System.out.println("ERROR: Cost of arm set to " + flatCost + ", less than or equal to zero.");
                System.exit(6);
            }
            for (int i = 0; i < numArms; i++)
            {
                armCosts[i] = flatCost;
                if (printRun) System.out.println("Arm  " + i + "'s cost set to: " + flatCost);
            }
            reader.readLine();
        } else
        {
            double cost;
            for (int i = 0; i < numArms; i++)
            {
                cost = Double.parseDouble(stringCost);
                if (cost <= 0)
                {
                    System.out.println("ERROR: Cost of arm set to " + cost + ", less than or equal to zero.");
                    System.exit(6);
                }
                armCosts[i] = cost;
                if (printRun) System.out.println("Arm " + i + "'s cost set to: " + cost);
                stringCost = reader.readLine();
            }
            if (!stringCost.isEmpty())
            {
                System.out.println("ERROR: Incorrect data for number of arms");
                System.exit(7);
            }
        }

        reader.readLine(); // # Mean Rewards

        String stringReward = reader.readLine();
        if (stringReward.equalsIgnoreCase("*"))
        {
            stringReward = reader.readLine();
            if (stringReward.equalsIgnoreCase("linear"))
                meanRewards = Utilities.getLinear(numArms);
            else if (stringReward.equalsIgnoreCase("sublinear"))
                meanRewards = Utilities.getSublinear(numArms);
            else if (stringReward.equalsIgnoreCase("superlinear"))
                meanRewards = Utilities.getSuperlinear(numArms);
            else
            {
                System.out.println("ERROR: " + stringReward + " is not a recognized distribution!");
                System.exit(9);
            }
            if (printRun)
            {
                for (int i = 0; i < meanRewards.length; i++)
                    System.out.println("Arm" + i + "'s mean set to: " + meanRewards[i]);
            }
            reader.readLine();
        } else
        {
            double meanReward;
            for (int i = 0; i < numArms; i++)
            {
                meanReward = Double.parseDouble(stringReward);
                meanRewards[i] = meanReward;
                if (printRun) System.out.println("Arm " + i + "'s mean reward set to: " + meanReward);
                stringReward = reader.readLine();
            }
            if (!stringReward.isEmpty())
            {
                System.out.println("ERROR: Incorrect data for number of arms");
                System.exit(7);
            }
        }

        reader.readLine(); // # Standard Deviations

        String stringDeviation = reader.readLine();
        if (stringDeviation.equalsIgnoreCase("*"))
        {
            double flatDeviation = Double.parseDouble(reader.readLine());
            if (flatDeviation < 0)
            {
                System.out.println("ERROR: Standard deviation of an arm set to " + flatDeviation
                        + ", less than zero.");
                System.exit(8);
            }
            for (int i = 0; i < numArms; i++)
            {
                stdDevs[i] = flatDeviation;
                if (printRun) System.out.println("Arm  " + i + "'s standard deviation set to: " + flatDeviation);
            }
            reader.readLine();
        } else
        {
            double stdDev;
            for (int i = 0; i < numArms; i++)
            {
                stdDev = Double.parseDouble(stringDeviation);
                if (stdDev < 0)
                {
                    System.out.println("ERROR: Standard deviation of an arm set to " + stdDev +", less than zero.");
                    System.exit(8);
                }
                stdDevs[i] = stdDev;
                if (printRun) System.out.println("Arm " + i + "'s standard deviation set to: " + stdDev);
                stringDeviation = reader.readLine();
            }
        }

        reader.readLine(); // # Algorithms

        String alg = reader.readLine();

        double parameter;
        while (alg != null)
        {

            Scanner scanInput = new Scanner(alg);
            scanInput.useDelimiter(", *");
            String algorithm = scanInput.next();

            boolean flag = false;

            for (Algorithms.AlgorithmNames a : Algorithms.AlgorithmNames.values())
            {
                if (a.name().equalsIgnoreCase(algorithm))
                {
                    flag = true;
                    if (scanInput.hasNext())
                    {
                        parameter = Double.parseDouble(scanInput.next());
                        algorithms.add(new AlgObject(algorithm.toUpperCase(), parameter));
                    } else
                        algorithms.add(new AlgObject(algorithm.toUpperCase()));

                    if (printRun) System.out.println("Algorithm added: " + alg);
                }
            }

            if (!flag)
                System.out.println("ERROR: Algorithm \"" + algorithm + "\" not found, excluding from dataset.");

            alg = reader.readLine();
        }

        if (printRun) System.out.println();

    } // end constructor

    public void runSet()
    {
        for (String distribution : distributions)
        {
            System.out.println("Distribution: " + distribution + "\n");

            for (int budget : budgets)
            {
                System.out.println("Budget: " + budget + "\n");

                double[][] totalRewards = new double[algorithms.size()][numTrials];

                Bandit bandit = new Bandit(numArms);

                for (int trial = 0; trial < numTrials; trial++)
                {
                    Arm[] trialArms = new Arm[numArms];
                    ArrayList<Integer> indices = new ArrayList<Integer>();
                    Utilities.generateIndices(indices, numArms);

                    int count = 0;
                    while (!indices.isEmpty())
                    {
                        int index = Utilities.randomIndex(indices, new Random());
                        trialArms[count] = new Arm(armCosts[index], stdDevs[index], meanRewards[index]);
                        count++;
                    }

                    int algIndex = 0;

                    for (AlgObject algObject : algorithms)
                    {

                        Arm[] agentArms = new Arm[numArms];
                        System.arraycopy(trialArms, 0, agentArms, 0, trialArms.length);

                        Agent agent = new Agent(budget, agentArms, bandit);

                        Algorithms.run(algObject, agent);
                        totalRewards[algIndex][trial] = agent.getTotalReward();
                        algIndex++;
                    }
                }

                double[] meanRewards = new double[algorithms.size()];

                double totalAverage = 0;

                for (int alg = 0; alg < meanRewards.length; alg++)
                {
                    double average = 0;
                    for (int place = 0; place < numTrials; place++)
                    {
                        average += totalRewards[alg][place];
                    }
                    average /= numTrials;
                    meanRewards[alg] = average;
                    totalAverage += average;
                }

                totalAverage /= algorithms.size();

                double[] normalizedRewards = new double[algorithms.size()];

                for (int alg = 0; alg < normalizedRewards.length; alg++)
                {
                    normalizedRewards[alg] = meanRewards[alg] - totalAverage;
                }

                displayMeans(normalizedRewards);
                outputFile(normalizedRewards, distribution, budget);
            }
        }
    }
    
    public void outputFile(double [] means, String distribution, int budget)
    {
        try
        {
            File output = new File("output/" + fileName + "_" + distribution + ".txt");
            FileWriter writer = new FileWriter(output);
            
            writer.append(((String.valueOf(budget))));
            for (double mean : means)
            {
                writer.append("\t");
                writer.append(String.valueOf(mean));
            }
            writer.append("\n");

            writer.close();
            
        } catch (IOException e)
        {
            System.err.print("IO Exception! " + e);
        }
    }

    public void displayMeans(double [] means)
    {
        int counter = 0;
        for (AlgObject alg : algorithms)
        {
            System.out.println(alg.getAlgorithm() + ", " + alg.getInputParameter() + ": " + means[counter]);
        }

        System.out.println();
    }


} // end Dataset