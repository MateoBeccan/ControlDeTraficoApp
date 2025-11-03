package com.backend.trafico.service;

import com.backend.trafico.model.Multa;
import com.backend.trafico.repository.MultaRepository;
import org.springframework.stereotype.Service;

@Service
public class MultaService extends BaseService<Multa, Integer> {
    public MultaService(MultaRepository repository) {
        super(repository);
    }
}
