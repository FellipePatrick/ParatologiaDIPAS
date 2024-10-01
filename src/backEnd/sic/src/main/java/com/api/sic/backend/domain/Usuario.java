package com.api.sic.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.Random;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "usuario")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Usuario extends AbstractEntity {
    @NotBlank(message = "O nome não pode estar em branco.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;
    @NotBlank(message = "O telefone não pode estar em branco.")
    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve ter 10 ou 11 dígitos.")
    private String telefone;
    @NotNull(message = "A role não pode ser nula.")
    @Enumerated(EnumType.STRING)
    private Role role;
    @NotBlank(message = "O email não pode estar em branco.")
    @Email(message = "O email deve ser válido.")
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String matricula;

    private String pathImage;
    @NotBlank(message = "A senha não pode estar em branco.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$", 
             message = "A senha deve conter pelo menos uma letra minúscula, uma letra maiúscula e um caractere especial.")
    private String senha;
    @PrePersist
    private void gerarMatrículaESenha() {
        this.matricula = Usuario.gerarMatricula();
        this.senha = Usuario.gerarSenha(8) + "@Sic";
    }

    public static String gerarMatricula() {
        Random random = new Random();
        String numeros = String.format("%04d1234", random.nextInt(10000));
        return "Sic" + +2024 + "@" + numeros;
    }

    public static String gerarSenha(int comprimento) {
        return RandomStringUtils.random(comprimento, true, true);
    }

    public enum Role {
        ADMINISTRADOR, ORIENTADOR, BOLSISTA;
    }
}
