package com.app.ventas.repository;

import com.app.ventas.entity.IngresoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngresoProductoRepository extends JpaRepository<IngresoProducto, Integer> {
    List<IngresoProducto> findByEstadoTrueOrderByFechaHoraDesc();
}
