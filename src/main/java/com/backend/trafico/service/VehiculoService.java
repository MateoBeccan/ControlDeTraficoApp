package com.backend.trafico.service;

import com.backend.trafico.model.Vehiculo;
import com.backend.trafico.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService extends BaseService<Vehiculo, String> {
    public VehiculoService(VehiculoRepository repository) {
        super(repository);
    }
}
