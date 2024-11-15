package com.api.sic.backend.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.api.sic.backend.domain.Imagem;
import com.api.sic.backend.repository.ImagemRepository;


    
@Service
public class ImagemService extends GenericService<Imagem, Long, ImagemRepository>{
     
    @SuppressWarnings("unused")
    private ImagemRepository repository;
    
    public ImagemService(ImagemRepository repository){
        super(repository);
        this.repository = repository;
    }

    public List<Imagem> findByRelatorioId(Long id){
        return repository.findByRelatorioId(id);
    }

    @Override
    public List<Imagem> findAll() {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}