package com.api.sic.backend.repository;

import com.api.sic.backend.domain.Chamado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChamadoRepository extends JpaRepository<Chamado, Long>{
    @Query("SELECT c FROM Chamado c WHERE c.dono.email = :email ")
    Page<Chamado> findByUsuarioEmail(String email, Pageable pageable);
    
}
