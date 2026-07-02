package com.app.ventas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "VentaDetalle")
public class VentaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codDetalle;

    @ManyToOne
    @JoinColumn(name = "codVenta", nullable = false)
    private VentaCabecera venta;

    @ManyToOne
    @JoinColumn(name = "codProducto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad = 1;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, length = 10)
    private String tipoVenta = "UNIDAD";

    public VentaDetalle() {}

    public Integer getCodDetalle() { return codDetalle; }
    public void setCodDetalle(Integer codDetalle) { this.codDetalle = codDetalle; }
    public VentaCabecera getVenta() { return venta; }
    public void setVenta(VentaCabecera venta) { this.venta = venta; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public String getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(String tipoVenta) { this.tipoVenta = tipoVenta; }
}
