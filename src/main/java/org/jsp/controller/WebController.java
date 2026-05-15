package org.jsp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String root() {
        return "redirect:/pages/home.html";
    }

    @RequestMapping("/")
    public String jspRoot() {
        return "redirect:/pages/home.html";
    }
}