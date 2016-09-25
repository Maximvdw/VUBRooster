package be.vubrooster.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HashUtils
 *
 * @author Maxim Van de Wynckel
 * @date 15-May-16
 */
public class HashUtils {
    /**
     * Convert data to sha256
     * @param data data to hash
     * @return hashed data
     * @throws NoSuchAlgorithmException
     */
    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }

    /**
     * Convert data to md5
     * @param data data to hash
     * @return hashed data
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());

            return bytesToHex(md.digest());
        }catch (Exception ex){

        }
        return "";
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}
