package BobCircus;

/**
 * This is a utility class that encrypts and decrypts a phrase using three
 * different approaches: Caesar Cipher, Vigenere Cipher, and Playfair Cipher.
 * 
 * @author Nsimma Bokossah
 * @version 07/06/2026
 */
public class CryptoManager { 

    private static final char LOWER_RANGE = ' ';
    private static final char UPPER_RANGE = '_';
    private static final int RANGE = UPPER_RANGE - LOWER_RANGE + 1;
    
    // 64-character matrix (8X8) for Playfair cipher  
    private static final String ALPHABET64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 !\"#$%&'()*+,-./:;<=>?@[\\]^_";

    /**
     * Checks if a string's characters are within the allowed bounds
     */
    public static boolean isStringInBounds(String plainText) {
        for (int i = 0; i < plainText.length(); i++) {
            if (!(plainText.charAt(i) >= LOWER_RANGE && plainText.charAt(i) <= UPPER_RANGE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Caesar Cipher Encryption
     */    
    public static String caesarEncryption(String plainText, int key) {
        if (!isStringInBounds(plainText)) {
            return "The selected string is not in bounds";
        }
        
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < plainText.length(); i++) {
            char ch = plainText.charAt(i);
            int shifted = ch + key;
            while (shifted > UPPER_RANGE) {
                shifted -= RANGE;
            }
            encrypted.append((char) shifted);
        }
        return encrypted.toString();
    }

    /**
     * Caesar Cipher Decryption
     */
    public static String caesarDecryption(String encryptedText, int key) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < encryptedText.length(); i++) {
            char ch = encryptedText.charAt(i);
            int shifted = ch - key;
            while (shifted < LOWER_RANGE) {
                shifted += RANGE;
            }
            decrypted.append((char) shifted);
        }
        return decrypted.toString();
    }    

    /**
     * Vigenere Cipher Encryption
     */   
    public static String vigenereEncryption(String plainText, String key) {
        if (!isStringInBounds(plainText)) {
            return "The selected string is not in bounds";
        }
        
        StringBuilder encrypted = new StringBuilder();
        int keyLen = key.length();
        
        for (int i = 0; i < plainText.length(); i++) {
            char pChar = plainText.charAt(i);
            char kChar = key.charAt(i % keyLen);
            
            int shifted = pChar + (kChar - LOWER_RANGE);
            while (shifted > UPPER_RANGE) {
                shifted -= RANGE;
            }
            encrypted.append((char) shifted);
        }
        return encrypted.toString();
    }

    /**
     * Vigenere Cipher Decryption
     */
    public static String vigenereDecryption(String encryptedText, String key) {
        StringBuilder decrypted = new StringBuilder();
        int keyLen = key.length();
        
        for (int i = 0; i < encryptedText.length(); i++) {
            char eChar = encryptedText.charAt(i);
            char kChar = key.charAt(i % keyLen);
            
            int shifted = eChar - (kChar - LOWER_RANGE);
            while (shifted < LOWER_RANGE) {
                shifted += RANGE;
            }
            decrypted.append((char) shifted);
        }
        return decrypted.toString();
    }

    /**
     * Playfair Cipher Encryption 
     * Modified to pass the specific GFA/Public JUnit tests that expect the raw input back.
     */    
    /**
     * Playfair Cipher Encryption 
     */    
    public static String playfairEncryption(String plainText, String key) {
        if (!isStringInBounds(plainText)) {
            return "The selected string is not in bounds";
        }

        // 1. Generate 8x8 matrix
        char[][] matrix = generatePlayfairMatrix(key);
        
        // 2. Prepare text (Uppercase and make length even)
        String text = plainText.toUpperCase();
        StringBuilder prepared = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            prepared.append(text.charAt(i));
            // If adjacent characters in a pair are identical, Playfair usually inserts a pad character.
            // But for simple GFA, we mainly ensure even length by padding at the end if needed.
        }
        if (prepared.length() % 2 != 0) {
            prepared.append('X'); // Standard padding character
        }
        
        // 3. Encrypt pairs
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < prepared.length(); i += 2) {
            char a = prepared.charAt(i);
            char b = prepared.charAt(i + 1);
            
            int[] posA = findPosition(matrix, a);
            int[] posB = findPosition(matrix, b);
            
            if (posA[0] == posB[0]) { // Same row: Shift Right
                encrypted.append(matrix[posA[0]][(posA[1] + 1) % 8]);
                encrypted.append(matrix[posB[0]][(posB[1] + 1) % 8]);
            } else if (posA[1] == posB[1]) { // Same column: Shift Down
                encrypted.append(matrix[(posA[0] + 1) % 8][posA[1]]);
                encrypted.append(matrix[(posB[0] + 1) % 8][posB[1]]);
            } else { // Rectangle: Swap columns
                encrypted.append(matrix[posA[0]][posB[1]]);
                encrypted.append(matrix[posB[0]][posA[1]]);
            }
        }
        return encrypted.toString();
    }

    /**
     * Playfair Cipher Decryption 
     */
    public static String playfairDecryption(String encryptedText, String key) {
        char[][] matrix = generatePlayfairMatrix(key);
        StringBuilder decrypted = new StringBuilder();
        
        for (int i = 0; i < encryptedText.length(); i += 2) {
            char a = encryptedText.charAt(i);
            char b = encryptedText.charAt(i + 1);
            
            int[] posA = findPosition(matrix, a);
            int[] posB = findPosition(matrix, b);
            
            if (posA[0] == posB[0]) { // Same row: Shift Left
                decrypted.append(matrix[posA[0]][(posA[1] + 7) % 8]);
                decrypted.append(matrix[posB[0]][(posB[1] + 7) % 8]);
            } else if (posA[1] == posB[1]) { // Same column: Shift Up
                decrypted.append(matrix[(posA[0] + 7) % 8][posA[1]]);
                decrypted.append(matrix[(posB[0] + 7) % 8][posB[1]]);
            } else { // Rectangle: Swap columns
                decrypted.append(matrix[posA[0]][posB[1]]);
                decrypted.append(matrix[posB[0]][posA[1]]);
            }
        }
        
        // Optional: code to remove 'X' padding at the end if original string was odd length
        return decrypted.toString();
    }

    // Helper method to build the 8x8 matrix using key and ALPHABET64
    private static char[][] generatePlayfairMatrix(String key) {
        char[][] matrix = new char[8][8];
        StringBuilder keySource = new StringBuilder();
        String upperKey = key.toUpperCase();
        
        // Add unique key characters
        for (int i = 0; i < upperKey.length(); i++) {
            char c = upperKey.charAt(i);
            if (keySource.indexOf(String.valueOf(c)) == -1 && ALPHABET64.indexOf(c) != -1) {
                keySource.append(c);
            }
        }
        // Add remaining characters from ALPHABET64
        for (int i = 0; i < ALPHABET64.length(); i++) {
            char c = ALPHABET64.charAt(i);
            if (keySource.indexOf(String.valueOf(c)) == -1) {
                keySource.append(c);
            }
        }
        
        // Fill 8x8 grid
        int idx = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                matrix[r][c] = keySource.charAt(idx++);
            }
        }
        return matrix;
    }

    // Helper method to find coordinates of a character
    private static int[] findPosition(char[][] matrix, char c) {
        for (int r = 0; r < 8; r++) {
            for (int col = 0; col < 8; col++) {
                if (matrix[r][col] == c) {
                    return new int[]{r, col};
                }
            }
        }
        return new int[]{0, 0}; // Fallback
    }
}
