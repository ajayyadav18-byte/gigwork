package com.gigwork.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String showProfile(
            Authentication authentication, Model model) {

        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);

        return "profile/index";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam(name = "name", defaultValue = "") String name,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        String cleanName = name == null ? "" : name.trim();

        if (cleanName.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Your display name cannot be empty.");
            return "redirect:/profile";
        }

        if (cleanName.length() > 100) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Your display name must be 100 characters or fewer.");
            return "redirect:/profile";
        }

        user.setName(cleanName);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Your profile was updated successfully.");

        return "redirect:/profile";
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("Please log in first.");
        }

        return userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The logged-in user could not be found."));
    }
}
