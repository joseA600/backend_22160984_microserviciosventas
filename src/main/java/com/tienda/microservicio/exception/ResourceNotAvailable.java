package com.tienda.microservicio.exception;

public class ResourceNotAvailable extends RuntimeException {

    public ResourceNotAvailable(String mensaje) {
        super(mensaje);
    }
}
