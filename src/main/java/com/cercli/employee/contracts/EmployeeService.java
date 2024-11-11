package com.cercli.employee.contracts;

import com.cercli.employee.dto.EmployeeDto;
import com.cercli.employee.dto.EmployeeResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeService {
    EmployeeResponseDto addEmployee(EmployeeDto employeeDto, String countryCode);
    EmployeeResponseDto updateEmployee(UUID employeeId, EmployeeDto employeeDto, String countryCode);
    Optional<EmployeeResponseDto> getEmployee(UUID employeeId, String countryCode);
    List<EmployeeResponseDto> getAllEmployees(int page, int size, String countryCode);
}
