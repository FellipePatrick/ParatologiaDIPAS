package cog.com.sic.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        return service.realizarLogin(matricula, senha, session);
    }
    @GetMapping("/login")
    public ModelAndView indexLogin(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        
        return new ModelAndView("login/index");
    }

    @GetMapping("/password")
    public ModelAndView edit(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        
        return new ModelAndView("login/edit");
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
