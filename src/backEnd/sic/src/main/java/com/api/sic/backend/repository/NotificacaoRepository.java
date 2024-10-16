package com.api.sic.backend.repository;

import com.api.sic.backend.domain.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long>{
    
}
