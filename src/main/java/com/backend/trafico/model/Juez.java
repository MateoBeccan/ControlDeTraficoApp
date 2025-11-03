package com.backend.trafico.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "JUEZ")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Juez {

    @Id
    @Column(name = "COD_JUEZ")
    private Integer codigo;

    @Column(name = "APELLIDO_JUEZ", length = 50)
    private String apellido;
}
