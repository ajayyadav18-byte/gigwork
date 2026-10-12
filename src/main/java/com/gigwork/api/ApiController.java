package com.gigwork.api;

import com.gigwork.api.dto.ApiDtos.*;
import com.gigwork.application.GigApplication;
import com.gigwork.application.GigApplicationService;
import com.gigwork.config.JwtUtil;
import com.gigwork.gig.Gig;
import com.gigwork.gig.GigRepository;
import com.gigwork.gig.GigService;
import com.gigwork.user.User;
import com.gigwork.user.UserRepository;
import com.gigwork.user.UserRole;
import com.gigwork.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final GigService gigService;
    private final GigRepository gigRepository;
    private final GigApplicationService applicationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public ApiController(
            UserService userService,
            UserRepository userRepository,
            GigService gigService,
            GigRepository gigRepository,
            GigApplicationService applicationService,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.gigService = gigService;
        this.gigRepository = gigRepository;
        this.applicationService = applicationService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    private User getAuthenticatedUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return null;
        }
        String email = jwtUtil.extractEmail(token);
        if (email == null) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    private GigDto toGigDto(Gig gig) {
        GigDto dto = new GigDto();
        dto.setId(gig.getId());
        dto.setTitle(gig.getTitle());
        dto.setDescription(gig.getDescription());
        dto.setCategory(gig.getCategory());
        dto.setLocation(gig.getLocation());
        dto.setBudget(gig.getBudget());
        dto.setStatus(gig.getStatus());
        dto.setCreatedAt(gig.getCreatedAt() != null ? gig.getCreatedAt().format(DATE_FORMATTER) : "");
        if (gig.getCreator() != null) {
            dto.setCreatorId(gig.getCreator().getId());
            dto.setCreatorName(gig.getCreator().getName());
        }
        return dto;
    }

    private ApplicationDto toAppDto(GigApplication app) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId());
        if (app.getGig() != null) {
            dto.setGigId(app.getGig().getId());
            dto.setGigTitle(app.getGig().getTitle());
            dto.setGigCategory(app.getGig().getCategory());
            dto.setGigLocation(app.getGig().getLocation());
            dto.setGigBudget(app.getGig().getBudget());
        }
        if (app.getApplicant() != null) {
            dto.setApplicantId(app.getApplicant().getId());
            dto.setApplicantName(app.getApplicant().getName());
            dto.setApplicantEmail(app.getApplicant().getEmail());
        }
        dto.setStatus(app.getStatus());
        dto.setCreatedAt(app.getCreatedAt() != null ? app.getCreatedAt().format(DATE_FORMATTER) : "");
        return dto;
    }

    // --- AUTH ---

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            if (request.getPassword() != null && !request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Passwords do not match."));
            }
            User user = userService.register(request.getName(), request.getEmail(), request.getPassword());
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required."));
        }
        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim().toLowerCase(Locale.ROOT)).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password."));
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name()));
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        return ResponseEntity.ok(new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name()));
    }

    // --- DASHBOARD ---

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        long gigCount = gigRepository.countByCreatorIdAndStatusIgnoreCase(user.getId(), "OPEN");
        long applicationCount = applicationService.getMyApplications(user).size();
        long receivedApplicationCount = applicationService.getReceivedApplications(user).size();
        List<GigDto> latestGigs = gigService.searchOpenGigs("", "all").stream()
                .limit(3)
                .map(this::toGigDto)
                .collect(Collectors.toList());

        DashboardDto dto = new DashboardDto(
                new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name()),
                gigCount,
                applicationCount,
                receivedApplicationCount,
                latestGigs
        );
        return ResponseEntity.ok(dto);
    }

    // --- GIGS ---

    @GetMapping("/gigs")
    public ResponseEntity<?> listGigs(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "all") String category) {
        List<Gig> gigs = gigService.searchOpenGigs(q, category);
        List<GigDto> dtoList = gigs.stream().map(this::toGigDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/gigs/{id}")
    public ResponseEntity<?> getGig(@PathVariable Long id) {
        try {
            Gig gig = gigService.findGigById(id);
            return ResponseEntity.ok(toGigDto(gig));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Gig not found."));
        }
    }

    @PostMapping("/gigs")
    public ResponseEntity<?> createGig(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody GigCreateRequest req) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        if (req.getTitle() == null || req.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please enter a gig title."));
        }
        if (req.getDescription() == null || req.getDescription().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please describe the project."));
        }
        if (req.getCategory() == null || req.getCategory().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please choose a category."));
        }
        if (req.getBudget() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please enter a budget."));
        }

        Gig gig = new Gig();
        gig.setTitle(req.getTitle().trim());
        gig.setDescription(req.getDescription().trim());
        gig.setCategory(req.getCategory().trim());
        gig.setLocation(req.getLocation() == null || req.getLocation().isBlank() ? "Remote" : req.getLocation().trim());
        gig.setBudget(req.getBudget());
        gig.setCreator(user);

        Gig saved = gigService.createGig(gig);
        return ResponseEntity.ok(toGigDto(saved));
    }

    // --- APPLICATIONS ---

    @PostMapping("/applications/apply/{gigId}")
    public ResponseEntity<?> apply(
            @PathVariable Long gigId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        try {
            applicationService.apply(gigId, user);
            return ResponseEntity.ok(new ApiResponse(true, "Application submitted successfully."));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/applications/my")
    public ResponseEntity<?> myApplications(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        List<GigApplication> apps = applicationService.getMyApplications(user);
        List<ApplicationDto> dtos = apps.stream().map(this::toAppDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/applications/received")
    public ResponseEntity<?> receivedApplications(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        List<GigApplication> apps = applicationService.getReceivedApplications(user);
        List<ApplicationDto> dtos = apps.stream().map(this::toAppDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/applications/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        String status = body.get("status");
        try {
            applicationService.updateApplicationStatus(id, user, status);
            return ResponseEntity.ok(new ApiResponse(true, "Application status updated."));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --- PROFILE ---

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        return ResponseEntity.ok(new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name()));
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        String name = body.get("name");
        if (name == null || name.trim().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Display name cannot be empty."));
        }
        if (name.trim().length() > 100) {
            return ResponseEntity.badRequest().body(Map.of("message", "Display name must be 100 characters or fewer."));
        }
        user.setName(name.trim());
        userRepository.save(user);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name()));
    }

    // --- ADMIN ---

    @GetMapping("/admin")
    public ResponseEntity<?> getAdminData(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied."));
        }

        List<User> users = userRepository.findAll();
        List<Gig> gigs = gigRepository.findAll();

        long openGigs = gigs.stream().filter(g -> "OPEN".equalsIgnoreCase(g.getStatus())).count();
        List<UserDto> userDtos = users.stream().map(u -> new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole().name())).collect(Collectors.toList());
        List<GigDto> gigDtos = gigs.stream().map(this::toGigDto).collect(Collectors.toList());

        return ResponseEntity.ok(new AdminDto(users.size(), gigs.size(), openGigs, userDtos, gigDtos));
    }

    @PostMapping("/admin/gigs/{id}/status")
    public ResponseEntity<?> updateGigStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied."));
        }

        String status = body.get("status");
        String cleanStatus = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);

        if (!cleanStatus.equals("OPEN") && !cleanStatus.equals("REMOVED")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid moderation action."));
        }

        Gig gig = gigRepository.findById(id).orElse(null);
        if (gig == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Gig not found."));
        }

        gig.setStatus(cleanStatus);
        gigRepository.save(gig);
        return ResponseEntity.ok(new ApiResponse(true, "Gig status updated to " + cleanStatus));
    }
}
