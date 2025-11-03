package com.backend.trafico.controller;

import com.backend.trafico.model.Persona;
import com.backend.trafico.service.PersonaService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = "*")
public class PersonaController {

    private final PersonaService service;

    public PersonaController(PersonaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Persona> getAll() { return service.findAll(); }

    @GetMapping("/{dni}")
    public Persona getById(@PathVariable Integer dni) { return service.findById(dni); }

    @PostMapping
    public Persona create(@Valid @RequestBody Persona persona) { return service.save(persona); }

    @PutMapping("/{dni}")
    public Persona update(@PathVariable Integer dni, @Valid @RequestBody Persona persona) {
        persona.setDni(dni);
        return service.update(dni, persona);
    }

    @DeleteMapping("/{dni}")
    public void delete(@PathVariable Integer dni) { service.delete(dni); }
}
