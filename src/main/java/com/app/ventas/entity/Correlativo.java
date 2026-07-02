package com.app.ventas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Correlativo",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tipoComprobante", "serie"}))
public class Correlativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codCorrelativo;

    @Column(nullable = false, length = 20)
    private String tipoComprobante;

    @Column(nullable = false, length = 10)
    private String serie;

    @Column(nullable = false)
    private Integer numeroActual = 0;

    public Correlativo() {}

    public Integer getCodCorrelativo() { return codCorrelativo; }
    public void setCodCorrelativo(Integer codCorrelativo) { this.codCorrelativo = codCorrelativo; }
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    public Integer getNumeroActual() { return numeroActual; }
    public void setNumeroActual(Integer numeroActual) { this.numeroActual = numeroActual; }
}
