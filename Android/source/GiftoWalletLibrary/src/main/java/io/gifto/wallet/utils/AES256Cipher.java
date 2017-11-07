package io.gifto.wallet.utils;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.gifto.wallet.utils.common.Logger;

/**
 * Created by thongnguyen on 9/13/17.
 */

public class AES256Cipher {

    private static final String TAG = "AES256Cipher";

    /**
     * Load native library
     */
    static {
        System.loadLibrary("gifto-native-utils");
    }

    /**
     * Native method - generate secret key
     * @param apiKey apiKey
     * @param identity identity data
     * @return secret key
     */
    private static native String generateSecretKey(String apiKey, String identity);
//    {
//        return Utils.MD5_Hash((apiKey == null? "" : apiKey) + (identity == null? "" : identity) + "thongnguyen_ahihi");
//    }

    /**
     * Encrypt data
     * @param ivBytes
     * @param keyBytes
     * @param textBytes
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static byte[] encrypt(byte[] ivBytes, byte[] keyBytes, byte[] textBytes)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

    /**
     * Decrypt data
     * @param ivBytes
     * @param keyBytes
     * @param textBytes
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static byte[] decrypt(byte[] ivBytes, byte[] keyBytes, byte[] textBytes)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

    /**
     * Encrypt String
     * @param apiKey apiKey
     * @param identity identityData
     * @param plainTex text to encrypt
     * @return encrypted text
     */
    public static String Encrypt(String apiKey, String identity, String plainTex)
    {
        try {
            String key = generateSecretKey(apiKey, identity);
            byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] cipherData;
            cipherData = AES256Cipher.encrypt(ivBytes, keyBytes, plainTex.getBytes("UTF-8"));
            String encrypted = Base64.encodeToString(cipherData, Base64.DEFAULT);
            return encrypted;
        }
        catch (Exception e)
        {
            Logger.e(TAG, "Error while encrypting");
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Decrypt String
     * @param apiKey apiKey
     * @param identity identity data
     * @param encrypted encrypted string
     * @return plain text
     */
    public static String Decrypt(String apiKey, String identity, String encrypted)
    {
        try {
            String key = generateSecretKey(apiKey, identity);
            byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] cipherData = AES256Cipher.decrypt(ivBytes, keyBytes, Base64.decode(encrypted.getBytes("UTF-8"), Base64.DEFAULT));
            String plainText = new String(cipherData, "UTF-8");
            return plainText;
        }
        catch (Exception e)
        {
            Logger.e(TAG, "Error while decrypting");
            e.printStackTrace();
        }
        return "";
    }
}
