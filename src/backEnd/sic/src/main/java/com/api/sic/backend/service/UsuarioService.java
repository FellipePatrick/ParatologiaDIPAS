package com.api.sic.backend.service;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    

   public Page<Usuario> findAllUsers(Pageable pageable) {
    Page<Usuario> usuariosPage = repository.findAll(pageable);
    List<Usuario> usuariosFiltrados = usuariosPage.stream()
                                                  .filter(usuario -> usuario.getDeletedAt() == null)
                                                  .toList();
    return new PageImpl<>(usuariosFiltrados, pageable, usuariosPage.getTotalElements());
    }

    public Page<Usuario> findAllUsersGestor(Pageable pageable, Usuario gestor) {
        Page<Usuario> usuariosPage = repository.findAllGestor(pageable, gestor);
        List<Usuario> usuariosFiltrados = usuariosPage.stream()
                                                    .filter(usuario -> usuario.getDeletedAt() == null)
                                                    .toList();
        return new PageImpl<>(usuariosFiltrados, pageable, usuariosPage.getTotalElements());
    }
    
    public Usuario findByIdGestor( Long id, Usuario gestor) {
        Usuario usuario = repository.findByIdGestor(id, gestor);
        return usuario;
    }

    public Optional<Usuario> findByEmail(String email){
        return Optional.ofNullable(repository.findByEmail(email));
    }

    public Optional<Usuario> findByMatricula(String matricula){
        return  Optional.ofNullable(repository.findByMatricula(matricula));
    }

    public Usuario update(Usuario usuario, Long id) {
        Usuario existingUsuario = repository.findById(id).get();
        if(existingUsuario == null){
            throw new RuntimeException("Usuário não encontrado");
        }
        existingUsuario.setNome(usuario.getNome());
        existingUsuario.setTelefone(usuario.getTelefone());
        existingUsuario.setRole(usuario.getRole());
        return repository.save(existingUsuario);
    }


    @Override
    public List<Usuario> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
}