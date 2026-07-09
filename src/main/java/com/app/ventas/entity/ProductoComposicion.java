package com.app.ventas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ProductoComposicion")
public class ProductoComposicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComposicion;

    @ManyToOne
    @JoinColumn(name = "codProductoPack", nullable = false)
    private Producto productoPack;

    @ManyToOne
    @JoinColumn(name = "codProductoComponente", nullable = false)
    private Producto productoComponente;

    @Column(nullable = false)
    private Integer cantidad = 1;

    public ProductoComposicion() {}

    public Integer getIdComposicion() { return idComposicion; }
    public void setIdComposicion(Integer idComposicion) { this.idComposicion = idComposicion; }
    public Producto getProductoPack() { return productoPack; }
    public void setProductoPack(Producto productoPack) { this.productoPack = productoPack; }
    public Producto getProductoComponente() { return productoComponente; }
    public void setProductoComponente(Producto productoComponente) { this.productoComponente = productoComponente; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
