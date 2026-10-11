package com.gigwork;

import com.gigwork.application.GigApplication;
import com.gigwork.application.GigApplicationRepository;
import com.gigwork.gig.Gig;
import com.gigwork.gig.GigRepository;
import com.gigwork.user.User;
import com.gigwork.user.UserRepository;
import com.gigwork.user.UserRole;
import com.gigwork.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GigWorkApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GigRepository gigRepository;

    @Autowired
    private GigApplicationRepository applicationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clearIsolatedTestData() {
        applicationRepository.deleteAll();
        gigRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoadsAndRoutesAreRegistered() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("illustrative sample listings")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("bookmark-button"))));

        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void registrationHashesPasswordAndLoginUsesEmail() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("name", "  Test Member  ")
                        .param("email", "Test.Member@example.test")
                        .param("password", "safe-pass-123")
                        .param("confirmPassword", "safe-pass-123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        User saved = userRepository
                .findByEmailIgnoreCase("test.member@example.test")
                .orElseThrow();
        assertEquals("Test Member", saved.getName());
        assertNotEquals("safe-pass-123", saved.getPasswordHash());
        assertTrue(passwordEncoder.matches(
                "safe-pass-123", saved.getPasswordHash()));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("email", "TEST.MEMBER@example.test")
                        .param("password", "safe-pass-123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("email", "test.member@example.test")
                        .param("password", "incorrect-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void profileRendersAndUpdatesOnlyTheAuthenticatedUsersName() throws Exception {
        User current = createUser("Current Member", "current@example.test");
        User other = createUser("Other Member", "other@example.test");

        mockMvc.perform(get("/profile")
                        .with(user(current.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/index"))
                .andExpect(content().string(containsString(current.getEmail())))
                .andExpect(content().string(containsString("Current Member")));

        mockMvc.perform(post("/profile/update")
                        .with(user(current.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("name", "  Updated Member  ")
                        .param("role", "ADMIN")
                        .param("email", other.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute(
                        "successMessage", "Your profile was updated successfully."));

        assertEquals("Updated Member", userRepository.findById(current.getId())
                .orElseThrow().getName());
        assertEquals(UserRole.USER, userRepository.findById(current.getId())
                .orElseThrow().getRole());
        assertEquals("Other Member", userRepository.findById(other.getId())
                .orElseThrow().getName());
    }

    @Test
    void profileRejectsBlankAndOversizedNamesAndRequiresCsrf() throws Exception {
        User current = createUser("Current Member", "current@example.test");

        mockMvc.perform(post("/profile/update")
                        .with(user(current.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("name", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(
                        "errorMessage", "Your display name cannot be empty."));

        mockMvc.perform(post("/profile/update")
                        .with(user(current.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("name", "x".repeat(101)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(
                        "errorMessage",
                        "Your display name must be 100 characters or fewer."));

        mockMvc.perform(post("/profile/update")
                        .with(user(current.getEmail()).roles("USER"))
                        .param("name", "No CSRF"))
                .andExpect(status().isForbidden());

        assertEquals("Current Member", userRepository.findById(current.getId())
                .orElseThrow().getName());
    }

    @Test
    void dashboardNavigationKeepsAuthenticatedBrandLinksAndAdminLinkScoped() throws Exception {
        User current = createUser("Current Member", "current@example.test");

        mockMvc.perform(get("/dashboard")
                        .with(user(current.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/dashboard")))
                .andExpect(content().string(containsString("/applications/my")))
                .andExpect(content().string(containsString("/profile")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("href=\"/admin\""))));

        mockMvc.perform(get("/dashboard")
                        .with(user("admin@example.test").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/admin\"")));

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/\"")));
    }

    @Test
    void authenticatedLogoTargetsDashboardAndGuestLogosStayPublic() throws Exception {
        List<String> authenticatedTemplates = List.of(
                "profile/index.html",
                "applications/my.html",
                "applications/received.html",
                "admin/index.html",
                "gigs/list.html",
                "gigs/detail.html",
                "gigs/new.html",
                "dashboard/index.html"
        );
        for (String template : authenticatedTemplates) {
            assertBrandTarget(template, "/dashboard");
        }

        assertBrandTarget("auth/login.html", "/");
        assertBrandTarget("auth/register.html", "/");
    }

    @Test
    void gigPostingValidatesBudgetAndAssociatesTheCreator() throws Exception {
        User owner = createUser("Gig Owner", "owner@example.test");

        mockMvc.perform(post("/gigs/new")
                        .with(user(owner.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("title", "Design work")
                        .param("description", "Create a clean brand system")
                        .param("category", "Design & Creative")
                        .param("location", "Remote")
                        .param("budget", "1000001"))
                .andExpect(status().isOk())
                .andExpect(view().name("gigs/new"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .model().attributeHasFieldErrors("gigForm", "budget"));

        assertTrue(gigRepository.findAll().isEmpty());

        mockMvc.perform(post("/gigs/new")
                        .with(user(owner.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("title", "Design work")
                        .param("description", "Create a clean brand system")
                        .param("category", "Unexpected category")
                        .param("location", "Remote")
                        .param("budget", "5000.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("gigs/new"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .model().attributeHasFieldErrors("gigForm", "category"));

        assertTrue(gigRepository.findAll().isEmpty());

        mockMvc.perform(post("/gigs/new")
                        .with(user(owner.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("title", "  Design work  ")
                        .param("description", "  Create a clean brand system  ")
                        .param("category", "  Design & Creative  ")
                        .param("location", " Remote ")
                        .param("budget", "5000.00"))
                .andExpect(status().is3xxRedirection());

        Gig saved = gigRepository.findAll().getFirst();
        assertEquals("Design work", saved.getTitle());
        assertEquals("Create a clean brand system", saved.getDescription());
        assertEquals(owner.getId(), saved.getCreator().getId());
        assertEquals("OPEN", saved.getStatus());
    }

    @Test
    void marketplaceSearchIsCaseInsensitiveAndHidesRemovedListings() throws Exception {
        User owner = createUser("Gig Owner", "owner@example.test");
        Gig matching = createGig(owner, "Open designer work", "Remote",
                "Design & Creative", "OPEN");
        createGig(owner, "Another listing", "Remote", "Technology", "OPEN");
        createGig(owner, "Hidden designer listing", "Remote",
                "Design & Creative", "REMOVED");

        mockMvc.perform(get("/gigs")
                        .with(user("member@example.test").roles("USER"))
                        .param("q", "  DESIGNER  ")
                        .param("category", " design & creative "))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Open designer work")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("Hidden designer listing"))))
                .andExpect(content().string(containsString("category-chip  active")));

        mockMvc.perform(get("/gigs/" + matching.getId())
                        .with(user("member@example.test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Gig Owner")))
                .andExpect(content().string(containsString("Posted")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("Posted recently"))));
    }

    @Test
    void missingAndRemovedGigDetailsAreHandledAndCannotBeAppliedTo() throws Exception {
        User owner = createUser("Gig Owner", "owner@example.test");
        User applicant = createUser("Applicant", "applicant@example.test");
        Gig removed = createGig(owner, "Removed work", "Remote",
                "Technology", "REMOVED");

        mockMvc.perform(get("/gigs/999999")
                        .with(user(applicant.getEmail()).roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gigs"));

        mockMvc.perform(get("/gigs/" + removed.getId())
                        .with(user(applicant.getEmail()).roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gigs"));

        mockMvc.perform(post("/applications/apply/" + removed.getId())
                        .with(user(applicant.getEmail()).roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertTrue(applicationRepository.findAll().isEmpty());
    }

    @Test
    void applicationWorkflowIsScopedFetchesTemplateDataAndEnforcesOwnerTransitions()
            throws Exception {
        User owner = createUser("Gig Owner", "owner@example.test");
        User applicant = createUser("Applicant", "applicant@example.test");
        User other = createUser("Other Member", "other@example.test");
        Gig gig = createGig(owner, "Build a website", "Remote",
                "Technology", "OPEN");

        mockMvc.perform(post("/applications/apply/" + gig.getId())
                        .with(user(applicant.getEmail()).roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertEquals(1, applicationRepository.count());

        mockMvc.perform(post("/applications/apply/" + gig.getId())
                        .with(user(applicant.getEmail()).roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(
                        "errorMessage", "You have already applied to this gig."));
        assertEquals(1, applicationRepository.count());

        mockMvc.perform(get("/applications/my")
                        .with(user(applicant.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Build a website")));

        mockMvc.perform(get("/applications/my")
                        .with(user(other.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("Build a website"))));

        mockMvc.perform(get("/applications/received")
                        .with(user(owner.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(applicant.getEmail())))
                .andExpect(content().string(containsString("Build a website")));

        mockMvc.perform(get("/applications/received")
                        .with(user(other.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString(applicant.getEmail()))));

        Long applicationId = applicationRepository.findAll().getFirst().getId();
        mockMvc.perform(post("/applications/" + applicationId + "/status")
                        .with(user(other.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("status", "ACCEPTED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage",
                        "You are not authorized to manage this application."));

        mockMvc.perform(post("/applications/" + applicationId + "/status")
                        .with(user(owner.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("status", "ACCEPTED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage",
                        "Application status updated successfully."));

        mockMvc.perform(post("/applications/" + applicationId + "/status")
                        .with(user(owner.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("status", "REJECTED"))
                .andExpect(status().is3xxRedirection());

        assertEquals("ACCEPTED", applicationRepository.findById(applicationId)
                .orElseThrow().getStatus());
    }

    @Test
    void selfApplicationsAndInvalidApplicationStatusesAreRejected() throws Exception {
        User owner = createUser("Gig Owner", "owner@example.test");
        Gig gig = createGig(owner, "My own gig", "Remote", "Technology", "OPEN");

        mockMvc.perform(post("/applications/apply/" + gig.getId())
                        .with(user(owner.getEmail()).roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(
                        "errorMessage", "You cannot apply to your own gig."));
        assertTrue(applicationRepository.findAll().isEmpty());

        mockMvc.perform(post("/applications/123/status")
                        .with(user(owner.getEmail()).roles("USER"))
                        .with(csrf())
                        .param("status", "PENDING"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(
                        "errorMessage", "Choose a valid application status."));
    }

    @Test
    void adminRequiresRoleAndCanModerateWithoutDeleting() throws Exception {
        User owner = createUser("Gig Owner", "owner@example.test");
        Gig gig = createGig(owner, "Moderated gig", "Remote", "Technology", "OPEN");

        mockMvc.perform(get("/admin")
                        .with(user("member@example.test").roles("USER")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/admin")
                        .with(user("admin@example.test").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"))
                .andExpect(content().string(containsString("Moderated gig")));

        mockMvc.perform(post("/admin/gigs/" + gig.getId() + "/status")
                        .with(user("member@example.test").roles("USER"))
                        .with(csrf())
                        .param("status", "REMOVED"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/admin/gigs/" + gig.getId() + "/status")
                        .with(user("admin@example.test").roles("ADMIN"))
                        .with(csrf())
                        .param("status", "REMOVED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        assertEquals("REMOVED", gigRepository.findById(gig.getId())
                .orElseThrow().getStatus());
        assertTrue(gigRepository.existsById(gig.getId()));

        mockMvc.perform(post("/admin/gigs/" + gig.getId() + "/status")
                        .with(user("admin@example.test").roles("ADMIN"))
                        .with(csrf())
                        .param("status", "OPEN"))
                .andExpect(status().is3xxRedirection());
        assertEquals("OPEN", gigRepository.findById(gig.getId())
                .orElseThrow().getStatus());

        mockMvc.perform(post("/admin/gigs/" + gig.getId() + "/status")
                        .with(user("admin@example.test").roles("ADMIN"))
                        .with(csrf())
                        .param("status", "PENDING"))
                .andExpect(status().is3xxRedirection());
        assertEquals("OPEN", gigRepository.findById(gig.getId())
                .orElseThrow().getStatus());
    }

    @Test
    void loggedOutUsersCannotUseProtectedPostRoutesAndLogoutNeedsCsrf()
            throws Exception {
        mockMvc.perform(post("/applications/apply/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/logout"))
                .andExpect(status().isForbidden());
    }

    private User createUser(String name, String email) {
        return userService.register(name, email, "test-password-123");
    }

    private void assertBrandTarget(String template, String expected)
            throws Exception {
        String html = new ClassPathResource("templates/" + template)
                .getContentAsString(StandardCharsets.UTF_8);
        Matcher matcher = Pattern.compile(
                "<a\\s+class=\"brand\"\\s+th:href=\"@\\{([^}]+)}\"")
                .matcher(html);

        assertTrue(matcher.find(), "No brand link in " + template);
        assertEquals(expected, matcher.group(1), "Brand destination in " + template);
    }

    private Gig createGig(
            User creator, String title, String location,
            String category, String status) {
        Gig gig = new Gig();
        gig.setTitle(title);
        gig.setDescription("A project description for " + title);
        gig.setLocation(location);
        gig.setCategory(category);
        gig.setBudget(new BigDecimal("5000.00"));
        gig.setCreator(creator);
        gig.setStatus(status);
        return gigRepository.save(gig);
    }
}
