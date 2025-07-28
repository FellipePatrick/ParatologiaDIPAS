package cog.com.sic.frontend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cog.com.sic.frontend.core.ConfigEnvs;
import cog.com.sic.frontend.dto.relatorio.RelatorioRequestDTO;
import cog.com.sic.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;

@Controller
public class SistemaController {
    private static final String RELATORIO_URL = ConfigEnvs.servidor + "/relatorios/";
    private final AuthService authService;
    private final HttpSession session;

    public SistemaController(AuthService authService, HttpSession session) {
        this.authService = authService;
        this.session = session;
    }

    

    @SuppressWarnings("unchecked")
    @GetMapping("/")
    public ModelAndView indexHome(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
       
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("home/index");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> responseEntity = restTemplate.exchange(
            RELATORIO_URL, 
            HttpMethod.GET, 
            entity, 
            Map.class
        );

        Map<String, Object> response = responseEntity.getBody();
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");

        int totais = 0;
        int andamento = 0;
        int finalizados = 0;
        int avaliandos = 0;
        try {
            List<RelatorioRequestDTO> relatorios = content.stream()
                    .map(this::mapToRelatorioRequestDTO)
                    .collect(Collectors.toList());


            for (RelatorioRequestDTO relatorio : relatorios) {
                switch (relatorio.getStatus()) {
                    case "PENDENTE":
                        andamento++;
                        break;
                    case "FINALIZADO":
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

    private RelatorioRequestDTO mapToRelatorioRequestDTO(Map<String, Object> dados) {
        Long id = Long.valueOf(dados.get("id").toString());
        String titulo = dados.get("titulo").toString();
        String descricao = dados.get("descricao").toString();
        String status = dados.get("status").toString();
        String diagnostico = dados.get("diagnostico").toString();

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

        return new RelatorioRequestDTO(titulo, descricao, id, status, dono, gestor, data, diagnostico);
    }

    @GetMapping("/processar")
    public ModelAndView indexProcess(@ModelAttribute String s,  RedirectAttributes redirectAttributes) { 
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        ModelAndView modelAndView = new ModelAndView("process/index");
        modelAndView.addObject("msg", "Adicione suas imagens para o processamento!");
        return modelAndView;
    }

    

    @GetMapping("/suporte")
    public ModelAndView indexSuporte(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("suporte/index");
    }

    @GetMapping("/politicas")
    public ModelAndView politicas(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("suporte/politics");
    }

    @GetMapping("/duvidas")
    public ModelAndView duvidas(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("suporte/duvidas");
    }


    @GetMapping("/chamado")
    public ModelAndView chamado(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("chamados/edit");
    }
}
