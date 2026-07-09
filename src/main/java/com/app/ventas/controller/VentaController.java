package com.app.ventas.controller;

import com.app.ventas.entity.Usuario;
import com.app.ventas.entity.VentaCabecera;
import com.app.ventas.entity.VentaDetalle;
import com.app.ventas.service.ClienteService;
import com.app.ventas.service.ProductoService;
import com.app.ventas.service.UsuarioService;
import com.app.ventas.service.VentaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    public VentaController(VentaService ventaService, ClienteService clienteService,
                           ProductoService productoService, UsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ventas", ventaService.listarTodas());
        return "ventas/lista";
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
                          Authentication auth, HttpServletRequest request, HttpSession session) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();

        if (cabecera.getCliente() == null || cabecera.getCliente().getCodCliente() == null) {
            return "redirect:/ventas/nuevo?error=sinCliente";
        }

        if (productoId == null || productoId.isEmpty()) {
            return "redirect:/ventas/nuevo?error=sinProductos";
        }

        List<VentaDetalle> detalles = new ArrayList<>();
        for (int i = 0; i < productoId.size(); i++) {
            if (productoId.get(i) == null) continue;

            var prodOpt = productoService.buscarPorId(productoId.get(i));
            if (prodOpt.isEmpty()) {
                return "redirect:/ventas/nuevo?error=productoNoEncontrado";
            }

            int cant = cantidad != null && i < cantidad.size() ? cantidad.get(i) : 1;
            if (cant <= 0) {
                return "redirect:/ventas/nuevo?error=cantidadInvalida";
            }

            VentaDetalle detalle = new VentaDetalle();
            detalle.setProducto(prodOpt.get());
            detalle.setCantidad(cant);
            detalle.setTipoVenta(tipoVenta != null && i < tipoVenta.size() ? tipoVenta.get(i) : "UNIDAD");

            var prod = prodOpt.get();
            if ("UNIDAD".equals(detalle.getTipoVenta()) && prod.getCantidadUnidad() < cant) {
                return "redirect:/ventas/nuevo?error=stockInsuficiente&producto=" + prod.getNombreProducto();
            }
            if ("FRACCION".equals(detalle.getTipoVenta())) {
                int fraccionesDisponibles = prod.getCantidadFraccion() + prod.getCantidadUnidad() * 10;
                if (fraccionesDisponibles < cant) {
                    return "redirect:/ventas/nuevo?error=stockInsuficiente&producto=" + prod.getNombreProducto();
                }
            }

            detalles.add(detalle);
        }

        if (detalles.isEmpty()) {
            return "redirect:/ventas/nuevo?error=sinProductos";
        }

        try {
            ventaService.registrarVenta(cabecera, detalles, actual, request);
        } catch (Exception e) {
            return "redirect:/ventas/nuevo?error=" + e.getMessage();
        }
        return "redirect:/ventas/nuevo?exito";
    }
}
