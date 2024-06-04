import java.util.Random;

public class Node {
    enum NodeType { FUNCTION, TERMINAL }
    
    public NodeType type;
    public String value;
    public Node left;
    public Node right;

    private static final String[] FUNCTIONS = {"+", "-", "*", "/"};
    private static final String[] TERMINALS = {"cap-diameter", "cap-shape", "gill-attachment", 
                                                "gill-color", "stem-height", "stem-width", 
                                                "stem-color", "season"};

    public Node(NodeType type, String value) {
        this.type = type;
        this.value = value;
    }

    public static Node generateRandomTree(int maxDepth, Random rand) {
        if (maxDepth == 0 || rand.nextBoolean()) {
            String terminal = TERMINALS[rand.nextInt(TERMINALS.length)];
            return new Node(NodeType.TERMINAL, terminal);
        } else {
            String function = FUNCTIONS[rand.nextInt(FUNCTIONS.length)];
            Node node = new Node(NodeType.FUNCTION, function);
            node.left = generateRandomTree(maxDepth - 1, rand);
            node.right = generateRandomTree(maxDepth - 1, rand);
            return node;
        }
    }

    public double evaluate(Mushroom mushroom) {
        if (type == NodeType.TERMINAL) {
            int index = -1;
            for (int i = 0; i < TERMINALS.length; i++) {
                if (TERMINALS[i].equals(value)) {
                    index = i;
                    break;
                }
            }
            return mushroom.getAttributes().get(index);
        } else {
            double leftValue = left.evaluate(mushroom);
            double rightValue = right.evaluate(mushroom);
            switch (value) {
                case "+": return leftValue + rightValue;
                case "-": return leftValue - rightValue;
                case "*": return leftValue * rightValue;
                case "/": return rightValue == 0 ? 1 : leftValue / rightValue;
                default: throw new IllegalArgumentException("Unknown function: " + value);
            }
        }
    }

    public Node copy() {
        Node copy = new Node(this.type, this.value);
        if (this.left != null) copy.left = this.left.copy();
        if (this.right != null) copy.right = this.right.copy();
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringHelper(sb, this);
        return sb.toString();
    }

    private void toStringHelper(StringBuilder sb, Node node) {
        if (node == null) {
            sb.append("null");
            return;
        }
        sb.append(node.value).append(" ");
        if (node.type == NodeType.FUNCTION) {
            sb.append("( ");
            toStringHelper(sb, node.left);
            sb.append(") ");
            sb.append("( ");
            toStringHelper(sb, node.right);
            sb.append(") ");
        }
    }

    public double size() {
        if (type == NodeType.TERMINAL) {
            return 1;
        } else {
            return 1 + left.size() + right.size();
        }
    }
}
