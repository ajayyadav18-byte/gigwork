package com.gigwork.application;

import com.gigwork.user.User;
import com.gigwork.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final GigApplicationService applicationService;
    private final UserRepository userRepository;

    public ApplicationController(
            GigApplicationService applicationService,
            UserRepository userRepository) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/apply/{gigId}")
    public String apply(
            @PathVariable Long gigId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = getLoggedInUser(authentication);

        try {
            applicationService.apply(gigId, user);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your application was submitted successfully!");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());
        }

        return "redirect:/gigs/" + gigId;
    }

    @GetMapping("/my")
    public String myApplications(
            Authentication authentication, Model model) {

        User user = getLoggedInUser(authentication);

        var applications = applicationService.getMyApplications(user);
        model.addAttribute("applications", applications);
        model.addAttribute("pendingCount", applications.stream()
                .filter(application -> "PENDING".equalsIgnoreCase(
                        application.getStatus()))
                .count());
        model.addAttribute("acceptedCount", applications.stream()
                .filter(application -> "ACCEPTED".equalsIgnoreCase(
                        application.getStatus()))
                .count());

        return "applications/my";
    }

    @GetMapping("/received")
    public String receivedApplications(
            Authentication authentication, Model model) {

        User employer = getLoggedInUser(authentication);
        var applications = applicationService.getReceivedApplications(employer);
        model.addAttribute("applications", applications);
        model.addAttribute("pendingCount", applications.stream()
                .filter(application -> "PENDING".equalsIgnoreCase(
                        application.getStatus()))
                .count());
        model.addAttribute("acceptedCount", applications.stream()
                .filter(application -> "ACCEPTED".equalsIgnoreCase(
                        application.getStatus()))
                .count());

        return "applications/received";
    }

    @PostMapping("/{applicationId}/status")
    public String updateStatus(
            @PathVariable Long applicationId,
            @RequestParam String status,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User employer = getLoggedInUser(authentication);

        try {
            applicationService.updateApplicationStatus(
                    applicationId, employer, status);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Application status updated successfully.");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());
        }

        return "redirect:/applications/received";
    }

    private User getLoggedInUser(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("Please log in first.");
        }

        return userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "The logged-in user could not be found."));
    }
}
