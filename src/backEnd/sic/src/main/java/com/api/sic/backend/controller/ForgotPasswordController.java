package com.api.sic.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.sic.backend.domain.TokenPassword;
import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.dto.EmailDTO;
import com.api.sic.backend.dto.ResetPasswordDTO;
import com.api.sic.backend.service.EmailService;
import com.api.sic.backend.service.TokenPasswordService;
import com.api.sic.backend.service.UsuarioService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/forgotpassword/")
public class ForgotPasswordController {

    
    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenPasswordService tokenPasswordService;

    @PostMapping
    public ResponseEntity<String> forgotPassword(@RequestBody EmailDTO emailDTO) {

        
        Optional<Usuario> optionalUser = usuarioService.findByEmail(emailDTO.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário autenticado não encontrado.");
        }

        ArrayList<TokenPassword> tokensAtivos = tokenPasswordService.findTokenAtivo(emailDTO.getEmail());
        
        if (!tokensAtivos.isEmpty()) {
            for (TokenPassword token : tokensAtivos) {
                token.setAtivo(false);
                tokenPasswordService.update(token, token.getId());
            }
        }   
        
        Usuario usuario = optionalUser.get();

        String token = UUID.randomUUID().toString();

        LocalDateTime expiracao = LocalDateTime.now().plusHours(1);

        TokenPassword tp = new TokenPassword();

        tp.setUsuario(usuario);
        tp.setToken(token);
        tp.setAtivo(true);
        tp.setExpiracao(expiracao);

        tokenPasswordService.create(tp);

        emailService.enviarLinkRedefinicaoSenha(emailDTO.getEmail(), token);

        return ResponseEntity.ok("Um mensagem foi enviada para esse email, com a url para redefinir a senha." + token);
    }

    @PostMapping("{token}")
    public ResponseEntity<String> redefinirSenha(
            @PathVariable("token") String token,
            @Valid @RequestBody ResetPasswordDTO dto) {

        // Verifica se as senhas batem
        if (!dto.getNovaSenha().equals(dto.getConfirmarSenha())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("As senhas não coincidem.");
        }

        Optional<TokenPassword> optionalToken = tokenPasswordService.findByToken(token);

        if (optionalToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido.");
        }

        TokenPassword tokenPassword = optionalToken.get();

        if (!tokenPassword.getAtivo() || tokenPassword.getExpiracao().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expirado ou inativo.");
        }

        Usuario usuario = tokenPassword.getUsuario();

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaCriptografada = encoder.encode(dto.getNovaSenha());
        usuario.setPassword(senhaCriptografada);

        usuarioService.update(usuario, usuario.getId());

        // Desativa o token após uso
        tokenPassword.setAtivo(false);
        tokenPasswordService.update(tokenPassword, tokenPassword.getId());

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
    
}
