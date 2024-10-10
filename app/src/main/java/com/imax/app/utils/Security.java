package com.imax.app.utils;

import android.content.Context;
import android.util.Base64;

import com.imax.app.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Security {
    public static String getSHA512Secure(String textToSHash){
        String generated = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            //md.update(salt.getBytes("UTF-8"));//StandardCharsets.UTF_8 requiere API 19
            md.update(BuildConfig.SHA_512_SALT.getBytes("UTF-8"));
            //md.update(getSalt());
            byte[] bytes = md.digest(textToSHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generated = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generated;
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static SecretKey generateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        return new SecretKeySpec(password.getBytes(), "AES");
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] EncryptionKey = new byte[16];
        random.nextBytes(EncryptionKey);
        return new SecretKeySpec(EncryptionKey, "AES");
    }

    public static byte[] encrypt(String message, SecretKey secretKey) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        //SecretKey secretKey = generateKey(context.getString(R.string.secret_key));
        /* Encrypt the message. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = cipher.doFinal(message.getBytes());
        //byte[] results = cipher.doFinal(Base64.encode(message.getBytes("UTF-8"), Base64.DEFAULT));
        return cipherText;
    }

    public static String decrypt(byte[] cipherText, SecretKey secretKey) throws InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        //SecretKey secretKey = generateKey(context.getString(R.string.secret_key));
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        //String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }

    public static String encriptar(Context context, String data)  {
        String encryptedString;
        try{
            SecretKey secretKey = generateKeySha(BuildConfig.SECRET_KEY);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
            encryptedString = data;
        }
        return encryptedString;
    }

    public static String desencriptar(Context context, String dataEncriptada) {
        String decryptedString;
        try{
            SecretKey secretKey = generateKeySha(BuildConfig.SECRET_KEY);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.decode(dataEncriptada, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            decryptedString = new String(decryptedBytes);
        }catch (Exception e){
            e.printStackTrace();
            decryptedString = dataEncriptada;
        }
        return decryptedString;
    }

    private static SecretKey generateKeySha(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = messageDigest.digest(key);
        return new SecretKeySpec(key, "AES");

    }
}
