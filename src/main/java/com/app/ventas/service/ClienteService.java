package com.app.ventas.service;

import com.app.ventas.entity.Cliente;
import com.app.ventas.entity.Usuario;
import com.app.ventas.repository.ClienteRepository;
import com.app.ventas.util.EncryptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    public ClienteService(ClienteRepository clienteRepository, AuditoriaService auditoriaService) {
        this.clienteRepository = clienteRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<Cliente> listarActivos() {
        List<Cliente> clientes = clienteRepository.findByEstadoTrue();
        return clientes.stream().map(this::descifrarCliente).collect(Collectors.toList());
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id).map(this::descifrarCliente);
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreClienteContainingIgnoreCaseAndEstadoTrue(nombre)
                .stream().map(this::descifrarCliente).collect(Collectors.toList());
    }

    @Transactional
    public Cliente guardar(Cliente cliente, Usuario usuarioActual, HttpServletRequest request) {
        boolean esNuevo = cliente.getCodCliente() == null;
        cliente.setNumeroDocumento(EncryptionUtil.cifrar(cliente.getNumeroDocumento()));
        if (cliente.getFechaNacimiento() != null && !cliente.getFechaNacimiento().isEmpty()) {
            cliente.setFechaNacimiento(EncryptionUtil.cifrar(cliente.getFechaNacimiento()));
        }
        Cliente guardado = clienteRepository.save(cliente);
        auditoriaService.registrar(usuarioActual, "Maestras", "Cliente",
                esNuevo ? "INSERT" : "UPDATE",
                guardado.getCodCliente(),
                esNuevo ? null : "{\"nombre\":\"" + cliente.getNombreCliente() + "\"}",
                "{\"nombre\":\"" + guardado.getNombreCliente() + "\"}",
                request);
        return descifrarCliente(guardado);
    }

    @Transactional
    public void eliminarLogico(Integer id, Usuario usuarioActual, HttpServletRequest request) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow();
        cliente.setEstado(false);
        clienteRepository.save(cliente);
        auditoriaService.registrar(usuarioActual, "Maestras", "Cliente",
                "DELETE", id, "{\"estado\":true}", "{\"estado\":false}", request);
    }

    private Cliente descifrarCliente(Cliente c) {
        try {
            c.setNumeroDocumento(EncryptionUtil.descifrar(c.getNumeroDocumento()));
            if (c.getFechaNacimiento() != null && !c.getFechaNacimiento().isEmpty()) {
                c.setFechaNacimiento(EncryptionUtil.descifrar(c.getFechaNacimiento()));
            }
        } catch (Exception e) {
            // ya está descifrado o es texto plano
        }
        return c;
    }
}
