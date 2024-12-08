package com.api.sic.backend.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UploadResponseDTO {
    private String message;
    private Long id_relatorio; 

}
