package com.hardsoft.sistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hardsoft.sistema.entities.PecaEntity;

@Repository
public interface PecaRepository extends JpaRepository<PecaEntity, Long> {
}