package cog.com.sic.frontend.controller;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cog.com.sic.frontend.dto.chamado.ChamadoPagedResponseDTO;
import cog.com.sic.frontend.dto.chamado.ChamadoRequestDTO;
import cog.com.sic.frontend.dto.chamado.ChamadoRequestUpdateStatus;
import cog.com.sic.frontend.dto.chamado.ChamadoResponseDTO;
import cog.com.sic.service.AuthService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ChamadoController {
    private static final String URL = "http://localhost:8081/chamados/";
    /**
     *
     */
    private final AuthService authService;
    private final HttpSession session;
    public ChamadoController(AuthService authService, HttpSession session){
        this.session = session;
        this.authService = authService;
    }

    @GetMapping("/chamados")
    public ModelAndView chamados(RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("chamados/index");

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + session.getAttribute("token"));
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<ChamadoPagedResponseDTO> response = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                entity,
                ChamadoPagedResponseDTO.class
            );
            Boolean adm = session.getAttribute("role").equals("ADMINISTRADOR") ? true : false;
            ChamadoPagedResponseDTO pagedResponse = response.getBody();
            

            @SuppressWarnings("null")
            List<ChamadoResponseDTO> chamados = pagedResponse.getContent();
            modelAndView.addObject("chamados", chamados);
            modelAndView.addObject("adm", adm);

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("errorMessage", "Erro ao carregar os chamados.");
        }
        return modelAndView;
    }

    @PostMapping("/chamados")
    public ModelAndView criarChamado(@ModelAttribute ChamadoRequestDTO chamadoRequestDTO,
            RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/chamados");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));
    
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ChamadoResponseDTO> response = restTemplate.exchange(
            URL,
            HttpMethod.POST,
            entity,
            ChamadoResponseDTO.class
        );
        if (response.getStatusCode().is2xxSuccessful()) {
            redirectAttributes.addFlashAttribute("successMessage", "Chamado criado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar o chamado.");
        }
        return modelAndView;
    }

    @PostMapping("/chamados/status")
    public ModelAndView doUpdate(@RequestParam Long id, @ModelAttribute ChamadoRequestUpdateStatus chamadoRequestDTO,
            RedirectAttributes redirectAttributes) {
    
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
    
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/chamados");
    
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + session.getAttribute("token"));
            headers.setContentType(MediaType.APPLICATION_JSON);
    
            HttpEntity<ChamadoRequestUpdateStatus> entity = new HttpEntity<>(chamadoRequestDTO, headers);
    
            restTemplate.exchange(
                URL + id,
                HttpMethod.PUT,
                entity,
                Void.class
            );
    
            redirectAttributes.addFlashAttribute("successMessage", "Chamado atualizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar o chamado.");
        }
    
        return modelAndView;
    }
    

    @GetMapping("/chamados/editar/{id}")
    public ModelAndView doEditar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
    
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("chamados/update");

        Boolean adm = session.getAttribute("role").equals("ADMINISTRADOR") ? true : false;
    
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + session.getAttribute("token"));
    
            HttpEntity<Void> entity = new HttpEntity<>(headers);
    
            ResponseEntity<ChamadoResponseDTO> response = restTemplate.exchange(
                URL + id,
                HttpMethod.GET,
                entity,
                ChamadoResponseDTO.class
            );
    
            ChamadoResponseDTO chamado = response.getBody();
            modelAndView.addObject("chamado", chamado);
            modelAndView.addObject("adm", adm);
    
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("errorMessage", "Erro ao carregar o chamado.");
        }
    
        return modelAndView;
    }
    

    @GetMapping("/chamados/delete/{id}")
public ModelAndView doDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    if (!authService.verificarTokenValido(session)) {
        redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
        return new ModelAndView("redirect:/login");
    }

    RestTemplate restTemplate = new RestTemplate();
    ModelAndView modelAndView = new ModelAndView("redirect:/chamados");

    try {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + session.getAttribute("token"));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
            URL + "/" + id,
            HttpMethod.DELETE,
            entity,
            Void.class
        );

        redirectAttributes.addFlashAttribute("successMessage", "Chamado deletado com sucesso!");
    } catch (Exception e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar o chamado.");
    }

    return modelAndView;
}

}
