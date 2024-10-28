package com.api.sic.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.sic.backend.domain.Chamado;
import com.api.sic.backend.repository.ChamadoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ChamadoService extends GenericService<Chamado, Long, ChamadoRepository> {

    private ChamadoRepository repository;

    public ChamadoService(ChamadoRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Chamado update(Chamado entity, Long id) {
        Chamado c = this.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado com id: " + id));
        entity.setId(id);
        entity.setAssunto(c.getAssunto());
        entity.setDescricao(c.getDescricao());
        entity.setDono(c.getDono());
        entity.setStatus(entity.getStatus());
        return this.repository.saveAndFlush(entity);
    }

    @Override
    public List<Chamado> findAll() {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}