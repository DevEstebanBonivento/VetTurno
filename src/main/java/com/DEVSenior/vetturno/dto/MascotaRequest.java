package com.DEVSenior.vetturno.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record MascotaRequest(
    String nombre,
    String especie,
    String raza,
    Long propietarioId
) {}
