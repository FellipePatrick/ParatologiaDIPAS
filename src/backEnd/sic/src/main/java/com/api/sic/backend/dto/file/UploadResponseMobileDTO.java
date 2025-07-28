package com.api.sic.backend.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UploadResponseMobileDTO {
    private String message;
    private List<String> imageUrls;  // Lista para armazenar os URLs das imagens
    private String diagnostico;

}
