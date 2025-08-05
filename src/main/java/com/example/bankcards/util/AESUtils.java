package com.example.bankcards.util;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESUtils {

    public static final String KEY_ALGORITHM = "AES";
    public static final String KEY_TRANSFORMATION = "AES/GCM/NoPadding";
    public static final int keySize = 256;//bits
    public static final int AuthTagSize = 128; //bits
    public static final int NonceSize = 12; //bytes

    public static byte[] decryptBASE64(String key) {
        return Base64.getDecoder().decode(key);
    }

    public static String encryptBASE64(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }

    public static byte[] encrypt(byte[] data, byte[] iv, SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(KEY_TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(AuthTagSize, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] iv, SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(KEY_TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(AuthTagSize, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        return cipher.doFinal(data);
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        keyGenerator.init(keySize);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static byte[] generateIv() {
        byte[] iv = new byte[NonceSize];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static String encodeCardNumber(String cardNumber, SecretKey aesKey) {
        try {
            byte[] iv = generateIv();
            byte[] encrypted = encrypt(cardNumber.getBytes(), iv, aesKey);

            return encryptBASE64(
                    ByteBuffer.allocate(iv.length + encrypted.length)
                            .put(iv)
                            .put(encrypted)
                            .array());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка шифрования карты ", e);
        }
    }

    public static String decryptCardNumber(String encryptedBase64, SecretKey aesKey) {
        byte[] bytes = decryptBASE64(encryptedBase64);
        byte[] iv = Arrays.copyOfRange(bytes, 0, NonceSize);
        byte[] encrypted = Arrays.copyOfRange(bytes, NonceSize, bytes.length);

        try {
            return new String(decrypt(encrypted, iv, aesKey));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка дешифрования карты ", e);
        }
    }
}
