package com.app.ventas.service;

import com.app.ventas.entity.Cliente;
import com.app.ventas.entity.Usuario;
import com.app.ventas.repository.ClienteRepository;
import com.app.ventas.util.EncryptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;
    private final EncryptionUtil encryptionUtil;

    public ClienteService(ClienteRepository clienteRepository, AuditoriaService auditoriaService,
                          EncryptionUtil encryptionUtil) {
        this.clienteRepository = clienteRepository;
        this.auditoriaService = auditoriaService;
        this.encryptionUtil = encryptionUtil;
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

        if (!esNuevo) {
            Cliente existente = clienteRepository.findById(cliente.getCodCliente()).orElse(null);
            if (existente != null) {
                boolean docEncrypted = estaEncriptado(existente.getNumeroDocumento());
                String docActual = docEncrypted ? encryptionUtil.descifrar(existente.getNumeroDocumento()) : existente.getNumeroDocumento();
                if (cliente.getNumeroDocumento().equals(docActual)) {
                    cliente.setNumeroDocumento(estaEncriptado(existente.getNumeroDocumento())
                            ? existente.getNumeroDocumento() : encryptionUtil.cifrar(cliente.getNumeroDocumento()));
                } else {
                    cliente.setNumeroDocumento(encryptionUtil.cifrar(cliente.getNumeroDocumento()));
                }

                String fechaGuardada = existente.getFechaNacimiento();
                if (fechaGuardada != null && !fechaGuardada.isEmpty()) {
                    boolean fechaEncrypted = estaEncriptado(fechaGuardada);
                    String fechaDescifrada = fechaEncrypted ? encryptionUtil.descifrar(fechaGuardada) : fechaGuardada;
                    if (cliente.getFechaNacimiento() != null && !cliente.getFechaNacimiento().isEmpty()) {
                        if (cliente.getFechaNacimiento().equals(fechaDescifrada)) {
                            cliente.setFechaNacimiento(fechaEncrypted ? fechaGuardada : encryptionUtil.cifrar(cliente.getFechaNacimiento()));
                        } else {
                            cliente.setFechaNacimiento(encryptionUtil.cifrar(cliente.getFechaNacimiento()));
                        }
                    }
                } else if (cliente.getFechaNacimiento() != null && !cliente.getFechaNacimiento().isEmpty()) {
                    cliente.setFechaNacimiento(encryptionUtil.cifrar(cliente.getFechaNacimiento()));
                }
            } else {
                cifrarCampos(cliente);
            }
        } else {
            cifrarCampos(cliente);
        }

        Cliente guardado;
        try {
            guardado = clienteRepository.save(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Ya existe un cliente con ese tipo y numero de documento", e);
        }

        auditoriaService.registrar(usuarioActual, "Maestras", "Cliente",
                esNuevo ? "INSERT" : "UPDATE",
                guardado.getCodCliente(),
                esNuevo ? null : "{\"nombre\":\"" + cliente.getNombreCliente() + "\"}",
                "{\"nombre\":\"" + guardado.getNombreCliente() + "\"}",
                request);
        return guardado;
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
            c.setNumeroDocumento(encryptionUtil.descifrar(c.getNumeroDocumento()));
        } catch (Exception ignored) {}
        try {
            if (c.getFechaNacimiento() != null && !c.getFechaNacimiento().isEmpty()) {
                c.setFechaNacimiento(encryptionUtil.descifrar(c.getFechaNacimiento()));
            }
        } catch (Exception ignored) {}
        return c;
    }

    private boolean estaEncriptado(String texto) {
        try {
            encryptionUtil.descifrar(texto);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void cifrarCampos(Cliente cliente) {
        cliente.setNumeroDocumento(encryptionUtil.cifrar(cliente.getNumeroDocumento()));
        if (cliente.getFechaNacimiento() != null && !cliente.getFechaNacimiento().isEmpty()) {
            cliente.setFechaNacimiento(encryptionUtil.cifrar(cliente.getFechaNacimiento()));
        }
    }
}
