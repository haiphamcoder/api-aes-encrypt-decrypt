package com.haiphamcoder.cryptography.layer.application.presentation;

import com.haiphamcoder.cryptography.layer.domain.service.IAESCryptographyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("encrypt")
public class EncryptController {
    private final IAESCryptographyService aesCryptographyService;

    public EncryptController(IAESCryptographyService aesCryptographyService) {
        this.aesCryptographyService = aesCryptographyService;
    }
}
