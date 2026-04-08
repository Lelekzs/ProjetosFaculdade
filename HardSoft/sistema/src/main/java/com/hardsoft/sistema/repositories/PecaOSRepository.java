package com.hardsoft.sistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hardsoft.sistema.entities.PecaOSEntity;

@Repository
public interface PecaOSRepository extends JpaRepository<PecaOSEntity, Long>{

}
