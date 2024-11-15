package com.api.sic.backend.dto.imagem;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ImagemResponseDTO extends RepresentationModel<ImagemResponseDTO>{
    private String nome;
    private String codigoIm;
    private String path;
}