package com.api.sic.backend.controller;
import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.sic.backend.domain.Notificacao;
import com.api.sic.backend.dto.notificacao.NotificacaoRequestDTO;
import com.api.sic.backend.dto.notificacao.NotificacaoResponseDTO;
import com.api.sic.backend.service.NotificacaoService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/notificacoes/")
@AllArgsConstructor
public class NotificacaoController {

    private final NotificacaoService service;
    private final ModelMapper mapper;

     @GetMapping
    public Page<NotificacaoResponseDTO> listAll(Pageable pageable) {
        Page<Notificacao> notificacaoPage = service.listAll(pageable);
        return notificacaoPage.map(this::convertToDto);
    }

    @PostMapping
    public ResponseEntity<NotificacaoResponseDTO> create(@Valid @RequestBody NotificacaoRequestDTO notificacao) {
        Notificacao created = service.create(convertToEntity(notificacao));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(convertToDto(created));
    }

    @GetMapping("{id}")
    public ResponseEntity<NotificacaoResponseDTO> listById(@PathVariable("id") Long id) {
        Notificacao notificacao = service.findById(id);
        NotificacaoResponseDTO dto = mapper.map(notificacao, NotificacaoResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        service.deleteById(id);
    }

    private NotificacaoResponseDTO convertToDto(Notificacao notificacao) {
        NotificacaoResponseDTO notificacaoResponseDTO = mapper.map(notificacao, NotificacaoResponseDTO.class);
        return notificacaoResponseDTO;
    }

     private Notificacao convertToEntity(NotificacaoRequestDTO notificacao) {
        Notificacao entityNotificacao = mapper.map(notificacao, Notificacao.class);
        return entityNotificacao;
    }
}
