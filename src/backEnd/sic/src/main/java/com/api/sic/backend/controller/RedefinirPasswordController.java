package com.api.sic.backend.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.dto.RedefinirPassowrdDTO;
import com.api.sic.backend.service.EmailService;
import com.api.sic.backend.service.UsuarioService;

@RestController
@RequestMapping("/redefinirpassword/")
public class RedefinirPasswordController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioService usuarioService;

   @PostMapping
    public ResponseEntity<String> redefinirPassword(@RequestBody RedefinirPassowrdDTO redefinirPassowrdDTO) {

        Long idUser = redefinirPassowrdDTO.getId();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Usuario> optionalUser = usuarioService.findByEmail(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário autenticado não encontrado.");
        }

        Usuario user = optionalUser.get();

        if (!(user.getRole().equals(Usuario.Role.ADMINISTRADOR) || user.getRole().equals(Usuario.Role.GESTOR))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para redefinir senhas.");
        }

        if (user.getRole().equals(Usuario.Role.GESTOR)) {
            if (usuarioService.findByIdGestor(idUser, user) == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem acesso a este usuário.");
            }
        }

        Usuario us = usuarioService.findById(idUser);
        if (us == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário a ser redefinido não encontrado.");
        }

        String novaSenha = Usuario.gerarSenha(8) + "@Sic";
        us.setPassword(novaSenha);

        emailService.enviarRedefinirPassword(
            us.getEmail(),
            us.getMatricula(),
            us.getPassword()
        );

        PasswordEncoder e = new BCryptPasswordEncoder();
        us.setPassword(e.encode(us.getPassword()));

        usuarioService.update(us, us.getId());

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}