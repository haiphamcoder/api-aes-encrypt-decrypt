package com.haiphamcoder.cryptography.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public class Response<E> {
    public static final int OK_CODE = 200;
    public static final int ERROR_CODE = 500;
    public static final int OK_STATUS = 1;
    public static final int ERROR_STATUS = 0;

    private String message;
    private Integer code;
    private Integer status;
    private E data;
}
