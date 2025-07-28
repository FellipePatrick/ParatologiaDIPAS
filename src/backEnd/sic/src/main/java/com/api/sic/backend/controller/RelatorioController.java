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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.sic.backend.domain.Relatorio;
import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.domain.enumerates.StatusRelatorio;
import com.api.sic.backend.dto.Relatorio.RelatorioRequestDTO;
import com.api.sic.backend.dto.Relatorio.RelatorioResponseDTO;
import com.api.sic.backend.dto.Relatorio.RelatorioUpdateRequestDTO;
import com.api.sic.backend.service.RelatorioService;
import com.api.sic.backend.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/relatorios/")
@AllArgsConstructor
public class RelatorioController {

    private final RelatorioService service;
    private final UsuarioService usuarioService;
    private final ModelMapper mapper;

    @GetMapping
    public Page<RelatorioResponseDTO> listAll(Pageable pageable) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(username).get();

        if( usuario.getRole().equals(Usuario.Role.ADMINISTRADOR)){
            Page<Relatorio> relatoriosPage = service.listAll(pageable);
            return relatoriosPage.map(this::convertToDto);
        }

        Page<Relatorio> relatoriosPage = service.findByEmail(username, pageable);
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
    
 
        if (id == null || id <= 0) 
            return null;

        Relatorio relatorio = service.findById(id);

        if(relatorio== null || !isDonoAdminGestor(relatorio))
            return null;
            
        RelatorioResponseDTO dto = mapper.map(relatorio, RelatorioResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
        Relatorio relatorio = service.findById(id);
    
        if (relatorio == null || !isDonoAdminGestor(relatorio)) {
            return ResponseEntity.badRequest().body(" ");
        }
    
        service.deleteById(id);
        return ResponseEntity.noContent().build(); 
    }
    

    @PutMapping("{id}")
    public ResponseEntity<RelatorioResponseDTO> update(@Valid @PathVariable("id") Long id, @RequestBody RelatorioUpdateRequestDTO relatorioUpdate) {
        
        if (id == null || id <= 0) 
            return null;

        Relatorio r = service.findById(id);

        if(r == null || !isDonoAdminGestor(r))
            return null;

        r.setDescricao(relatorioUpdate.getDescricao());
        r.setTitulo(relatorioUpdate.getTitulo());
        if(r.getStatus() == StatusRelatorio.RASCUNHO) {
            r.setStatus(StatusRelatorio.PENDENTE);
        }      
        
        if(relatorioUpdate.getStatus().equalsIgnoreCase("PENDENTE"))
            r.setStatus(StatusRelatorio.PENDENTE);
        else if(relatorioUpdate.getStatus().equalsIgnoreCase("AVALIANDO"))
            r.setStatus(StatusRelatorio.AVALIANDO);
        else if(relatorioUpdate.getStatus().equalsIgnoreCase("FINALIZADO"))
            r.setStatus(StatusRelatorio.FINALIZADO);
        else if(relatorioUpdate.getStatus().equalsIgnoreCase("REJEITADO"))
            r.setStatus(StatusRelatorio.REJEITADO);

        Relatorio updated = service.update(r, id);

        return ResponseEntity.ok(convertToDto(updated));
    }

    private RelatorioResponseDTO convertToDto(Relatorio relatorio) {
        RelatorioResponseDTO relatorioResponseDTO = mapper.map(relatorio, RelatorioResponseDTO.class);
        return relatorioResponseDTO;
    }

    private Relatorio convertToEntity(RelatorioRequestDTO relatorio) {
        Relatorio entityRelatorio = mapper.map(relatorio, Relatorio.class);
        return entityRelatorio;
    }

    private boolean isDonoAdminGestor(Relatorio relatorio){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(username).get();

        return relatorio.getUsuario().getEmail().equals(usuario.getEmail()) || relatorio.getUsuario().getGestor().getEmail().equals(usuario.getEmail()) || usuario.getRole().equals(Usuario.Role.ADMINISTRADOR);
    }
}
