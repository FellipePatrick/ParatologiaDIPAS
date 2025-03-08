package com.api.sic.backend.dto.Usuario;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve ter 10 ou 11 dígitos.")
    private String telefone;
    @NotNull(message = "A role não pode ser nula.")
    private String role; 
    // @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    // @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$", 
    //          message = "A senha deve conter pelo menos uma letra minúscula, uma letra maiúscula e um caractere especial.")
    // private String senha;
    @NotBlank(message = "O email não pode estar em branco.")
    @Email(message = "O email deve ser válido.")
    @Column(unique = true)
    private String email;
}
