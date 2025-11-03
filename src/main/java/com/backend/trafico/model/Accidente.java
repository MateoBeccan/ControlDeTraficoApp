package com.backend.trafico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "ACCIDENTES")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Accidente {

    @Id
    @NotNull(message = "El número de accidente es obligatorio")
    @Column(name = "NRO_ACCIDENTE")
    private Integer numero;

    @NotNull(message = "La fecha del accidente es obligatoria")
    @PastOrPresent(message = "La fecha del accidente no puede ser futura")
    @Column(name = "FECHA_ACCIDENTE")
    private LocalDate fecha;

    @NotBlank(message = "El lugar del accidente no puede estar vacío")
    @Size(max = 100, message = "El lugar no puede exceder 100 caracteres")
    @Column(name = "LUGAR_ACCIDENTE", length = 100)
    private String lugar;
}
