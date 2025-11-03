package com.backend.trafico.repository;

import com.backend.trafico.model.DetalleAccidente;
import com.backend.trafico.model.DetalleAccidenteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleAccidenteRepository extends JpaRepository<DetalleAccidente, DetalleAccidenteId> {
}
