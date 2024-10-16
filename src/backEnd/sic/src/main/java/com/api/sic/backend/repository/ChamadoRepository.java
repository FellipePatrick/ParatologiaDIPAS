package com.api.sic.backend.repository;

import com.api.sic.backend.domain.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChamadoRepository extends JpaRepository<Chamado, Long>{
    
}
