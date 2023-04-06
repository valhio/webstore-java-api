package com.github.valhio.storeapi.exception.domain;

public class PasswordNotMatchException extends Exception {
    public PasswordNotMatchException(String message) {
        super(message);
    }
}
