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

        ArrayList<Integer> lengths = new ArrayList<Integer>();
        boolean startBit = BinaryStdIn.readBoolean();
        boolean current = startBit;
        int count = 1;
        int size = 0;
        while(!BinaryStdIn.isEmpty()){
            boolean nextBit = BinaryStdIn.readBoolean();
            if(nextBit == current){
                count++;
                current = nextBit;
            }
            else{
                lengths.add(count);
                count = 0;
                current = nextBit;
            }
            size++;
        }
        int max = findMax(lengths);
        int numberOfBits = log2(max) + 1;
        BinaryStdOut.write(size);
        BinaryStdOut.write(numberOfBits);
        BinaryStdOut.write(castBoolean(startBit), 1);
        current = startBit;
        for(int length : lengths){
            for(int i = 0; i < length; i++){
                BinaryStdOut.write(length, numberOfBits);
            }
            current = !startBit;
        }
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {

        int size = BinaryStdIn.readInt();
        int sequenceLength = BinaryStdIn.readInt();
        boolean current = BinaryStdIn.readBoolean();

        for(int i = 0; i < size/sequenceLength; i++){
            int nextLength = BinaryStdIn.readInt(sequenceLength);
            for(int j = 0; j < nextLength; j++){
                BinaryStdOut.write(castBoolean(current), 1);
            }
            current = !current;
        }

        BinaryStdOut.close();
    }

    public static int castBoolean(boolean bool) {
        return (bool) ? 1 : 0;
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