package com.api.sic.backend.dto.Relatorio;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.api.sic.backend.controller.RelatorioController;
import com.api.sic.backend.domain.Relatorio;
import com.api.sic.backend.dto.Usuario.UsuarioDefaultResponseDTO;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RelatorioResponseDTO extends RepresentationModel<RelatorioResponseDTO> {
    private Long id;
    private String titulo;
    private String descricao;
    private String diagnostico;
    private String status;
    private LocalDateTime dataModificacao;
    private UsuarioDefaultResponseDTO usuario;
    private UsuarioDefaultResponseDTO gestor; 

    public void addLinks(Relatorio relatorio) {
        this.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(RelatorioController.class)
                .listById(relatorio.getId()))
                .withSelfRel());
    }
}
