package com.api.sic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.sic.backend.domain.Relatorio;
import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.domain.enumerates.StatusRelatorio;
import com.api.sic.backend.service.FileStorageService;
import com.api.sic.backend.service.ImageProcessService;
import com.api.sic.backend.service.RelatorioService;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/file/")

public class FileController {
    private final FileStorageService fileStorageService;
    private final ImageProcessService imageProcessService;
    private final RelatorioService relatorioService;

    public FileController(FileStorageService fileStorageService, ImageProcessService imageProcessService, RelatorioService relatorioService) {
        this.fileStorageService = fileStorageService;
        this.imageProcessService = imageProcessService;
        this.relatorioService = relatorioService;
    }

    @PostMapping
    public ResponseEntity<String> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("A lista de arquivos não pode estar vazia.");
        }

        // Criando relatorio na mão so para fins de testes

        Relatorio r = new Relatorio();
        
        r.setTitulo("");
        r.setDescricao("");
        r.setStatus(StatusRelatorio.PENDENTE);
        r.setDataModificacao(LocalDateTime.now());
        r.setUsuario(null);

        Usuario gestor = new Usuario();
        
        gestor.setRole(Usuario.Role.GESTOR);

        r.setGestor(gestor);

        relatorioService.create(r);

        List<String> savedFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String cdd = UUID.randomUUID().toString();
                String uniqueFileName = cdd + getFileExtension(file.getOriginalFilename());
                fileStorageService.save(file, uniqueFileName);
                savedFileNames.add(uniqueFileName);
                imageProcessService.processarImagem(uniqueFileName, r.getId(), cdd);
            }
        }

        return ResponseEntity.ok("Imagens salvas com sucesso: " + String.join(", ", savedFileNames));
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
