package cog.com.sic.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cog.com.sic.frontend.dto.imagem.ImagemRequestDTO;
import cog.com.sic.frontend.dto.relatorio.RelatorioRequestDTO;
import cog.com.sic.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RelatorioController {
    private static final String URL = "http://localhost:8081/file/";

    private static final String RELATORIO_URL = "http://localhost:8081/relatorios/";

    private final AuthService authService;
    private final HttpSession session;

    public RelatorioController(AuthService authService, HttpSession session) {
        this.authService = authService;
        this.session = session;
    }

    @GetMapping("/analisar")
    public ModelAndView analisarFotos(@ModelAttribute("id_relatorio") Long idRelatorio,  RedirectAttributes redirectAttributes){
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("process/analise");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<ImagemRequestDTO>> response = restTemplate.exchange(
                URL + idRelatorio,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ImagemRequestDTO>>() {}
            );
            
            List<ImagemRequestDTO> imagens = response.getBody();

            if (imagens != null && !imagens.isEmpty()) {
                List<ImagemRequestDTO> imagensOriginal = imagens.stream()
                        .filter(imagem -> "Original".equals(imagem.getNome()))
                        .collect(Collectors.toList());

                List<ImagemRequestDTO> imagensProcessada = imagens.stream()
                        .filter(imagem -> "Circulada".equals(imagem.getNome()))
                        .collect(Collectors.toList());

                modelAndView.addObject("baseImageUrl", "http://localhost:8081/images/");
                modelAndView.addObject("imagensOriginal", imagensOriginal);
                modelAndView.addObject("imagensProcessada", imagensProcessada);
                modelAndView.addObject("idRelatorio", idRelatorio);
            } else {
                modelAndView.addObject("errorMessage", "Nenhuma imagem encontrada.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("errorMessage", "Erro ao carregar as imagens.");
        }

        return modelAndView;
    }


    @GetMapping("/relatorios/delete/{id}")
    public ModelAndView deleteRelatorio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/relatorios");
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + session.getAttribute("token"));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            restTemplate.exchange(
                RELATORIO_URL + id,
                HttpMethod.DELETE,
                entity,
                Void.class 
            );

            redirectAttributes.addFlashAttribute("successMessage", "Relatório fechado com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao fechar o relatório ");
            e.printStackTrace();
        }

        return modelAndView;
    }


    @GetMapping("/relatorios")
    public ModelAndView indexRelatorios(@ModelAttribute String s, RedirectAttributes redirectAttributes){
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("relatorios/index");

        int totais = 0;
        int andamento = 0;
        int finalizados = 0;
        int avaliandos = 0;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));
    
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                RELATORIO_URL,
                HttpMethod.GET,
                entity,
                Map.class
            );
            @SuppressWarnings("unchecked")
            Map<String, Object> response = responseEntity.getBody();
        

            @SuppressWarnings({ "unchecked", "null" })
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");

            List<RelatorioRequestDTO> relatorios = content.stream()
                    .map(this::mapToRelatorioRequestDTO)
                    .collect(Collectors.toList());


            for (RelatorioRequestDTO relatorio : relatorios) {
                switch (relatorio.getStatus()) {
                    case "PENDENTE":
                        andamento++;
                        break;
                    case "APROVADO":
                        finalizados++;
                        break;
                    case "RASCUNHO":
                        andamento++;
                        break;
                    case "AVALIANDO":
                        avaliandos++;
                        break;
                    default:
                        break;
                }
            }
            totais = andamento + finalizados + avaliandos;

            modelAndView.addObject("totais", totais);
            modelAndView.addObject("andamento", andamento);
            modelAndView.addObject("finalizados", finalizados);
            modelAndView.addObject("avaliandos", avaliandos);

            modelAndView.addObject("relatorios", relatorios);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar relatórios: " + e.getMessage());
            modelAndView.setViewName("redirect:/");
        }

        return modelAndView;
    }

    @GetMapping("/relatorios/{id}")
    public ModelAndView editRelatorio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
    
        ModelAndView modelAndView = new ModelAndView("relatorios/unit");
        RestTemplate restTemplate = new RestTemplate();
    
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));
            HttpEntity<String> entity = new HttpEntity<>(headers);
    
            ResponseEntity<List<ImagemRequestDTO>> imageResponse = restTemplate.exchange(
                URL + id,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ImagemRequestDTO>>() {}
            );
    
            List<ImagemRequestDTO> imagens = imageResponse.getBody();
    
            if (imagens != null && !imagens.isEmpty()) {
                List<ImagemRequestDTO> imagensOriginal = imagens.stream()
                        .filter(imagem -> "Original".equals(imagem.getNome()))
                        .collect(Collectors.toList());
    
                List<ImagemRequestDTO> imagensProcessada = imagens.stream()
                        .filter(imagem -> "Circulada".equals(imagem.getNome()))
                        .collect(Collectors.toList());
    
                modelAndView.addObject("baseImageUrl", "http://localhost:8081/images/");
                modelAndView.addObject("imagensOriginal", imagensOriginal);
                modelAndView.addObject("imagensProcessada", imagensProcessada);
            }
    
            ResponseEntity<Map> response = restTemplate.exchange(
                RELATORIO_URL + "?page=0&size=1000", 
                HttpMethod.GET,
                entity,
                Map.class
            );
    
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
    
            if (content != null) {
                List<RelatorioRequestDTO> relatorios = content.stream()
                        .map(this::mapToRelatorioRequestDTO)
                        .collect(Collectors.toList());
    
                RelatorioRequestDTO relatorio = relatorios.stream()
                        .filter(r -> r.getId().equals(id))
                        .findFirst()
                        .orElse(null);
    
                modelAndView.addObject("relatorio", relatorio);
            }
    
            modelAndView.addObject("idRelatorio", id);
    
        } catch (Exception e) {
            modelAndView.addObject("errorMessage", "Erro ao carregar o relatório.");
            e.printStackTrace();
        }
    
        return modelAndView;
    }
    
    @PostMapping("/relatorio")
    public ModelAndView updateRelatorio(
            @ModelAttribute @Valid RelatorioRequestDTO relatorioRequestDTO,
            RedirectAttributes redirectAttributes) {
    
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
    
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + session.getAttribute("token"));
    
            HttpEntity<RelatorioRequestDTO> entity = new HttpEntity<>(relatorioRequestDTO, headers);
            RestTemplate restTemplate = new RestTemplate();
    
            ResponseEntity<String> response = restTemplate.exchange(
                    RELATORIO_URL + "/" + relatorioRequestDTO.getId(),
                    HttpMethod.PUT,
                    entity,
                    String.class
            );
    
            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("successMessage", "Relatório atualizado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Falha ao atualizar o relatório. Código: " + response.getStatusCode());
            }
    
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao se comunicar com o servidor externo.");
        }
    
        return new ModelAndView("redirect:/relatorios");
    }
    

    @PostMapping("/images")
    public ModelAndView enviarImagens(@RequestParam("imagens") List<MultipartFile> arquivos,
            @RequestParam(value = "zoom", required = false) String zoom,
            RedirectAttributes redirectAttributes) {

        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/analisar");
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            for (MultipartFile arquivo : arquivos) {
                body.add("files", arquivo.getResource());
            }

            body.add("zoom", zoom != null ? "true" : "false");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    URL, HttpMethod.POST, requestEntity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseBody = objectMapper.readTree(response.getBody());

            Long idRelatorio = responseBody.path("id_relatorio").asLong();

            redirectAttributes.addFlashAttribute("successMessage", "Imagens enviadas com sucesso!");
            redirectAttributes.addFlashAttribute("id_relatorio", idRelatorio);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao enviar as imagens.");
        }
        return modelAndView;
    }

    private RelatorioRequestDTO mapToRelatorioRequestDTO(Map<String, Object> dados) {
        Long id = Long.valueOf(dados.get("id").toString());
        String titulo = dados.get("titulo").toString();
        String descricao = dados.get("descricao").toString();
        String status = dados.get("status").toString();

        @SuppressWarnings("unchecked")
        Map<String, Object> usuario = (Map<String, Object>) dados.get("usuario");
        String dono = usuario != null && usuario.get("nome") != null ? usuario.get("nome").toString() : "Não informado";

        @SuppressWarnings("unchecked")
        Map<String, Object> gestorMap = (Map<String, Object>) dados.get("gestor");
        String gestor = gestorMap != null && gestorMap.get("nome") != null ? gestorMap.get("nome").toString()
                : "Não informado";

        String data = dados.get("dataModificacao").toString();

        LocalDateTime dateTime = LocalDateTime.parse(data);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        data = dateTime.format(formatter);

        return new RelatorioRequestDTO(titulo, descricao, id, status, dono, gestor, data);
    }
}
