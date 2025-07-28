package com.api.sic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.dto.ResetPasswordDTO;
import com.api.sic.backend.service.UsuarioService;

import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/resetpassword/")
public class ResetPasswordController {

    @Autowired
    private UsuarioService usuarioService;

     @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<String> redefinirSenha(@Valid @RequestBody ResetPasswordDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Usuario> optionalUsuario = usuarioService.findByEmail(username);
        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.status(404).body("Usuário não encontrado.");
        }

        Usuario usuario = optionalUsuario.get();

        if (!dto.getNovaSenha().equals(dto.getConfirmarSenha())) {
            return ResponseEntity.badRequest().body("As senhas não coincidem.");
        }

        try {
            usuario.setPassword(passwordEncoder.encode(dto.getNovaSenha()));
            usuarioService.update(usuario, usuario.getId());
            return ResponseEntity.ok("Senha redefinida com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao redefinir senha: " + e.getMessage());
        }
    }
}
