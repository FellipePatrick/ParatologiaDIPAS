package com.api.sic.backend.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.sic.backend.domain.TokenPassword;
import com.api.sic.backend.repository.TokenPasswordRepositoy;

@Service
public class TokenPasswordService extends GenericService<TokenPassword, Long, TokenPasswordRepositoy> {

    public TokenPasswordService(TokenPasswordRepositoy repository) {
        super(repository);
    }

    @Override
    public List<TokenPassword> findAll() {
        return this.repository.findAll();
    }

    public ArrayList<TokenPassword> findTokenAtivo(String email){
        return this.repository.findTokenAtivo(email);

    }

    public Optional<TokenPassword> findByToken(String token) {
        return this.repository.findByToken(token);
    }   

}