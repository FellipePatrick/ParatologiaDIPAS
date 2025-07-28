package com.api.sic.backend.dto.Usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class UsuarioRequestUpdateDTO {
    @NotBlank(message = "O nome não pode estar em branco.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;
    @NotBlank(message = "O telefone não pode estar em branco.")
    private String telefone;
    @NotNull(message = "A role não pode ser nula.")
    private String role; 
}
