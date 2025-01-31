package cog.com.sic.frontend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cog.com.sic.frontend.dto.relatorio.RelatorioRequestDTO;

@Controller
public class SistemaController {
    private static final String RELATORIO_URL = "http://localhost:8081/relatorios/";


    @GetMapping("/")
    public ModelAndView indexHome(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("home/index");

        int totais = 0;
        int andamento = 0;
        int finalizados = 0;
        int avaliandos = 0;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(RELATORIO_URL, Map.class);

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

    @GetMapping("/processar")
    public ModelAndView indexProcess(@ModelAttribute String s) {
        ModelAndView modelAndView = new ModelAndView("process/index");
        modelAndView.addObject("msg", "Adicione suas imagens para o processamento!");
        return modelAndView;
    }

    
    @GetMapping("/relatorios/unit")
    public ModelAndView unitRelatorios(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("relatorios/unit");
    }

    @GetMapping("/notificacao")
    public ModelAndView indexNotifi(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("notific/index");
    }

    @GetMapping("/suporte")
    public ModelAndView indexSuporte(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("suporte/index");
    }

    @GetMapping("/contato")
    public ModelAndView contato(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("suporte/contato");
    }

    @GetMapping("/politicas")
    public ModelAndView politicas(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("suporte/politics");
    }


    @GetMapping("/chamado")
    public ModelAndView chamado(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("chamados/edit");
    }
}
