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
import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.domain.enumerates.StatusChamado;
import com.api.sic.backend.dto.chamado.ChamadoRequestDTO;
import com.api.sic.backend.dto.chamado.ChamadoResponseDTO;
import com.api.sic.backend.dto.chamado.ChamadoUpdateRequestDTO;
import com.api.sic.backend.service.ChamadoService;
import com.api.sic.backend.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/chamados/")
@AllArgsConstructor
public class ChamadoController {
    private final ChamadoService service;
    private final UsuarioService usuarioService;
    private final ModelMapper mapper;

    @GetMapping
    public Page<ChamadoResponseDTO> listAll(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(username).get();

        if( usuario.getRole().equals(Usuario.Role.ADMINISTRADOR)){
            Page<Chamado> chamadoPage = service.listAll(pageable);
            return chamadoPage.map(this::convertToDto);
        }

        Page<Chamado> chamadoPage = service.findByEmail(username, pageable);
        return chamadoPage.map(this::convertToDto);
    }

    private ChamadoResponseDTO convertToDto(Chamado chamado) {
        ChamadoResponseDTO chamadoResponseDTO = mapper.map(chamado, ChamadoResponseDTO.class);
        return chamadoResponseDTO;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponseDTO> create(@Valid @RequestBody ChamadoRequestDTO chamado) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(username).get();
        chamado.setDono(usuario);
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

        if (id == null || id <= 0) 
           return null;

        
        Chamado c = service.findById(id);
        if(c == null || !isDonoAdminGestor(c))
            return null;
        
        ChamadoResponseDTO dto = mapper.map(c, ChamadoResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        Chamado c = new Chamado();
        if (id == null || id <= 0) 
            c = service.findById(id);

        if(!(c == null || !isDonoAdminGestor(c)) && !(id == null || id <= 0) )
            service.deleteById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<ChamadoResponseDTO> update(@Valid @PathVariable("id") Long id, @RequestBody ChamadoUpdateRequestDTO chamadoUpdate) {
        Chamado entityToUpdate = convertToEntity(chamadoUpdate);
       
        if (id == null || id <= 0) 
        return null;
     
        entityToUpdate = service.findById(id);
        if(entityToUpdate == null || !isDonoAdminGestor(entityToUpdate))
            return null;
        
        switch (chamadoUpdate.getStatus()) {
            case "ABERTO":
                entityToUpdate.setStatus(StatusChamado.ABERTO);       
                break;
            case "ANDAMENTO":
                entityToUpdate.setStatus(StatusChamado.ANDAMENTO);
                break;
            case "FECHADO":
                entityToUpdate.setStatus(StatusChamado.CONCLUIDO);
                break;
            default:
                entityToUpdate.setStatus(StatusChamado.ABERTO);
                break;
        }
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

    private boolean isDonoAdminGestor(Chamado chamado){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(username).get();

        return chamado.getDono().getEmail().equals(usuario.getEmail()) || usuario.getRole().equals(Usuario.Role.ADMINISTRADOR);
    }
}
