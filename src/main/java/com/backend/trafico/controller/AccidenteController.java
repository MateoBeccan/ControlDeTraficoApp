package com.backend.trafico.controller;

import com.backend.trafico.model.Accidente;
import com.backend.trafico.service.AccidenteService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/accidentes")
@CrossOrigin(origins = "*")
public class AccidenteController {

    private final AccidenteService service;

    public AccidenteController(AccidenteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Accidente> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Accidente getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public Accidente create(@Valid @RequestBody Accidente accidente) {
        return service.save(accidente);
    }

    @PutMapping("/{id}")
    public Accidente update(@PathVariable Integer id, @Valid @RequestBody Accidente accidente) {
        accidente.setNumero(id);
        return service.update(id, accidente);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
