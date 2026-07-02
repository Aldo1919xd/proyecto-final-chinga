package com.app.ventas.controller;

import com.app.ventas.entity.Usuario;
import com.app.ventas.entity.VentaCabecera;
import com.app.ventas.entity.VentaDetalle;
import com.app.ventas.service.ClienteService;
import com.app.ventas.service.ProductoService;
import com.app.ventas.service.UsuarioService;
import com.app.ventas.service.VentaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ventas", ventaService.listarTodas());
        return "ventas/lista";
    }

    public VentaController(VentaService ventaService, ClienteService clienteService,
                           ProductoService productoService, UsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("venta", new VentaCabecera());
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("productos", productoService.listarActivos());
        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(VentaCabecera cabecera,
                          @RequestParam(required = false) List<Integer> productoId,
                          @RequestParam(required = false) List<Integer> cantidad,
                          @RequestParam(required = false) List<String> tipoVenta,
                          Authentication auth, HttpServletRequest request) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();

        List<VentaDetalle> detalles = new ArrayList<>();
        if (productoId != null) {
            for (int i = 0; i < productoId.size(); i++) {
                VentaDetalle detalle = new VentaDetalle();
                detalle.setProducto(productoService.buscarPorId(productoId.get(i)).orElseThrow());
                detalle.setCantidad(cantidad != null && i < cantidad.size() ? cantidad.get(i) : 1);
                detalle.setTipoVenta(tipoVenta != null && i < tipoVenta.size() ? tipoVenta.get(i) : "UNIDAD");
                detalles.add(detalle);
            }
        }

        ventaService.registrarVenta(cabecera, detalles, actual, request);
        return "redirect:/ventas/nuevo?exito";
    }
}
