package com.esteban.vetturno.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record CitaRequest(
    LocalDateTime fechaHora,
    String motivo,
    Long mascotaId,
    Long veterinarioId
) {}
