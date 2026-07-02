package com.app.ventas.service;

import com.app.ventas.entity.Producto;
import com.app.ventas.entity.Usuario;
import com.app.ventas.repository.ProductoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final AuditoriaService auditoriaService;

    public ProductoService(ProductoRepository productoRepository, AuditoriaService auditoriaService) {
        this.productoRepository = productoRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<Producto> listarActivos() {
        return productoRepository.findByEstadoTrue();
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreProductoContainingIgnoreCaseAndEstadoTrue(nombre);
    }

    @Transactional
    public Producto guardar(Producto producto, Usuario usuarioActual, HttpServletRequest request) {
        boolean esNuevo = producto.getCodProducto() == null;
        Producto guardado = productoRepository.save(producto);
        auditoriaService.registrar(usuarioActual, "Maestras", "Producto",
                esNuevo ? "INSERT" : "UPDATE",
                guardado.getCodProducto(),
                esNuevo ? null : "{\"nombre\":\"" + producto.getNombreProducto() + "\"}",
                "{\"nombre\":\"" + guardado.getNombreProducto() + "\"}",
                request);
        return guardado;
    }

    @Transactional
    public void eliminarLogico(Integer id, Usuario usuarioActual, HttpServletRequest request) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setEstado(false);
        productoRepository.save(producto);
        auditoriaService.registrar(usuarioActual, "Maestras", "Producto",
                "DELETE", id, "{\"estado\":true}", "{\"estado\":false}", request);
    }
}
