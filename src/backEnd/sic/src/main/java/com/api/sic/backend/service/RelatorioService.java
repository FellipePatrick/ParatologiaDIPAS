package com.api.sic.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.sic.backend.domain.Relatorio;
import com.api.sic.backend.repository.RelatorioRepository;

@Service
public class RelatorioService extends GenericService<Relatorio, Long, RelatorioRepository> {

    public RelatorioService(RelatorioRepository repository) {
        super(repository);
    }

    @Override
    public Relatorio create(Relatorio entity) {
        validateGestor(entity);
        return this.repository.save(entity);
    }

    @Override
    public Relatorio update(Relatorio entity, Long id) {
        validateGestor(entity);
        entity.setId(id); 
        return this.repository.saveAndFlush(entity);
    }

    private void validateGestor(Relatorio entity) {
        if ( entity.getGestor() == null || !entity.getGestor().getRole().toString().equalsIgnoreCase("GESTOR")) {
            throw new IllegalArgumentException("Somente Gestores podem orientar um relatorio");
        }
    }

    @Override
    public List<Relatorio> findAll() {
        return this.repository.findAll();
    }
}
