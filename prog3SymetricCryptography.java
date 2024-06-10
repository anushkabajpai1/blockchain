import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MultipleBlockchains {

    // Block class
    static class Block {
        private String data;
        private String hash;
        private String previousHash;

        // Constructor
        public Block(String data, String previousHash) {
            this.data = data;
            this.previousHash = previousHash;
            this.hash = calculateHash();
        }

        // Calculate hash function
        public String calculateHash() {
            String dataToHash = data + previousHash;
            return applySHA256(dataToHash);
        }

        // Apply SHA-256 hash function
        private String applySHA256(String input) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // Getters
        public String getData() {
            return data;
        }

        public String getHash() {
            return hash;
        }

        public String getPreviousHash() {
            return previousHash;
        }
    }

    // Blockchain class
    static class Blockchain {
        private List<Block> chain;
        private String previousHash;

        // Constructor
        public Blockchain() {
            chain = new ArrayList<>();
            previousHash = "0"; // Genesis block
        }

        // Add block to the chain
        public void addBlock(String data) {
            Block newBlock = new Block(data, previousHash);
            chain.add(newBlock);
            previousHash = newBlock.getHash();
        }

        // Get block by index
        public Block getBlock(int index) {
            return chain.get(index);
        }

        // Get size of the chain
        public int size() {
            return chain.size();
        }
    }

    // Caesar Cipher encryption function
    public static String caesarCipherEncrypt(String plainText, int shift) {
        StringBuilder encryptedText = new StringBuilder();
        for (char c : plainText.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                encryptedText.append((char) ((c - base + shift) % 26 + base));
            } else {
                encryptedText.append(c);
            }
        }
        return encryptedText.toString();
    }

    // DES encryption function
    public static String desEncrypt(String plainText, byte[] key) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // AES encryption function
    public static String aesEncrypt(String plainText, byte[] key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static void main(String[] args) throws Exception {
        // Test data
        String[] data = {"Block 1 data", "Block 2 data", "Block 3 data"};
        String secureKey = "your_secure_key";
        byte[] desKey = MessageDigest.getInstance("SHA-256").digest(secureKey.getBytes(StandardCharsets.UTF_8));
        byte[] aesKey = MessageDigest.getInstance("SHA-256").digest(secureKey.getBytes(StandardCharsets.UTF_8));

        // Create and populate blockchains
        Blockchain caesarBlockchain = new Blockchain();
        Blockchain desBlockchain = new Blockchain();
        Blockchain aesBlockchain = new Blockchain();

        for (String blockData : data) {
            // Encrypt data for each blockchain
            String caesarEncryptedData = caesarCipherEncrypt(blockData, 3);
            String desEncryptedData = desEncrypt(blockData, desKey);
            String aesEncryptedData = aesEncrypt(blockData, aesKey);

            // Add blocks to blockchains
            caesarBlockchain.addBlock(caesarEncryptedData);
            desBlockchain.addBlock(desEncryptedData);
            aesBlockchain.addBlock(aesEncryptedData);
        }

        // Print blockchain data
        System.out.println("Caesar Cipher Blockchain:");
        printBlockchain(caesarBlockchain);
        System.out.println("\nDES Blockchain:");
        printBlockchain(desBlockchain);
        System.out.println("\nAES Blockchain:");
        printBlockchain(aesBlockchain);
    }

    // Helper method to print blockchain data
    public static void printBlockchain(Blockchain blockchain) {
        for (int i = 0; i < blockchain.size(); i++) {
            Block block = blockchain.getBlock(i);
            System.out.println("Block " + (i + 1) + ":");
            System.out.println("Data: " + block.getData());
            System.out.println("Hash: " + block.getHash());
            System.out.println("Previous Hash: " + block.getPreviousHash());
            System.out.println();
        }
    }
}
