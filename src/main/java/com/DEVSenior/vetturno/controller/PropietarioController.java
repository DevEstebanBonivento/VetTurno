package com.DEVSenior.vetturno.controller;

import com.DEVSenior.vetturno.dto.PropietarioDTO;
import com.DEVSenior.vetturno.dto.PropietarioRequest;
import com.DEVSenior.vetturno.service.PropietarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propietarios")
public class PropietarioController {

    private final PropietarioService propietarioService;

    public PropietarioController(PropietarioService propietarioService) {
        this.propietarioService = propietarioService;
    }

    @PostMapping
    public ResponseEntity<PropietarioDTO> create(@RequestBody PropietarioRequest request) {
        PropietarioDTO dto = propietarioService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<PropietarioDTO>> getAll() {
        List<PropietarioDTO> propietarios = propietarioService.findAll();
        return ResponseEntity.ok(propietarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropietarioDTO> getById(@PathVariable Long id) {
        PropietarioDTO dto = propietarioService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propietarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
