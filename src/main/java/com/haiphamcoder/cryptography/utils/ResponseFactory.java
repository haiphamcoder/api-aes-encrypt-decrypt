package com.haiphamcoder.cryptography.utils;

public class ResponseFactory {
    public static Response<?> getSuccessResponse(String message, Object entity) {
        return Response.builder()
                .code(Response.OK_CODE)
                .status(Response.OK_STATUS)
                .message(message)
                .data(entity)
                .build();
    }

    public static Response<?> getSuccessResponse(String message) {
        return Response.builder()
                .code(Response.OK_CODE)
                .status(Response.OK_STATUS)
                .message(message)
                .data(null)
                .build();
    }

    public static Response<?> getServerErrorResponse(String message, Object entities) {
        return Response.builder()
                .code(Response.ERROR_CODE)
                .status(Response.ERROR_STATUS)
                .message(message)
                .data(entities)
                .build();
    }

    public static Response<?> getServerErrorResponse(String message) {
        return Response.builder()
                .code(Response.ERROR_CODE)
                .status(Response.ERROR_STATUS)
                .message(message)
                .data(null)
                .build();
    }
}
