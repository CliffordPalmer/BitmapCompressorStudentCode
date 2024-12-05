/******************************************************************************
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 *
 *  Compress or expand binary input from standard input.
 *
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 *
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 ******************************************************************************/

//import jdk.incubator.vector.VectorOperators;
import java.util.ArrayList;
/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author YOUR NAME HERE
 */
public class BitmapCompressor {

    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress() {
        // ArrayList to store bitmap in a convenient way
        ArrayList<Integer> lengths = new ArrayList<Integer>();
        boolean startBit = BinaryStdIn.readBoolean();
        boolean current = startBit;
        int count = 0;
        // Fill ArrayList with integers corresponding to the length of each repeat
        while(!BinaryStdIn.isEmpty()){
            // Advance to the next bit
            boolean nextBit = BinaryStdIn.readBoolean();
            // Add one to count if next bit is a repeat
            if(nextBit == current){
                count++;
                current = nextBit;
            }
            // Otherwise add the current count to the ArrayList, and reset the count.
            else{
                lengths.add(count + 1);
                count = 0;
                current = nextBit;
            }
        }
        // Add the final repeat to the ArrayList
        lengths.add(count);

        // For each repeat write to the binary file
        for(int length : lengths){
            // If a repeat requires more than 8 bits
            while(length > 255){
                // Write 255
                BinaryStdOut.write(255, 8);
                length -= 255;
                // Write a zero to switch the expander back 1 or 0
                BinaryStdOut.write(0,8);
            }
            BinaryStdOut.write(length, 8);
        }
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {
        // Boolean for a pixel
        boolean type = false;
        // Loop until the end of string
        while(!BinaryStdIn.isEmpty()){
            // Read length of a repeat from file in
            int runLength = BinaryStdIn.readInt(8);
            // Write the appropriate amount of bits
            for(int i = 0; i < runLength; i++){
                BinaryStdOut.write(castBoolean(type), 1);
            }
            // Switch the bit from 0 to 1 or 1 to 0
            type = !type;
        }
        BinaryStdOut.close();
    }

    // Method for casting a boolean to a 1 or a 0 in integer form
    public static int castBoolean(boolean bool) {
        if(bool){
            return 1;
        }
        else{
            return 0;
        }
    }

    public static int log2(int N)
    {
        return (int)(Math.log(N) / Math.log(2));

    }

    public static int findMax(ArrayList<Integer> nums){
        int max = nums.get(0);
        for(int i = 1; i < nums.size(); i++){
            if(nums.get(i) > max){
                max = nums.get(i);
            }
        }
        return max;
    }
    /**
     * When executed at the command-line, run {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}