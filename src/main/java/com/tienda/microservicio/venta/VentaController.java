package com.tienda.microservicio.venta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Ventas", description = "Registro y consulta de ventas con detalle")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @Operation(summary = "Listar ventas", description = "Lista todas las ventas. Admite filtro por cliente.")
    @GetMapping
    public ResponseEntity<List<Venta>> listar(@RequestParam(required = false) String cliente) {
        if (cliente != null && !cliente.isEmpty()) {
            return ResponseEntity.ok(ventaService.buscarPorCliente(cliente));
        }
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @Operation(summary = "Obtener venta por ID con su detalle")
    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscarPorId(@PathVariable Long id) {
        return ventaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.tienda.microservicio.exception.ResourceNotAvailable(
                        "No se encontro la venta con ID: " + id));
    }

    @Operation(summary = "Registrar una venta", description = "Descuenta el stock de cada producto y calcula el total.")
    @PostMapping
    public ResponseEntity<Venta> registrar(@Valid @RequestBody VentaRequestDTO request) {
        Venta venta = ventaService.registrar(request);
        return new ResponseEntity<>(venta, HttpStatus.CREATED);
    }

    @Operation(summary = "Cancelar una venta", description = "Devuelve el stock de los productos y marca la venta como CANCELADA.")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Venta> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.cancelar(id));
    }

    @Operation(summary = "Eliminar una venta", description = "Elimina la venta y su detalle de la base de datos.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reporte: total de ventas completadas")
    @GetMapping("/reportes/total")
    public ResponseEntity<Map<String, BigDecimal>> totalVentas() {
        return ResponseEntity.ok(Map.of("total", ventaService.totalVentasCompletadas()));
    }
}
