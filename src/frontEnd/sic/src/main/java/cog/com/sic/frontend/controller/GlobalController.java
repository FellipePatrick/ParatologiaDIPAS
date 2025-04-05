package cog.com.sic.frontend.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import cog.com.sic.frontend.dto.relatorio.RelatorioRequestDTO;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("DataHoraAtual")
    public String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE · MMMM d, yyyy · h:mm a", Locale.forLanguageTag("pt-BR"));
        String formattedDate = LocalDateTime.now().format(formatter);
        return capitalizeFirstLetters(formattedDate);
    }

    private String capitalizeFirstLetters(String str) {
        String[] words = str.split(" ");
        StringBuilder capitalizedWords = new StringBuilder();
        for (String word : words) {
            if (word.length() > 1) {
                capitalizedWords.append(Character.toUpperCase(word.charAt(0)))
                                .append(word.substring(1).toLowerCase());
            } else {
                capitalizedWords.append(word.toUpperCase());
            }
            capitalizedWords.append(" ");
        }
        return capitalizedWords.toString().trim();
    }

    @ModelAttribute("ChamadosSearch")
    public List<RelatorioRequestDTO> getChamadosSearch(HttpSession session) {
        final String RELATORIO_URL = "http://localhost:8081/relatorios/";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                RELATORIO_URL,
                HttpMethod.GET,
                entity,
                Map.class
            );

            Map<String, Object> response = responseEntity.getBody();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");

            return content.stream()
                    .map(this::mapToRelatorioRequestDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
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
