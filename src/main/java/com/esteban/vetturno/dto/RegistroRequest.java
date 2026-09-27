package com.esteban.vetturno.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record RegistroRequest(
    String email,
    String password
) {}
