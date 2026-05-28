package com.hardsoft.sistema.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hardsoft.sistema.entities.OrdemServicoEntity;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServicoEntity, Long> {

    List<OrdemServicoEntity> findByClienteId(Long clienteId);

    List<OrdemServicoEntity> findByAdminId(Long adminId);

    List<OrdemServicoEntity> findBySetupIdSetup(Long idSetup);

    List<OrdemServicoEntity> findByStatusIgnoreCase(String status);
}
