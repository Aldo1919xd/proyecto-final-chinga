package com.app.ventas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Cliente",
       uniqueConstraints = @UniqueConstraint(columnNames = {"codTipoDocumento", "numeroDocumento"}))
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codCliente;

    @NotBlank(message = "El numero de documento es obligatorio")
    @Column(nullable = false, length = 255)
    private String numeroDocumento;

    @Size(max = 150, message = "La razon social no puede exceder 150 caracteres")
    @Column(length = 150)
    private String razonSocial;

    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(length = 150)
    private String nombreCliente;

    @NotNull(message = "Debe seleccionar un tipo de documento")
    @ManyToOne
    @JoinColumn(name = "codTipoDocumento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(length = 255)
    private String fechaNacimiento;

    @Column(nullable = false)
    private Boolean estado = true;

    public Cliente() {}

    public Cliente(Integer codCliente) {
        this.codCliente = codCliente;
    }

    public Integer getCodCliente() { return codCliente; }
    public void setCodCliente(Integer codCliente) { this.codCliente = codCliente; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
