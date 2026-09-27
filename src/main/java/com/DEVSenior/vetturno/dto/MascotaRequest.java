package com.DEVSenior.vetturno.dto;



public record MascotaRequest(
    String nombre,
    String especie,
    String raza,
    Long propietarioId
) {}
