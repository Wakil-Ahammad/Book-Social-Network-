package com.example.demo.exception;

public class OperationNotPermittedException extends RuntimeException {

    public OperationNotPermittedException(String msg) {
        super(msg);
    }
}
