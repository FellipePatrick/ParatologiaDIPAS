package com.api.sic.backend.dto.notificacao;

import org.springframework.hateoas.RepresentationModel;

import com.api.sic.backend.controller.NotificacaoController;
import com.api.sic.backend.domain.Notificacao;
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
public class NotificacaoResponseDTO extends RepresentationModel<NotificacaoResponseDTO> {
    private String titulo;
    private String descricao;
    private UsuarioDefaultResponseDTO receptor;
    private UsuarioDefaultResponseDTO emissor;

     public void addLinks(Notificacao notificacao){
        this.add(linkTo(NotificacaoController.class).slash(notificacao.getId()).withSelfRel());
    }
}
