package com.api.sic.backend.dto.relatorio;

import java.time.LocalDateTime;

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
public class RelatorioRequestDTO {

    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @NotBlank(message = "O status é obrigatório.")
    private String status;

    @NotNull(message = "O usuário é obrigatório.")
    private Usuario usuario;

    @NotNull(message = "O gestor é obrigatório.")
    private Usuario gestor;

    private LocalDateTime dataModificacao = LocalDateTime.now();

}
