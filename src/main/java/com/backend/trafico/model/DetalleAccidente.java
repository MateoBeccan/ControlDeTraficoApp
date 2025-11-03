package com.backend.trafico.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DETALLE_ACCIDENTES")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@IdClass(DetalleAccidenteId.class)
public class DetalleAccidente {

    @Id
    @ManyToOne
    @JoinColumn(name = "NRO_ACCIDENTE")
    private Accidente accidente;

    @Id
    @ManyToOne
    @JoinColumn(name = "PATENTE_VEHICULO")
    private Vehiculo vehiculo;

    @Column(name = "DESCRIPCION_ACCIDENTE", length = 200)
    private String descripcion;
}
