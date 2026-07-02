package com.app.ventas.repository;

import com.app.ventas.entity.VentaCabecera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VentaCabeceraRepository extends JpaRepository<VentaCabecera, Integer> {
    List<VentaCabecera> findByEstadoTrueOrderByFechaHoraDesc();
}
