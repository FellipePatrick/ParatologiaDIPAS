package com.api.sic.backend.controller;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.sic.backend.dto.LoginDTO;
import com.api.sic.backend.dto.TokenResponseDTO;
import com.api.sic.backend.service.TokenService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@AllArgsConstructor
@RequestMapping("/login/")
public class AuthenticationController {

    private final TokenService service;
    private final AuthenticationManager authenticationManager;


    @PostMapping
    public ResponseEntity<TokenResponseDTO> getToken(@RequestBody LoginDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
        );
        TokenResponseDTO tokenResponse = service.generateToken(authentication);
        return ResponseEntity.ok(tokenResponse);
    }

}