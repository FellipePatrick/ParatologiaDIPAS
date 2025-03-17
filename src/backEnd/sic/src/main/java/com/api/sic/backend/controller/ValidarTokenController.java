package com.api.sic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/validarToken/")
public class ValidarTokenController {
    
    @GetMapping
    public ResponseEntity<Void> validarToken() {
        return ResponseEntity.ok().build();
    }
}
