package com.haiphamcoder.cryptography.layer.domain.service.impl;

import com.haiphamcoder.cryptography.layer.domain.entity.DecryptedInfoResponse;
import com.haiphamcoder.cryptography.layer.domain.entity.EncryptedInfoResponse;
import com.haiphamcoder.cryptography.layer.domain.service.IAESCryptographyService;
import com.haiphamcoder.cryptography.utils.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.generators.SCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
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
    private static final Integer SCRYPT_BLOCK_SIZE = 16;
    private static final Integer SCRYPT_PARALLELISM_FACTOR = 1;
    private static final Integer KEY_LENGTH = 64; // 512 bits = 64 bytes
    private static final Integer AES_KEY_LENGTH = 32; // 256 bits = 32 bytes
    private static final Integer HMAC_SHA_KEY_LENGTH = 32; // 256 bits = 32 bytes
    private static final Integer AES_CBC_BLOCK_SIZE = 16; // 128 bits = 16 bytes

    private final byte[] SECRET_KEY;
    private final byte[] SALT;
    private final byte[] IV;

    public AESCryptographyService(@Value("${cryptography.secret-key.base64}") String secretKey,
                                  @Value("${cryptography.salt.base64}") String salt,
                                  @Value("${cryptography.iv.base64}") String iv) {
        this.SECRET_KEY = Base64.getDecoder().decode(secretKey);
        this.SALT = Base64.getDecoder().decode(salt);
        this.IV = Base64.getDecoder().decode(iv);
    }

    @Override
    public EncryptedInfoResponse encrypt(String plainText) {
        try {
            // PKCS7 padding for the data to be encrypted
            byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] paddedData = pkcs7Padding(data, AES_CBC_BLOCK_SIZE);

            // Generate derived key from the secret key and salt
            Pair<byte[], byte[]> derivedKey = derivedKey(SECRET_KEY, SALT);
            byte[] encryptKey = derivedKey.getFirstElement();
            byte[] hmacKey = derivedKey.getSecondElement();

            // Encrypt the data
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(encryptKey), makeIV(IV));
            byte[] encryptedData = cipher.doFinal(paddedData);
            byte[] mac = calculateHMAC(encryptedData, hmacKey, "HmacSHA256");

            // Return the encrypted data and MAC as a response
            EncryptedInfoResponse response = new EncryptedInfoResponse();
            EncryptedInfoResponse.EncryptedInfoData dataResponse = new EncryptedInfoResponse.EncryptedInfoData();
            dataResponse.setEncryptedData(Base64.getEncoder().encodeToString(encryptedData));
            dataResponse.setMac(Hex.encodeHexString(mac));
            response.setData(dataResponse);
            return response;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DecryptedInfoResponse decrypt(String cipherText, String mac) {
        try {
            // Generate derived key from the secret key and salt
            Pair<byte[], byte[]> derivedKey = derivedKey(SECRET_KEY, SALT);
            byte[] encryptKey = derivedKey.getFirstElement();
            byte[] hmacKey = derivedKey.getSecondElement();

            // Verify the MAC
            byte[] calculatedMac = calculateHMAC(Base64.getDecoder().decode(cipherText), hmacKey, "HmacSHA256");
            if (!Hex.encodeHexString(calculatedMac).equals(mac)) {
                return new DecryptedInfoResponse("MAC is not matched", 1);
            }

            // Decrypt the data
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, makeKey(encryptKey), makeIV(IV));
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(cipherText));

            // PKCS7 unpadding for the decrypted data
            byte[] unpaddedData = pkcs7Unpadding(decryptedData);
            String plainText = new String(unpaddedData, StandardCharsets.UTF_8);

            // Return the decrypted data as a response
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

    private Pair<byte[], byte[]> derivedKey(byte[] secretKey, byte[] salt) {
        byte[] derivedKey = SCrypt.generate(secretKey, salt, SCRYPT_INTERATIONS_COUNT, SCRYPT_BLOCK_SIZE, SCRYPT_PARALLELISM_FACTOR, KEY_LENGTH);
        byte[] encryptKey = new byte[AES_KEY_LENGTH];
        System.arraycopy(derivedKey, 0, encryptKey, 0, AES_KEY_LENGTH);
        byte[] hmacKey = new byte[HMAC_SHA_KEY_LENGTH];
        System.arraycopy(derivedKey, AES_KEY_LENGTH, hmacKey, 0, HMAC_SHA_KEY_LENGTH);
        return new Pair<>(encryptKey, hmacKey);
    }

    private byte[] calculateHMAC(byte[] data, byte[] hmacKey, String algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            Key key = new SecretKeySpec(hmacKey, algorithm);
            mac.init(key);
            mac.update(data);
            return mac.doFinal();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
