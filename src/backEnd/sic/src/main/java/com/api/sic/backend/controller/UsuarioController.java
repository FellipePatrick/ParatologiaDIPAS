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
import org.springframework.web.server.ResponseStatusException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/usuarios/")
@AllArgsConstructor
public class UsuarioController {
    private final UsuarioService service;
    private final ModelMapper mapper;


    private boolean isAdmin(){
        return retornaUser().getRole().equals(Usuario.Role.ADMINISTRADOR);
    }

    private boolean isGestor(){
        return retornaUser().getRole().equals(Usuario.Role.GESTOR);
    }

    private Usuario retornaUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario user = service.findByEmail(username).get();
        return user;
    }

    @GetMapping
    public Page<UsuarioResponseDTO> listAll(Pageable pageable) {
        if(!isAdmin() && !isGestor()){
            return null;
        }
        Page<Usuario> usuariosPage = null;
        if(isAdmin())
            usuariosPage = service.findAllUsers(pageable);
        
        if(isGestor())
            usuariosPage = service.findAllUsersGestor(pageable, retornaUser());
        

        return  usuariosPage.map(this::convertToDto) ;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO usuario) {
        if(!isAdmin() && !isGestor()){
            return null;
        }
        usuario.setRole(usuario.getRole().toUpperCase());
        Optional<Usuario> u = service.findByEmail(usuario.getEmail());
        if(u.isPresent()){
            Usuario us = u.get();
            if(isAdmin()){
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
            }
            if(isGestor())
                us.setRole(Role.USUARIO);
            us.setDeletedAt(null);
            us.setNome(usuario.getNome());
            us.setTelefone(usuario.getTelefone());
            Usuario UsuarioUpdated = service.update(us, us.getId());
            return ResponseEntity.ok(convertToDto(UsuarioUpdated));
        }else{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Usuario gestor = service.findByEmail(username).get();

            usuario.setGestor(gestor);
            if(isGestor())
                usuario.setRole("USUARIO");

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
        if(isAdmin() || retornaUser().getId().equals(id) || isGestor()){
            if(isGestor() && !retornaUser().getId().equals(id)){
                Usuario p = service.findByIdGestor(id, retornaUser());
                UsuarioResponseDTO dto = mapper.map(p, UsuarioResponseDTO.class);
                return ResponseEntity.ok(dto);
            }
            Usuario p = service.findById(id);
            UsuarioResponseDTO dto = mapper.map(p, UsuarioResponseDTO.class);
            return ResponseEntity.ok(dto);
        }
        return null;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        if (!isAdmin() && !isGestor()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas pessoas autorizadas podem deletar usuários.");
        }
        if (!retornaUser().getId().equals(id)) {
            if(isGestor()){
                Usuario p = service.findByIdGestor(id, retornaUser());
                if(p == null || p.getEmail() != null)
                    service.deleteById(id);
            }
            if(isAdmin())
                service.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode deletar a si mesmo.");
        }
    }


    @PutMapping("{id}")
    public ResponseEntity<UsuarioResponseDTO> update(@Valid @RequestBody UsuarioRequestUpdateDTO requestDto, @PathVariable("id") Long id) {
        if(!isAdmin() && !isGestor())
            return null;

        if((retornaUser().getId().equals(id)))
            return null;

       if(isGestor()){
        Usuario p = service.findByIdGestor(id, retornaUser());
            if(p == null || p.getEmail() == null)
                return null;
       }
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