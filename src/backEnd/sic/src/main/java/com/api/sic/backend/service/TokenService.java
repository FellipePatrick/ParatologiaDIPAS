package com.api.sic.backend.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.dto.TokenResponseDTO;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final JwtEncoder encoder;
    private final UsuarioService service;

    public TokenService(JwtEncoder encoder, UsuarioService service) {
        this.encoder = encoder;
        this.service = service;
    }

   public TokenResponseDTO generateToken(Authentication authentication) {
    Instant now = Instant.now();

    String scope = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

    // Buscar usuário autenticado pelo email
    Optional<Usuario> usuario = service.findByEmail(authentication.getName());
    
    if (usuario.isEmpty()) {
        throw new UsernameNotFoundException("Usuário não encontrado");
    }

    Usuario user = usuario.get();
    
    // Criar as CLAIMS do JWT
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(1, ChronoUnit.HOURS))
            .subject(authentication.getName())
            .claim("scope", scope)
            .build();

    String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    return new TokenResponseDTO(token, user);
}


}