package com.app.ventas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "IngresoProducto")
public class IngresoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codIngreso;

    @Column(nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaHora;

    @NotNull(message = "Debe seleccionar un producto")
    @ManyToOne
    @JoinColumn(name = "codProducto", nullable = false)
    private Producto producto;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(nullable = false)
    private Integer cantidadUnidad = 0;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(nullable = false)
    private Integer cantidadFraccion = 0;

    @Column(length = 200)
    private String observacion;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "usuarioRegistro")
    private Usuario usuarioRegistro;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    public IngresoProducto() {}

    @AssertTrue(message = "Debe ingresar al menos una unidad o fraccion")
    public boolean isCantidadValida() {
        return (cantidadUnidad != null && cantidadUnidad > 0)
            || (cantidadFraccion != null && cantidadFraccion > 0);
    }

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
