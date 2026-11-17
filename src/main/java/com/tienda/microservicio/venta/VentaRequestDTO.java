package com.tienda.microservicio.venta;

import java.util.List;

public record VentaRequestDTO(
        String cliente,
        List<DetalleVentaDTO> detalles
) {

    public record DetalleVentaDTO(
            Long productoId,
            Integer cantidad
    ) {}
}
