package com.app.ventas.repository;

import com.app.ventas.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    List<Auditoria> findByTablaAfectadaOrderByFechaHoraDesc(String tablaAfectada);
}
