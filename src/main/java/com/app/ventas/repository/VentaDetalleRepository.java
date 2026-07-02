package com.app.ventas.repository;

import com.app.ventas.entity.VentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Integer> {
    List<VentaDetalle> findByVentaCodVenta(Integer codVenta);
}
