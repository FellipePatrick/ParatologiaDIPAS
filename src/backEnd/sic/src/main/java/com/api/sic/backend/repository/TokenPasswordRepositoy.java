package com.api.sic.backend.repository;

import com.api.sic.backend.domain.TokenPassword;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenPasswordRepositoy extends JpaRepository<TokenPassword, Long>{
    @Query("SELECT r FROM TokenPassword r WHERE r.usuario.email = :email AND r.ativo")
    ArrayList<TokenPassword> findTokenAtivo(String email);
    
    Optional<TokenPassword> findByToken(String token);

}