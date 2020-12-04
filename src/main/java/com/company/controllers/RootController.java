package com.company.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/")
public class RootController {
    @GetMapping
    String index(Model model) {
        model.addAttribute("status", "Online");
        model.addAttribute("statusColor", "green");
        model.addAttribute("release", System.getenv("HEROKU_RELEASE_VERSION"));

        return "status";
    }
}
