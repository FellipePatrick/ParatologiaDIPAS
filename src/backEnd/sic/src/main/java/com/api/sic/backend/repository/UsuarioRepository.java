package com.api.sic.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.sic.backend.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    
    boolean existsByMatricula(String matricula);

   @Query(value = "SELECT u FROM usuario u WHERE u.deletedAt IS NULL", nativeQuery = true)
   List<Usuario> findAllWhereDeletedAtIsNull();
}