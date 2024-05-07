package com.example.social_network.exceptions;

public class InvalidJwtAuthenticationException extends RuntimeException {
    public InvalidJwtAuthenticationException(String e) {
        super(e);
    }
}
