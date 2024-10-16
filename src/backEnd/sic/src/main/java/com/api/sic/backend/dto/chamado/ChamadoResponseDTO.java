package com.api.sic.backend.dto.chamado;

import org.springframework.hateoas.RepresentationModel;

import com.api.sic.backend.controller.ChamadoController;
import com.api.sic.backend.domain.Chamado;
import com.api.sic.backend.dto.usuario.UsuarioDefaultResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChamadoResponseDTO extends RepresentationModel<ChamadoResponseDTO> {
    private String assunto;
    private String descricao;
    private String status;
    private UsuarioDefaultResponseDTO dono;

     public void addLinks(Chamado chamado){
        this.add(linkTo(ChamadoController.class).slash(chamado.getId()).withSelfRel());
    }
}
