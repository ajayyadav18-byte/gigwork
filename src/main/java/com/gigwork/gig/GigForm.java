
package com.gigwork.gig;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class GigForm {

    @NotBlank(message = "Please enter a gig title.")
    @Size(max = 120, message = "The title must be 120 characters or fewer.")
    private String title;

    @NotBlank(message = "Please describe the project.")
    @Size(max = 5000, message = "The description must be 5000 characters or fewer.")
    private String description;

    @NotBlank(message = "Please choose a category.")
    @Pattern(
            regexp = "^\\s*(Design & Creative|Technology|Writing|Marketing|Education|Other)\\s*$",
            message = "Please choose a valid category."
    )
    @Size(max = 60, message = "The category is too long.")
    private String category;

    @Size(max = 120, message = "The location must be 120 characters or fewer.")
    private String location = "Remote";

    @NotNull(message = "Please enter a budget.")
    @DecimalMin(
            value = "1.00",
            message = "The minimum budget is ₹1."
    )
    @DecimalMax(
            value = "1000000.00",
            message = "The maximum budget per gig is ₹10,00,000."
    )
    @Digits(
            integer = 7,
            fraction = 2,
            message = "Enter a valid budget with up to two decimal places."
    )
    private BigDecimal budget;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
}

