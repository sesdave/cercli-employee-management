package com.cercli.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String position;
    private String department;
    private String email;
    private Float salary;
    private LocalDate hireDate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
