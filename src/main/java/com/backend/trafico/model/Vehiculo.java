package com.backend.trafico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "VEHICULOS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Vehiculo {

    @Id
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 7, message = "La patente no puede exceder 7 caracteres")
    @Column(name = "PATENTE_VEHICULO", length = 7)
    private String patente;

    @NotNull(message = "El propietario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "DNI_PERSONA")
    private Persona propietario;

    @NotBlank(message = "La marca no puede estar vacía")
    @Size(max = 30, message = "La marca no puede exceder 30 caracteres")
    @Column(name = "MARCA_VEHICULO", length = 30)
    private String marca;
}
