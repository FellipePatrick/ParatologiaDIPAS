package cog.com.sic.frontend.controller;

import java.util.List;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import cog.com.sic.frontend.dto.Usuario.UsuarioRequestDTO;
import cog.com.sic.frontend.dto.Usuario.UsuarioResponseDTO;
import cog.com.sic.frontend.dto.chamado.ChamadoPagedResponseDTO;
import cog.com.sic.frontend.dto.chamado.ChamadoRequestDTO;
import cog.com.sic.frontend.dto.chamado.ChamadoResponseDTO;

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
public class ChamadoController {
    private static final String URL = "http://localhost:8081/chamados/";

    @GetMapping("/chamados")
    public ModelAndView chamados() {
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("chamados/index");

        try {
            ResponseEntity<ChamadoPagedResponseDTO> response = restTemplate.getForEntity(URL,
                    ChamadoPagedResponseDTO.class);
            ChamadoPagedResponseDTO pagedResponse = response.getBody();

            @SuppressWarnings("null")
            List<ChamadoResponseDTO> chamados = pagedResponse.getContent();
            modelAndView.addObject("chamados", chamados);

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("errorMessage", "Erro ao carregar os chamados.");
        }
        return modelAndView;
    }

    @PostMapping("/chamados")
    public ModelAndView criarChamado(@ModelAttribute ChamadoRequestDTO chamadoRequestDTO,
            RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/chamados");  
        ResponseEntity<ChamadoResponseDTO> response = restTemplate.postForEntity(URL, chamadoRequestDTO,
                ChamadoResponseDTO.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            redirectAttributes.addFlashAttribute("successMessage", "Chamado criado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar o chamado.");
        }
        return modelAndView;
    }

    @GetMapping("/chamados/delete/{id}")
    public ModelAndView doDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/chamados");

        try {
            restTemplate.delete(URL + "/" + id);
            redirectAttributes.addFlashAttribute("successMessage", "Chamado deletado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar os chamados.");
        }
        return modelAndView;
    }
}
