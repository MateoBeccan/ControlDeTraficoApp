package com.backend.trafico.controller;

import com.backend.trafico.model.DetalleMulta;
import com.backend.trafico.model.DetalleMultaId;
import com.backend.trafico.service.DetalleMultaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/detalle-multas")
@CrossOrigin(origins = "*")
public class DetalleMultaController {

    private final DetalleMultaService service;

    public DetalleMultaController(DetalleMultaService service) {
        this.service = service;
    }

    @GetMapping
    public List<DetalleMulta> getAll() {
        return service.findAll();
    }

    @GetMapping("/buscar")
    public DetalleMulta getById(@RequestParam Integer multa, @RequestParam String vehiculo) {
        DetalleMultaId id = new DetalleMultaId();
        id.setMulta(multa);
        id.setVehiculo(vehiculo);
        return service.findById(id);
    }

    @PostMapping
    public DetalleMulta create(@RequestBody DetalleMulta detalle) {
        return service.save(detalle);
    }

    @PutMapping
    public DetalleMulta update(@RequestBody DetalleMulta detalle) {
        DetalleMultaId id = new DetalleMultaId();
        id.setMulta(detalle.getMulta().getNumero());
        id.setVehiculo(detalle.getVehiculo().getPatente());
        return service.update(id, detalle);
    }

    @DeleteMapping
    public void delete(@RequestParam Integer multa, @RequestParam String vehiculo) {
        DetalleMultaId id = new DetalleMultaId();
        id.setMulta(multa);
        id.setVehiculo(vehiculo);
        service.delete(id);
    }
}
