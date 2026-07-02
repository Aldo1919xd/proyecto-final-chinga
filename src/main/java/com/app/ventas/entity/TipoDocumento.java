package com.app.ventas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TipoDocumento")
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codTipoDocumento;

    @Column(nullable = false, length = 40)
    private String descripcion;

    @Column(nullable = false)
    private Boolean estado = true;

    public TipoDocumento() {}

    public TipoDocumento(Integer codTipoDocumento) {
        this.codTipoDocumento = codTipoDocumento;
    }

    public Integer getCodTipoDocumento() { return codTipoDocumento; }
    public void setCodTipoDocumento(Integer codTipoDocumento) { this.codTipoDocumento = codTipoDocumento; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
