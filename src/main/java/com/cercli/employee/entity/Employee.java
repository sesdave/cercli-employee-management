package com.cercli.employee.entity;

import com.cercli.employee.listeners.AuditEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditEntityListener.class)
public class Employee extends AuditableEntity {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String position;
    private String department;
    private String email;
    private LocalDate hireDate;
    private Float salary;

    @Version
    private Long version;

    @Override
    public String captureHistory() {
        System.out.println("Entered to capture history");
        return String.format("Employee [name=%s, position=%s, department=%s, email=%s, salary=%.2f]",
                String.join(" ",firstName, lastName), position, department, email, salary);
    }
}
