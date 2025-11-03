package com.backend.trafico.repository;

import com.backend.trafico.model.Accidente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccidenteRepository extends JpaRepository<Accidente, Integer> {
}
