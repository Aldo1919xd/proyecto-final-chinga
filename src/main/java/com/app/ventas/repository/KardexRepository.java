package com.app.ventas.repository;

import com.app.ventas.entity.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KardexRepository extends JpaRepository<Kardex, Integer> {
    List<Kardex> findByProductoCodProductoOrderByFechaHoraAsc(Integer codProducto);
}
