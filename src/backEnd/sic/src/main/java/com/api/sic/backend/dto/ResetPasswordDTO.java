package com.api.sic.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordDTO {
    @NotBlank(message = "A senha não pode estar em branco.")
    @Size(min = 8, max = 100, message = "A nova senha deve ter no mínimo 8 caracteres.")
    @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z]).+$",
    message = "A nova senha deve conter pelo menos uma letra maiúscula e uma letra minúscula."
    )
    private String novaSenha;
    private String confirmarSenha;

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
}