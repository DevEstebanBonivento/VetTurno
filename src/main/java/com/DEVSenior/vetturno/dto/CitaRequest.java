package com.DEVSenior.vetturno.dto;



import java.time.LocalDateTime;

public record CitaRequest(
    LocalDateTime fechaHora,
    String motivo,
    Long mascotaId,
    Long veterinarioId
) {}
