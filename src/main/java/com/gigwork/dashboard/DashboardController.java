package com.gigwork.dashboard;

import com.gigwork.application.GigApplicationService;
import com.gigwork.gig.GigRepository;
import com.gigwork.gig.GigService;
import com.gigwork.user.User;
import com.gigwork.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final GigRepository gigRepository;
    private final GigService gigService;
    private final GigApplicationService applicationService;

    public DashboardController(
            UserRepository userRepository,
            GigRepository gigRepository,
            GigService gigService,
            GigApplicationService applicationService) {
        this.userRepository = userRepository;
        this.gigRepository = gigRepository;
        this.gigService = gigService;
        this.applicationService = applicationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        User user = userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "The logged-in user could not be found."));

        model.addAttribute("user", user);
        model.addAttribute("gigCount",
                gigRepository.countByCreatorIdAndStatusIgnoreCase(
                        user.getId(), "OPEN"));
        model.addAttribute("applicationCount",
                applicationService.getMyApplications(user).size());
        model.addAttribute("receivedApplicationCount",
                applicationService.getReceivedApplications(user).size());
        model.addAttribute("latestGigs",
                gigService.searchOpenGigs("", "all").stream().limit(3).toList());

        return "dashboard/index";
    }
}

