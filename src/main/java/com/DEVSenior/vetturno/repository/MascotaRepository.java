package com.DEVSenior.vetturno.repository;

import com.DEVSenior.vetturno.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
}
