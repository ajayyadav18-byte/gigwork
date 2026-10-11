
package com.gigwork.user;

import com.gigwork.gig.DemoGigSeeder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<DemoGigSeeder> demoGigSeederProvider;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ObjectProvider<DemoGigSeeder> demoGigSeederProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.demoGigSeederProvider = demoGigSeederProvider;
    }

    @Transactional
    public User register(String name, String email, String rawPassword) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Please enter your name.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Please enter your email address.");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Please create a password.");
        }

        String cleanName = name.trim();
        String cleanEmail = email.trim().toLowerCase(Locale.ROOT);

        if (cleanName.length() > 100) {
            throw new IllegalArgumentException(
                    "Name must be 100 characters or fewer.");
        }
        if (cleanEmail.length() > 190) {
            throw new IllegalArgumentException(
                    "Email must be 190 characters or fewer.");
        }

        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User(
                cleanName,
                cleanEmail,
                passwordHash,
                UserRole.USER
        );

        User registeredUser = userRepository.save(user);
        demoGigSeederProvider.ifAvailable(seeder -> seeder.seedForOwner(registeredUser));
        return registeredUser;
    }
}

