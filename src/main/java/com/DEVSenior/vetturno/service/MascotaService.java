package com.DEVSenior.vetturno.service;

import com.DEVSenior.vetturno.dto.MascotaDTO;
import com.DEVSenior.vetturno.dto.MascotaRequest;
import com.DEVSenior.vetturno.model.Mascota;
import com.DEVSenior.vetturno.model.Propietario;
import com.DEVSenior.vetturno.repository.MascotaRepository;
import com.DEVSenior.vetturno.repository.PropietarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private PropietarioRepository propietarioRepository;

    public MascotaDTO save(MascotaRequest request) {
        Propietario propietario = propietarioRepository.findById(request.propietarioId())
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Mascota mascota = new Mascota(null, request.nombre(), request.especie(), request.raza(), propietario);
        Mascota saved = mascotaRepository.save(mascota);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<MascotaDTO> findAll() {
        return mascotaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MascotaDTO findById(Long id) {
        return mascotaRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public void deleteById(Long id) {
        mascotaRepository.deleteById(id);
    }

    private MascotaDTO toDTO(Mascota mascota) {
        return new MascotaDTO(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getPropietario().getId(),
                mascota.getPropietario().getNombre()
        );
    }
}
