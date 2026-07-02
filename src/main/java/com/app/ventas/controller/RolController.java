package com.app.ventas.controller;

import com.app.ventas.entity.Rol;
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

import java.util.List;

@Controller
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;
    private final UsuarioService usuarioService;

    public RolController(RolService rolService, UsuarioService usuarioService) {
        this.rolService = rolService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("roles", rolService.listarTodos());
        return "roles/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("rol", new Rol());
        return "roles/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Rol rol, BindingResult result,
                          Authentication auth, HttpServletRequest request) {
        if (result.hasErrors()) return "roles/formulario";
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        rolService.guardar(rol, usuario, request);
        return "redirect:/roles";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("rol", rolService.buscarPorId(id).orElseThrow());
        return "roles/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth, HttpServletRequest request) {
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        rolService.eliminarLogico(id, usuario, request);
        return "redirect:/roles";
    }

    @GetMapping("/permisos/{idRol}")
    public String permisos(@PathVariable Integer idRol, Model model) {
        model.addAttribute("rol", rolService.buscarPorId(idRol).orElseThrow());
        model.addAttribute("arbol", rolService.listarArbol());
        model.addAttribute("permisosMap", rolService.listarPermisosMap(idRol));
        return "roles/permisos";
    }

    @PostMapping("/permisos/{idRol}/guardar")
    public String guardarPermisos(@PathVariable Integer idRol,
                                  @RequestParam(required = false) List<Integer> ver,
                                  @RequestParam(required = false) List<Integer> crear,
                                  @RequestParam(required = false) List<Integer> editar,
                                  @RequestParam(required = false) List<Integer> eliminar,
                                  @RequestParam(required = false) List<Integer> imprimir,
                                  Authentication auth, HttpServletRequest request) {
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        rolService.guardarPermisos(idRol, ver, crear, editar, eliminar, imprimir, usuario, request);
        return "redirect:/roles/permisos/" + idRol;
    }
}
