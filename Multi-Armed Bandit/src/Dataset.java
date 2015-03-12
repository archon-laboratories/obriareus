import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private ArrayList<Utilities.Distribution> distributions = new ArrayList<Utilities.Distribution>();

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
     * Constructs the data for a dataset from a given file.
     *
     * @param file Formatted File to construct the dataset from.
     * @throws java.io.IOException If the file is not formatted correctly.
     */
    public Dataset(File file, String name) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        fileName = name;

        if (printRun) System.out.println("Adding Dataset: " + fileName);

        getDistributions(reader);

        getBudgets(reader);

        getNumTrials(reader);

        getNumArms(reader);

        getArmCosts(reader);

        getMeanRewards(reader);

        getStandardDeviations(reader);

        getAlgorithms(reader);

        if (printRun) System.out.println();

    } // end constructor

    /**
     * Adds the distributions to the dataset.
     *
     * @param reader BufferedReader that contains the input file.
     */
    private void getDistributions(BufferedReader reader)
    {
        try
        {
            reader.readLine(); // # Distributions to Run

            String distribution = reader.readLine();
            do
            {
                try
                {
                    distributions.add(Utilities.Distribution.valueOf(distribution.toUpperCase()));
                    if (printRun) System.out.println("Added Distribution: " + distribution);
                } catch (Exception e)
                {
                    System.err.println("Could not add distribution \"" + distribution + "\": " + e);
                }
                distribution = reader.readLine();

            } while (!distribution.isEmpty());

        } catch (IOException e)
        {
            System.err.println("Error in getting distributions for dataset \"" + fileName + "\": " + e);
        }
    } // end getDistributions

    /**
     * Adds the budget to the dataset.
     *
     * @param reader BufferedReader that contains the input file.
     */
    private void getBudgets(BufferedReader reader)
    {
        try
        {
            reader.readLine(); // # Budgets to Run

            String budget = reader.readLine();
            if (budget.equalsIgnoreCase("*")) // Simplified notation
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
                    if (printRun) System.out.println("Added Budget: " + i);
                }

                reader.readLine(); // Skip blank line

            } else // Full notation
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
                    if (printRun) System.out.println("Added Budget: " + budget);

                    budget = reader.readLine();

                } while (!budget.isEmpty());
            }

        } catch (IOException e)
        {
            System.err.println("Error in getting budgets for dataset \"" + fileName + "\": " + e);
        }
    } // end getBudgets

    /**
     * Adds the number of trials to the dataset.
     *
     * @param reader BufferedReader that contains the input file.
     */
    private void getNumTrials(BufferedReader reader)
    {
        try
        {
            reader.readLine(); // # Number of Trials

            numTrials = Integer.parseInt(reader.readLine());
            if (numTrials < 0)
            {
                System.out.println("ERROR: Number of trials, " + numTrials + ", is less than zero.");
                System.exit(4);
            }
            if (printRun) System.out.println("Number of Trials: " + numTrials);

            reader.readLine();

        } catch (IOException e)
        {
            System.err.println("Error in getting number of trials for dataset \"" + fileName + "\": " + e);
        }
    } // end getNumTrials

    /**
     * Adds the number of arms to the dataset.
     *
     * @param reader BufferedReader that contains the input file.
     */
    private void getNumArms(BufferedReader reader)
    {
        try
        {
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

            reader.readLine(); // Skip blank line

        } catch (IOException e)
        {
            System.err.println("Error in getting number of arms for dataset \"" + fileName + "\": " + e);
        }
    } // end getNumArms

    /**
     * Adds the costs to pull each arm to the dataset.
     *
     * @param reader BufferedReader that contains the input file.
     */
    private void getArmCosts(BufferedReader reader)
    {
        try
        {
            reader.readLine(); // # Arm Costs

            String stringCost = reader.readLine();
            if (stringCost.equalsIgnoreCase("*")) // special notation
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
            } else // full notation
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

        } catch (IOException e)
        {
            System.err.println("Error in getting arm costs for dataset \"" + fileName + "\": " + e);
        }
    } // end getArmCosts

    /**
     * Add the mean rewards for the arms to the dataset.
     *
     * @param reader BufferedReader that contains the input file.
     */
    private void getMeanRewards(BufferedReader reader)
    {
        try
        {
            reader.readLine(); // # Mean Rewards

            String stringReward = reader.readLine();
            if (stringReward.equalsIgnoreCase("*")) // simplified notation
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
            } else // full notation
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

        } catch (IOException e)
        {
            System.err.println("Error in getting mean arm rewards for dataset \"" + fileName + "\": " + e);
        }
    } // end getMeanRewards

    /**
     * Adds the arm standard deviations to the dataset.
     *
     * @param reader BufferedReader that contains the input file.
     */
    private void getStandardDeviations(BufferedReader reader)
    {
        try
        {
            reader.readLine(); // # Standard Deviations

            String stringDeviation = reader.readLine();
            if (stringDeviation.equalsIgnoreCase("*")) // simplified notation
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
                reader.readLine(); // skip blank line

            } else // standard notation
            {
                double stdDev;
                for (int i = 0; i < numArms; i++)
                {
                    stdDev = Double.parseDouble(stringDeviation);
                    if (stdDev < 0)
                    {
                        System.out.println("ERROR: Standard deviation of an arm set to " + stdDev + ", less than zero.");
                        System.exit(8);
                    }
                    stdDevs[i] = stdDev;
                    if (printRun) System.out.println("Arm " + i + "'s standard deviation set to: " + stdDev);
                    stringDeviation = reader.readLine();
                }
            }

        } catch (IOException e)
        {
            System.err.println("Error in getting arm standard deviations for dataset \"" + fileName + "\": " + e);
        }
    } // end getStandardDeviations

    /**
     * Adds the active algorithms to the dataset.
     *
     * @param reader BufferedReader that contains the input file.
     */
    private void getAlgorithms(BufferedReader reader)
    {
        try
        {
            reader.readLine(); // # Algorithms

            String alg = reader.readLine();

            double parameter;
            while (alg != null)
            {

                Scanner scanInput = new Scanner(alg);
                scanInput.useDelimiter(", *");
                // TODO Use the Class.forName magic here to get an actual algorithm

                String algorithmName = scanInput.next();
                Algorithm algorithm = new Algorithm();

                // Get the algorithm
                try
                {
                    algorithm = (Algorithm) Class.forName(algorithmName).newInstance();
                } catch (ClassNotFoundException e)
                {
                    System.out.println("Error! Algorithm " + algorithmName + ".");
                    e.printStackTrace();
                    return;
                } catch (InstantiationException e)
                {
                    System.out.println("Error! Algorithm " + algorithmName + ".");
                    e.printStackTrace();
                    return;
                } catch (IllegalAccessException e)
                {
                    System.out.println("Error! Algorithm " + algorithmName + ".");
                    e.printStackTrace();
                    return;
                }
                boolean flag = false;

                for (Algorithms.AlgorithmNames a : Algorithms.AlgorithmNames.values())
                {
                    if (a.name().equalsIgnoreCase(algorithm))
                    {
                        flag = true;
                        if (scanInput.hasNext())
                        {
                            // TODO Scan for input parameters
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

        } catch (IOException e)
        {
            System.err.println("Error in getting algorithms for dataset \"" + fileName + "\": " + e);
        }
    } // end getAlgorithms


    /**
     * Runs a given dataset, outputting the results to both the console
     * and a file in the <code>output/</code>.
     */
    public void runSet()
    {
        System.out.println("Dataset: " + fileName + "\n");

        // run for each dataset
        for (Utilities.Distribution distribution : distributions)
        {
            System.out.println("Distribution: " + distribution.toString() + "\n");

            try // delete the old output if it exists
            {
                Files.delete(Paths.get("output/" + fileName + "_" + distribution.toString().toLowerCase() + ".txt"));
            } catch (IOException x)
            {
                // NOOP
            }

            // run for each budget
            for (int budget : budgets)
            {
                System.out.println("Budget: " + budget + "\n");

                double[][] totalRewards = new double[algorithms.size()][numTrials];

                Bandit bandit = new Bandit(numArms);

                // run the number of trials specified in the dataset
                for (int trial = 0; trial < numTrials; trial++)
                {
                    Arm[] trialArms = new Arm[numArms];
                    ArrayList<Integer> indices = new ArrayList<Integer>();
                    Utilities.generateIndices(indices, numArms);

                    int count = 0;
                    while (!indices.isEmpty())
                    {
                        int index = Utilities.randomIndex(indices, new Random());
                        trialArms[count] = new Arm(armCosts[index], stdDevs[index], meanRewards[index], distribution);
                        count++;
                    }

                    int algIndex = 0;

                    for (AlgObject algObject : algorithms)
                    {

                        //Arm[] agentArms = new Arm[numArms];
                        //System.arraycopy(trialArms, 0, agentArms, 0, trialArms.length);

                        Agent agent = new Agent(budget, trialArms, algObject, bandit);

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
                outputFile(normalizedRewards, distribution.toString().toLowerCase(), budget);
            }
        }
    } // end runSet

    /**
     * Writes the output for one budget to a file. Appends the file for each consecutive budget.
     *
     * @param means        An array of the mean rewards for each algorithm.
     * @param distribution The current distribution being used.
     * @param budget       The budget that this data is being outputted to.
     */
    public void outputFile(double[] means, String distribution, int budget)
    {
        try
        {
            File output = new File("output/" + fileName + "_" + distribution + ".txt");
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output, true)));

            writer.write(((String.valueOf(budget))));
            for (double mean : means)
            {
                writer.write(",");
                writer.write(String.valueOf(mean));
            }
            writer.write("\n");

            writer.close();

        } catch (IOException e)
        {
            System.err.print("IO Exception! You might not have an output folder." + e + "\n");
        }
    } // end outputFile

    /**
     * Outputs the normalized mean rewards of the algorithm to console.
     *
     * @param means double array of the normalized mean rewards.
     */
    public void displayMeans(double[] means)
    {
        int counter = 0;
        for (AlgObject alg : algorithms)
        {
            System.out.printf("%-20s %, 10.3f\n",
                    alg.getAlgorithm() + ", " + alg.getInputParameters() + ": ",
                    means[counter]);
            counter++;
        }

        System.out.println();
    } // end displayMeans

} // end Dataset