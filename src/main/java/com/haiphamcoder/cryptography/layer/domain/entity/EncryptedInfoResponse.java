package com.haiphamcoder.cryptography.layer.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedInfoResponse {
    @JsonProperty(value = "error_message")
    private String messate = null;

    @JsonProperty(value = "error_code")
    private int code = 0;

    @JsonProperty(value = "data")
    private EncryptedInfoData data = null;

    public EncryptedInfoResponse(String message, int code) {
        this.messate = message;
        this.code = code;
        this.data = null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EncryptedInfoData {
        @JsonProperty(value = "encrypted_data")
        private String encryptedData = null;

        @JsonProperty(value = "mac")
        private String mac = null;
    }
}
