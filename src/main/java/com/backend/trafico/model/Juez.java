package com.backend.trafico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "JUEZ")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Juez {

    @Id
    @NotNull(message = "El código es obligatorio")
    @Column(name = "COD_JUEZ")
    private Integer codigo;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras")
    @Column(name = "APELLIDO_JUEZ", length = 50)
    private String apellido;
}
