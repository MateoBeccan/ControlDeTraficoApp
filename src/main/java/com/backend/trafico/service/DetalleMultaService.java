package com.backend.trafico.service;

import com.backend.trafico.model.DetalleMulta;
import com.backend.trafico.model.DetalleMultaId;
import com.backend.trafico.repository.DetalleMultaRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleMultaService extends BaseService<DetalleMulta, DetalleMultaId> {
    public DetalleMultaService(DetalleMultaRepository repository) {
        super(repository);
    }
}
