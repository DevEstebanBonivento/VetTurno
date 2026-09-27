package com.DEVSenior.vetturno.dto;



import java.time.LocalDateTime;

public record CitaDTO(
    Long id,
    LocalDateTime fechaHora,
    String motivo,
    String mascotaNombre,
    String propietarioNombre,
    String veterinarioNombre
) {}
