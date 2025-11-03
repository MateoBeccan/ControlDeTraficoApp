package com.backend.trafico.service;

import com.backend.trafico.model.Persona;
import com.backend.trafico.repository.PersonaRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonaService extends BaseService<Persona, Integer> {
    public PersonaService(PersonaRepository repository) {
        super(repository);
    }
}
