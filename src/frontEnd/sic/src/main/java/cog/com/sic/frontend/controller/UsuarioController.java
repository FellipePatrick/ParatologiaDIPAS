package cog.com.sic.frontend.controller;

import java.util.List;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import cog.com.sic.frontend.dto.Usuario.UsuarioPagedResponseDTO;
import cog.com.sic.frontend.dto.Usuario.UsuarioRequestDTO;
import cog.com.sic.frontend.dto.Usuario.UsuarioResponseDTO;
import cog.com.sic.frontend.dto.Usuario.UsuarioUpdateRequestDTO;
import cog.com.sic.service.AuthService;
import jakarta.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
public class UsuarioController {

    //URL da API
    private static final String URL = "http://localhost:8081/usuarios/";
    
    private final HttpSession session;

     private final AuthService authService;

    public UsuarioController(AuthService authService, HttpSession session) {
        this.authService = authService;
        this.session = session;
    }
    

    @GetMapping("/perfil")
    public ModelAndView perfil(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }

        return new ModelAndView("perfil/index");
    }


    @GetMapping("/usuarios")
    public ModelAndView usuarios(RedirectAttributes redirectAttributes){ 

        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }

        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("usuario/index");

        String email = (String) session.getAttribute("email");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<UsuarioPagedResponseDTO> response = restTemplate.exchange(
                URL, 
                HttpMethod.GET, 
                entity, 
                UsuarioPagedResponseDTO.class
            );
            UsuarioPagedResponseDTO pagedResponse = response.getBody();

            @SuppressWarnings("null")
            List<UsuarioResponseDTO> usuarios = pagedResponse.getContent();
            modelAndView.addObject("usuarios", usuarios);
            modelAndView.addObject("email", email);

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("errorMessage", "Erro ao carregar os usuários.");
        }

        return modelAndView;
    }

    @GetMapping("/usuarios/editar/{id}")
    public ModelAndView doEdite(@PathVariable Long id, RedirectAttributes redirectAttributes){ 

        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }

        if(!session.getAttribute("userId").equals(id.toString())){
            return new ModelAndView("redirect:/usuarios");
        }

        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("usuario/editar");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            String url = URL + "/" + id;
            ResponseEntity<UsuarioResponseDTO> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                UsuarioResponseDTO.class
            );
            UsuarioResponseDTO usuario = response.getBody();
            modelAndView.addObject("usuario", usuario);

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("errorMessage", "Erro ao carregar o usuário.");
        }

        return modelAndView;
    }

    @GetMapping("/usuarios/delete/{id}")
    public ModelAndView doDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        if(session.getAttribute("userId").equals(id.toString())){
            RestTemplate restTemplate = new RestTemplate();
            ModelAndView modelAndView = new ModelAndView("redirect:/usuarios");
    
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));
    
            HttpEntity<String> entity = new HttpEntity<>(headers);
            try {
                restTemplate.exchange(
            URL + "/" + id,
            HttpMethod.DELETE,
            entity,
            Void.class
        );
                redirectAttributes.addFlashAttribute("successMessage", "Usuário deletado com sucesso!");
    
            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar o usuário.");
            }
            return modelAndView;
        }
        return new ModelAndView("redirect:/usuarios");
        
    }

    @PostMapping("/usuarios/editar/{id}")
    public ModelAndView editarUsuario(@PathVariable("id") Long id,
            @ModelAttribute UsuarioUpdateRequestDTO usuarioRequestDTO,
            RedirectAttributes redirectAttributes) {
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
        
        if(!session.getAttribute("userId").equals(id.toString())){
            return new ModelAndView("redirect:/usuarios");
        }
        
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/usuarios/editar/" + id);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));

        HttpEntity<String> entity = new HttpEntity<>(headers);
    
        try {
            String url = URL + "/" + id;
            ResponseEntity<UsuarioResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                UsuarioResponseDTO.class
            );
    
            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("successMessage", "Usuário atualizado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar o usuário.");
            }
    
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> errors = objectMapper.readValue(ex.getResponseBodyAsString(),
                            new TypeReference<Map<String, String>>() {});
    
                    for (Map.Entry<String, String> error : errors.entrySet()) {
                        redirectAttributes.addFlashAttribute(error.getKey() + "Error", error.getValue());
                    }
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Ocorreram violações de restrição na atualização.");
    
                } catch (Exception parseException) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Erro inesperado ao processar a resposta da API.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar o usuário.");
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao atualizar o usuário.");
        }
    
        return modelAndView;
    }
    
    @PostMapping("/usuarios")
    public ModelAndView criarUsuario(@ModelAttribute UsuarioRequestDTO usuarioRequestDTO,
            RedirectAttributes redirectAttributes) {
    
        if (!authService.verificarTokenValido(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return new ModelAndView("redirect:/login");
        }
    
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/usuarios");
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + (String) session.getAttribute("token"));
    
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(usuarioRequestDTO);
    
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers); 
    
            ResponseEntity<UsuarioResponseDTO> response = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                entity,
                UsuarioResponseDTO.class
            );
    
            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("successMessage", "Usuário criado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar o usuário.");
            }
    
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> errors = objectMapper.readValue(ex.getResponseBodyAsString(),
                            new TypeReference<Map<String, String>>() {});
                    for (Map.Entry<String, String> error : errors.entrySet()) {
                        redirectAttributes.addFlashAttribute(error.getKey() + "Error", error.getValue());
                    }
                    redirectAttributes.addFlashAttribute("errorMessage", "Ocorreram violações de restrição no cadastro.");
                } catch (Exception parseException) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao processar a resposta da API.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar o usuário.");
            }
    
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Já existe um usuário com este email.");
        }
    
        return modelAndView;
    }
    

}
