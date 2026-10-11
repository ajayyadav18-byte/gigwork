package com.gigwork.gig;

import com.gigwork.user.User;
import com.gigwork.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Adds clearly identified showcase listings when explicitly enabled for a
 * registered owner. Disabled by default for every environment.
 */
@Component
@ConditionalOnProperty(
        prefix = "gigwork.demo",
        name = "seed-enabled",
        havingValue = "true")
public class DemoGigSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoGigSeeder.class);

    private static final List<DemoGig> SHOWCASE_GIGS = List.of(
            new DemoGig(
                    "Build a responsive student portfolio",
                    "Technology",
                    "Remote",
                    "3200.00",
                    "Create a polished, mobile-friendly portfolio page that introduces a student, highlights three projects, and includes a contact section. Share a link to a few previous projects or a simple design direction when applying."),
            new DemoGig(
                    "Create social media artwork for a campus event",
                    "Design & Creative",
                    "Hybrid",
                    "1800.00",
                    "Design a coordinated set of social posts for a campus innovation day. Include a square announcement graphic and two story-sized variations. Please share a portfolio link or a sample of your visual style."),
            new DemoGig(
                    "Turn research notes into a clear student guide",
                    "Writing",
                    "Remote",
                    "1200.00",
                    "Organize a short set of research notes into a friendly, well-structured guide with headings, a concise introduction, and a source list. Strong editing and clear communication are important for this project."),
            new DemoGig(
                    "Clean a spreadsheet and summarize the results",
                    "Technology",
                    "Remote",
                    "1500.00",
                    "Tidy an event-registration spreadsheet, standardize categories, remove duplicate rows, and prepare a short summary of the results. Please be comfortable working with spreadsheet filters and formulas."),
            new DemoGig(
                    "Design a one-page landing page for a student showcase",
                    "Design & Creative",
                    "Remote",
                    "2800.00",
                    "Create a clean landing-page concept for a student project showcase. The page should introduce the event, highlight a few projects, and make the schedule easy to scan. A responsive layout and clear visual hierarchy are important.")
    );

    private final UserRepository userRepository;
    private final GigRepository gigRepository;
    private final GigService gigService;
    private final String ownerEmail;

    public DemoGigSeeder(
            UserRepository userRepository,
            GigRepository gigRepository,
            GigService gigService,
            @Value("${gigwork.demo.owner-email}") String ownerEmail) {
        this.userRepository = userRepository;
        this.gigRepository = gigRepository;
        this.gigService = gigService;
        this.ownerEmail = ownerEmail;
    }

    @Override
    public void run(ApplicationArguments args) {
        User owner = userRepository.findByEmailIgnoreCase(ownerEmail)
                .orElse(null);
        if (owner == null) {
            log.info("Showcase listings are waiting for the configured owner to register.");
            return;
        }

        seedForOwner(owner);
    }

    public void seedForOwner(User owner) {
        if (owner == null || ownerEmail == null
                || !owner.getEmail().equalsIgnoreCase(ownerEmail.trim())) {
            return;
        }

        int created = 0;
        for (DemoGig showcaseGig : SHOWCASE_GIGS) {
            if (gigRepository.existsByCreatorIdAndTitleIgnoreCase(
                    owner.getId(), showcaseGig.title())) {
                continue;
            }

            Gig gig = new Gig();
            gig.setTitle(showcaseGig.title());
            gig.setDescription(showcaseGig.description());
            gig.setCategory(showcaseGig.category());
            gig.setLocation(showcaseGig.location());
            gig.setBudget(new BigDecimal(showcaseGig.budget()));
            gig.setCreator(owner);
            gigService.createGig(gig);
            created++;
        }

        log.info("Created {} showcase listings.", created);
    }

    private record DemoGig(
            String title,
            String category,
            String location,
            String budget,
            String description) {
    }
}
