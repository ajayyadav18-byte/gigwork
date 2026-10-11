
package com.gigwork.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Please enter your name.")
    @Size(max = 100, message = "Name must be 100 characters or fewer.")
    private String name;

    @NotBlank(message = "Please enter your email address.")
    @Email(message = "Please enter a valid email address.")
    @Size(max = 190, message = "Email must be 190 characters or fewer.")
    private String email;

    @NotBlank(message = "Please create a password.")
    @Size(min = 8, max = 72,
            message = "Password must contain between 8 and 72 characters.")
    private String password;

    @NotBlank(message = "Please confirm your password.")
    private String confirmPassword;

    public RegistrationForm() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}


