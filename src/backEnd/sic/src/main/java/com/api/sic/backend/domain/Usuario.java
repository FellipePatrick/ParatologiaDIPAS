package com.api.sic.backend.domain;

import jakarta.persistence.*;
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

    private String nome;
    private String telefone;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String matricula;
    private String pathImage;
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
        ADMINISTRADOR, GESTOR, USUARIO;
    }
}
