package cog.com.sic.frontend.dto.imagem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ImagemRequestDTO {
    private String nome;
    private String codigoIm;
    private String path;
}
