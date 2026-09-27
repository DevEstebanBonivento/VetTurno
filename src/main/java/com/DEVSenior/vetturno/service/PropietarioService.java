package com.DEVSenior.vetturno.service;

import com.DEVSenior.vetturno.dto.PropietarioDTO;
import com.DEVSenior.vetturno.dto.PropietarioRequest;
import com.DEVSenior.vetturno.model.Propietario;
import com.DEVSenior.vetturno.repository.PropietarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropietarioService {

    @Autowired
    private PropietarioRepository propietarioRepository;

    public PropietarioDTO save(PropietarioRequest request) {
        Propietario propietario = new Propietario(null, request.nombre(), request.telefono(), request.email());
        Propietario saved = propietarioRepository.save(propietario);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<PropietarioDTO> findAll() {
        return propietarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PropietarioDTO findById(Long id) {
        return propietarioRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public void deleteById(Long id) {
        propietarioRepository.deleteById(id);
    }

    private PropietarioDTO toDTO(Propietario propietario) {
        return new PropietarioDTO(
                propietario.getId(),
                propietario.getNombre(),
                propietario.getTelefono(),
                propietario.getEmail()
        );
    }
}
