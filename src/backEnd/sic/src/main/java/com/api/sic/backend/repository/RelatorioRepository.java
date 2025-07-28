package com.api.sic.backend.repository;
import com.api.sic.backend.domain.Relatorio;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Long>{

    @Query("SELECT r FROM Relatorio r WHERE r.usuario.email = :email OR r.gestor.email = :email")
    Page<Relatorio> findByUsuarioEmail(String email, Pageable pageable);

}