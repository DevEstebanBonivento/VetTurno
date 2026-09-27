package com.DEVSenior.vetturno.repository;

import com.DEVSenior.vetturno.model.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, Long> {
}
