package com.DEVSenior.vetturno.controller;

import com.DEVSenior.vetturno.dto.CitaDTO;
import com.DEVSenior.vetturno.dto.CitaRequest;
import com.DEVSenior.vetturno.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping
    public ResponseEntity<CitaDTO> create(@Valid @RequestBody CitaRequest request) {
        CitaDTO dto = citaService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<CitaDTO>> getAll() {
        List<CitaDTO> citas = citaService.findAll();
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaDTO> getById(@PathVariable Long id) {
        CitaDTO dto = citaService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/veterinario/{id}")
    public ResponseEntity<List<CitaDTO>> getByVeterinarioId(@PathVariable Long id) {
        List<CitaDTO> citas = citaService.findByVeterinarioId(id);
        return ResponseEntity.ok(citas);
    }


}
