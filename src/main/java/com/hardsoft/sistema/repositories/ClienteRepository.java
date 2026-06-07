package com.hardsoft.sistema.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hardsoft.sistema.entities.ClienteEntity;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByCpfCnpj(String cpfCnpj);

    Optional<ClienteEntity> findByEmail(String email);

    boolean existsByCpfCnpj(String cpfCnpj);

    boolean existsByEmail(String email);
}
