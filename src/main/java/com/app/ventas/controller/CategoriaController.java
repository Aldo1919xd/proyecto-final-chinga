package com.app.ventas.controller;

import com.app.ventas.entity.Categoria;
import com.app.ventas.entity.Usuario;
import com.app.ventas.service.CategoriaService;
import com.app.ventas.service.PermisoService;
import com.app.ventas.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final PermisoService permisoService;

    public CategoriaController(CategoriaService categoriaService, UsuarioService usuarioService,
                               PermisoService permisoService) {
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.permisoService = permisoService;
    }

    @GetMapping
    public String listar(Authentication auth, Model model) {
        if (!permisoService.tieneVer(auth, "Categorias")) return "redirect:/inicio?error=sinPermiso";
        model.addAttribute("categorias", categoriaService.listarTodos());
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Authentication auth, Model model) {
        if (!permisoService.tieneCrear(auth, "Categorias")) return "redirect:/categorias?error=sinPermiso";
        model.addAttribute("categoria", new Categoria());
        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Categoria categoria, BindingResult result,
                          Authentication auth, HttpServletRequest request) {
        boolean esNuevo = categoria.getCodCategoria() == null;
        if (esNuevo && !permisoService.tieneCrear(auth, "Categorias")) return "redirect:/categorias?error=sinPermiso";
        if (!esNuevo && !permisoService.tieneEditar(auth, "Categorias")) return "redirect:/categorias?error=sinPermiso";
        if (result.hasErrors()) return "categorias/formulario";
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        categoriaService.guardar(categoria, actual, request);
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Authentication auth, Model model) {
        if (!permisoService.tieneEditar(auth, "Categorias")) return "redirect:/categorias?error=sinPermiso";
        model.addAttribute("categoria", categoriaService.buscarPorId(id).orElseThrow());
        return "categorias/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth, HttpServletRequest request) {
        if (!permisoService.tieneEliminar(auth, "Categorias")) return "redirect:/categorias?error=sinPermiso";
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        categoriaService.eliminarLogico(id, actual, request);
        return "redirect:/categorias";
    }
}
