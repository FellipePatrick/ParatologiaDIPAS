package com.ufrn.zcheck.controller.frontEnd;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SistemaController {
    @GetMapping("/")
    public ModelAndView indexHome(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("home/index");  
    }

    @GetMapping("/processar")
    public ModelAndView indexProcess(@ModelAttribute String s) {
        ModelAndView modelAndView = new ModelAndView("process/index");
        modelAndView.addObject("msg", "Adicione suas imagens para o processamento!");
        return modelAndView;
    }
    

    @GetMapping("/relatorios")
    public ModelAndView indexRelatorios(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("relatorios/index");
    }

    @GetMapping("/analisar")
    public ModelAndView analisarFotos(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("process/analise");
    }

    @GetMapping("/relatorios/unit")
    public ModelAndView unitRelatorios(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("relatorios/unit");
    }

    @GetMapping("/notific")
    public ModelAndView indexNotifi(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        return new ModelAndView("notific/index");
    }
}
