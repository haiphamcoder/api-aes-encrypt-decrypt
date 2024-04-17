package com.haiphamcoder.cryptography.layer.domain.service;

public interface IAESCryptographyService {
    String encrypt(String plainText, String secretKey);
    String decrypt(String cipherText, String secretKey);
}
