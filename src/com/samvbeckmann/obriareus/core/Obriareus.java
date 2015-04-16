package com.samvbeckmann.obriareus.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Implementation of Obriareus, a Multi-Armed Bandit Problem utility.
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Obriareus
{
    private static ArrayList<Dataset> datasets = new ArrayList<Dataset>();

    public static void main(String[] args) throws IOException
    {
        if (args.length != 0) // Use input arguments if they exits
        {
            for (String arg : args)
                addDataset(arg);

        } else // prompt user
        {
            Scanner console = new Scanner(System.in);

            System.out.print("Enter dataset(s) to run, separated by comma: ");
            String input = console.nextLine();

            Scanner scanInput = new Scanner(input);
            scanInput.useDelimiter(", *");
            while (scanInput.hasNext())
            {
                String set = scanInput.next();
                addDataset(set);
            }
        }

        for (Dataset dataset : datasets)
        {
            dataset.runSet();
        }
    } // end main

    /**
     * Adds a new dataset to the list of datasets to be run.
     *
     * @param datasetName String name of dataset file to be added.
     */
    private static void addDataset(String datasetName)
    {
        try
        {
            File inputFile = new File("datasets/" + datasetName);
            Dataset dataset = new Dataset(inputFile, datasetName);
            datasets.add(dataset);
        } catch (IOException e)
        {
            System.err.println("Error trying to read dataset \"" + datasetName + "\": " + e);
        }
    }
} // end Obriareus
