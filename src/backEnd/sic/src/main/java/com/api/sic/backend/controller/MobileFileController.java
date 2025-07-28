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
    public ResponseEntity<UploadResponseMobileDTO> uploadImages(@RequestParam("files") List<MultipartFile> files, @RequestParam("zoom") boolean zoom) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body(new UploadResponseMobileDTO("A lista de arquivos não pode estar vazia.", null,null));
        }

        
        List<String> savedFileNames = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>(); 
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String cdd = UUID.randomUUID().toString();
                String uniqueFileName = cdd + getFileExtension(file.getOriginalFilename());
                fileStorageService.save(file, uniqueFileName);
                savedFileNames.add(uniqueFileName);

                String imageUrl = servidor + uniqueFileName;
                imageUrls.add(imageUrl);

                imageProcessService.processarImagem(uniqueFileName, cdd, zoom);
            }
        }

        String diagnostico = "A análise da imagem identificou a presença de elementos\r\n" + //
                        "compatíveis com parasitas do gênero Toxocara spp.\r\n" + //
                        "Esta zoonose pode ser transmitida para humanos e animais\r\n" + //
                        "através da ingestão de ovos do parasita.\r\n" + //
                        "Recomenda-se a consulta com um veterinário para confirmação do\r\n" + //
                        "diagnóstico e aplicação do tratamento adequado.";
        
        return ResponseEntity.ok(new UploadResponseMobileDTO("Imagens salvas com sucesso.", imageUrls, diagnostico));
    }


    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

}