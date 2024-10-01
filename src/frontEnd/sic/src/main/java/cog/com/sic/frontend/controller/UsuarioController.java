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
import cog.com.sic.frontend.dto.UsuarioPagedResponseDTO;
import cog.com.sic.frontend.dto.UsuarioRequestDTO;
import cog.com.sic.frontend.dto.UsuarioResponseDTO;

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
public class UsuarioController {

    //URL da API
    private static final String URL = "http://localhost:8081/usuarios/";

    

    @GetMapping("/perfil")
    public ModelAndView perfil(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("perfil/index");
    }

    @GetMapping("/usuarios")
    public ModelAndView usuarios() {
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("usuario/index");

        try {
            ResponseEntity<UsuarioPagedResponseDTO> response = restTemplate.getForEntity(URL,
                    UsuarioPagedResponseDTO.class);
            UsuarioPagedResponseDTO pagedResponse = response.getBody();

            @SuppressWarnings("null")
            List<UsuarioResponseDTO> usuarios = pagedResponse.getContent();
            modelAndView.addObject("usuarios", usuarios);

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("errorMessage", "Erro ao carregar os usuários.");
        }

        return modelAndView;
    }

    @GetMapping("/usuarios/editar/{id}")
    public ModelAndView doEdite(@PathVariable Long id) {
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("usuario/editar");

        try {
            String url = URL + "/" + id;
            ResponseEntity<UsuarioResponseDTO> response = restTemplate.getForEntity(url, UsuarioResponseDTO.class);
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
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/usuarios");

        try {
            restTemplate.delete(URL + "/" + id);
            redirectAttributes.addFlashAttribute("successMessage", "Usuário deletado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar os usuários.");
        }
        return modelAndView;
    }
    // Está com problemas
    @PostMapping("/usuarios/editar/{id}")
    public ModelAndView editarUsuario(@PathVariable("id") Long id,
            @ModelAttribute UsuarioRequestDTO usuarioRequestDTO,
            RedirectAttributes redirectAttributes) {
        // Imprime o ID do usuário e o objeto request que está chegando
        System.out.println("ID do Usuário: " + id);
        System.out.println("Request DTO: " + usuarioRequestDTO);
    
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/usuarios/editar/" + id);
    
        try {
            // Envia a requisição para a API de atualização
            String url = URL + "/" + id; // Atualize a URL conforme necessário
            ResponseEntity<UsuarioResponseDTO> response = restTemplate.exchange(url, HttpMethod.PUT,
                    new HttpEntity<>(usuarioRequestDTO), UsuarioResponseDTO.class);
    
            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("successMessage", "Usuário atualizado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar o usuário.");
            }
    
        } catch (HttpClientErrorException ex) {
            // Tratamento de erros do lado do cliente (400)
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
        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/usuarios");

        try {
            // Envia a requisição para a API
            ResponseEntity<UsuarioResponseDTO> response = restTemplate.postForEntity(URL, usuarioRequestDTO,
                    UsuarioResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("successMessage", "Usuário criado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar o usuário.");
            }

        } catch (HttpClientErrorException ex) {
            // Tratamento de erros do lado do cliente (400)
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> errors = objectMapper.readValue(ex.getResponseBodyAsString(),
                            new TypeReference<Map<String, String>>() {
                            });

                    for (Map.Entry<String, String> error : errors.entrySet()) {
                        redirectAttributes.addFlashAttribute(error.getKey() + "Error", error.getValue());
                    }
                    System.out.println("erros" + errors);
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Ocorreram violações de restrição no cadastro.");

                } catch (Exception parseException) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Erro inesperado ao processar a resposta da API.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar o usuário.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Já existe um usuário com este email.");
        }

        return modelAndView;
    }

}
