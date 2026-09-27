package com.DEVSenior.vetturno.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record VeterinarioDTO(
    Long id,
    String nombre,
    String especialidad
) {}
