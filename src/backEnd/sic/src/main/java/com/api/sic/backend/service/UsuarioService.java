package com.api.sic.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.repository.UsuarioRepository;

@Service
public class UsuarioService extends GenericService<Usuario, Long, UsuarioRepository>{
    
    private UsuarioRepository repository;
    
    public UsuarioService(UsuarioRepository repository){
        super(repository);
        this.repository = repository;
    }
    
    @Override
    public List<Usuario> findAll() {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    public Usuario update(Usuario usuario, Long id) {
        Usuario existingUsuario = repository.findById(id).get();

        if(existingUsuario == null){
            throw new RuntimeException("Usuário não encontrado");
        }
        existingUsuario.setNome(usuario.getNome());
        existingUsuario.setTelefone(usuario.getTelefone());
        existingUsuario.setRole(usuario.getRole());
        existingUsuario.setEmail(usuario.getEmail());

        return repository.save(existingUsuario);
    }
}