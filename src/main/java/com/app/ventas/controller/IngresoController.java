package com.app.ventas.controller;

import com.app.ventas.entity.IngresoProducto;
import com.app.ventas.entity.Usuario;
import com.app.ventas.service.IngresoService;
import com.app.ventas.service.ProductoService;
import com.app.ventas.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ingresos")
public class IngresoController {

    private final IngresoService ingresoService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ingresos", ingresoService.listarTodos());
        return "ingresos/lista";
    }

    public IngresoController(IngresoService ingresoService, ProductoService productoService,
                             UsuarioService usuarioService) {
        this.ingresoService = ingresoService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ingreso", new IngresoProducto());
        model.addAttribute("productos", productoService.listarActivos());
        return "ingresos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(IngresoProducto ingreso, Authentication auth, HttpServletRequest request) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        ingresoService.registrarIngreso(ingreso, actual, request);
        return "redirect:/ingresos/nuevo?exito";
    }
}
