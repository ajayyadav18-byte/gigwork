package com.gigwork;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appName", "GigWork");
        model.addAttribute(
                "tagline",
                "Your skills. Your opportunities. Your next gig."
        );

        return "index";
    }
}

