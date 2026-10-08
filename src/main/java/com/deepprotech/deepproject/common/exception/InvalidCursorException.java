package com.deepprotech.deepproject.common.exception;

public class InvalidCursorException extends RuntimeException {

    public InvalidCursorException(String cursor) {
        super("Invalid cursor: " + cursor);
    }
}
