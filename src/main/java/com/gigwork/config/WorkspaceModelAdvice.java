package com.gigwork.config;

import com.gigwork.user.UserRepository;
import com.gigwork.user.User;
import com.gigwork.user.UserRole;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class WorkspaceModelAdvice {

    private final UserRepository userRepository;

    public WorkspaceModelAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addSignedInUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseGet(() -> new User(
                        "Member", authentication.getName(), "", UserRole.USER));
        model.addAttribute("user", user);
    }
}
