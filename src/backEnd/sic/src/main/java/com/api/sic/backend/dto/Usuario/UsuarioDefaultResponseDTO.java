package com.api.sic.backend.dto.usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UsuarioDefaultResponseDTO extends RepresentationModel<UsuarioDefaultResponseDTO>{
    private String nome;
    private String telefone;
    private String email;
}