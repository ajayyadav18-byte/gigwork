package com.gigwork.api.dto;

import java.math.BigDecimal;
import java.util.List;

public class ApiDtos {

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        private String confirmPassword;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }

    public static class AuthResponse {
        private String token;
        private Long id;
        private String name;
        private String email;
        private String role;

        public AuthResponse() {}

        public AuthResponse(String token, Long id, String name, String email, String role) {
            this.token = token;
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public String getToken() { return token; }
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }

    public static class UserDto {
        private Long id;
        private String name;
        private String email;
        private String role;

        public UserDto(Long id, String name, String email, String role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }

    public static class GigDto {
        private Long id;
        private String title;
        private String description;
        private String category;
        private String location;
        private BigDecimal budget;
        private String status;
        private String createdAt;
        private Long creatorId;
        private String creatorName;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public BigDecimal getBudget() { return budget; }
        public void setBudget(BigDecimal budget) { this.budget = budget; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public Long getCreatorId() { return creatorId; }
        public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
        public String getCreatorName() { return creatorName; }
        public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    }

    public static class GigCreateRequest {
        private String title;
        private String description;
        private String category;
        private String location;
        private BigDecimal budget;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public BigDecimal getBudget() { return budget; }
        public void setBudget(BigDecimal budget) { this.budget = budget; }
    }

    public static class ApplicationDto {
        private Long id;
        private Long gigId;
        private String gigTitle;
        private String gigCategory;
        private String gigLocation;
        private BigDecimal gigBudget;
        private Long applicantId;
        private String applicantName;
        private String applicantEmail;
        private String status;
        private String createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getGigId() { return gigId; }
        public void setGigId(Long gigId) { this.gigId = gigId; }
        public String getGigTitle() { return gigTitle; }
        public void setGigTitle(String gigTitle) { this.gigTitle = gigTitle; }
        public String getGigCategory() { return gigCategory; }
        public void setGigCategory(String gigCategory) { this.gigCategory = gigCategory; }
        public String getGigLocation() { return gigLocation; }
        public void setGigLocation(String gigLocation) { this.gigLocation = gigLocation; }
        public BigDecimal getGigBudget() { return gigBudget; }
        public void setGigBudget(BigDecimal gigBudget) { this.gigBudget = gigBudget; }
        public Long getApplicantId() { return applicantId; }
        public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
        public String getApplicantName() { return applicantName; }
        public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
        public String getApplicantEmail() { return applicantEmail; }
        public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    public static class DashboardDto {
        private UserDto user;
        private long gigCount;
        private long applicationCount;
        private long receivedApplicationCount;
        private List<GigDto> latestGigs;

        public DashboardDto(UserDto user, long gigCount, long applicationCount, long receivedApplicationCount, List<GigDto> latestGigs) {
            this.user = user;
            this.gigCount = gigCount;
            this.applicationCount = applicationCount;
            this.receivedApplicationCount = receivedApplicationCount;
            this.latestGigs = latestGigs;
        }

        public UserDto getUser() { return user; }
        public long getGigCount() { return gigCount; }
        public long getApplicationCount() { return applicationCount; }
        public long getReceivedApplicationCount() { return receivedApplicationCount; }
        public List<GigDto> getLatestGigs() { return latestGigs; }
    }

    public static class AdminDto {
        private int totalUsers;
        private int totalGigs;
        private long openGigs;
        private List<UserDto> users;
        private List<GigDto> gigs;

        public AdminDto(int totalUsers, int totalGigs, long openGigs, List<UserDto> users, List<GigDto> gigs) {
            this.totalUsers = totalUsers;
            this.totalGigs = totalGigs;
            this.openGigs = openGigs;
            this.users = users;
            this.gigs = gigs;
        }

        public int getTotalUsers() { return totalUsers; }
        public int getTotalGigs() { return totalGigs; }
        public long getOpenGigs() { return openGigs; }
        public List<UserDto> getUsers() { return users; }
        public List<GigDto> getGigs() { return gigs; }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
