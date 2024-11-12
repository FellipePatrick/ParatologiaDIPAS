package com.api.sic.backend.controller;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.sic.backend.service.FileStorageService;
import com.api.sic.backend.service.ImageProcessService;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/file/")

public class FileController {
    private final FileStorageService fileStorageService;
    private final ImageProcessService imageProcessService;

    public FileController(FileStorageService fileStorageService, ImageProcessService imageProcessService) {
        this.fileStorageService = fileStorageService;
        this.imageProcessService = imageProcessService;
    }

    @PostMapping
    public ResponseEntity<String> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("A lista de arquivos não pode estar vazia.");
        }

        List<String> savedFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String uniqueFileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
                fileStorageService.save(file, uniqueFileName);
                savedFileNames.add(uniqueFileName);
                imageProcessService.processarImagem(uniqueFileName);
            }
        }

        return ResponseEntity.ok("Imagens salvas com sucesso: " + String.join(", ", savedFileNames));
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
