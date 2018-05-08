import javafx.util.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

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
           // if (o1.freq != o2.freq) {
                return o1.freq - o2.freq;
           // } else {
               // return ((int) o1.value - o2.value);
        //    }
        }
    }

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

	public HuffmanTree(BitInputStream in) {//for decode
        if (in.hasBits()) {
            in.readBits(32); //read the magical number
            root = ConstructorH(in);
        }
	}

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


    public void serialize(BitOutputStream out) {//print the title
		out.writeBits((int)1846, 32);
		serializeH(out,root);
	}

	private Map<Short, Pair<Integer,Integer>> Code = new HashMap<>();

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

    public void encode(BitInputStream in, BitOutputStream out) {
	    findCode(root,0,0);
		while(in.hasBits()) {
		    short cur = (short) in.readBits(8);
		    if (cur != 0) {
                Pair<Integer, Integer> output = Code.get(cur);
                out.writeBits(output.getKey(), output.getValue());
            } else {
                Pair<Integer, Integer> output = Code.get((short) 256);
		        out.writeBits(output.getKey(),output.getValue());
            }
        }
	}

    public void decode(BitInputStream in, BitOutputStream out) {
		//Node nodePtr = root;
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
	}
}