package com.DEVSenior.vetturno.repository;

import com.DEVSenior.vetturno.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
}
