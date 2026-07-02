package com.app.ventas.repository;

import com.app.ventas.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    List<Rol> findByEstadoTrue();
}
