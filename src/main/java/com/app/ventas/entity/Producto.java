package com.app.ventas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @DecimalMin(value = "0.00", message = "El precio unitario no puede ser negativo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "El precio fraccion no puede ser negativo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioFraccion = BigDecimal.ZERO;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer cantidadUnidad = 0;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer cantidadFraccion = 0;

    @Column(nullable = false)
    private Boolean esPack = false;

    @Min(value = 1, message = "La cantidad por item debe ser mayor a 0")
    @Column(nullable = false)
    private Integer cantidadItem = 1;

    @Column(nullable = false)
    private Boolean estado = true;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "productoPack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoComposicion> composiciones = new ArrayList<>();

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
    public Boolean getEsPack() { return esPack; }
    public void setEsPack(Boolean esPack) { this.esPack = esPack; }
    public Integer getCantidadItem() { return cantidadItem; }
    public void setCantidadItem(Integer cantidadItem) { this.cantidadItem = cantidadItem; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public List<ProductoComposicion> getComposiciones() { return composiciones; }
    public void setComposiciones(List<ProductoComposicion> composiciones) { this.composiciones = composiciones; }
}
