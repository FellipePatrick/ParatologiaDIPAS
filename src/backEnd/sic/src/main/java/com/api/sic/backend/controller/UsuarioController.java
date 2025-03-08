package com.api.sic.backend.controller;

import java.net.URI;
import java.util.Optional;

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

import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.domain.Usuario.Role;
import com.api.sic.backend.dto.Usuario.UsuarioRequestDTO;
import com.api.sic.backend.dto.Usuario.UsuarioRequestUpdateDTO;
import com.api.sic.backend.dto.Usuario.UsuarioResponseDTO;
import com.api.sic.backend.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/usuarios/")
@AllArgsConstructor
public class UsuarioController {
    private final UsuarioService service;
    private final ModelMapper mapper;

    @GetMapping
    public Page<UsuarioResponseDTO> listAll(Pageable pageable) {
        Page<Usuario> usuariosPage = service.findAllUsers(pageable);
        return usuariosPage.map(this::convertToDto);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO usuario) {
        usuario.setRole(usuario.getRole().toUpperCase());
        Optional<Usuario> u = service.findByEmail(usuario.getEmail());
        if(u.isPresent()){
            Usuario us = u.get();
            switch (usuario.getRole()) {
                case "ADMINISTRADOR":
                    us.setRole(Role.ADMINISTRADOR);
                    break;
                case "GESTOR":
                    us.setRole(Role.GESTOR);
                    break;
                default:
                    us.setRole(Role.USUARIO);
                    break;
            }
            us.setDeletedAt(null);
            us.setNome(usuario.getNome());
            us.setTelefone(usuario.getTelefone());
            Usuario UsuarioUpdated = service.update(us, us.getId());
            return ResponseEntity.ok(convertToDto(UsuarioUpdated));
        }else{
            Usuario created = service.create(convertToEntity(usuario));
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("{id}")
                    .buildAndExpand(created.getId())
                    .toUri();
            return ResponseEntity.created(location).body(convertToDto(created));
        }
    }

    
    @GetMapping("{id}")
    public ResponseEntity<UsuarioResponseDTO> listById(@PathVariable("id") Long id) {
        Usuario p = service.findById(id);
        UsuarioResponseDTO dto = mapper.map(p, UsuarioResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        service.deleteById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<UsuarioResponseDTO> update(@Valid @RequestBody UsuarioRequestUpdateDTO requestDto, @PathVariable("id") Long id) {
        try {
            @SuppressWarnings("unused")
            Usuario p = service.findById(id);
        } catch (Exception e) {
            UsuarioRequestDTO requestDto2 = mapper.map(requestDto, UsuarioRequestDTO.class);
            return this.create(requestDto2);
        }
        Usuario usuario = mapper.map(requestDto, Usuario.class);
        Usuario UsuarioUpdated = service.update(usuario, id);
        return ResponseEntity.ok(convertToDto(UsuarioUpdated));
    }
    
    private UsuarioResponseDTO convertToDto(Usuario created) {
        UsuarioResponseDTO UsuarioResponseDTO = mapper.map(created, UsuarioResponseDTO.class);
        UsuarioResponseDTO.addLinks(created);
        return UsuarioResponseDTO;
    }

    private Usuario convertToEntity(UsuarioRequestDTO usuario) {
        Usuario entityPessoa = mapper.map(usuario, Usuario.class);
        return entityPessoa;
    }
}