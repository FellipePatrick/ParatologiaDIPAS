package com.ufrn.zcheck.controller.frontEnd;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginController {
    @GetMapping("/login")
    public ModelAndView indexLogin(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        
        return new ModelAndView("login/index");
    }
    @GetMapping("/password")
    public ModelAndView edit(@ModelAttribute String s, RedirectAttributes redirectAttributes) {
        
        return new ModelAndView("login/edit");
    }
}
