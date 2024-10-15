package com.api.sic.backend.service;

import java.util.List;
import java.util.Optional;

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
        if ( entity.getGestor() == null || !entity.getGestor().getRole().toString().equalsIgnoreCase("GESTOR")) {
            throw new IllegalArgumentException("Somente Gestores podem orientar um relatorio");
        }
        return this.repository.save(entity);
    }

    @Override
    public Relatorio update(Relatorio entity, Long id) {
        entity.setId(id); 
        Optional<Relatorio> before = this.repository.findById(id);
        entity.setGestor(before.get().getGestor());
        entity.setUsuario(before.get().getUsuario());
        return this.repository.saveAndFlush(entity);
    }



    @Override
    public List<Relatorio> findAll() {
        return this.repository.findAll();
    }
}
