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

        if (ingreso.getCantidadUnidad() != null && ingreso.getCantidadUnidad() > 0) {
            Kardex kU = new Kardex();
            kU.setProducto(producto);
            kU.setTipoOperacion(new TipoOperacion(1)); // Ingreso
            kU.setCantidadInicial(stockUndAntes);
            kU.setCantidadMovimiento(ingreso.getCantidadUnidad());
            kU.setCantidadFinal(stockUndAntes + ingreso.getCantidadUnidad());
            kU.setSaldoUnitario(stockUndAntes + ingreso.getCantidadUnidad());
            kU.setSaldoFraccionario(stockFraccAntes + ingreso.getCantidadFraccion());
            kU.setObservacion("[U] " + (ingreso.getObservacion() != null ? ingreso.getObservacion() : "Ingreso de unidades"));
            kU.setUsuarioRegistro(usuarioActual);
            kardexRepository.save(kU);
        }

        if (ingreso.getCantidadFraccion() != null && ingreso.getCantidadFraccion() > 0) {
            Kardex kF = new Kardex();
            kF.setProducto(producto);
            kF.setTipoOperacion(new TipoOperacion(1)); // Ingreso
            kF.setCantidadInicial(stockFraccAntes);
            kF.setCantidadMovimiento(ingreso.getCantidadFraccion());
            kF.setCantidadFinal(stockFraccAntes + ingreso.getCantidadFraccion());
            kF.setSaldoUnitario(stockUndAntes + ingreso.getCantidadUnidad());
            kF.setSaldoFraccionario(stockFraccAntes + ingreso.getCantidadFraccion());
            kF.setObservacion("[F] " + (ingreso.getObservacion() != null ? ingreso.getObservacion() : "Ingreso de fracciones"));
            kF.setUsuarioRegistro(usuarioActual);
            kardexRepository.save(kF);
        }

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
