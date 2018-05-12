import javafx.util.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.lang.*;

public class HuffmanTree {

    private  class Node {
        private Short value;
        private int freq;
        private Node left;
        private Node right;

        public Node(){
            this.value = null;
            this.left = null;
            this.right = null;
        }

        public Node(Short value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }

        public Node (Short value, int freq) {
            this.value = value;
            this.freq = freq;
        }

        public Node (int freq) {
            this.freq = freq;
        }

    }

    Node root;

    private static class NodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) {
            return o1.freq - o2.freq;
        }
    }

    /*
     *
     * Builds a tree based on the frequency of the characters
     * @param m, Map<Short, Integer>
     */
    public HuffmanTree(Map<Short, Integer> m) {//for encode
        PriorityQueue<Node> minPQ = new PriorityQueue<>(m.size(),new NodeComparator());
        for (Map.Entry<Short,Integer> entry : m.entrySet()) {
            Node input = new Node(entry.getKey(),entry.getValue());
            minPQ.add(input);
        }

        //build the tree
        while (minPQ.size() > 1) {
            Node left = minPQ.poll();
            Node right = minPQ.poll();
            Node internalNode = new Node(left.freq + right.freq);
            internalNode.left = left;
            internalNode.right = right;
            minPQ.add(internalNode);
        }

        root = minPQ.poll();
    }

    /*
     * A helper method used to recursively build the tree based on input stream
     * @param: in, BitInputStream
     */
    private Node ConstructorH (BitInputStream in) {
        if (in.hasBits()) {
            if (in.readBit() == 1) {
                Node internalNode = new Node();
                internalNode.left = ConstructorH(in);
                internalNode.right = ConstructorH(in);
                return internalNode;
            } else {
                Short value = new Short((short)in.readBits(9));
                Node leave = new Node(value);
                return leave;
            }
        } else {
            return null;
        }
    }

    /*
     * A constructor: build a tree from given grin file
     * @param: in, BitInputStream
     */
    public HuffmanTree(BitInputStream in) {//for decode
        if (in.hasBits()) {
            in.readBits(32); //read the magical number
            root = ConstructorH(in);
        }
    }


    /*
     * A helper method write the pre-order tree with 1 representing internal node and 0 for leave
     * @param: out, BitOutputStream
     * @param: curr, Node
     */
    private void serializeH(BitOutputStream out, Node cur) {
        if (cur.value == null) {
            out.writeBit(1);
            serializeH(out, cur.left);
            serializeH(out,cur.right);
        } else {
            out.writeBit(0);
            out.writeBits((int) cur.value, 9);
        }
    }

    /*
     * Add the title to the output file
     * @param: out, BitOutputStream
     */
    public void serialize(BitOutputStream out) {//print the title
        out.writeBits((int)1846, 32);
        serializeH(out,root);
    }

    private Map<Short, Pair<Integer,Integer>> Code = new HashMap<>();

    /*
     * Store the code for the char in a map
     * @param cur, Node
     * @param code, Integer
     * @param size, Integer
     */
    private void findCode(Node cur, Integer code, Integer size) {
        if(cur.value == null) {
            Integer codeLeft = (code << 1);
            Integer sizeChild = size + 1;
            Integer codeRight = codeLeft + 1;
            findCode(cur.left, codeLeft, sizeChild);
            findCode(cur.right, codeRight, sizeChild);
        } else {
            Pair<Integer, Integer> pair = new Pair<>(code, size);
            this.Code.put(cur.value,pair);
        }
    }

    /*
     * Reads the given file to encode and returns the coded value of that file
     * @param in, BitInputStream
     * @param out, BitOutputStream
     */
    public void encode(BitInputStream in, BitOutputStream out) {
        findCode(root,0,0);
        while(in.hasBits()) {
            short cur = (short) in.readBits(8);
                Pair<Integer, Integer> output = Code.get(cur);
                out.writeBits(output.getKey(), output.getValue());
        }
        Pair<Integer, Integer> output = Code.get((short) 256);
        out.writeBits(output.getKey(),output.getValue());
        in.close();
        out.close();
    }

    /*
     * Decodes the given file by finding the coded value and printing the correct value to output file
     * @param in, BitInputStream
     * @param out, BitOutputStream
     */
    public void decode(BitInputStream in, BitOutputStream out) {
        short value;
        while (in.hasBits()) {
            Node nodePtr = root;
            while (nodePtr.value == null) {
                if (in.readBit() == 0) {
                    nodePtr = nodePtr.left;
                } else {
                    nodePtr = nodePtr.right;
                }
            } //now nodePtr reaches to one leave

            value = nodePtr.value;
            out.writeBits((int) value, 8);
        }
        in.close();
        out.close();
    }
}
