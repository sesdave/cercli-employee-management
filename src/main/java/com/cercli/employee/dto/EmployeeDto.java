package com.cercli.employee.dto;

import com.cercli.employee.annotation.ValidEmail;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    @NotNull(message = "first name is required")
    private String firstName;

    @NotNull(message = "last name is required")
    private String lastName;

    @NotNull(message = "phone number is required")
    private String phoneNumber;

    @NotNull(message = "Position is required")
    private String position;

    private String department;

    @NotNull(message = "Email is required")
    @ValidEmail
    private String email;

    @NotNull(message = "Salary is required")
    @Min(value = 0, message = "Salary must be a positive value")
    private Float salary;

    private LocalDate hireDate;
}
