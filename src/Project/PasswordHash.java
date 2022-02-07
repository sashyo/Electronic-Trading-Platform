package Project;
import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


/**
 * This class hold the functionality for PBKDF2 salted password hashing.
 * Author: havoc AT defuse.ca
 * Source: http://crackstation.net/hashing-security.htm
 */
public class PasswordHash
{
    /**
     * The hashing algorithm.
     */
    public static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    /**
     * The number of salt bytes.
     */
    public static final int SALT_BYTES = 24;

    /**
     * The number of hash bytes.
     */
    public static final int HASH_BYTES = 24;

    /**
     * The number of PBKDF2 iterations.
     */
    public static final int PBKDF2_ITERATIONS = 1000;

    /**
     * The iteration index.
     */
    public static final int ITERATION_INDEX = 0;

    /**
     * The salt index.
     */
    public static final int SALT_INDEX = 1;

    /**
     * The PBKDF2 index.
     */
    public static final int PBKDF2_INDEX = 2;

    /**
     * Hashes the password.
     *
     * @param password The password to hash.
     * @return A salted PBKDF2 hash of the password.
     * @throws NoSuchAlgorithmException when something goes wrong.
     * @throws InvalidKeySpecException when something goes wrong.
     */
    public static String createHash(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES);
        // format PBKDF2_ITERATIONS:salt:hash
        return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" +  toHex(hash);

    }


    /**
     * Compares input password to hashed password stored in database.
     *
     * @param password The password to compare.
     * @param goodHash The hashed password to compare.
     * @return True if the passwords match, otherwise returns false.
     * @throws NoSuchAlgorithmException when something goes wrong.
     * @throws InvalidKeySpecException when something goes wrong.
     */
    public static boolean validatePassword(String password, String goodHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Decode the hash into its parameters
        String[] params = goodHash.split(":");
        int PBKDF2_ITERATIONS = Integer.parseInt(params[ITERATION_INDEX]);
        byte[] salt = fromHex(params[SALT_INDEX]);
        byte[] hash = fromHex(params[PBKDF2_INDEX]);
        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     *
     * @param   a       The first byte array
     * @param   b       The second byte array
     * @return          True if both byte arrays are the same, false if not
     */
    private static boolean slowEquals(byte[] a, byte[] b)
    {
        if(a == null)
            throw new NullPointerException("Please enter a password");
        if(b == null)
            throw new NullPointerException("No password in database, check if user account exists");
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    /**
     *  Computes the PBKDF2 hash of a password.
     *
     * @param   password    The password to hash.
     * @param   salt        The salt
     * @param   PBKDF2_ITERATIONS  The iteration count (slowness factor)
     * @param   bytes       The length of the hash to compute in bytes
     * @return              The PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int PBKDF2_ITERATIONS, int bytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param   hex         The hex string
     * @return              The hex string decoded into a byte array
     */
    private static byte[] fromHex(String hex)
    {
        byte[] binary = new byte[hex.length() / 2];
        for(int i = 0; i < binary.length; i++)
        {
            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return binary;
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param   array       The byte array to convert
     * @return              A length*2 character string encoding the byte array
     */
    private static String toHex(byte[] array)
    {

        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

}