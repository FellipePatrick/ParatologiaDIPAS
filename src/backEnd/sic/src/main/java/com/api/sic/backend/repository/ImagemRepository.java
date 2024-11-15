package com.api.sic.backend.repository;

import com.api.sic.backend.domain.Imagem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, Long>{
    public List<Imagem> findByRelatorioId(Long id);
    
}
