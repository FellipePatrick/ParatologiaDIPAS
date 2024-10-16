package com.api.sic.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.sic.backend.domain.Notificacao;
import com.api.sic.backend.repository.NotificacaoRepository;

@Service
public class NotificacaoService extends GenericService<Notificacao, Long, NotificacaoRepository>{
    
    @SuppressWarnings("unused")
    private NotificacaoRepository repository;
    
    public NotificacaoService(NotificacaoRepository repository){
        super(repository);
        this.repository = repository;
    }

    @Override
    public List<Notificacao> findAll() {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
    