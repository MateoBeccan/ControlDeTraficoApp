package com.backend.trafico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "MULTAS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Multa {

    @Id
    @NotNull(message = "El número de multa es obligatorio")
    @Column(name = "NRO_MULTA")
    private Integer numero;

    @NotNull(message = "El juez es obligatorio")
    @ManyToOne
    @JoinColumn(name = "COD_JUEZ")
    private Juez juez;

    @NotNull(message = "La fecha de la multa es obligatoria")
    @PastOrPresent(message = "La fecha de la multa no puede ser futura")
    @Column(name = "FECHA_MULTA")
    private LocalDate fecha;

    @NotBlank(message = "El lugar de la multa no puede estar vacío")
    @Size(max = 100, message = "El lugar no puede exceder 100 caracteres")
    @Column(name = "LUGAR_MULTA", length = 100)
    private String lugar;
}
