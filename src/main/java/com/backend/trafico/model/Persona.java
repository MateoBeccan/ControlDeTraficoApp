package com.backend.trafico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "PERSONAS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Persona {

    @Id
    @NotNull(message = "El DNI es obligatorio")
    @Positive(message = "El DNI debe ser un número positivo")
    @Column(name = "DNI_PERSONA")
    private Integer dni;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(name = "NOMBRE_PERSONA", length = 50)
    private String nombre;

    @Size(max = 100, message = "La dirección no puede exceder 100 caracteres")
    @Column(name = "DIRECCION_PERSONA", length = 100)
    private String direccion;
}
