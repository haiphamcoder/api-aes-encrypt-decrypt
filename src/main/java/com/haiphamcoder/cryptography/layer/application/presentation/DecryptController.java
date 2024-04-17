package com.haiphamcoder.cryptography.layer.application.presentation;

import com.haiphamcoder.cryptography.layer.domain.service.IAESCryptographyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("decrypt")
public class DecryptController {
    private final IAESCryptographyService aesCryptographyService;

    public DecryptController(IAESCryptographyService aesCryptographyService) {
        this.aesCryptographyService = aesCryptographyService;
    }
}
