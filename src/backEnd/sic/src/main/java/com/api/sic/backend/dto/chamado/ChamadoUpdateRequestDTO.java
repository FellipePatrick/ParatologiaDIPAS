package com.api.sic.backend.dto.chamado;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChamadoUpdateRequestDTO {
    @NotBlank(message = "O status é obrigatório.")
    private String status;
}
