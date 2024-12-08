package cog.com.sic.frontend.dto.relatorio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioRequestDTO {
    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    private Long id;

}
