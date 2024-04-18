package com.haiphamcoder.cryptography.layer.application.presentation;

import com.haiphamcoder.cryptography.layer.domain.service.IAESCryptographyService;
import com.haiphamcoder.cryptography.utils.Response;
import com.haiphamcoder.cryptography.utils.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("decrypt")
public class DecryptController {
    private final IAESCryptographyService aesCryptographyService;

    public DecryptController(IAESCryptographyService aesCryptographyService) {
        this.aesCryptographyService = aesCryptographyService;
    }

    @GetMapping("/{data}")
    public Response<?> decrypt(
            @RequestHeader Map<String, String> requestHeaders,
            @PathVariable(value = "data") String data
    ) {
        String mac = requestHeaders.get("mac");
        return ResponseFactory.getSuccessResponse("Successful!", aesCryptographyService.decrypt(data, mac));
    }
}
