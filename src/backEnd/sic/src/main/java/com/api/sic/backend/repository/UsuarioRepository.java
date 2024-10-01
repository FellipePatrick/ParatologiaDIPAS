package com.api.sic.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.sic.backend.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    
    boolean existsByMatricula(String matricula);
}