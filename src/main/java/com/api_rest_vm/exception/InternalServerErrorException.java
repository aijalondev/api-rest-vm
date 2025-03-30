package com.api_rest_vm.exception;

public class InternalServerErrorException  extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
    }
}
