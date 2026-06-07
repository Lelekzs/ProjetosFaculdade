package com.hardsoft.sistema.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entidade, Long id) {
        super(entidade + " com id " + id + " nao encontrado(a).");
    }
}
