package com.haiphamcoder.cryptography.layer.application.presentation;

import com.haiphamcoder.cryptography.layer.domain.service.IAESCryptographyService;
import com.haiphamcoder.cryptography.utils.Response;
import com.haiphamcoder.cryptography.utils.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            @RequestHeader Map<String, String> requestHeaders,
            @PathVariable(value = "data") String data
    ) {
        String secretKey = requestHeaders.get("secret-key");
        return ResponseFactory.getSuccessResponse("Successful!", aesCryptographyService.encrypt(data, secretKey));
    }
}
