package com.api.sic.backend.dto.chamado;

import com.api.sic.backend.domain.Usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChamadoRequestDTO {
    @NotBlank(message = "O assunto é obrigatório.")
    @Size(max = 255, message = "O assunto deve ter no máximo 255 caracteres.")
    private String assunto;
    @NotBlank(message = "A descrição é obrigatório.")
    private String descricao;
    // @NotNull(message = "O dono é obrigatório.")
    // private Usuario dono;
}
