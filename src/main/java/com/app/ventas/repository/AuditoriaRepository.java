package com.app.ventas.repository;

import com.app.ventas.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    List<Auditoria> findAllByOrderByFechaHoraDesc();
    List<Auditoria> findByModuloOrderByFechaHoraDesc(String modulo);
    List<Auditoria> findByOperacionOrderByFechaHoraDesc(String operacion);
    List<Auditoria> findByModuloAndOperacionOrderByFechaHoraDesc(String modulo, String operacion);
    List<Auditoria> findByTablaAfectadaOrderByFechaHoraDesc(String tablaAfectada);
}
