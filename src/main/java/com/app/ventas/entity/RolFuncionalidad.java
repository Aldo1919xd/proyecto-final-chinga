package com.app.ventas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "RolFuncionalidad",
       uniqueConstraints = @UniqueConstraint(columnNames = {"idRol", "idFuncionalidad"}))
public class RolFuncionalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRolFuncionalidad;

    @ManyToOne
    @JoinColumn(name = "idRol", nullable = false)
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "idFuncionalidad", nullable = false)
    private Funcionalidad funcionalidad;

    @Column(nullable = false)
    private Boolean ver = false;

    @Column(nullable = false)
    private Boolean crear = false;

    @Column(nullable = false)
    private Boolean editar = false;

    @Column(nullable = false)
    private Boolean eliminar = false;

    @Column(nullable = false)
    private Boolean imprimir = false;

    public RolFuncionalidad() {}

    public Integer getIdRolFuncionalidad() { return idRolFuncionalidad; }
    public void setIdRolFuncionalidad(Integer idRolFuncionalidad) { this.idRolFuncionalidad = idRolFuncionalidad; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public Funcionalidad getFuncionalidad() { return funcionalidad; }
    public void setFuncionalidad(Funcionalidad funcionalidad) { this.funcionalidad = funcionalidad; }
    public Boolean getVer() { return ver; }
    public void setVer(Boolean ver) { this.ver = ver; }
    public Boolean getCrear() { return crear; }
    public void setCrear(Boolean crear) { this.crear = crear; }
    public Boolean getEditar() { return editar; }
    public void setEditar(Boolean editar) { this.editar = editar; }
    public Boolean getEliminar() { return eliminar; }
    public void setEliminar(Boolean eliminar) { this.eliminar = eliminar; }
    public Boolean getImprimir() { return imprimir; }
    public void setImprimir(Boolean imprimir) { this.imprimir = imprimir; }
}
