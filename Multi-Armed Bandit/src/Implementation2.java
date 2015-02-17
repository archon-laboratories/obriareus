import java.io.File;
import java.io.IOException;
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

        System.out.print("Enter Dataset(s) to run, separated by comma: "); // TODO: Support multiple datasets
        String input = console.next();
        File inputFile = new File("datasets/dataset" + input + ".txt");
        Dataset dataset = new Dataset(inputFile);
    }

} // end Implementation2
