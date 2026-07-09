package com.app.ventas.service;

import com.app.ventas.entity.*;
import com.app.ventas.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoComposicionRepository composicionRepository;
    private final KardexRepository kardexRepository;
    private final AuditoriaService auditoriaService;

    public ProductoService(ProductoRepository productoRepository,
                           ProductoComposicionRepository composicionRepository,
                           KardexRepository kardexRepository,
                           AuditoriaService auditoriaService) {
        this.productoRepository = productoRepository;
        this.composicionRepository = composicionRepository;
        this.kardexRepository = kardexRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<Producto> listarActivos() {
        return productoRepository.findByEstadoTrue();
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreProductoContainingIgnoreCaseAndEstadoTrue(nombre);
    }

    public List<Producto> listarPosiblesComponentes() {
        return productoRepository.findByEstadoTrue().stream()
                .filter(p -> !Boolean.TRUE.equals(p.getEsPack()))
                .collect(Collectors.toList());
    }

    public List<ProductoComposicion> obtenerComposicion(Integer codProductoPack) {
        return composicionRepository.findByProductoPack_CodProducto(codProductoPack);
    }

    public BigDecimal calcularPrecioPack(Integer codProductoPack) {
        List<ProductoComposicion> comps = composicionRepository.findByProductoPack_CodProducto(codProductoPack);
        BigDecimal suma = BigDecimal.ZERO;
        for (ProductoComposicion c : comps) {
            BigDecimal sub = c.getProductoComponente().getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(c.getCantidad()));
            suma = suma.add(sub);
        }
        return suma;
    }

    @Transactional
    public Producto guardar(Producto producto, List<Integer> componenteIds, List<Integer> componenteCantidades,
                            Usuario usuarioActual, HttpServletRequest request) {
        boolean esNuevo = producto.getCodProducto() == null;

        if (Boolean.TRUE.equals(producto.getEsPack()) && componenteIds != null && !componenteIds.isEmpty()) {
            int totalItem = 0;
            for (int i = 0; i < componenteIds.size(); i++) {
                if (componenteIds.get(i) == null) continue;
                int cant = componenteCantidades != null && i < componenteCantidades.size()
                        ? componenteCantidades.get(i) : 1;
                totalItem += cant;
            }
            producto.setCantidadItem(totalItem);
        } else {
            producto.setEsPack(false);
            producto.setCantidadItem(1);
        }

        Producto guardado = productoRepository.save(producto);

        composicionRepository.deleteByProductoPack_CodProducto(guardado.getCodProducto());

        if (Boolean.TRUE.equals(producto.getEsPack()) && componenteIds != null) {
            for (int i = 0; i < componenteIds.size(); i++) {
                if (componenteIds.get(i) == null) continue;
                ProductoComposicion pc = new ProductoComposicion();
                pc.setProductoPack(guardado);
                pc.setProductoComponente(new Producto(componenteIds.get(i)));
                pc.setCantidad(componenteCantidades != null && i < componenteCantidades.size()
                        ? componenteCantidades.get(i) : 1);
                composicionRepository.save(pc);
            }
        }

        if (Boolean.TRUE.equals(producto.getEsPack())) {
            BigDecimal precioCalculado = calcularPrecioPack(guardado.getCodProducto());
            if (precioCalculado.compareTo(BigDecimal.ZERO) > 0) {
                guardado.setPrecioUnitario(precioCalculado);
                guardado.setPrecioFraccion(BigDecimal.ZERO);
                productoRepository.save(guardado);
            }
        }

        auditoriaService.registrar(usuarioActual, "Maestras", "Producto",
                esNuevo ? "INSERT" : "UPDATE",
                guardado.getCodProducto(),
                esNuevo ? null : "{\"nombre\":\"" + producto.getNombreProducto() + "\"}",
                "{\"nombre\":\"" + guardado.getNombreProducto() + "\"}",
                request);
        return guardado;
    }

    @Transactional
    public void eliminarLogico(Integer id, Usuario usuarioActual, HttpServletRequest request) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setEstado(false);
        productoRepository.save(producto);
        auditoriaService.registrar(usuarioActual, "Maestras", "Producto",
                "DELETE", id, "{\"estado\":true}", "{\"estado\":false}", request);
    }

    @Transactional
    public void menudear(Integer codProductoPack, Integer cantidadDesarmar,
                         Usuario usuarioActual, HttpServletRequest request) {
        Producto pack = productoRepository.findById(codProductoPack).orElseThrow();
        if (!Boolean.TRUE.equals(pack.getEsPack())) {
            throw new RuntimeException("El producto no es un pack");
        }
        if (pack.getCantidadUnidad() < cantidadDesarmar) {
            throw new RuntimeException("Stock insuficiente del pack");
        }

        List<ProductoComposicion> comps = composicionRepository.findByProductoPack_CodProducto(codProductoPack);
        if (comps.isEmpty()) {
            throw new RuntimeException("El pack no tiene composicion definida");
        }

        int stockPackAntes = pack.getCantidadUnidad();
        pack.setCantidadUnidad(pack.getCantidadUnidad() - cantidadDesarmar);
        productoRepository.save(pack);

        Kardex kardexSalida = new Kardex();
        kardexSalida.setProducto(pack);
        kardexSalida.setTipoOperacion(new TipoOperacion(5));
        kardexSalida.setCantidadInicial(stockPackAntes);
        kardexSalida.setCantidadMovimiento(cantidadDesarmar);
        kardexSalida.setCantidadFinal(pack.getCantidadUnidad());
        kardexSalida.setSaldoUnitario(pack.getCantidadUnidad());
        kardexSalida.setSaldoFraccionario(pack.getCantidadFraccion());
        kardexSalida.setObservacion("[U] Menudeo: " + cantidadDesarmar + " pack(s) desarmado(s)");
        kardexSalida.setUsuarioRegistro(usuarioActual);
        kardexRepository.save(kardexSalida);

        for (ProductoComposicion comp : comps) {
            Producto componente = comp.getProductoComponente();
            int stockCompAntes = componente.getCantidadUnidad();
            int aumento = cantidadDesarmar * comp.getCantidad();
            componente.setCantidadUnidad(stockCompAntes + aumento);
            productoRepository.save(componente);

            Kardex kardexEntrada = new Kardex();
            kardexEntrada.setProducto(componente);
            kardexEntrada.setTipoOperacion(new TipoOperacion(5));
            kardexEntrada.setCantidadInicial(stockCompAntes);
            kardexEntrada.setCantidadMovimiento(aumento);
            kardexEntrada.setCantidadFinal(componente.getCantidadUnidad());
            kardexEntrada.setSaldoUnitario(componente.getCantidadUnidad());
            kardexEntrada.setSaldoFraccionario(componente.getCantidadFraccion());
            kardexEntrada.setObservacion("[U] Menudeo: " + aumento + " und desde pack " + pack.getNombreProducto());
            kardexEntrada.setUsuarioRegistro(usuarioActual);
            kardexRepository.save(kardexEntrada);
        }

        auditoriaService.registrar(usuarioActual, "Almacen", "Producto",
                "MENUDEO", codProductoPack,
                "{\"stockPack\":" + stockPackAntes + "}",
                "{\"stockPack\":" + pack.getCantidadUnidad()
                        + ",\"componentes\":\"+" + cantidadDesarmar + " packs\"}",
                request);
    }
}
