package com.haiphamcoder.cryptography.layer.application.presentation;

import com.haiphamcoder.cryptography.layer.domain.service.IAESCryptographyService;
import com.haiphamcoder.cryptography.utils.Response;
import com.haiphamcoder.cryptography.utils.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{data}")
    public Response<?> encrypt(
            @PathVariable(value = "data") String data
    ) {
        return ResponseFactory.getSuccessResponse("Successful!", aesCryptographyService.encrypt(data));
    }
}
