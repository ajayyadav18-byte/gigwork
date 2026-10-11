
package com.gigwork.gig;

import com.gigwork.user.User;
import com.gigwork.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/gigs")
public class GigController {

    private final GigService gigService;
    private final UserRepository userRepository;

    public GigController(
            GigService gigService,
            UserRepository userRepository) {
        this.gigService = gigService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listGigs(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "all") String category,
            Model model) {

        List<String> categories = List.of(
                "Design & Creative",
                "Technology",
                "Writing",
                "Marketing",
                "Education",
                "Other"
        );
        String query = q == null ? "" : q.trim();
        String submittedCategory = category == null ? "all" : category.trim();
        String selectedCategory = categories.stream()
                .filter(item -> item.equalsIgnoreCase(submittedCategory))
                .findFirst()
                .orElse(submittedCategory.isBlank() ? "all" : submittedCategory);

        model.addAttribute("gigs",
                gigService.searchOpenGigs(query, selectedCategory));
        model.addAttribute("query", query);
        model.addAttribute("selectedCategory", selectedCategory);
        model.addAttribute("categories", categories);

        return "gigs/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("gigForm", new GigForm());
        return "gigs/new";
    }

    @PostMapping("/new")
    public String createGig(
            @Valid @ModelAttribute("gigForm") GigForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "gigs/new";
        }

        User creator = userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The logged-in user could not be found."));

        Gig gig = new Gig();
        gig.setTitle(form.getTitle().trim());
        gig.setDescription(form.getDescription().trim());
        gig.setCategory(form.getCategory().trim());
        gig.setLocation(
                form.getLocation() == null
                        || form.getLocation().isBlank()
                        ? "Remote"
                        : form.getLocation().trim());
        gig.setBudget(form.getBudget());
        gig.setCreator(creator);

        Gig savedGig = gigService.createGig(gig);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your gig has been published successfully!");

        return "redirect:/gigs/" + savedGig.getId();
    }

    @GetMapping("/{id}")
    public String gigDetails(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Gig gig = gigService.findGigById(id);
            if (!"OPEN".equalsIgnoreCase(gig.getStatus())) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage", "This gig is no longer available.");
                return "redirect:/gigs";
            }
            model.addAttribute("gig", gig);
            return "gigs/detail";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "That gig could not be found.");
            return "redirect:/gigs";
        }
    }
}
