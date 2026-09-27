package com.DEVSenior.vetturno.controller;

import com.DEVSenior.vetturno.dto.VeterinarioDTO;
import com.DEVSenior.vetturno.dto.VeterinarioRequest;
import com.DEVSenior.vetturno.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @PostMapping
    public ResponseEntity<VeterinarioDTO> create(@Valid @RequestBody VeterinarioRequest request) {
        VeterinarioDTO dto = veterinarioService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<VeterinarioDTO>> getAll() {
        List<VeterinarioDTO> veterinarios = veterinarioService.findAll();
        return ResponseEntity.ok(veterinarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> getById(@PathVariable Long id) {
        VeterinarioDTO dto = veterinarioService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }


}
