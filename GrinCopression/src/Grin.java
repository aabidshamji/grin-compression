import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Grin {
	public static void decode(String infile, String outfile) throws IOException {
		BitInputStream in = new BitInputStream(infile);
		BitOutputStream out = new BitOutputStream(outfile);
		HuffmanTree tree = new HuffmanTree(in);
		tree.decode(in,out);
	}

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

	public static void encode(String infile, String outfile) throws IOException {
		BitInputStream in = new BitInputStream(infile);
		BitOutputStream out = new BitOutputStream(outfile);

		Map<Short,Integer> Code = createFrequencyMap(infile);

		HuffmanTree tree = new HuffmanTree(Code);
		tree.serialize(out);

		tree.encode(in,out);
	}

	public static void main(String[] args) throws IOException {
		String choice = "decode"; //args[0];
		String infile = "code.grin"; //args[1];
		String outfile = "test.txt"; //args[2];

		if (choice.equalsIgnoreCase("encode")) {
			encode(infile,outfile);
		} else if (choice.equalsIgnoreCase("decode")) {
			decode(infile,outfile);
		} else {
			System.out.println("Please check your spell, bye");
		}

	}

}
