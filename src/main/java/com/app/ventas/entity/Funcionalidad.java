package com.app.ventas.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Funcionalidad")
public class Funcionalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFuncionalidad;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(length = 60)
    private String icono;

    @ManyToOne
    @JoinColumn(name = "padre")
    private Funcionalidad padre;

    @OneToMany(mappedBy = "padre")
    @OrderBy("nombre ASC")
    private List<Funcionalidad> hijos;

    public Funcionalidad() {}

    public Funcionalidad(Integer idFuncionalidad) {
        this.idFuncionalidad = idFuncionalidad;
    }

    public Integer getIdFuncionalidad() { return idFuncionalidad; }
    public void setIdFuncionalidad(Integer idFuncionalidad) { this.idFuncionalidad = idFuncionalidad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }
    public Funcionalidad getPadre() { return padre; }
    public void setPadre(Funcionalidad padre) { this.padre = padre; }
    public List<Funcionalidad> getHijos() { return hijos; }
    public void setHijos(List<Funcionalidad> hijos) { this.hijos = hijos; }
}
