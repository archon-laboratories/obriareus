import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Implementation of the Multi-Armed Bandit Problem
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Implementation2
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
            File inputFile = new File("datasets/dataset" + set + ".txt");
            Dataset dataset = new Dataset(inputFile);
            datasets.add(dataset);
        }

    }

} // end Implementation2
