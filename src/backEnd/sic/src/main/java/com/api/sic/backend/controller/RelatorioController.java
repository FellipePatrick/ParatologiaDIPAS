package com.api.sic.backend.controller;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; 
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.sic.backend.domain.Relatorio;
import com.api.sic.backend.domain.enumerates.StatusRelatorio;
import com.api.sic.backend.dto.relatorio.RelatorioRequestDTO;
import com.api.sic.backend.dto.relatorio.RelatorioResponseDTO;
import com.api.sic.backend.dto.relatorio.RelatorioUpdateRequestDTO;
import com.api.sic.backend.service.RelatorioService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/relatorios/")
@AllArgsConstructor
public class RelatorioController {

    private final RelatorioService service;
    private final ModelMapper mapper;

    @GetMapping
    public Page<RelatorioResponseDTO> listAll(Pageable pageable) {
        Page<Relatorio> relatoriosPage = service.listAll(pageable);
        return relatoriosPage.map(this::convertToDto);
    }

    @PostMapping
    public ResponseEntity<RelatorioResponseDTO> create(@Valid @RequestBody RelatorioRequestDTO relatorio) {
        Relatorio created = service.create(convertToEntity(relatorio));
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(convertToDto(created));
    }



    @GetMapping("{id}")
    public ResponseEntity<RelatorioResponseDTO> listById(@PathVariable("id") Long id) {
        Relatorio relatorio = service.findById(id);
        RelatorioResponseDTO dto = mapper.map(relatorio, RelatorioResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        service.deleteById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<RelatorioResponseDTO> update(@Valid @PathVariable("id") Long id, @RequestBody RelatorioUpdateRequestDTO relatorioUpdate) {
        Relatorio r = service.findById(id);

        r.setDescricao(relatorioUpdate.getDescricao());
        r.setTitulo(relatorioUpdate.getTitulo());
        if(r.getStatus() == StatusRelatorio.RASCUNHO) {
            r.setStatus(StatusRelatorio.PENDENTE);
        }
        Relatorio updated = service.update(r, id);

        return ResponseEntity.ok(convertToDto(updated));
    }

    private RelatorioResponseDTO convertToDto(Relatorio relatorio) {
        RelatorioResponseDTO relatorioResponseDTO = mapper.map(relatorio, RelatorioResponseDTO.class);
        return relatorioResponseDTO;
    }

    private Relatorio convertToEntity(RelatorioUpdateRequestDTO relatorioUpdate) {
        Relatorio entityRelatorio = mapper.map(relatorioUpdate, Relatorio.class);
        return entityRelatorio;
    }


    private Relatorio convertToEntity(RelatorioRequestDTO relatorio) {
        Relatorio entityRelatorio = mapper.map(relatorio, Relatorio.class);
        return entityRelatorio;
    }
}
