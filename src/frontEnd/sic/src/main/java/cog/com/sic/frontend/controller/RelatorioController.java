package cog.com.sic.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cog.com.sic.frontend.dto.imagem.ImagemPagedResponseDTO;
import cog.com.sic.frontend.dto.imagem.ImagemRequestDTO;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RelatorioController {
    private static final String URL = "http://localhost:8081/file/";

    private static final String URLRelatorios = "http://localhost:8081/file/3";

    @GetMapping("/analisar")
public ModelAndView analisarFotos() {
    RestTemplate restTemplate = new RestTemplate();
    ModelAndView modelAndView = new ModelAndView("process/analise");

    try {
        // Realizando a requisição para a API
        ResponseEntity<List<ImagemRequestDTO>> response = restTemplate.exchange(
                URLRelatorios,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ImagemRequestDTO>>() {}
        );

        // Obtendo a lista de imagens da resposta
        List<ImagemRequestDTO> imagens = response.getBody();

        if (imagens != null && !imagens.isEmpty()) {
            // Filtrando as imagens para pegar as "Original" e "Circulada"
            List<ImagemRequestDTO> imagensOriginal = imagens.stream()
                    .filter(imagem -> "Original".equals(imagem.getNome()))
                    .collect(Collectors.toList());

            List<ImagemRequestDTO> imagensProcessada = imagens.stream()
                    .filter(imagem -> "Circulada".equals(imagem.getNome()))
                    .collect(Collectors.toList());

            // Adicionando as imagens filtradas ao modelo
            modelAndView.addObject("baseImageUrl", "http://localhost:8081/images/");
            modelAndView.addObject("imagensOriginal", imagensOriginal);
            modelAndView.addObject("imagensProcessada", imagensProcessada);
        } else {
            modelAndView.addObject("errorMessage", "Nenhuma imagem encontrada.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        modelAndView.addObject("errorMessage", "Erro ao carregar as imagens.");
    }

    return modelAndView;
}

    @PostMapping("/images")
    public ModelAndView enviarImagens(@RequestParam("imagens") List<MultipartFile> arquivos,
                                      RedirectAttributes redirectAttributes) {

        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/analisar");
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            for (MultipartFile arquivo : arquivos) {
                body.add("files", arquivo.getResource());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);

            redirectAttributes.addFlashAttribute("successMessage", "Imagens enviadas com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao enviar as imagens.");
        }
        return modelAndView;
    }
}
