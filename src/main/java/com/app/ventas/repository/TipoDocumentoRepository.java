package com.app.ventas.repository;

import com.app.ventas.entity.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Integer> {
    List<TipoDocumento> findByEstadoTrue();
}
