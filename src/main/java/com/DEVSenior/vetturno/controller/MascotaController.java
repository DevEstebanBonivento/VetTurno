package com.DEVSenior.vetturno.controller;

import com.DEVSenior.vetturno.dto.MascotaDTO;
import com.DEVSenior.vetturno.dto.MascotaRequest;
import com.DEVSenior.vetturno.service.MascotaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @PostMapping
    public ResponseEntity<MascotaDTO> create(@Valid @RequestBody MascotaRequest request) {
        MascotaDTO dto = mascotaService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<MascotaDTO>> getAll() {
        List<MascotaDTO> mascotas = mascotaService.findAll();
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> getById(@PathVariable Long id) {
        MascotaDTO dto = mascotaService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }


}
