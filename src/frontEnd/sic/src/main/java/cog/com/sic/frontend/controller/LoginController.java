package cog.com.sic.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cog.com.sic.service.AuthService;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {

    private AuthService service;
    
    public LoginController(AuthService authService){
        this.service = authService;
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String matricula, @RequestParam String senha, HttpSession session) {  
            if (session.getAttribute("token") != null) {
                return new ModelAndView("redirect:/");
            } else {
                return service.realizarLogin(matricula, senha, session);
        }
    }

    @PostMapping("/forgotpassword")
    public ModelAndView forgotPassword(@RequestParam String email, HttpSession session) {  
            return service.enviarEmailForgotPassword(email, session);
        }

    @GetMapping("/forgotpassword")
    public ModelAndView GetforgotPassword(HttpSession session) {  
            return new ModelAndView("password/index");
        }

   @GetMapping("/forgotpassword/{id}")
    public ModelAndView GetRedefinir(@PathVariable("id") String token) {
        return new ModelAndView("password/edit").addObject("token", token);
    }

    @PostMapping("/forgotpassword/{id}")
    public ModelAndView PostRedefinir(
            @PathVariable("id") String token,
            @RequestParam("novaSenha") String novaSenha,
            @RequestParam("confirmarSenha") String confirmarSenha,
            HttpSession session) {

        if (!novaSenha.equals(confirmarSenha)) {
            return new ModelAndView("password/edit")
                .addObject("token", token)
                .addObject("errorMessage", "As senhas não coincidem.");
        }

        return service.redefineSenha(novaSenha, confirmarSenha, token, session);
    }


    @GetMapping("/login")
    public ModelAndView indexLogin(@ModelAttribute String s, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("token") != null) {
            return new ModelAndView("redirect:/");
        } else {
            
            return new ModelAndView("login/index");
        }   
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("token") != null) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("successMessage", "Logout realizado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nenhuma sessão ativa encontrada.");
        }
        return new ModelAndView("redirect:/login");
    }


}
