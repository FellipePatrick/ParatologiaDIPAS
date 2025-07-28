package cog.com.sic.frontend.dto.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
    
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequestDTO {
        private String nome;
        private String telefone;
        private String role; 
}