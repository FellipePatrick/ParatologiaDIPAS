package com.api.sic.backend.dto.notificacao;

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
public class NotificacaoRequestDTO {
    
    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatório.")
    private String descricao;
    
    
    @NotNull(message = "O receptor é obrigatório.")
    private Usuario receptor;

    @NotNull(message = "O emissor é obrigatório.")
    private Usuario emissor;
}
