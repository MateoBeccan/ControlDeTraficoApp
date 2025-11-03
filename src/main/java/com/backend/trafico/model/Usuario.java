package com.backend.trafico.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USUARIOS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Usuario {

    @Id
    @Column(name = "USERNAME", length = 50)
    private String username;

    @Column(name = "PASSWORD", length = 100, nullable = false)
    private String password;

    @Column(name = "ROLE", length = 20, nullable = false)
    private String role;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = true;
}