package com.app.ventas.controller;

import com.app.ventas.entity.Kardex;
import com.app.ventas.repository.KardexRepository;
import com.app.ventas.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/kardex")
public class KardexController {

    private final KardexRepository kardexRepository;
    private final ProductoService productoService;

    public KardexController(KardexRepository kardexRepository, ProductoService productoService) {
        this.kardexRepository = kardexRepository;
        this.productoService = productoService;
    }

    @GetMapping
    public String resumen(@RequestParam(required = false) Integer productoId, Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        if (productoId != null) {
            List<Kardex> movimientos = kardexRepository.findByProductoCodProductoOrderByFechaHoraDesc(productoId);
            model.addAttribute("movimientos", movimientos);
            model.addAttribute("productoSeleccionado", productoService.buscarPorId(productoId).orElse(null));
        }
        return "kardex/resumen";
    }
}
