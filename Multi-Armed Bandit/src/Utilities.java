/**
 * Utility classes for the Multi-Armed Bandit problem
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public class Utilities
{
    /**
     * Gets an array of linear spaced elements of a given size.
     *
     * @param numItems number of items in the array to return
     * @return A double array of linearly spaced items of size numItems
     */
    public static double [] getLinear(int numItems)
    {
        double [] linValues = new double [numItems];
        for (int i = 0; i < linValues.length; i++)
        {
            linValues[i] = (i+1.)/numItems;
        }
        return linValues;
    }

    /**
     * Gets an array of superLinear spaced elements of a given size.
     *
     * @param numItems number of items in the array to return
     * @return A double array of superlinearly spaced items of size numItems
     */
    public static double [] getSuperlinear(int numItems)
    {
        double [] superlinValues = new double [numItems];
        for (int i = 0; i < superlinValues.length; i++)
        {
            superlinValues[i] = Math.log(((Math.exp(1) - 1) * (i+1) / numItems) + 1);
        }
        return superlinValues;
    }

    /**
     * Gets an array of subLinear spaced elements of a given size.
     *
     * @param numItems number of items in the array to return
     * @return A double array of sublinearly spaced items of size numItems
     */
    public static double [] getSublinear(int numItems)
    {
        double [] sublinValues = new double [numItems];
        for (int i = 0; i < sublinValues.length; i++)
        {
            sublinValues[i] = Math.pow(10 * ((i+1.) / numItems), 2) / 100;
        }
        return sublinValues;
    }
}
