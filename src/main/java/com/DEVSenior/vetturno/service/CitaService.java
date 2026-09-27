package com.DEVSenior.vetturno.service;

import com.DEVSenior.vetturno.dto.CitaDTO;
import com.DEVSenior.vetturno.dto.CitaRequest;
import com.DEVSenior.vetturno.exception.RecursoNoEncontradoException;
import com.DEVSenior.vetturno.model.Cita;
import com.DEVSenior.vetturno.model.Mascota;
import com.DEVSenior.vetturno.model.Veterinario;
import com.DEVSenior.vetturno.repository.CitaRepository;
import com.DEVSenior.vetturno.repository.MascotaRepository;
import com.DEVSenior.vetturno.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitaService {

    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;

    public CitaService(CitaRepository citaRepository, MascotaRepository mascotaRepository, VeterinarioRepository veterinarioRepository) {
        this.citaRepository = citaRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    public CitaDTO save(CitaRequest request) {
        // Validar que la fecha sea futura
        if (request.fechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la cita debe ser futura");
        }

        // Buscar mascota
        Mascota mascota = mascotaRepository.findById(request.mascotaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota con ID " + request.mascotaId() + " no encontrada"));

        // Buscar veterinario
        Veterinario veterinario = veterinarioRepository.findById(request.veterinarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario con ID " + request.veterinarioId() + " no encontrado"));

        // Validar que no exista cita para el veterinario en esa fechaHora exacta
        if (citaRepository.existsByVeterinarioIdAndFechaHora(request.veterinarioId(), request.fechaHora())) {
            throw new IllegalArgumentException("El veterinario ya tiene una cita programada para esa fecha y hora");
        }

        Cita cita = new Cita(null, request.fechaHora(), request.motivo(), mascota, veterinario);
        Cita saved = citaRepository.save(cita);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> findAll() {
        return citaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CitaDTO findById(Long id) {
        return citaRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> findByVeterinarioId(Long veterinarioId) {
        return citaRepository.findAll()
                .stream()
                .filter(cita -> cita.getVeterinario().getId().equals(veterinarioId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    private CitaDTO toDTO(Cita cita) {
        return new CitaDTO(
                cita.getId(),
                cita.getFechaHora(),
                cita.getMotivo(),
                cita.getMascota().getNombre(),
                cita.getMascota().getPropietario().getNombre(),
                cita.getVeterinario().getNombre()
        );
    }
}
