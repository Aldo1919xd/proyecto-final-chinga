package com.app.ventas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "Producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 120, message = "Maximo 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombreProducto;

    @NotNull(message = "Debe seleccionar una categoria")
    @ManyToOne
    @JoinColumn(name = "codCategoria", nullable = false)
    private Categoria categoria;

    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @DecimalMin(value = "0.01", message = "El precio fraccion debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioFraccion = BigDecimal.ZERO;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer cantidadUnidad = 0;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer cantidadFraccion = 0;

    @Column(nullable = false)
    private Boolean estado = true;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    public Producto() {}

    public Producto(Integer codProducto) {
        this.codProducto = codProducto;
    }

    public Integer getCodProducto() { return codProducto; }
    public void setCodProducto(Integer codProducto) { this.codProducto = codProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getPrecioFraccion() { return precioFraccion; }
    public void setPrecioFraccion(BigDecimal precioFraccion) { this.precioFraccion = precioFraccion; }
    public Integer getCantidadUnidad() { return cantidadUnidad; }
    public void setCantidadUnidad(Integer cantidadUnidad) { this.cantidadUnidad = cantidadUnidad; }
    public Integer getCantidadFraccion() { return cantidadFraccion; }
    public void setCantidadFraccion(Integer cantidadFraccion) { this.cantidadFraccion = cantidadFraccion; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
