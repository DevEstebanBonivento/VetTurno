package com.DEVSenior.vetturno.model;

import jakarta.persistence.*;

@Entity
@Table(name = "propietario")
public class Propietario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String email;

    public Propietario() {
    }

    public Propietario(Long id, String nombre, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
