package com.api.sic.backend.dto.Usuario;
import com.api.sic.backend.domain.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank(message = "O nome não pode estar em branco.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;
    @NotBlank(message = "O telefone não pode estar em branco.")
    private String telefone;
    @NotNull(message = "A role não pode ser nula.")
    private String role; 
    @NotBlank(message = "O email não pode estar em branco.")
    @Email(message = "O email deve ser válido.")
    private String email;

    private Usuario gestor;
}