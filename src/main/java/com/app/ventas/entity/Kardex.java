package com.app.ventas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Kardex")
public class Kardex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codKardex;

    @ManyToOne
    @JoinColumn(name = "codProducto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "codTipoOperacion", nullable = false)
    private TipoOperacion tipoOperacion;

    @Column(nullable = false)
    private Integer cantidadInicial = 0;

    @Column(nullable = false)
    private Integer cantidadMovimiento = 0;

    @Column(nullable = false)
    private Integer cantidadFinal = 0;

    @Column(nullable = false)
    private Integer saldoUnitario = 0;

    @Column(nullable = false)
    private Integer saldoFraccionario = 0;

    @Column(nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaHora;

    @Column(length = 20)
    private String codDocumento;

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

    public Kardex() {}

    public Integer getCodKardex() { return codKardex; }
    public void setCodKardex(Integer codKardex) { this.codKardex = codKardex; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public TipoOperacion getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(TipoOperacion tipoOperacion) { this.tipoOperacion = tipoOperacion; }
    public Integer getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(Integer cantidadInicial) { this.cantidadInicial = cantidadInicial; }
    public Integer getCantidadMovimiento() { return cantidadMovimiento; }
    public void setCantidadMovimiento(Integer cantidadMovimiento) { this.cantidadMovimiento = cantidadMovimiento; }
    public Integer getCantidadFinal() { return cantidadFinal; }
    public void setCantidadFinal(Integer cantidadFinal) { this.cantidadFinal = cantidadFinal; }
    public Integer getSaldoUnitario() { return saldoUnitario; }
    public void setSaldoUnitario(Integer saldoUnitario) { this.saldoUnitario = saldoUnitario; }
    public Integer getSaldoFraccionario() { return saldoFraccionario; }
    public void setSaldoFraccionario(Integer saldoFraccionario) { this.saldoFraccionario = saldoFraccionario; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getCodDocumento() { return codDocumento; }
    public void setCodDocumento(String codDocumento) { this.codDocumento = codDocumento; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Usuario getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(Usuario usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }
}
