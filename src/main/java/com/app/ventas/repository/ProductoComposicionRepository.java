package com.app.ventas.repository;

import com.app.ventas.entity.ProductoComposicion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoComposicionRepository extends JpaRepository<ProductoComposicion, Integer> {
    List<ProductoComposicion> findByProductoPack_CodProducto(Integer codProductoPack);
    void deleteByProductoPack_CodProducto(Integer codProductoPack);
}
