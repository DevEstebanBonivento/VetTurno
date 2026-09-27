package com.esteban.vetturno.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record PropietarioDTO(
    Long id,
    String nombre,
    String telefono,
    String email
) {}
