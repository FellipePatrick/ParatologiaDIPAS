package com.api.sic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.api.sic.backend.service.UsuarioService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file/")
public class FileController {
    private final FileStorageService fileStorageService;
    private final ImageProcessService imageProcessService;
    private final RelatorioService relatorioService;
    private final ImagemService imagemService;
    private final UsuarioService usuarioService;

    public FileController(ImagemService imagemService, UsuarioService usuarioService ,FileStorageService fileStorageService, ImageProcessService imageProcessService, RelatorioService relatorioService) {
        this.fileStorageService = fileStorageService;
        this.imageProcessService = imageProcessService;
        this.relatorioService = relatorioService;
        this.imagemService = imagemService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("{id}")
    public ResponseEntity<List<ImagemResponseDTO>> getImagesRelatorio(@PathVariable Long id) {

        if(id == null || id < 0)
            return null;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Usuario> usuario = usuarioService.findByEmail(username);


        Relatorio r = new Relatorio();
        r.setTitulo("Rascunho");
        r.setDescricao("");
        r.setStatus(StatusRelatorio.RASCUNHO);
        r.setDataModificacao(LocalDateTime.now());
     
        r.setUsuario(usuario.get());
        r.setGestor(usuario.get().getGestor());
        
        //Setando diagnostico
        r.setDiagnostico("A análise da imagem identificou a presença de elementos " + //
                        "compatíveis com parasitas do gênero Toxocara spp. " + //
                        "Esta zoonose pode ser transmitida para humanos e animais " + //
                        "através da ingestão de ovos do parasita. " + //
                        "Recomenda-se a consulta com um veterinário para confirmação do " + //
                        "diagnóstico e aplicação do tratamento adequado.");

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
