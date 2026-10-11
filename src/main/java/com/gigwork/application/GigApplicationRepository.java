package com.gigwork.application;

import com.gigwork.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GigApplicationRepository
        extends JpaRepository<GigApplication, Long> {

    @EntityGraph(attributePaths = "gig")
    List<GigApplication> findByApplicantOrderByCreatedAtDesc(User applicant);

    @EntityGraph(attributePaths = {"gig", "applicant"})
    List<GigApplication> findByGig_Creator_IdOrderByCreatedAtDesc(
            Long creatorId);

    boolean existsByApplicantIdAndGigId(
            Long applicantId, Long gigId);
}
