package com.DEVSenior.vetturno.controller;

import com.DEVSenior.vetturno.dto.MascotaDTO;
import com.DEVSenior.vetturno.dto.MascotaRequest;
import com.DEVSenior.vetturno.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @PostMapping
    public ResponseEntity<MascotaDTO> create(@RequestBody MascotaRequest request) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mascotaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
