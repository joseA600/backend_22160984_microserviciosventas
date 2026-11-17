package com.tienda.microservicio.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String detalle,
        String ruta,
        Map<String, String> erroresCampos
) {}
