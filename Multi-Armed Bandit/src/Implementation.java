import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Implementation of the Multi-Armed Bandit Problem
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Implementation
{

    public static void main(String[] args) throws IOException
    {
        Scanner console = new Scanner(System.in);

        ArrayList<Dataset> datasets = new ArrayList<Dataset>();

        System.out.print("Enter Dataset(s) to run, separated by comma: ");
        String input = console.nextLine();

        Scanner scanInput = new Scanner(input);
        scanInput.useDelimiter(", *");
        while (scanInput.hasNext())
        {
            String set = scanInput.next();
            try
            {
                File inputFile = new File("datasets/dataset" + set + ".txt");
                Dataset dataset = new Dataset(inputFile, set);
                datasets.add(dataset);
            } catch(IOException e)
            {
                System.err.println("Error trying to read dataset \"" + set + "\": " + e);
            }
        }

        for (Dataset dataset : datasets)
        {
            dataset.runSet();
        }

    }

} // end Implementation
