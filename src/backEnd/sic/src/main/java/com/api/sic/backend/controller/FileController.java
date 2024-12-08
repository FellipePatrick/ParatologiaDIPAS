package com.api.sic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.sic.backend.domain.Imagem;
import com.api.sic.backend.domain.Relatorio;
import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.domain.enumerates.StatusRelatorio;
import com.api.sic.backend.dto.file.UploadResponseDTO;
import com.api.sic.backend.dto.imagem.ImagemResponseDTO;
import com.api.sic.backend.service.FileStorageService;
import com.api.sic.backend.service.ImageProcessService;
import com.api.sic.backend.service.ImagemService;
import com.api.sic.backend.service.RelatorioService;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file/")

public class FileController {
    private final FileStorageService fileStorageService;
    private final ImageProcessService imageProcessService;
    private final RelatorioService relatorioService;
    private final ImagemService imagemService;

    public FileController(ImagemService imagemService, FileStorageService fileStorageService, ImageProcessService imageProcessService, RelatorioService relatorioService) {
        this.fileStorageService = fileStorageService;
        this.imageProcessService = imageProcessService;
        this.relatorioService = relatorioService;
        this.imagemService = imagemService;
    }

    @GetMapping("{id}")
    public ResponseEntity<List<ImagemResponseDTO>> getImagesRelatorio(@PathVariable Long id) {
        List<Imagem> imagens = imagemService.findByRelatorioId(id);
        
        List<ImagemResponseDTO> imagensDTO = imagens.stream()
                .map(imagem -> new ImagemResponseDTO(imagem.getNome(), imagem.getCodigoIm(), imagem.getPath()))
                .collect(Collectors.toList());
    
        return ResponseEntity.ok(imagensDTO);
    }
    

    @PostMapping
    public ResponseEntity<UploadResponseDTO> uploadImages(@RequestParam("files") List<MultipartFile> files, @RequestParam("zoom") boolean zoom) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body(new UploadResponseDTO("A lista de arquivos não pode estar vazia.", null));
        }

        // !!!ATENÇÃO!!!
        // Lembrar de pegar o usuario da sessão quando estivar com o spring security no projeto
        // !!!ATENÇÃO!!!

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
                imageProcessService.processarImagem(uniqueFileName, r.getId(), cdd, zoom);
            }
        }

        return ResponseEntity.ok(new UploadResponseDTO("Imagens salvas com sucesso: " + String.join(", ", savedFileNames), r.getId()));
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
