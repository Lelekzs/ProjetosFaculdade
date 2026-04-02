package com.hardsoft.sistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hardsoft.sistema.entities.TipoServicoEntity;

@Repository
public interface TipoServicoRepository extends JpaRepository<TipoServicoEntity, Long> {
}