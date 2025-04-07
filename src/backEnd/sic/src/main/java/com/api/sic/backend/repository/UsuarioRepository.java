package com.api.sic.backend.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.sic.backend.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
 
    Usuario findByMatricula(String matricula);
    
    boolean existsByMatricula(String matricula);

    @Query("SELECT u FROM usuario u WHERE u.deletedAt IS NOT NULL AND u.email = :email")
    Usuario findByEmailAtivo(@Param("email") String email);


   @Query(value = "SELECT u FROM usuario u WHERE u.deletedAt IS NULL", nativeQuery = true)
   List<Usuario> findAllWhereDeletedAtIsNull();

   @Query(value = "SELECT u FROM usuario u WHERE u.role = 'USUARIO' and u.gestor = :gestor ")
   Page<Usuario> findAllGestor(Pageable pageable, @Param("gestor") Usuario gestor);

   @Query("SELECT u FROM usuario u WHERE u.id = :id and u.role = 'USUARIO' and u.gestor = :gestor ")
   Usuario findByIdGestor(@Param("id") Long id, @Param("gestor") Usuario gestor);

}