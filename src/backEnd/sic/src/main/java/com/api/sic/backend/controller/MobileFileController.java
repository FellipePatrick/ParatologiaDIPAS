package com.api.sic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.sic.backend.core.ConfigEnvs;
import com.api.sic.backend.dto.file.UploadResponseMobileDTO;
import com.api.sic.backend.service.FileStorageService;
import com.api.sic.backend.service.ImageProcessService;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/Mobilefile/")

public class MobileFileController {

    private final FileStorageService fileStorageService;
    private final ImageProcessService imageProcessService;
    
    //Variavel de ambiente que aponta para o servidor na resposta para o mobile
    public final String servidor = ConfigEnvs.servidor_to_mobile;

    public MobileFileController( FileStorageService fileStorageService, ImageProcessService imageProcessService) {
        this.fileStorageService = fileStorageService;
        this.imageProcessService = imageProcessService;
    }

    @PostMapping
    public ResponseEntity<UploadResponseMobileDTO> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("zoom") boolean zoom) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                new UploadResponseMobileDTO("O arquivo não pode estar vazio.", null, null)
            );
        }

        String cdd = UUID.randomUUID().toString();
        String uniqueFileName = cdd + getFileExtension(file.getOriginalFilename());
        fileStorageService.save(file, uniqueFileName);

        String imageUrl = servidor + uniqueFileName;
        imageProcessService.processarImagem(uniqueFileName, cdd, zoom);

        String diagnostico = "A análise da imagem identificou a presença de elementos " + 
            "compatíveis com parasitas do gênero Toxocara spp. " + 
            "Esta zoonose pode ser transmitida para humanos e animais" + 
            "através da ingestão de ovos do parasita. " + 
            "Recomenda-se a consulta com um veterinário para confirmação do " + 
            "diagnóstico e aplicação do tratamento adequado.";

        return ResponseEntity.ok(
            new UploadResponseMobileDTO("Imagem salva com sucesso.", List.of(imageUrl), diagnostico)
        );
    }



    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

}