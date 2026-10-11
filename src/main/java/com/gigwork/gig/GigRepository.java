
package com.gigwork.gig;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GigRepository extends JpaRepository<Gig, Long> {

    @Override
    @EntityGraph(attributePaths = "creator")
    Optional<Gig> findById(Long id);

    List<Gig> findByStatusOrderByCreatedAtDesc(String status);

    List<Gig> findByStatusAndCategoryIgnoreCaseOrderByCreatedAtDesc(
            String status,
            String category
    );

    List<Gig> findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            String status,
            String title
    );

    List<Gig> findByStatusAndDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
            String status,
            String description
    );
}

