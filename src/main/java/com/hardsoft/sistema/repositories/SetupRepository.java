package com.hardsoft.sistema.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hardsoft.sistema.entities.SetupEntity;

@Repository
public interface SetupRepository extends JpaRepository<SetupEntity, Long> {
    List<SetupEntity> findByClienteId(Long clienteId);
}
