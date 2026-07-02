package com.app.ventas.controller;

import com.app.ventas.entity.Usuario;
import com.app.ventas.service.RolService;
import com.app.ventas.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("user", new Usuario());
        model.addAttribute("roles", rolService.listarActivos());
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("user") Usuario usuario, BindingResult result,
                          Model model, Authentication auth, HttpServletRequest request) {
        boolean esNuevo = usuario.getIdUsuario() == null;

        if (esNuevo && (usuario.getPassword() == null || usuario.getPassword().isBlank())) {
            result.rejectValue("password", "error.usuario", "La contrasena es obligatoria");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.listarActivos());
            return "usuarios/formulario";
        }
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        usuarioService.guardar(usuario, actual, request);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("user", usuarioService.buscarPorId(id).orElseThrow());
        model.addAttribute("roles", rolService.listarActivos());
        return "usuarios/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth, HttpServletRequest request) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        usuarioService.eliminarLogico(id, actual, request);
        return "redirect:/usuarios";
    }

    @GetMapping("/cambiar-password")
    public String cambiarPasswordForm() {
        return "usuarios/cambiar-password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordNueva,
                                  Authentication auth, HttpServletRequest request) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        usuarioService.cambiarPassword(actual.getIdUsuario(), passwordNueva, actual, request);
        return "redirect:/inicio";
    }
}
