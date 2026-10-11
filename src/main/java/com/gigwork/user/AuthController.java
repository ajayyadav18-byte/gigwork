
package com.gigwork.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registrationForm") RegistrationForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (form.getPassword() != null
                && !form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue(
                    "confirmPassword",
                    "password.mismatch",
                    "Passwords do not match."
            );
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(
                    form.getName(),
                    form.getEmail(),
                    form.getPassword()
            );
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue(
                    "email",
                    "email.exists",
                    ex.getMessage()
            );

            return "auth/register";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your account has been created. Please log in."
        );

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }
}

