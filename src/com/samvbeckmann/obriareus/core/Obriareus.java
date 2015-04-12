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

    public static void main(String[] args) throws IOException
    {
        Scanner console = new Scanner(System.in);

        ArrayList<Dataset> datasets = new ArrayList<Dataset>();

        System.out.print("Enter dataset(s) to run, separated by comma: ");
        String input = console.nextLine();

        Scanner scanInput = new Scanner(input);
        scanInput.useDelimiter(", *");
        while (scanInput.hasNext())
        {
            String set = scanInput.next();
            try
            {
                File inputFile = new File("datasets/dataset" + set + ".dat");
                Dataset dataset = new Dataset(inputFile, set);
                datasets.add(dataset);
            } catch (IOException e)
            {
                System.err.println("Error trying to read dataset \"" + set + "\": " + e);
            }
        }

        for (Dataset dataset : datasets)
        {
            dataset.runSet();
        }
    } // end main

} // end Implementation
