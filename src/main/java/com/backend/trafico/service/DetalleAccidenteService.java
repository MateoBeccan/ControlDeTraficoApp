package com.backend.trafico.service;

import com.backend.trafico.model.DetalleAccidente;
import com.backend.trafico.model.DetalleAccidenteId;
import com.backend.trafico.repository.DetalleAccidenteRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleAccidenteService extends BaseService<DetalleAccidente, DetalleAccidenteId> {
    public DetalleAccidenteService(DetalleAccidenteRepository repository) {
        super(repository);
    }
}
