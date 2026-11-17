package com.tienda.microservicio.venta;

import com.tienda.microservicio.exception.ResourceNotAvailable;
import com.tienda.microservicio.producto.Producto;
import com.tienda.microservicio.producto.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoService productoService;

    public VentaService(VentaRepository ventaRepository,
                        DetalleVentaRepository detalleVentaRepository,
                        ProductoService productoService) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoService = productoService;
    }

    @Transactional(readOnly = true)
    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Venta> buscarPorCliente(String cliente) {
        return ventaRepository.findByClienteContainingIgnoreCase(cliente);
    }

    @Transactional
    public Venta registrar(VentaRequestDTO request) {
        if (request.cliente() == null || request.cliente().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (request.detalles() == null || request.detalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto");
        }

        Venta venta = new Venta(request.cliente().trim());
        BigDecimal total = BigDecimal.ZERO;

        for (VentaRequestDTO.DetalleVentaDTO item : request.detalles()) {
            Producto producto = productoService.obtenerEntidadPorId(item.productoId());

            productoService.descontarStock(producto.getId(), item.cantidad());

            DetalleVenta detalle = new DetalleVenta(
                    producto.getId(),
                    producto.getNombre(),
                    item.cantidad(),
                    producto.getPrecio());

            venta.agregarDetalle(detalle);
            total = total.add(detalle.getSubtotal());
        }

        venta.setTotal(total);
        return ventaRepository.save(venta);
    }

    @Transactional
    public Venta cancelar(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotAvailable("No se encontro la venta con ID: " + id));

        if ("CANCELADA".equalsIgnoreCase(venta.getEstado())) {
            throw new IllegalArgumentException("La venta ya esta cancelada");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            productoService.devolverStock(detalle.getProductoId(), detalle.getCantidad());
        }

        venta.setEstado("CANCELADA");
        return ventaRepository.save(venta);
    }

    @Transactional
    public void eliminar(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotAvailable("No se encontro la venta con ID: " + id));
        ventaRepository.delete(venta);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalVentasCompletadas() {
        return ventaRepository.findByEstado("COMPLETADA").stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
