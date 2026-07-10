package com.app.ventas.controller;

import com.app.ventas.entity.Producto;
import com.app.ventas.entity.ProductoComposicion;
import com.app.ventas.entity.Usuario;
import com.app.ventas.service.CategoriaService;
import com.app.ventas.service.PermisoService;
import com.app.ventas.service.ProductoService;
import com.app.ventas.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final PermisoService permisoService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService,
                              UsuarioService usuarioService, PermisoService permisoService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.permisoService = permisoService;
    }

    @GetMapping
    public String listar(Authentication auth, Model model) {
        if (!permisoService.tieneVer(auth, "Productos")) return "redirect:/inicio?error=sinPermiso";
        model.addAttribute("productos", productoService.listarActivos());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Authentication auth, Model model) {
        if (!permisoService.tieneCrear(auth, "Productos")) return "redirect:/productos?error=sinPermiso";
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarActivos());
        model.addAttribute("componentesDisponibles", productoService.listarPosiblesComponentes());
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Producto producto, BindingResult result, Model model,
                          @RequestParam(required = false) List<Integer> componenteId,
                          @RequestParam(required = false) List<Integer> componenteCantidad,
                          Authentication auth, HttpServletRequest request) {
        boolean esNuevo = producto.getCodProducto() == null;
        if (esNuevo && !permisoService.tieneCrear(auth, "Productos")) return "redirect:/productos?error=sinPermiso";
        if (!esNuevo && !permisoService.tieneEditar(auth, "Productos")) return "redirect:/productos?error=sinPermiso";
        if (result.hasErrors()) {
            System.out.println("Validation errors during product save: " + result.getAllErrors());
            model.addAttribute("categorias", categoriaService.listarActivos());
            model.addAttribute("componentesDisponibles", productoService.listarPosiblesComponentes());
            return "productos/formulario";
        }
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        try {
            productoService.guardar(producto, componenteId, componenteCantidad, actual, request);
        } catch (RuntimeException e) {
            return "redirect:/productos?error=" + e.getMessage();
        }
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Authentication auth, Model model) {
        if (!permisoService.tieneEditar(auth, "Productos")) return "redirect:/productos?error=sinPermiso";
        Producto producto = productoService.buscarPorId(id).orElseThrow();
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarActivos());
        model.addAttribute("componentesDisponibles", productoService.listarPosiblesComponentes());
        model.addAttribute("composicion", productoService.obtenerComposicion(id));
        if (Boolean.TRUE.equals(producto.getEsPack())) {
            model.addAttribute("precioCalculado", productoService.calcularPrecioPack(id));
        }
        return "productos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth, HttpServletRequest request) {
        if (!permisoService.tieneEliminar(auth, "Productos")) return "redirect:/productos?error=sinPermiso";
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

    @GetMapping("/composicion/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerComposicion(@PathVariable Integer id) {
        List<ProductoComposicion> comps = productoService.obtenerComposicion(id);
        List<Map<String, Object>> data = comps.stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("idComposicion", c.getIdComposicion());
            m.put("codProductoComponente", c.getProductoComponente().getCodProducto());
            m.put("nombreComponente", c.getProductoComponente().getNombreProducto());
            m.put("cantidad", c.getCantidad());
            m.put("precioUnitario", c.getProductoComponente().getPrecioUnitario());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/componentes-disponibles")
    @ResponseBody
    public ResponseEntity<?> componentesDisponibles(@RequestParam(required = false) String q) {
        List<Producto> componentes;
        if (q != null && !q.trim().isEmpty()) {
            componentes = productoService.buscarPorNombre(q).stream()
                    .filter(p -> !Boolean.TRUE.equals(p.getEsPack()))
                    .collect(Collectors.toList());
        } else {
            componentes = productoService.listarPosiblesComponentes();
        }
        List<Map<String, Object>> data = componentes.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("codProducto", p.getCodProducto());
            m.put("nombreProducto", p.getNombreProducto());
            m.put("precioUnitario", p.getPrecioUnitario());
            m.put("stock", p.getCantidadUnidad());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/calcular-precio/{id}")
    @ResponseBody
    public ResponseEntity<?> calcularPrecio(@PathVariable Integer id) {
        BigDecimal precio = productoService.calcularPrecioPack(id);
        Map<String, Object> data = new HashMap<>();
        data.put("precioCalculado", precio);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/menudear")
    public String menudear(@RequestParam Integer codProducto,
                           @RequestParam Integer cantidad,
                           Authentication auth, HttpServletRequest request, HttpSession session) {
        if (requiereVerificacion2fa(auth, session)) {
            return "redirect:/usuarios/2fa/verificar-sesion?redirect=/productos";
        }
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        try {
            productoService.menudear(codProducto, cantidad, actual, request);
        } catch (Exception e) {
            return "redirect:/productos?error=" + e.getMessage();
        }
        return "redirect:/productos?exitoMenudeo";
    }

    private boolean requiereVerificacion2fa(Authentication auth, HttpSession session) {
        if (auth == null || !auth.isAuthenticated()) return false;
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElse(null);
        if (actual == null) return false;
        boolean tiene2fa = actual.getSecretKey2fa() != null && !actual.getSecretKey2fa().isEmpty();
        if (!tiene2fa) return false;
        Boolean verificado = (Boolean) session.getAttribute("2fa_verified");
        return verificado == null || !verificado;
    }
}
