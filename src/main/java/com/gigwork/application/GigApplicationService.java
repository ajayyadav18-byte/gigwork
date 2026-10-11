package com.gigwork.application;

import com.gigwork.gig.Gig;
import com.gigwork.gig.GigService;
import com.gigwork.user.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class GigApplicationService {

    private final GigApplicationRepository applicationRepository;
    private final GigService gigService;

    public GigApplicationService(
            GigApplicationRepository applicationRepository,
            GigService gigService) {
        this.applicationRepository = applicationRepository;
        this.gigService = gigService;
    }

    @Transactional
    public void apply(Long gigId, User applicant) {
        Gig gig = gigService.findGigById(gigId);

        if (!"OPEN".equalsIgnoreCase(gig.getStatus())) {
            throw new IllegalStateException(
                    "This gig is no longer accepting applications.");
        }

        if (gig.getCreator() != null
                && gig.getCreator().getId().equals(applicant.getId())) {
            throw new IllegalStateException(
                    "You cannot apply to your own gig.");
        }

        if (applicationRepository.existsByApplicantIdAndGigId(
                applicant.getId(), gigId)) {
            throw new IllegalStateException(
                    "You have already applied to this gig.");
        }

        GigApplication application = new GigApplication();
        application.setGig(gig);
        application.setApplicant(applicant);
        application.setStatus("PENDING");

        try {
            applicationRepository.saveAndFlush(application);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalStateException(
                    "You have already applied to this gig.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<GigApplication> getMyApplications(User applicant) {
        return applicationRepository
                .findByApplicantOrderByCreatedAtDesc(applicant);
    }

    @Transactional(readOnly = true)
    public List<GigApplication> getReceivedApplications(User employer) {
        return applicationRepository
                .findByGig_Creator_IdOrderByCreatedAtDesc(
                        employer.getId());
    }

    @Transactional
    public void updateApplicationStatus(
            Long applicationId, User employer, String requestedStatus) {

        String status = requestedStatus == null
                ? ""
                : requestedStatus.trim().toUpperCase(Locale.ROOT);

        if (!status.equals("ACCEPTED") && !status.equals("REJECTED")) {
            throw new IllegalArgumentException(
                    "Choose a valid application status.");
        }

        GigApplication application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Application not found."));

        Gig gig = application.getGig();

        if (gig.getCreator() == null
                || !gig.getCreator().getId().equals(employer.getId())) {
            throw new IllegalStateException(
                    "You are not authorized to manage this application.");
        }

        if (!"PENDING".equalsIgnoreCase(application.getStatus())) {
            throw new IllegalStateException(
                    "This application has already been reviewed.");
        }

        application.setStatus(status);
        applicationRepository.save(application);
    }
}
