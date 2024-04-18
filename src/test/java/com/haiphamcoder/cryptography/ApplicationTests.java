package com.haiphamcoder.cryptography;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootTest
class ApplicationTests {
    @Test
    void Test(){
        System.out.println(Base64.getEncoder().encodeToString(generateSalt()));
    }

    private byte[] generateSalt() {
        // Generate a random salt
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }
}
