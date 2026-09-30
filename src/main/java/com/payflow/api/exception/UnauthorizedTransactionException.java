package com.payflow.api.exception;

public class UnauthorizedTransactionException extends BusinessException {
    public UnauthorizedTransactionException(String message) {
        super(message);
    }
}
