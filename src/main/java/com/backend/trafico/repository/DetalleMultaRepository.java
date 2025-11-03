package com.backend.trafico.repository;

import com.backend.trafico.model.DetalleMulta;
import com.backend.trafico.model.DetalleMultaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleMultaRepository extends JpaRepository<DetalleMulta, DetalleMultaId> {
}
