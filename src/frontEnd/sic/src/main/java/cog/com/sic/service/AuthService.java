package cog.com.sic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cog.com.sic.frontend.core.ConfigEnvs;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;


   public ModelAndView enviarEmailForgotPassword(String email, HttpSession session) {
        String url = ConfigEnvs.servidor + "/forgotpassword/";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return new ModelAndView("password/index")
                    .addObject("successMessage", "Mensagem enviada, por favor verifique sua caixa de email!");
            } else {
                return new ModelAndView("password/index")
                    .addObject("errorMessage", "Falha ao enviar o email!");
            }
        } catch (Exception e) {
            return new ModelAndView("password/index")
                .addObject("errorMessage", "Usuário inexistente ou erro de conexão.");
        }
    }


    public ModelAndView realizarLogin(String credencialMatricula, String senha, HttpSession session) {
        String url = ConfigEnvs.servidor + "/login/";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("matricula", credencialMatricula);
        requestBody.put("password", senha);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String token = (String) responseBody.get("token");
                Integer userId = (Integer) responseBody.get("id");
                String nome = (String) responseBody.get("nome");
                String email = (String) responseBody.get("email");
                String role = (String) responseBody.get("role");
                String telefone = (String) responseBody.get("telefone");
                String matricula = (String) responseBody.get("matricula");

                session.setAttribute("token", token);
                session.setAttribute("userId", userId);
                session.setAttribute("nome", nome);
                session.setAttribute("email", email);
                session.setAttribute("role", role);
                session.setAttribute("matricula", matricula);
                session.setAttribute("telefone", telefone);

                return new ModelAndView("redirect:/");

            } else {
                return new ModelAndView("login/index").addObject("errorMessage", "Falha no login!");
            }
        } catch (Exception e) {
            return new ModelAndView("login/index").addObject("errorMessage", "Usuário inexistente ou senha inválida.");
        }
    }

   public boolean verificarTokenValido(HttpSession session) {
    String token = (String) session.getAttribute("token");

    String url = ConfigEnvs.servidor + "/validarToken/";

    if (token != null) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return false;    
}


// ...

public ModelAndView redefineSenha(String senha, String confirmarSenha, String token, HttpSession session) {
    String url = ConfigEnvs.servidor + "/forgotpassword/" + token;

    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("novaSenha", senha);
    requestBody.put("confirmarSenha", confirmarSenha);

    try {
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return new ModelAndView("password/edit")
                .addObject("successMessage", "Senha redefinida com sucesso!");
        } else {
            return new ModelAndView("login/index")
                .addObject("errorMessage", "Falha ao redefinir senha!");
        }

    } catch (HttpClientErrorException e) {
        try {
            String responseBody = e.getResponseBodyAsString();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(responseBody, new TypeReference<>() {});
            Map<String, String> errors = (Map<String, String>) body.get("errors");

            // Pega a primeira mensagem de erro, se existir
            String mensagem = errors.values().stream().findFirst().orElse("Erro ao redefinir senha.");

            return new ModelAndView("password/edit").addObject("errorMessage", mensagem);

        } catch (Exception ex) {
            return new ModelAndView("password/edit").addObject("errorMessage", "Token expirado ou inativo.");
        }
    } catch (Exception e) {
        return new ModelAndView("password/edit").addObject("errorMessage", "Token expirado ou inativo.");
    }
}

}

