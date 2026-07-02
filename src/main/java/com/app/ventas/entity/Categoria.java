package com.app.ventas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codCategoria;

    @NotBlank(message = "El nombre de categoria es obligatorio")
    @Size(max = 80, message = "Maximo 80 caracteres")
    @Column(nullable = false, length = 80)
    private String nombreCategoria;

    @Column(nullable = false)
    private Boolean estado = true;

    public Categoria() {}

    public Categoria(Integer codCategoria) {
        this.codCategoria = codCategoria;
    }

    public Integer getCodCategoria() { return codCategoria; }
    public void setCodCategoria(Integer codCategoria) { this.codCategoria = codCategoria; }
    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
