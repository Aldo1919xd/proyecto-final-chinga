package com.app.ventas.controller;

import com.app.ventas.entity.Categoria;
import com.app.ventas.entity.Usuario;
import com.app.ventas.service.CategoriaService;
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

    public CategoriaController(CategoriaService categoriaService, UsuarioService usuarioService) {
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodos());
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Categoria categoria, BindingResult result,
                          Authentication auth, HttpServletRequest request) {
        if (result.hasErrors()) return "categorias/formulario";
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        categoriaService.guardar(categoria, actual, request);
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("categoria", categoriaService.buscarPorId(id).orElseThrow());
        return "categorias/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth, HttpServletRequest request) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        categoriaService.eliminarLogico(id, actual, request);
        return "redirect:/categorias";
    }
}
