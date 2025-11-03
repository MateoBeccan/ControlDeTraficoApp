package com.backend.trafico.controller;

import com.backend.trafico.model.DetalleAccidente;
import com.backend.trafico.model.DetalleAccidenteId;
import com.backend.trafico.service.DetalleAccidenteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/detalle-accidentes")
@CrossOrigin(origins = "*")
public class DetalleAccidenteController {

    private final DetalleAccidenteService service;

    public DetalleAccidenteController(DetalleAccidenteService service) {
        this.service = service;
    }

    @GetMapping
    public List<DetalleAccidente> getAll() {
        return service.findAll();
    }

    @GetMapping("/buscar")
    public DetalleAccidente getById(@RequestParam Integer accidente, @RequestParam String vehiculo) {
        DetalleAccidenteId id = new DetalleAccidenteId();
        id.setAccidente(accidente);
        id.setVehiculo(vehiculo);
        return service.findById(id);
    }

    @PostMapping
    public DetalleAccidente create(@RequestBody DetalleAccidente detalle) {
        return service.save(detalle);
    }

    @PutMapping
    public DetalleAccidente update(@RequestBody DetalleAccidente detalle) {
        DetalleAccidenteId id = new DetalleAccidenteId();
        id.setAccidente(detalle.getAccidente().getNumero());
        id.setVehiculo(detalle.getVehiculo().getPatente());
        return service.update(id, detalle);
    }

    @DeleteMapping
    public void delete(@RequestParam Integer accidente, @RequestParam String vehiculo) {
        DetalleAccidenteId id = new DetalleAccidenteId();
        id.setAccidente(accidente);
        id.setVehiculo(vehiculo);
        service.delete(id);
    }
}
