package com.gigwork.admin;

import com.gigwork.gig.Gig;
import com.gigwork.gig.GigRepository;
import com.gigwork.user.User;
import com.gigwork.user.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final GigRepository gigRepository;

    public AdminController(
            UserRepository userRepository,
            GigRepository gigRepository) {
        this.userRepository = userRepository;
        this.gigRepository = gigRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<User> users = userRepository.findAll();
        List<Gig> gigs = gigRepository.findAll();

        model.addAttribute("users", users);
        model.addAttribute("gigs", gigs);
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalGigs", gigs.size());
        model.addAttribute(
                "openGigs",
                gigs.stream()
                        .filter(gig -> "OPEN".equalsIgnoreCase(gig.getStatus()))
                        .count()
        );

        return "admin/index";
    }

    @PostMapping("/gigs/{id}/status")
    public String updateGigStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        String cleanStatus = status == null
                ? ""
                : status.trim().toUpperCase(Locale.ROOT);

        if (!cleanStatus.equals("OPEN")
                && !cleanStatus.equals("REMOVED")) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Invalid moderation action.");
            return "redirect:/admin";
        }

        Gig gig = gigRepository.findById(id).orElse(null);

        if (gig == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The selected gig could not be found.");
            return "redirect:/admin";
        }

        gig.setStatus(cleanStatus);
        gigRepository.save(gig);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                cleanStatus.equals("REMOVED")
                        ? "The gig has been removed from the public marketplace."
                        : "The gig has been reopened."
        );

        return "redirect:/admin";
    }
}
