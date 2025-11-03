package com.backend.trafico.repository;

import com.backend.trafico.model.Multa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultaRepository extends JpaRepository<Multa, Integer> {
}
