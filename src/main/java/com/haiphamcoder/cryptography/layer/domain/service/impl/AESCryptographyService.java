package com.haiphamcoder.cryptography.layer.domain.service.impl;

import com.haiphamcoder.cryptography.layer.domain.entity.DecryptedInfoResponse;
import com.haiphamcoder.cryptography.layer.domain.entity.EncryptedInfoResponse;
import com.haiphamcoder.cryptography.layer.domain.service.IAESCryptographyService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.generators.SCrypt;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

@Service
@Slf4j
public class AESCryptographyService implements IAESCryptographyService {
    private static final Integer SCRYPT_INTERATIONS_COUNT = 16384;
    private static final Integer SCRYPT_BLOCK_SIZE = 8;
    private static final Integer SCRYPT_PARALLELISM_FACTOR = 1;
    private static final Integer KEY_LENGTH = 512; // bits
    private static final Integer SALT_LENGTH = 128; // bits
    private static final Integer IV_LENGTH = 128; // bits
    private static final Integer AES_KEY_LENGTH = 256; // bits
    private static final Integer HMAC_SHA_KEY_LENGTH = 256; // bits

    @Override
    public EncryptedInfoResponse encrypt(String plainText, String secretKey) {
        try {
            byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] paddedData = pkcs7Padding(data, SCRYPT_BLOCK_SIZE / 8);

            byte[] salt = generateSalt();
            byte[] derivedKey = derivedKey(Base64.getDecoder().decode(secretKey), salt);
            byte[] encryptKey = new byte[AES_KEY_LENGTH / 8];
            System.arraycopy(derivedKey, 0, encryptKey, 0, AES_KEY_LENGTH / 8);
            byte[] hmacKey = new byte[HMAC_SHA_KEY_LENGTH / 8];
            System.arraycopy(derivedKey, AES_KEY_LENGTH / 8, hmacKey, 0, HMAC_SHA_KEY_LENGTH / 8);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Key key = makeKey(encryptKey);
            byte[] iv = generateSalt();
            AlgorithmParameterSpec ivSpec = makeIV(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            String encryptedData = Base64.getEncoder().encodeToString(cipher.doFinal(paddedData));

            EncryptedInfoResponse response = new EncryptedInfoResponse();
            EncryptedInfoResponse.EncryptedInfoData dataResponse = new EncryptedInfoResponse.EncryptedInfoData();
            dataResponse.setIv(Base64.getEncoder().encodeToString(iv));
            dataResponse.setSalt(Base64.getEncoder().encodeToString(salt));
            dataResponse.setEncryptedData(encryptedData);
            response.setData(dataResponse);
            return response;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DecryptedInfoResponse decrypt(String cipherText, String secretKey, String salt, String iv, String mac) {
        try {
            byte[] derivedKey = derivedKey(Base64.getDecoder().decode(secretKey), Base64.getDecoder().decode(salt));
            byte[] encryptKey = new byte[AES_KEY_LENGTH / 8];
            System.arraycopy(derivedKey, 0, encryptKey, 0, AES_KEY_LENGTH / 8);
            byte[] hmacKey = new byte[HMAC_SHA_KEY_LENGTH / 8];
            System.arraycopy(derivedKey, AES_KEY_LENGTH / 8, hmacKey, 0, HMAC_SHA_KEY_LENGTH / 8);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Key key = makeKey(encryptKey);
            AlgorithmParameterSpec ivSpec = makeIV(Base64.getDecoder().decode(iv));
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(cipherText));

            byte[] unpaddedData = pkcs7Unpadding(decryptedData);
            String plainText = new String(unpaddedData, StandardCharsets.UTF_8);

            DecryptedInfoResponse response = new DecryptedInfoResponse();
            DecryptedInfoResponse.DecryptedInfoData dataResponse = new DecryptedInfoResponse.DecryptedInfoData();
            dataResponse.setDecryptedData(plainText);
            response.setData(dataResponse);
            return response;
        } catch (RuntimeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

    }

    private Key makeKey(byte[] key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = md.digest(key);
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private AlgorithmParameterSpec makeIV(byte[] iv) {
        return new IvParameterSpec(iv);
    }

    private byte[] generateSalt() {
        // Generate a random salt
        byte[] salt = new byte[SALT_LENGTH / 8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    private byte[] pkcs7Padding(byte[] data, int blockSize) {
        int paddingLength = blockSize - data.length % blockSize;
        byte[] result = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, result, 0, data.length);
        for (int i = data.length; i < result.length; i++) {
            result[i] = (byte) paddingLength;
        }
        return result;
    }

    private byte[] pkcs7Unpadding(byte[] data) {
        int paddingLength = data[data.length - 1];
        byte[] result = new byte[data.length - paddingLength];
        System.arraycopy(data, 0, result, 0, result.length);
        return result;
    }

    private byte[] derivedKey(byte[] secretKey, byte[] salt) {
        return SCrypt.generate(secretKey,
                salt,
                SCRYPT_INTERATIONS_COUNT,
                SCRYPT_BLOCK_SIZE / 8,
                SCRYPT_PARALLELISM_FACTOR,
                KEY_LENGTH / 8);
    }
}
