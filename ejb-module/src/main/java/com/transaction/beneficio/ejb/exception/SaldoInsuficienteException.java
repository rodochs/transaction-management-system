package com.transaction.beneficio.ejb.exception;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
