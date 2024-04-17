package com.haiphamcoder.cryptography.layer.domain.service;

import com.haiphamcoder.cryptography.layer.domain.entity.DecryptedInfoResponse;
import com.haiphamcoder.cryptography.layer.domain.entity.EncryptedInfoResponse;

public interface IAESCryptographyService {
    EncryptedInfoResponse encrypt(String plainText, String secretKey);
    DecryptedInfoResponse decrypt(String cipherText, String secretKey, String salt, String iv, String mac);
}
