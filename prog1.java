import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;

class DataNode {
    int marks;
    String hashValue;
    DataNode previousNode;
    String previousHashValue;

    public DataNode(int marks, DataNode previousNode) {
        this.marks = marks;
        this.previousNode = previousNode;
        this.previousHashValue = previousNode != null ? previousNode.hashValue : "0";
        this.hashValue = calculateHash();
    }

    private String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = marks + previousHashValue;
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

public class Blockchain {
    private List<DataNode> nodeList;

    public Blockchain() {
        this.nodeList = new ArrayList<>();
    }

    public void addNode(int marks) {
        DataNode previousNode = nodeList.isEmpty() ? null : nodeList.get(nodeList.size() - 1);
        DataNode newNode = new DataNode(marks, previousNode);
        nodeList.add(newNode);
    }

    public void printBlockchain() {
        int i = 1;
        for (DataNode node : nodeList) {
            System.out.println("Block " + i + "\n\tMarks: " + node.marks + "\n\tHash Value: " + node.hashValue + "\n\tPrevious Hash Value: " + node.previousHashValue + "\n\n");
            i = i + 1;
        }
    }

    public static void main(String[] args) {
        Blockchain block = new Blockchain();
        block.addNode(90);
        block.addNode(85);
        block.addNode(95);
        block.printBlockchain();
    }
}
