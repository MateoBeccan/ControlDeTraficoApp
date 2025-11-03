package com.backend.trafico.controller;

import com.backend.trafico.model.Juez;
import com.backend.trafico.service.JuezService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jueces")
@CrossOrigin(origins = "*")
public class JuezController {

    private final JuezService service;

    public JuezController(JuezService service) {
        this.service = service;
    }

    @GetMapping
    public List<Juez> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Juez getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public Juez create(@RequestBody Juez juez) {
        return service.save(juez);
    }

    @PutMapping("/{id}")
    public Juez update(@PathVariable Integer id, @RequestBody Juez juez) {
        juez.setCodigo(id);
        return service.update(id, juez);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
