package cog.com.sic.frontend.dto.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

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
    private String pathImage;
    private LocalDate deletedAt;
    private Long id;
}