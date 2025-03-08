package com.api.sic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.sic.backend.dto.file.UploadResponseMobileDTO;
import com.api.sic.backend.service.FileStorageService;
import com.api.sic.backend.service.ImageProcessService;
import com.api.sic.backend.service.ImagemService;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/Mobilefile/")

public class MobileFileController {

    private final FileStorageService fileStorageService;
    private final ImageProcessService imageProcessService;
    private final ImagemService imagemService;

    public MobileFileController(ImagemService imagemService, FileStorageService fileStorageService, ImageProcessService imageProcessService) {
        this.fileStorageService = fileStorageService;
        this.imageProcessService = imageProcessService;
        this.imagemService = imagemService;
    }

    @PostMapping
    public ResponseEntity<UploadResponseMobileDTO> uploadImages(@RequestParam("files") List<MultipartFile> files, @RequestParam("zoom") boolean zoom) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body(new UploadResponseMobileDTO("A lista de arquivos não pode estar vazia.", null));
        }

        List<String> savedFileNames = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>(); 
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String cdd = UUID.randomUUID().toString();
                String uniqueFileName = cdd + getFileExtension(file.getOriginalFilename());
                fileStorageService.save(file, uniqueFileName);
                savedFileNames.add(uniqueFileName);

                String imageUrl = "http://192.168.0.108:8081/images/Circulada" + uniqueFileName;
                imageUrls.add(imageUrl);

                imageProcessService.processarImagem(uniqueFileName, cdd, zoom);
            }
        }
        return ResponseEntity.ok(new UploadResponseMobileDTO("Imagens salvas com sucesso.", imageUrls));
    }


    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

}