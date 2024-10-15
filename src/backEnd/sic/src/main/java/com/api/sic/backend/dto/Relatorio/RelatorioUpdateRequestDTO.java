package com.api.sic.backend.dto.Relatorio;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioUpdateRequestDTO {

    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @NotBlank(message = "O status é obrigatório.")
    private String status;

    @NotBlank(message = "O caminho das fotos é obrigatório.")
    private String pathFotos;

    private LocalDateTime dataModificacao = LocalDateTime.now();

}
