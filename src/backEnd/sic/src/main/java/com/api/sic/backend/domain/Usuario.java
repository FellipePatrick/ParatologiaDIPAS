package com.api.sic.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.Year;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "usuario")
@SQLDelete(sql = "UPDATE usuario SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Usuario extends AbstractEntity implements UserDetails {

    @NotBlank(message = "O nome não pode estar em branco.")
    private String nome;

    private Boolean admin;

    @NotBlank(message = "O email não pode estar em branco.")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "A senha não pode estar em branco.")
    @Size(min = 8, max = 100, message = "A senha deve ter no mínimo 8 caracteres.")
    private String password;

    private String telefone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "id_gestor")
    private Usuario gestor;

    @Column(unique = true)
    private String matricula;

    private String pathImage;

   public static String gerarMatricula() {
        Random random = new Random();
        String numeros = String.format("%04d", random.nextInt(10000));
        int anoAtual = Year.now().getValue(); 
        return "Sic" + anoAtual + "@" + numeros;
    }

    public static String gerarSenha(int comprimento) {
        return RandomStringUtils.random(comprimento, true, true);
    }

    public enum Role {
        ADMINISTRADOR, GESTOR, USUARIO;
    }

    public boolean isAdmin() {
        return Boolean.TRUE.equals(this.admin);
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (Boolean.TRUE.equals(this.admin)) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Usuario orElse(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElse'");
    }
}
