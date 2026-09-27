package com.DEVSenior.vetturno.dto;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;



public record CitaRequest(
    @NotNull(message = "Las fecha y las hora son obligatorias")
    @Future(message = "La fecha debe ser futura") LocalDateTime fechaHora,
    @NotBlank(message = "El motivo es obligatorio") String motivo,
    @NotNull(message = "El mascotaId es obligatorio") Long mascotaId,
    @NotNull(message = "El veterinarioId es obligatorio") Long veterinarioId
) {}
