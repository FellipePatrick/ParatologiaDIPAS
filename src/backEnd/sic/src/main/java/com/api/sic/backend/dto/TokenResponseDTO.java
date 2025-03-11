package com.api.sic.backend.dto;

import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.domain.Usuario.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TokenResponseDTO {
    private String token;
    private Long id;
    private String nome;
    private String email;
    private Role role;
    
    public TokenResponseDTO(String token, Usuario usuario) {
        this.token = token;
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
    }

}