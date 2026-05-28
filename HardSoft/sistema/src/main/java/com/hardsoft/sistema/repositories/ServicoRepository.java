package com.hardsoft.sistema.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hardsoft.sistema.entities.ServicoEntity;

@Repository
public interface ServicoRepository extends JpaRepository<ServicoEntity, Long> {
    List<ServicoEntity> findByOrdemServicoIdOrdemServico(Long idOrdemServico);
}
