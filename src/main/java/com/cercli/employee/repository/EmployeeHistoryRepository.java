package com.cercli.employee.repository;

import com.cercli.employee.entity.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, UUID> {

    // Custom query to find history by employeeId
    List<EmployeeHistory> findByEmployeeId(UUID employeeId);

}
