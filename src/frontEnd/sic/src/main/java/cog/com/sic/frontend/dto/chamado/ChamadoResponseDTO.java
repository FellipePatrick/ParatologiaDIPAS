package cog.com.sic.frontend.dto.chamado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import cog.com.sic.frontend.dto.Usuario.UsuarioRequestDTO;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ChamadoResponseDTO extends RepresentationModel<ChamadoResponseDTO>{
    private Long id;
    private String assunto;
    private String descricao;
    private String status;
    private LocalDate createdAt;
    private UsuarioRequestDTO dono;
}