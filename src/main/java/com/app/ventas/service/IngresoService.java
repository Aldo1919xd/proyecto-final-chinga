package com.app.ventas.service;

import com.app.ventas.entity.*;
import com.app.ventas.repository.IngresoProductoRepository;
import com.app.ventas.repository.KardexRepository;
import com.app.ventas.repository.ProductoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngresoService {

    private final IngresoProductoRepository ingresoRepository;
    private final ProductoRepository productoRepository;
    private final KardexRepository kardexRepository;
    private final AuditoriaService auditoriaService;

    public List<IngresoProducto> listarTodos() {
        return ingresoRepository.findByEstadoTrueOrderByFechaHoraDesc();
    }

    public IngresoService(IngresoProductoRepository ingresoRepository,
                          ProductoRepository productoRepository,
                          KardexRepository kardexRepository,
                          AuditoriaService auditoriaService) {
        this.ingresoRepository = ingresoRepository;
        this.productoRepository = productoRepository;
        this.kardexRepository = kardexRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public IngresoProducto registrarIngreso(IngresoProducto ingreso, Usuario usuarioActual,
                                            HttpServletRequest request) {
        Producto producto = productoRepository.findById(ingreso.getProducto().getCodProducto()).orElseThrow();

        int stockUndAntes = producto.getCantidadUnidad();
        int stockFraccAntes = producto.getCantidadFraccion();

        producto.setCantidadUnidad(stockUndAntes + ingreso.getCantidadUnidad());
        producto.setCantidadFraccion(stockFraccAntes + ingreso.getCantidadFraccion());
        productoRepository.save(producto);

        Kardex kardex = new Kardex();
        kardex.setProducto(producto);
        kardex.setTipoOperacion(new TipoOperacion(1));
        kardex.setCantidadInicial(stockUndAntes);
        kardex.setCantidadMovimiento(ingreso.getCantidadUnidad());
        kardex.setCantidadFinal(producto.getCantidadUnidad());
        kardex.setSaldoUnitario(producto.getCantidadUnidad());
        kardex.setSaldoFraccionario(producto.getCantidadFraccion());
        kardex.setObservacion(ingreso.getObservacion());
        kardex.setUsuarioRegistro(usuarioActual);
        kardexRepository.save(kardex);

        ingreso.setUsuarioRegistro(usuarioActual);
        IngresoProducto guardado = ingresoRepository.save(ingreso);

        auditoriaService.registrar(usuarioActual, "Almacen", "IngresoProducto",
                "INSERT", guardado.getCodIngreso(),
                null, "{\"producto\":\"" + producto.getNombreProducto()
                        + "\",\"unidades\":" + ingreso.getCantidadUnidad()
                        + ",\"fracciones\":" + ingreso.getCantidadFraccion() + "}",
                request);
        return guardado;
    }
}
