package com.esteban.vetturno.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record PropietarioRequest(
    String nombre,
    String telefono,
    String email
) {}
