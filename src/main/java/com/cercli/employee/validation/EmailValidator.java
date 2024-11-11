package com.cercli.employee.validation;

import com.cercli.employee.annotation.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

// Custom email validator implementation
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        System.out.println("validating email");
        if (email == null || email.isEmpty()) {
            return true;  // Let @NotNull or other validators handle this
        }
        return EMAIL_PATTERN.matcher(email).matches(); // Reuse precompiled pattern
    }
}
