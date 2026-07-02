package com.app.ventas.repository;

import com.app.ventas.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByEstadoTrue();
    List<Producto> findByNombreProductoContainingIgnoreCaseAndEstadoTrue(String nombre);
}
