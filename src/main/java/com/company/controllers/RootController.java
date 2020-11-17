package com.company.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class RootController {

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("status", "Online");
        model.addAttribute("statusColor", "green");
        model.addAttribute("release", System.getenv("HEROKU_RELEASE_VERSION"));

        return "status";
    }
}
