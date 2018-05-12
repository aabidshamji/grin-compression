import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Grin {

    /*
     * Decodes the given file
     * @param infile, outfile, String
     */
    public static void decode(String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        HuffmanTree tree = new HuffmanTree(in);
        tree.decode(in,out);
    }

    /*
     * creates a map with the frequencies of the chars in the file
     * @param: file, String
     * @throws IOException
     * @returns Code, Map<Short,Integer>
     */
    public static Map<Short, Integer> createFrequencyMap(String file) throws IOException {
        BitInputStream in = new BitInputStream(file);
        Map<Short,Integer> Code = new HashMap<>();
        while (in.hasBits()) {
            Short value = (short) in.readBits(8);
            if (value == 0) {
                continue;
            }
            if (Code.containsKey(value)) {
                Integer newFreq = Code.get(value) + 1;
                Code.put(value,newFreq);
            } else {
                Code.put(value,1);
            }
        }
        Short EOF = 1 << 8;
        Code.put(EOF,1);

        return Code;
    }

    /*
     * Given a file, encodes the given file
     * @param infile, String
     * @param outfile, String
     * @throws IOException
     */
    public static void encode(String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);

        Map<Short,Integer> Code = createFrequencyMap(infile);

        HuffmanTree tree = new HuffmanTree(Code);
        tree.serialize(out);

        tree.encode(in,out);
    }

    public static void main(String[] args) throws IOException {
        String choice = args[1];
        String outfile = args[2];
        String infile = args[3];

        if (choice.equalsIgnoreCase("encode")) {
            encode(infile,outfile);
        } else if (choice.equalsIgnoreCase("decode")) {
            decode(infile,outfile);
        } else {
            System.out.println("Please check your spell, bye");
        }
    }


}
