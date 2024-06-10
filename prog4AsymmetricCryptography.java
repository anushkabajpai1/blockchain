import java.security.*;

public class TransactionAuthentication {

    // Generate key pair
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // key size
        return keyGen.generateKeyPair();
    }

    // Sign transaction
    public static String signTransaction(String transactionData, PrivateKey privateKey) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(transactionData.getBytes());
        byte[] signatureBytes = sign.sign();
        return bytesToHex(signatureBytes);
    }

    // Verify transaction signature
    public static boolean verifyTransaction(String transactionData, String signature, PublicKey publicKey) throws Exception {
        Signature verify = Signature.getInstance("SHA256withRSA");
        verify.initVerify(publicKey);
        verify.update(transactionData.getBytes());
        byte[] signatureBytes = hexToBytes(signature);
        return verify.verify(signatureBytes);
    }

    // Convert byte array to hex string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Convert hex string to byte array
    public static byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public static void main(String[] args) throws Exception {
        // Generate key pair
        KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Transaction data
        String transactionData = "Sample transaction data";

        // Sign transaction
        String signature = signTransaction(transactionData, privateKey);

        // Verify transaction signature
        boolean isVerified = verifyTransaction(transactionData, signature, publicKey);

        // Output
        System.out.println("Original Transaction Data: " + transactionData);
        System.out.println("Generated Signature: " + signature);
        System.out.println("Transaction Verification Result: " + (isVerified ? "Success" : "Failure"));
    }
}
