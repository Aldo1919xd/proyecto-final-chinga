package com.app.ventas.controller;

import com.app.ventas.entity.IngresoProducto;
import com.app.ventas.entity.Usuario;
import com.app.ventas.service.IngresoService;
import com.app.ventas.service.PermisoService;
import com.app.ventas.service.ProductoService;
import com.app.ventas.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ingresos")
public class IngresoController {

    private final IngresoService ingresoService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;
    private final PermisoService permisoService;

    public IngresoController(IngresoService ingresoService, ProductoService productoService,
                             UsuarioService usuarioService, PermisoService permisoService) {
        this.ingresoService = ingresoService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
        this.permisoService = permisoService;
    }

    @GetMapping
    public String listar(Authentication auth, Model model) {
        if (!permisoService.tieneVer(auth, "Ingresos")) return "redirect:/inicio?error=sinPermiso";
        model.addAttribute("ingresos", ingresoService.listarTodos());
        return "ingresos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Authentication auth, Model model) {
        if (!permisoService.tieneCrear(auth, "Ingresos")) return "redirect:/ingresos?error=sinPermiso";
        model.addAttribute("ingreso", new IngresoProducto());
        model.addAttribute("productos", productoService.listarActivos());
        return "ingresos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid IngresoProducto ingreso, BindingResult result, Model model,
                          Authentication auth, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.listarActivos());
            return "ingresos/formulario";
        }
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        ingresoService.registrarIngreso(ingreso, actual, request);
        return "redirect:/ingresos/nuevo?exito";
    }
}
