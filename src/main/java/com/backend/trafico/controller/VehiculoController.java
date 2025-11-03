package com.backend.trafico.controller;

import com.backend.trafico.model.Vehiculo;
import com.backend.trafico.service.VehiculoService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    private final VehiculoService service;

    public VehiculoController(VehiculoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Vehiculo> getAll() { return service.findAll(); }

    @GetMapping("/{patente}")
    public Vehiculo getById(@PathVariable String patente) { return service.findById(patente); }

    @PostMapping
    public Vehiculo create(@Valid @RequestBody Vehiculo vehiculo) { return service.save(vehiculo); }

    @PutMapping("/{patente}")
    public Vehiculo update(@PathVariable String patente, @Valid @RequestBody Vehiculo vehiculo) {
        vehiculo.setPatente(patente);
        return service.update(patente, vehiculo);
    }

    @DeleteMapping("/{patente}")
    public void delete(@PathVariable String patente) { service.delete(patente); }
}
