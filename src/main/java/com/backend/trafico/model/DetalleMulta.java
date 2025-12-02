package com.backend.trafico.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DETALLE_MULTAS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@IdClass(DetalleMultaId.class)
public class DetalleMulta {

    @Id
    @ManyToOne
        @JoinColumn(name = "NRO_MULTA")
        private Multa multa;

        @Id
        @ManyToOne
        @JoinColumn(name = "PATENTE_VEHICULO")
        private Vehiculo vehiculo;

        @Column(name = "DESCRIPCION_MULTA", length = 200)
        private String descripcion;
    }
