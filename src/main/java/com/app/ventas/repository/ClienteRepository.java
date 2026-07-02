package com.app.ventas.repository;

import com.app.ventas.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByEstadoTrue();
    List<Cliente> findByNombreClienteContainingIgnoreCaseAndEstadoTrue(String nombre);
}
