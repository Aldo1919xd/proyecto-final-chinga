package com.app.ventas.controller;

import com.app.ventas.entity.Producto;
import com.app.ventas.entity.Usuario;
import com.app.ventas.service.CategoriaService;
import com.app.ventas.service.ProductoService;
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
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService,
                              UsuarioService usuarioService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarActivos());
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Producto producto, BindingResult result, Model model,
                          Authentication auth, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarActivos());
            return "productos/formulario";
        }
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        productoService.guardar(producto, actual, request);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id).orElseThrow());
        model.addAttribute("categorias", categoriaService.listarActivos());
        return "productos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth, HttpServletRequest request) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        productoService.eliminarLogico(id, actual, request);
        return "redirect:/productos";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<?> buscar(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(productoService.listarActivos());
        }
        return ResponseEntity.ok(productoService.buscarPorNombre(q));
    }
}
