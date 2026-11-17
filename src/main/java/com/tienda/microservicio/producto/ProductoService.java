package com.tienda.microservicio.producto;

import com.tienda.microservicio.exception.ResourceNotAvailable;
import com.tienda.microservicio.producto.dto.ProductoRequestDTO;
import com.tienda.microservicio.producto.dto.ProductoResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        return toResponseDTO(buscarEntidadPorId(id));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProductoResponseDTO saveProducto(ProductoRequestDTO requestDTO) {
        Producto nuevoProducto = toEntity(requestDTO);
        return toResponseDTO(productoRepository.save(nuevoProducto));
    }

    @Transactional
    public ProductoResponseDTO updateProducto(Long id, ProductoRequestDTO requestDTO) {
        Producto productoExistente = buscarEntidadPorId(id);

        productoExistente.setNombre(requestDTO.nombre());
        productoExistente.setDescripcion(requestDTO.descripcion());
        productoExistente.setPrecio(requestDTO.precio());
        productoExistente.setStock(requestDTO.stock());
        productoExistente.setCategoria(requestDTO.categoria());

        return toResponseDTO(productoRepository.save(productoExistente));
    }

    @Transactional
    public void deleteProducto(Long id) {
        Producto productoExistente = buscarEntidadPorId(id);
        productoRepository.delete(productoExistente);
    }

    @Transactional
    public ProductoResponseDTO cambiarEstado(Long id, boolean activo) {
        Producto producto = buscarEntidadPorId(id);
        producto.setActivo(activo);
        return toResponseDTO(productoRepository.save(producto));
    }

    public void descontarStock(Long productoId, Integer cantidad) {
        Producto producto = buscarEntidadPorId(productoId);

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0 para '" + producto.getNombre() + "'");
        }
        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto '" + producto.getNombre()
                    + "'. Disponible: " + producto.getStock());
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    public void devolverStock(Long productoId, Integer cantidad) {
        Producto producto = buscarEntidadPorId(productoId);
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);
    }

    public Producto obtenerEntidadPorId(Long id) {
        return buscarEntidadPorId(id);
    }

    private Producto buscarEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotAvailable(
                        "No se encontro el producto con ID: " + id));
    }

    private Producto toEntity(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(dto.precio());
        producto.setStock(dto.stock() != null ? dto.stock() : 0);
        producto.setCategoria(dto.categoria());
        return producto;
    }

    private ProductoResponseDTO toResponseDTO(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria(),
                producto.getActivo());
    }
}
