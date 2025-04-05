package cog.com.sic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    public ModelAndView realizarLogin(String matricula, String senha, HttpSession session) {
        String url = "http://localhost:8081/login/";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "admin@sic.com");
        requestBody.put("password", "admin123");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String token = (String) responseBody.get("token");
                Integer userId = (Integer) responseBody.get("id");
                String nome = (String) responseBody.get("nome");
                String email = (String) responseBody.get("email");
                String role = (String) responseBody.get("role");

                session.setAttribute("token", token);
                session.setAttribute("userId", userId);
                session.setAttribute("nome", nome);
                session.setAttribute("email", email);
                session.setAttribute("role", role);

                // Redirecionando para a página inicial
                return new ModelAndView("redirect:/");

            } else {
                return new ModelAndView("login/index").addObject("errorMessage", "Falha no login!");
            }
        } catch (Exception e) {
            System.err.println(e);
            return new ModelAndView("login/index").addObject("errorMessage", "Erro ao conectar ao servidor.");
        }
    }

   public boolean verificarTokenValido(HttpSession session) {
    String token = (String) session.getAttribute("token");

    // URL do servidor externo para verificar o token
    String url = "http://localhost:8081/validarToken/";

    if (token != null) {
        // Criando o cabeçalho com o token no formato Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Criando a requisição com o cabeçalho
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Enviando requisição GET para a API com o token no cabeçalho
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            // Verificando se a resposta foi bem-sucedida
            if (response.getStatusCode().is2xxSuccessful()) {
                return true; // Token válido
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    return false; 
}
}

