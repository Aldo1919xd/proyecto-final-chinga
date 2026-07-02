package com.app.ventas.repository;

import com.app.ventas.entity.RolFuncionalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RolFuncionalidadRepository extends JpaRepository<RolFuncionalidad, Integer> {
    List<RolFuncionalidad> findByRolIdRol(Integer idRol);
    Optional<RolFuncionalidad> findByRolIdRolAndFuncionalidadIdFuncionalidad(Integer idRol, Integer idFuncionalidad);
    void deleteByRolIdRol(Integer idRol);
}
