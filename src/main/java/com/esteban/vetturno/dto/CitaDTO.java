package com.esteban.vetturno.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record CitaDTO(
    Long id,
    LocalDateTime fechaHora,
    String motivo,
    String mascotaNombre,
    String propietarioNombre,
    String veterinarioNombre
) {}
