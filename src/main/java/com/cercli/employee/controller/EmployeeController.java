package com.cercli.employee.controller;

import com.cercli.employee.dto.ApiResponse;
import com.cercli.employee.dto.EmployeeDto;
import com.cercli.employee.dto.EmployeeResponseDto;
import com.cercli.employee.contracts.EmployeeService;
import com.cercli.employee.util.EntityContextUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employee Management", description = "Operations related to employee management")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EntityContextUtils entityContextUtils;
    private final MessageSource messageSource;

    @Operation(summary = "Add a new employee", description = "Adds a new employee to the system.")
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> addEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeResponseDto employee = employeeService.addEmployee(employeeDto, entityContextUtils.getCountryCode());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), getMessage("employee.add.success"), employee));
    }

    @Operation(summary = "Update an existing employee", description = "Updates an existing employee's information.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateEmployee(@PathVariable UUID id, @RequestBody EmployeeDto employeeDto) {
        EmployeeResponseDto updatedEmployee = employeeService.updateEmployee(id, employeeDto, entityContextUtils.getCountryCode());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), getMessage("employee.update.success"), updatedEmployee));
    }

    @Operation(summary = "Get employee details", description = "Fetches details of an employee by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> getEmployee(@PathVariable UUID id) {
        System.out.println("Entered get Id");

        Optional<EmployeeResponseDto> employee = employeeService.getEmployee(id, entityContextUtils.getCountryCode());

        if (employee.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), getMessage("employee.fetch.success"), employee.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), getMessage("employee.not.found"), null));
        }
    }

    @Operation(summary = "Get all employees", description = "Fetches a list of all employees with pagination.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponseDto>>> getAllEmployees(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all employees with page: {} and size: {}", page, size);
        List<EmployeeResponseDto> employees = employeeService.getAllEmployees(page, size, entityContextUtils.getCountryCode());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), getMessage("employee.fetch.all.success"), employees));
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

}
