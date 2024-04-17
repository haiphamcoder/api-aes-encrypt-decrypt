package com.haiphamcoder.cryptography.layer.domain.service.impl;

import com.haiphamcoder.cryptography.layer.domain.service.IAESCryptographyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AESCryptographyService implements IAESCryptographyService {
    @Override
    public String encrypt(String plainText, String secretKey) {
        return null;
    }

    @Override
    public String decrypt(String cipherText, String secretKey) {
        return null;
    }
}
