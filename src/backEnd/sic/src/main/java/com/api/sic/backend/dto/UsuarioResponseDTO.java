package com.api.sic.backend.dto;
import com.api.sic.backend.controller.UsuarioController;
import com.api.sic.backend.domain.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UsuarioResponseDTO extends RepresentationModel<UsuarioResponseDTO>{
    private String nome;
    private String telefone;
    private String role; 
    private String email;
    private String matricula;
    private String senha;
    private Long id;
    private String pathImage;

    public void addLinks(Usuario usuario){
        this.add(linkTo(UsuarioController.class).slash(usuario.getId()).withSelfRel());
    }
}