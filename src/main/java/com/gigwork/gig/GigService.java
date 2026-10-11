
package com.gigwork.gig;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class GigService {

    private final GigRepository gigRepository;

    public GigService(GigRepository gigRepository) {
        this.gigRepository = gigRepository;
    }

    @Transactional(readOnly = true)
    public List<Gig> searchOpenGigs(String query, String category) {
        List<Gig> openGigs =
                gigRepository.findByStatusOrderByCreatedAtDesc("OPEN");

        String cleanQuery = normalize(query);
        String cleanCategory = normalize(category);

        return openGigs.stream()
                .filter(gig -> matchesQuery(gig, cleanQuery))
                .filter(gig -> matchesCategory(gig, cleanCategory))
                .toList();
    }

    private boolean matchesQuery(Gig gig, String query) {
        if (query.isEmpty()) {
            return true;
        }

        return containsIgnoreCase(gig.getTitle(), query)
                || containsIgnoreCase(gig.getDescription(), query)
                || containsIgnoreCase(gig.getCategory(), query)
                || containsIgnoreCase(gig.getLocation(), query);
    }

    private boolean matchesCategory(Gig gig, String category) {
        if (category.isEmpty() || category.equals("all")) {
            return true;
        }

        return normalize(gig.getCategory()).equals(category);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim().toLowerCase(Locale.ROOT);
    }

    public Gig createGig(Gig gig) {
        gig.setStatus("OPEN");
        return gigRepository.save(gig);
    }

    @Transactional(readOnly = true)
    public Gig findGigById(Long id) {
        return gigRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Gig not found."));
    }
}
