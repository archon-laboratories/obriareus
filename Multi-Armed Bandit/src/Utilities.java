import java.util.List;
import java.util.Random;

/**
 * Utility classes for the Multi-Armed Bandit problem
 *
 * @author Sam Beckmann, Nate Beckemeyer
 */
public final class Utilities
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

//    /**
//     * Partitions an array for quickSort.
//     *
//     * @param first    is the index of the first element to sort with
//     *                 <code>first <= last</code>.
//     * @param last     is the index of the last element to sort with
//     *                 <code>first <= last</code>.
//     * @param theArray is the array to be sorted: the element
//     *                 between <code>first</code> and <code>last</code> (with
//     *                 <code>first <= last</code>)will be sorted.
//     * @return the index of the pivot element of
//     * theArray[first..last]. Upon completion of the method, this will
//     * be the index value lastS1 such that <code>S1 =
//     * theArray[first..lastS1-1] < pivot theArray[lastS1] == pivot S2 =
//     * theArray[lastS1+1..last] >= pivot </code>
//     */
//    public static <E extends Comparable<? super E>> int partition(E[] theArray,
//                                                                   int first, int last)
//    {
//        // tempItem is used to swap elements in the array
//        E tempItem;
//        E pivot = theArray[first];   // reference pivot
//        // initially, everything but pivot is in unknown
//        int lastS1 = first;          // index of last item in S1
//        // move one item at a time until unknown region is empty
//        for (int firstUnknown = first + 1; firstUnknown <= last;
//             ++firstUnknown)
//        {
//            // Invariant: theArray[first+1..lastS1] < pivot
//            //            theArray[lastS1+1..firstUnknown-1] >= pivot
//            // move item from unknown to proper region
//            if (theArray[firstUnknown].compareTo(pivot) < 0)
//            {
//                // item from unknown belongs in S1
//                ++lastS1;
//                tempItem = theArray[firstUnknown];
//                theArray[firstUnknown] = theArray[lastS1];
//                theArray[lastS1] = tempItem;
//            }  // end if
//            // else item from unknown belongs in S2
//        }  // end for
//        // place pivot in proper position and mark its location
//        tempItem = theArray[first];
//        theArray[first] = theArray[lastS1];
//        theArray[lastS1] = tempItem;
//        return lastS1;
//    }  // end partition

    /**
     * Restores the given ArrayList of indices to contain all indices.
     *
     * @param indices The ArrayList of indices to generate
     * @param bound   The bound of indices to be used should remainingIndices need to be regenerated.
     */
    public static void generateIndices(List<Integer> indices, int bound)
    {
        indices.clear();
        for (int i = 0; i < bound; i++)
            indices.add(i);
    }

    /**
     * Generates a random armIndex given an ArrayList of remaining armIndices. Then removes the
     *
     * @param remainingIndices The ArrayList of indices that have yet to be used in the algorithm's random selection.
     * @return the index of the arm to be checked
     */
    public static int randomIndex(List<Integer> remainingIndices, Random rnd)
    {
        if (remainingIndices.size() == 0)
            return -1;

        int indexLocation = rnd.nextInt(remainingIndices.size()); // The location in remainingIndices of the arm's index
        int index = remainingIndices.get(indexLocation); // Location in arms of the arm.
        remainingIndices.remove(indexLocation);
        return index;

    }
}
