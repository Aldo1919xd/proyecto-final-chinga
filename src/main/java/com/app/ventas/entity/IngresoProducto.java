package com.app.ventas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "IngresoProducto")
public class IngresoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codIngreso;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "codProducto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidadUnidad = 0;

    @Column(nullable = false)
    private Integer cantidadFraccion = 0;

    @Column(length = 200)
    private String observacion;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuarioRegistro")
    private Usuario usuarioRegistro;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    public IngresoProducto() {}

    public Integer getCodIngreso() { return codIngreso; }
    public void setCodIngreso(Integer codIngreso) { this.codIngreso = codIngreso; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getCantidadUnidad() { return cantidadUnidad; }
    public void setCantidadUnidad(Integer cantidadUnidad) { this.cantidadUnidad = cantidadUnidad; }
    public Integer getCantidadFraccion() { return cantidadFraccion; }
    public void setCantidadFraccion(Integer cantidadFraccion) { this.cantidadFraccion = cantidadFraccion; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Usuario getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(Usuario usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
