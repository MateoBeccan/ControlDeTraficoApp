package com.backend.trafico.service;

import com.backend.trafico.model.Accidente;
import com.backend.trafico.repository.AccidenteRepository;
import org.springframework.stereotype.Service;

@Service
public class AccidenteService extends BaseService<Accidente, Integer> {
    public AccidenteService(AccidenteRepository repository) {
        super(repository);
    }
}
