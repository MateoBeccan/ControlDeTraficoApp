package com.backend.trafico.controller;

import com.backend.trafico.model.Multa;
import com.backend.trafico.service.MultaService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/multas")
@CrossOrigin(origins = "*")
public class MultaController {

    private final MultaService service;

    public MultaController(MultaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Multa> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Multa getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public Multa create(@Valid @RequestBody Multa multa) {
        return service.save(multa);
    }

    @PutMapping("/{id}")
    public Multa update(@PathVariable Integer id, @Valid @RequestBody Multa multa) {
        multa.setNumero(id);
        return service.update(id, multa);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
