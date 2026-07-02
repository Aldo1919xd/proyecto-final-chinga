package com.app.ventas.controller;

import com.app.ventas.entity.Cliente;
import com.app.ventas.entity.Usuario;
import com.app.ventas.service.ClienteService;
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

import java.util.Map;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final TipoDocumentoService tipoDocumentoService;
    private final UsuarioService usuarioService;

    public ClienteController(ClienteService clienteService, TipoDocumentoService tipoDocumentoService,
                             UsuarioService usuarioService) {
        this.clienteService = clienteService;
        this.tipoDocumentoService = tipoDocumentoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarActivos());
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("tiposDocumento", tipoDocumentoService.listarActivos());
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
                          Authentication auth, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("tiposDocumento", tipoDocumentoService.listarActivos());
            return "clientes/formulario";
        }
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        clienteService.guardar(cliente, actual, request);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id).orElseThrow());
        model.addAttribute("tiposDocumento", tipoDocumentoService.listarActivos());
        return "clientes/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth, HttpServletRequest request) {
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
