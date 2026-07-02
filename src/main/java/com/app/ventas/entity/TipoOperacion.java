package com.app.ventas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TipoOperacion")
public class TipoOperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codTipoOperacion;

    @Column(nullable = false, unique = true, length = 30)
    private String descripcion;

    public TipoOperacion() {}

    public TipoOperacion(Integer codTipoOperacion) {
        this.codTipoOperacion = codTipoOperacion;
    }

    public Integer getCodTipoOperacion() { return codTipoOperacion; }
    public void setCodTipoOperacion(Integer codTipoOperacion) { this.codTipoOperacion = codTipoOperacion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
