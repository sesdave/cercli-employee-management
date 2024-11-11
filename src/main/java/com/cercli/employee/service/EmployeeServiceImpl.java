package com.cercli.employee.service;

import com.cercli.employee.contracts.EmployeeService;
import com.cercli.employee.dto.EmployeeDto;
import com.cercli.employee.dto.EmployeeResponseDto;
import com.cercli.employee.entity.EmployeeHistory;
import com.cercli.employee.exception.EmailAlreadyExistsException;
import com.cercli.employee.exception.EmployeeNotFoundException;
import com.cercli.employee.entity.Employee;
import com.cercli.employee.repository.EmployeeHistoryRepository;
import com.cercli.employee.repository.EmployeeRepository;
import com.cercli.employee.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing employee-related operations such as adding,
 * updating, and retrieving employee details.
 * <p>
 * This service interacts with the database to persist employee information and provides
 * an interface for adding, updating, and retrieving employee data. It also validates
 * email uniqueness before adding new employees.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DateUtil dateUtil;
    private final MessageSource messageSource;
    private final EmployeeHistoryRepository employeeHistoryRepository;

    /**
     * Adds a new employee to the system.
     *
     * @param employeeDto The data transfer object containing employee information.
     * @param countryCode The country code for converting timestamps to the local time zone.
     * @return The response DTO containing the employee's details after being saved.
     * @throws IllegalArgumentException if the email is already associated with an existing employee.
     * @throws RuntimeException if there is a database error or unexpected failure during the operation.
     */
    @Override
    @Transactional
    public EmployeeResponseDto addEmployee(EmployeeDto employeeDto, String countryCode) {
        // Check if the email already exists
        validateEmailUniqueness(employeeDto.getEmail());
        try {
            Employee employee = convertToEntity(employeeDto);
            Employee savedEmployee = employeeRepository.save(employee);

            log.info("Employee added successfully with ID: {}", savedEmployee.getId());
            return convertToDto(savedEmployee, countryCode);
        } catch (DataAccessException e) {
            log.error("Failed to add employee due to database error: {}", e.getMessage(), e);
            throw new RuntimeException(messageSource.getMessage("employee.add.error", null, "Failed to add employee due to database error", LocaleContextHolder.getLocale()));
        } catch (Exception e) {
            log.error("Unexpected error while adding employee: {}", e.getMessage(), e);
            throw new RuntimeException(messageSource.getMessage("employee.runtime_error", null, "An unexpected error occurred.", LocaleContextHolder.getLocale()));

        }
    }

    /**
     * Updates an existing employee's details.
     *
     * @param id The unique identifier of the employee to be updated.
     * @param employeeDto The data transfer object containing updated employee information.
     * @param countryCode The country code for converting timestamps to the local time zone.
     * @return The updated employee's response DTO.
     * @throws EmployeeNotFoundException if no employee is found with the given ID.
     * @throws RuntimeException if there is a database error or unexpected failure during the operation.
     */

    @Override
    @Transactional
    public EmployeeResponseDto updateEmployee(UUID id, EmployeeDto employeeDto, String countryCode) {
        try {
            Employee existingEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException(messageSource.getMessage("employee.not.found_with_id", new Object[]{id}, "Employee not found with ID: "+ id, LocaleContextHolder.getLocale())));

            updateEmployeeFields(existingEmployee, employeeDto);

            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            log.info("Employee updated successfully with ID: {}", updatedEmployee.getId());
            return convertToDto(updatedEmployee, countryCode);
        } catch (EmployeeNotFoundException e) {
            log.warn("Attempted to update non-existent employee with ID: {}", id);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while updating employee with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException(messageSource.getMessage("employee.update.error", null, "Unable to update employee at this time, please try again later.", LocaleContextHolder.getLocale()));
        } catch (Exception e) {
            log.error("Unexpected error while updating employee with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException(messageSource.getMessage("employee.runtime_error", null, "An unexpected error occurred.", LocaleContextHolder.getLocale() ));

        }
    }

    @Override
    public Optional<EmployeeResponseDto> getEmployee(UUID id, String countryCode) {
        try {
            return employeeRepository.findById(id)
                    .map(employee -> {
                        log.info("Employee retrieved successfully with ID: {}", id);
                        return convertToDto(employee, countryCode);
                    });
        } catch (DataAccessException e) {
            log.error("Failed to retrieve employee with ID {} due to database error: {}", id, e.getMessage(), e);
            throw new RuntimeException(messageSource.getMessage("employee.database_error", null, "Unable to fetch employees at this time.", LocaleContextHolder.getLocale()));
        } catch (Exception e) {
            log.error("Unexpected error while retrieving employee with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException(messageSource.getMessage("employee.runtime_error", null, "An unexpected error occurred.", LocaleContextHolder.getLocale() ));

        }
    }

    @Override
    public List<EmployeeResponseDto> getAllEmployees(int page, int size, String countryCode) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Employee> employeePage = employeeRepository.findAll(pageable);
            log.info("Retrieved {} employees on page {}", employeePage.getNumberOfElements(), page);

            return employeePage.stream()
                    .map(emp -> convertToDto(emp, countryCode))
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Failed to retrieve employees due to database error: {}", e.getMessage(), e);
            throw new RuntimeException(messageSource.getMessage("employee.database_error", null, "Unable to fetch employees at this time.", LocaleContextHolder.getLocale()));
        } catch (Exception e) {
            log.error("Unexpected error while retrieving employees: {}", e.getMessage(), e);
            throw new RuntimeException(messageSource.getMessage("employee.runtime_error", null, "An unexpected error occurred.", LocaleContextHolder.getLocale() ));
        }
    }

    /**
     * Validates whether the email provided already exists in the system.
     *
     * @param email The email address to be checked for uniqueness.
     * @throws IllegalArgumentException if an employee with the given email already exists.
     */
    private void validateEmailUniqueness(String email) {
        employeeRepository.findByEmail(email)
                .ifPresent(emp -> {
                    log.warn("Employee with email {} already exists", email);
                    System.out.println("Default Local "+ LocaleContextHolder.getLocale());
                    throw new EmailAlreadyExistsException(messageSource.getMessage("employee.email.exists", null, "Employee with this email already exists", LocaleContextHolder.getLocale() ));
                });
    }


    /**
     * Converts an EmployeeDto to an Employee entity.
     *
     * @param employeeDto The employee data transfer object to be converted.
     * @return The corresponding Employee entity.
     */

    private Employee convertToEntity(EmployeeDto employeeDto) {
        return Employee.builder()
                .firstName(employeeDto.getFirstName())
                .lastName(employeeDto.getLastName())
                .phoneNumber(employeeDto.getPhoneNumber())
                .position(employeeDto.getPosition())
                .department(employeeDto.getDepartment())
                .email(employeeDto.getEmail())
                .salary(employeeDto.getSalary())
                .hireDate(employeeDto.getHireDate())
                .build();
    }

    /**
     * Converts an Employee entity to an EmployeeResponseDto with local time conversion.
     *
     * @param employee The employee entity to be converted.
     * @param countryCode The country code for converting timestamps to the local time zone.
     * @return The corresponding EmployeeResponseDto with converted time fields.
     */
    private EmployeeResponseDto convertToDto(Employee employee, String countryCode) {
        return new EmployeeResponseDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPhoneNumber(),
                employee.getPosition(),
                employee.getDepartment(),
                employee.getEmail(),
                employee.getSalary(),
                employee.getHireDate(),
                dateUtil.toLocalTime(employee.getCreatedAt(), countryCode),
                dateUtil.toLocalTime(employee.getModifiedAt(), countryCode)
        );
    }

    private void updateEmployeeFields(Employee existingEmployee, EmployeeDto employeeDto) {
        if (employeeDto.getFirstName() != null) {
            existingEmployee.setFirstName(employeeDto.getFirstName());
        }
        if (employeeDto.getLastName() != null) {
            existingEmployee.setLastName(employeeDto.getLastName());
        }
        if (employeeDto.getPhoneNumber() != null) {
            existingEmployee.setPhoneNumber(employeeDto.getPhoneNumber());
        }
        if (employeeDto.getPosition() != null) {
            existingEmployee.setPosition(employeeDto.getPosition());
        }
        if (employeeDto.getSalary() != null) {
            existingEmployee.setSalary(employeeDto.getSalary());
        }
        if (employeeDto.getDepartment() != null) {
            existingEmployee.setDepartment(employeeDto.getDepartment());
        }
        if (employeeDto.getHireDate() != null) {
            existingEmployee.setHireDate(employeeDto.getHireDate());
        }
    }


}
