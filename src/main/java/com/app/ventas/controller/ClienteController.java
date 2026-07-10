package com.app.ventas.controller;

import com.app.ventas.entity.Cliente;
import com.app.ventas.entity.TipoDocumento;
import com.app.ventas.entity.Usuario;
import com.app.ventas.service.ClienteService;
import com.app.ventas.service.PermisoService;
import com.app.ventas.service.TipoDocumentoService;
import com.app.ventas.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final TipoDocumentoService tipoDocumentoService;
    private final UsuarioService usuarioService;
    private final PermisoService permisoService;

    public ClienteController(ClienteService clienteService, TipoDocumentoService tipoDocumentoService,
                             UsuarioService usuarioService, PermisoService permisoService) {
        this.clienteService = clienteService;
        this.tipoDocumentoService = tipoDocumentoService;
        this.usuarioService = usuarioService;
        this.permisoService = permisoService;
    }

    @GetMapping
    public String listar(Authentication auth, Model model) {
        if (!permisoService.tieneVer(auth, "Clientes")) return "redirect:/inicio?error=sinPermiso";
        model.addAttribute("clientes", clienteService.listarActivos());
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Authentication auth, Model model) {
        if (!permisoService.tieneCrear(auth, "Clientes")) return "redirect:/clientes?error=sinPermiso";
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("tiposDocumento", tipoDocumentoService.listarActivos());
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
                          Authentication auth, HttpServletRequest request) {
        boolean esNuevo = cliente.getCodCliente() == null;
        if (esNuevo && !permisoService.tieneCrear(auth, "Clientes")) return "redirect:/clientes?error=sinPermiso";
        if (!esNuevo && !permisoService.tieneEditar(auth, "Clientes")) return "redirect:/clientes?error=sinPermiso";
        if (cliente.getNombreCliente() == null || cliente.getNombreCliente().isBlank()) {
            if (cliente.getRazonSocial() == null || cliente.getRazonSocial().isBlank()) {
                result.rejectValue("nombreCliente", "error.cliente",
                        "Debe ingresar al menos un nombre o razon social");
            }
        }

        if (cliente.getNumeroDocumento() != null && cliente.getTipoDocumento() != null
                && cliente.getTipoDocumento().getCodTipoDocumento() != null) {
            Integer tipoId = cliente.getTipoDocumento().getCodTipoDocumento();
            String doc = cliente.getNumeroDocumento();
            String descripcion = switch (tipoId) {
                case 1 -> "DNI";
                case 2 -> "RUC";
                case 3 -> "CE";
                default -> null;
            };
            if ("DNI".equals(descripcion) && !doc.matches("\\d{8}")) {
                result.rejectValue("numeroDocumento", "error.cliente",
                        "El DNI debe tener exactamente 8 digitos");
            } else if ("RUC".equals(descripcion) && !doc.matches("\\d{11}")) {
                result.rejectValue("numeroDocumento", "error.cliente",
                        "El RUC debe tener exactamente 11 digitos");
            } else if ("CE".equals(descripcion) && !doc.matches("[a-zA-Z0-9]{1,12}")) {
                result.rejectValue("numeroDocumento", "error.cliente",
                        "El CE debe tener maximo 12 caracteres alfanumericos");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("tiposDocumento", tipoDocumentoService.listarActivos());
            return "clientes/formulario";
        }
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        try {
            clienteService.guardar(cliente, actual, request);
        } catch (RuntimeException e) {
            model.addAttribute("tiposDocumento", tipoDocumentoService.listarActivos());
            model.addAttribute("error", e.getMessage());
            return "clientes/formulario";
        }
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Authentication auth, Model model) {
        if (!permisoService.tieneEditar(auth, "Clientes")) return "redirect:/clientes?error=sinPermiso";
        model.addAttribute("cliente", clienteService.buscarPorId(id).orElseThrow());
        model.addAttribute("tiposDocumento", tipoDocumentoService.listarActivos());
        return "clientes/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth, HttpServletRequest request) {
        if (!permisoService.tieneEliminar(auth, "Clientes")) return "redirect:/clientes?error=sinPermiso";
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        clienteService.eliminarLogico(id, actual, request);
        return "redirect:/clientes";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<?> buscar(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(clienteService.listarActivos());
        }
        return ResponseEntity.ok(clienteService.buscarPorNombre(q));
    }
}
