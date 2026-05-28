package com.hardsoft.sistema.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hardsoft.sistema.entities.TipoServicoEntity;

@Repository
public interface TipoServicoRepository extends JpaRepository<TipoServicoEntity, Long> {
    Optional<TipoServicoEntity> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
