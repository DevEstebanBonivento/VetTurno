package com.DEVSenior.vetturno.service;

import com.DEVSenior.vetturno.dto.VeterinarioDTO;
import com.DEVSenior.vetturno.dto.VeterinarioRequest;
import com.DEVSenior.vetturno.model.Veterinario;
import com.DEVSenior.vetturno.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioService(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    public VeterinarioDTO save(VeterinarioRequest request) {
        Veterinario veterinario = new Veterinario(null, request.nombre(), request.especialidad());
        Veterinario saved = veterinarioRepository.save(veterinario);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<VeterinarioDTO> findAll() {
        return veterinarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VeterinarioDTO findById(Long id) {
        return veterinarioRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public void deleteById(Long id) {
        veterinarioRepository.deleteById(id);
    }

    private VeterinarioDTO toDTO(Veterinario veterinario) {
        return new VeterinarioDTO(
                veterinario.getId(),
                veterinario.getNombre(),
                veterinario.getEspecialidad()
        );
    }
}
