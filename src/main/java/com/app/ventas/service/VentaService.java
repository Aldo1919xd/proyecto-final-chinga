package com.app.ventas.service;

import com.app.ventas.entity.*;
import com.app.ventas.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class VentaService {

    private static final BigDecimal IGV_TASA = new BigDecimal("0.18");

    private final VentaCabeceraRepository ventaCabeceraRepository;
    private final VentaDetalleRepository ventaDetalleRepository;
    private final ProductoRepository productoRepository;
    private final CorrelativoRepository correlativoRepository;
    private final KardexRepository kardexRepository;
    private final AuditoriaService auditoriaService;
    private final ClienteRepository clienteRepository;

    public VentaService(VentaCabeceraRepository ventaCabeceraRepository,
                        VentaDetalleRepository ventaDetalleRepository,
                        ProductoRepository productoRepository,
                        CorrelativoRepository correlativoRepository,
                        KardexRepository kardexRepository,
                        AuditoriaService auditoriaService,
                        ClienteRepository clienteRepository) {
        this.ventaCabeceraRepository = ventaCabeceraRepository;
        this.ventaDetalleRepository = ventaDetalleRepository;
        this.productoRepository = productoRepository;
        this.correlativoRepository = correlativoRepository;
        this.kardexRepository = kardexRepository;
        this.auditoriaService = auditoriaService;
        this.clienteRepository = clienteRepository;
    }

    public List<VentaCabecera> listarTodas() {
        return ventaCabeceraRepository.findByEstadoTrueOrderByFechaHoraDesc();
    }

    @Transactional
    public VentaCabecera registrarVenta(VentaCabecera cabecera, List<VentaDetalle> detalles,
                                        Usuario usuarioActual, HttpServletRequest request) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto a la venta");
        }

        Cliente cliente = clienteRepository.findById(cabecera.getCliente().getCodCliente()).orElseThrow();
        String tipoDoc = cliente.getTipoDocumento().getDescripcion();
        String tipoComprobante = "RUC".equalsIgnoreCase(tipoDoc) ? "FACTURA" : "BOLETA";
        cabecera.setTipoComprobante(tipoComprobante);

        String serie = tipoComprobante.equals("FACTURA") ? "F001" : "B001";
        Correlativo correlativo = correlativoRepository
                .findByTipoComprobanteAndSerie(tipoComprobante, serie)
                .orElseGet(() -> {
                    Correlativo nuevo = new Correlativo();
                    nuevo.setTipoComprobante(tipoComprobante);
                    nuevo.setSerie(serie);
                    nuevo.setNumeroActual(0);
                    return correlativoRepository.save(nuevo);
                });

        correlativo.setNumeroActual(correlativo.getNumeroActual() + 1);
        correlativoRepository.save(correlativo);

        cabecera.setSerie(serie);
        cabecera.setNumeroCorrelativo(correlativo.getNumeroActual());
        cabecera.setUsuarioRegistro(usuarioActual);

        BigDecimal subtotalTotal = BigDecimal.ZERO;

        for (VentaDetalle detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getProducto().getCodProducto())
                    .orElseThrow();

            int stockUndAntes = producto.getCantidadUnidad();
            int stockFraccAntes = producto.getCantidadFraccion();

            BigDecimal precio = detalle.getTipoVenta().equals("FRACCION")
                    ? producto.getPrecioFraccion()
                    : producto.getPrecioUnitario();
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(precio.multiply(BigDecimal.valueOf(detalle.getCantidad())));
            subtotalTotal = subtotalTotal.add(detalle.getSubtotal());

            int unidadesConvertidas = 0;
            String docNro = tipoComprobante + "-" + serie + "-" + correlativo.getNumeroActual();

            if (detalle.getTipoVenta().equals("FRACCION")) {
                int needed = detalle.getCantidad();
                int available = producto.getCantidadFraccion() + producto.getCantidadUnidad() * 10;
                if (available < needed) {
                    throw new RuntimeException("Stock insuficiente para " + producto.getNombreProducto());
                }
                while (producto.getCantidadFraccion() < needed && producto.getCantidadUnidad() > 0) {
                    producto.setCantidadUnidad(producto.getCantidadUnidad() - 1);
                    producto.setCantidadFraccion(producto.getCantidadFraccion() + 10);
                    unidadesConvertidas++;
                }
                producto.setCantidadFraccion(producto.getCantidadFraccion() - needed);
            } else {
                if (producto.getCantidadUnidad() < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para " + producto.getNombreProducto());
                }
                producto.setCantidadUnidad(producto.getCantidadUnidad() - detalle.getCantidad());
            }

            productoRepository.save(producto);

            // Si se convirtieron unidades a fracciones (Menudeo automático)
            if (unidadesConvertidas > 0) {
                // 1. Salida de Unidades por conversión
                Kardex kSalidaU = new Kardex();
                kSalidaU.setProducto(producto);
                kSalidaU.setTipoOperacion(new TipoOperacion(5)); // Menudeo
                kSalidaU.setCantidadInicial(stockUndAntes);
                kSalidaU.setCantidadMovimiento(unidadesConvertidas);
                kSalidaU.setCantidadFinal(stockUndAntes - unidadesConvertidas);
                kSalidaU.setSaldoUnitario(stockUndAntes - unidadesConvertidas);
                kSalidaU.setSaldoFraccionario(stockFraccAntes);
                kSalidaU.setCodDocumento(docNro);
                kSalidaU.setObservacion("[U] Conversión automática (Menudeo)");
                kSalidaU.setUsuarioRegistro(usuarioActual);
                kardexRepository.save(kSalidaU);

                // 2. Entrada de Fracciones por conversión
                Kardex kEntradaF = new Kardex();
                kEntradaF.setProducto(producto);
                kEntradaF.setTipoOperacion(new TipoOperacion(5)); // Menudeo
                kEntradaF.setCantidadInicial(stockFraccAntes);
                kEntradaF.setCantidadMovimiento(unidadesConvertidas * 10);
                kEntradaF.setCantidadFinal(stockFraccAntes + (unidadesConvertidas * 10));
                kEntradaF.setSaldoUnitario(stockUndAntes - unidadesConvertidas);
                kEntradaF.setSaldoFraccionario(stockFraccAntes + (unidadesConvertidas * 10));
                kEntradaF.setCodDocumento(docNro);
                kEntradaF.setObservacion("[F] Conversión automática (Menudeo)");
                kEntradaF.setUsuarioRegistro(usuarioActual);
                kardexRepository.save(kEntradaF);
            }

            // Registrar la Venta (Venta) en Kardex
            Kardex kardex = new Kardex();
            kardex.setProducto(producto);
            kardex.setTipoOperacion(new TipoOperacion(2)); // Venta
            if (detalle.getTipoVenta().equals("FRACCION")) {
                int inicialF = stockFraccAntes + (unidadesConvertidas * 10);
                kardex.setCantidadInicial(inicialF);
                kardex.setCantidadMovimiento(detalle.getCantidad());
                kardex.setCantidadFinal(inicialF - detalle.getCantidad());
                kardex.setObservacion("[F] Venta de fracciones");
            } else {
                kardex.setCantidadInicial(stockUndAntes);
                kardex.setCantidadMovimiento(detalle.getCantidad());
                kardex.setCantidadFinal(stockUndAntes - detalle.getCantidad());
                kardex.setObservacion("[U] Venta de unidades");
            }
            kardex.setSaldoUnitario(producto.getCantidadUnidad());
            kardex.setSaldoFraccionario(producto.getCantidadFraccion());
            kardex.setCodDocumento(docNro);
            kardex.setUsuarioRegistro(usuarioActual);
            kardexRepository.save(kardex);
        }

        cabecera.setSubtotal(subtotalTotal);
        if (tipoComprobante.equals("FACTURA")) {
            cabecera.setIgv(subtotalTotal.multiply(IGV_TASA).setScale(2, RoundingMode.HALF_UP));
            cabecera.setTotal(subtotalTotal.add(cabecera.getIgv()));
        } else {
            cabecera.setIgv(BigDecimal.ZERO);
            cabecera.setTotal(subtotalTotal);
        }

        VentaCabecera guardada = ventaCabeceraRepository.save(cabecera);

        for (VentaDetalle detalle : detalles) {
            detalle.setVenta(guardada);
            ventaDetalleRepository.save(detalle);
        }

        auditoriaService.registrar(usuarioActual, "Ventas", "VentaCabecera",
                "INSERT", guardada.getCodVenta(),
                null, "{\"comprobante\":\"" + tipoComprobante + "-" + serie + "-"
                        + correlativo.getNumeroActual() + "\",\"total\":" + cabecera.getTotal() + "}",
                request);
        return guardada;
    }
}
