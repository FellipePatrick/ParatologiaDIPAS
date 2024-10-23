package cog.com.sic.frontend.dto.chamado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ChamadoRequestDTO {
    private String assunto;
    private String descricao;
}
