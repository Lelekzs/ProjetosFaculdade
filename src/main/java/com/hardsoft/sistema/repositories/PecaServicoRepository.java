package com.hardsoft.sistema.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hardsoft.sistema.entities.PecaServicoEntity;

@Repository
public interface PecaServicoRepository extends JpaRepository<PecaServicoEntity, Long> {
    List<PecaServicoEntity> findByServicoIdServico(Long idServico);
}
