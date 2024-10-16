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

import com.api.sic.backend.domain.Chamado;
import com.api.sic.backend.dto.chamado.ChamadoRequestDTO;
import com.api.sic.backend.dto.chamado.ChamadoResponseDTO;
import com.api.sic.backend.dto.chamado.ChamadoUpdateRequestDTO;
import com.api.sic.backend.service.ChamadoService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/chamados/")
@AllArgsConstructor
public class ChamadoController {
    private final ChamadoService service;
    private final ModelMapper mapper;

    @GetMapping
    public Page<ChamadoResponseDTO> listAll(Pageable pageable) {
        Page<Chamado> chamadoPage = service.listAll(pageable);
        return chamadoPage.map(this::convertToDto);
    }

    private ChamadoResponseDTO convertToDto(Chamado chamado) {
        ChamadoResponseDTO chamadoResponseDTO = mapper.map(chamado, ChamadoResponseDTO.class);
        return chamadoResponseDTO;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponseDTO> create(@Valid @RequestBody ChamadoRequestDTO chamado) {
        Chamado created = service.create(convertToEntity(chamado));
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(convertToDto(created));
    }
    @GetMapping("{id}")
    public ResponseEntity<ChamadoResponseDTO> listById(@PathVariable("id") Long id) {
        Chamado chamado = service.findById(id);
        ChamadoResponseDTO dto = mapper.map(chamado, ChamadoResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        service.deleteById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<ChamadoResponseDTO> update(@Valid @PathVariable("id") Long id, @RequestBody ChamadoUpdateRequestDTO chamadoUpdate) {
        Chamado entityToUpdate = convertToEntity(chamadoUpdate);
        Chamado updated = service.update(entityToUpdate, id);

        return ResponseEntity.ok(convertToDto(updated));
    }

    private Chamado convertToEntity(ChamadoUpdateRequestDTO chamadoUpdate) {
        Chamado entityChamado = mapper.map(chamadoUpdate, Chamado.class);
        return entityChamado;
    }

    private Chamado convertToEntity(ChamadoRequestDTO chamadoUpdate) {
        Chamado entityChamado = mapper.map(chamadoUpdate, Chamado.class);
        return entityChamado;
    }
}
