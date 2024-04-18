package com.haiphamcoder.cryptography.layer.domain.service;

import com.haiphamcoder.cryptography.layer.domain.entity.DecryptedInfoResponse;
import com.haiphamcoder.cryptography.layer.domain.entity.EncryptedInfoResponse;

public interface IAESCryptographyService {
    EncryptedInfoResponse encrypt(String plainText);

    DecryptedInfoResponse decrypt(String cipherText, String mac);
}
