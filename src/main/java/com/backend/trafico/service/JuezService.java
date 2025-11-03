package com.backend.trafico.service;

import com.backend.trafico.model.Juez;
import com.backend.trafico.repository.JuezRepository;
import org.springframework.stereotype.Service;

@Service
public class JuezService extends BaseService<Juez, Integer>{
    public JuezService(JuezRepository repository) {
        super(repository);
    }
}
