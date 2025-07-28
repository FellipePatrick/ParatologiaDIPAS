package com.api.sic.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.repository.UsuarioRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService implements UserDetailsService {

    UsuarioRepository repository;
    BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> credencials = Optional.ofNullable(repository.findByEmail(email));

        if(credencials.isPresent()){
            return credencials.get();
        } else {
            throw new UsernameNotFoundException("Usuário não cadastrado com esse email: " + email);
        }
    }
}