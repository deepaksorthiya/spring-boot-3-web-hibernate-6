package com.example.global.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final long userId;

    public ResourceNotFoundException(long userId) {
        this.userId = userId;
    }
}
