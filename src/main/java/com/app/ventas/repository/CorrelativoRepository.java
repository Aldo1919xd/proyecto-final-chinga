package com.app.ventas.repository;

import com.app.ventas.entity.Correlativo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CorrelativoRepository extends JpaRepository<Correlativo, Integer> {
    Optional<Correlativo> findByTipoComprobanteAndSerie(String tipoComprobante, String serie);
}
