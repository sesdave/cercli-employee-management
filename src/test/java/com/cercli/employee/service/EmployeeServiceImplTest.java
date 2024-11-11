package com.cercli.employee.service;

import com.cercli.employee.dto.EmployeeDto;
import com.cercli.employee.dto.EmployeeResponseDto;
import com.cercli.employee.exception.EmailAlreadyExistsException;
import com.cercli.employee.exception.EmployeeNotFoundException;
import com.cercli.employee.entity.Employee;
import com.cercli.employee.repository.EmployeeRepository;
import com.cercli.employee.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {
    private static final java.util.UUID UUID = java.util.UUID.fromString("4f4b8a6f-c2c2-4f0f-a787-3b8b264e0d50");

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DateUtil dateUtil;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void addEmployee_ShouldReturnEmployeeResponseDto_WhenEmployeeAddedSuccessfully() {
        EmployeeDto employeeDto = new EmployeeDto("John", "Doe", "123456789", "Developer", "IT", "john.doe@example.com", 5000f, null);
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .position("Developer")
                .department("IT")
                .email("john.doe@example.com")
                .salary(500f)
                .build();

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDto result = employeeService.addEmployee(employeeDto, "NG");

        assertEquals(employee.getFirstName(), result.getFirstName());
        assertEquals(employee.getLastName(), result.getLastName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void addEmployee_ShouldThrowRuntimeException_WhenDatabaseErrorOccurs() {
        EmployeeDto employeeDto = new EmployeeDto("John", "Doe", "123456789", "Developer", "IT", "john.doe@example.com", 5000f, null);

        when(messageSource.getMessage(anyString(), nullable(Object[].class), anyString(), any(Locale.class)))
                .thenReturn("Unable to save employee at this time, please try again later.");


        // Use a concrete subclass of DataAccessException
        when(employeeRepository.save(any(Employee.class))).thenThrow(new CannotGetJdbcConnectionException("Database not reachable"));

        // Assert that a RuntimeException is thrown with the expected message
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.addEmployee(employeeDto, "US"));

        assertEquals("Unable to save employee at this time, please try again later.", exception.getMessage());
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee_WhenEmployeeExists() {
        java.util.UUID employeeId = UUID.randomUUID();
        Employee existingEmployee = Employee.builder()
                .firstName("John")
                .lastName("Smith")
                .phoneNumber("987654321")
                .position("Developer")
                .department("IT")
                .email("john.doe@example.com")
                .salary(5000f)
                .build();
        EmployeeDto updateDto = new EmployeeDto("John", "Smith", "987654321", "Senior Developer", "IT", "john.smith@example.com", 5000f, null);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(existingEmployee);

        EmployeeResponseDto updatedEmployee = employeeService.updateEmployee(employeeId, updateDto, anyString());

        assertEquals("John", updatedEmployee.getFirstName());
        assertEquals("Smith", updatedEmployee.getLastName());
        assertEquals("987654321", updatedEmployee.getPhoneNumber());
    }

    @Test
    void updateEmployee_ShouldThrowEmployeeNotFoundException_WhenEmployeeNotFound() {
        UUID employeeId = UUID.randomUUID();
        EmployeeDto updateDto = new EmployeeDto("John", "Smith", "987654321", "Senior Developer", "IT", "john.smith@example.com", 6000f, null);

        when(messageSource.getMessage(anyString(), nullable(Object[].class), anyString(), any(Locale.class)))
                .thenReturn("Employee not found with ID: " + employeeId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(employeeId, updateDto, anyString()));

        assertEquals("Employee not found with ID: " + employeeId, exception.getMessage());
    }

    @Test
    void getEmployee_ShouldReturnEmployeeResponseDto_WhenEmployeeExists() {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .position("Developer")
                .department("IT")
                .email("john.doe@example.com")
                .salary(500f)
                .build();
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employee));
        when(dateUtil.toLocalTime(any(), any())).thenReturn(null); // Mocking dateUtil

        EmployeeResponseDto result = employeeService.getEmployee(UUID.randomUUID(), "NG").orElse(null);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void getEmployee_ShouldReturnEmpty_WhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Optional<EmployeeResponseDto> result = employeeService.getEmployee(UUID.randomUUID(), "NG");

        assertFalse(result.isPresent());
    }

    @Test
    void getEmployee_ShouldThrowRuntimeException_WhenDatabaseErrorOccurs() {
        when(messageSource.getMessage(anyString(), nullable(Object[].class), anyString(), any(Locale.class)))
                .thenReturn("Unable to fetch employee details at this time.");
        when(employeeRepository.findById(any(UUID.class))).thenThrow(new CannotGetJdbcConnectionException("Database not reachable"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.getEmployee(UUID.randomUUID(), "NG"));

        assertEquals("Unable to fetch employee details at this time.", exception.getMessage());
    }

    @Test
    void getAllEmployees_ShouldReturnEmployeeList_WhenEmployeesExist() {
        Employee employee1 = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .position("Developer")
                .department("IT")
                .email("john.doe@example.com")
                .salary(5000f)
                .build();
        Employee employee2 = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .phoneNumber("987654321")
                .position("Manager")
                .department("HR")
                .email("jane.doe@example.com")
                .salary(6000f)
                .build();
        Page<Employee> employeePage = mock(Page.class);
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);
        when(employeePage.getNumberOfElements()).thenReturn(2);
        when(employeePage.stream()).thenReturn(List.of(employee1, employee2).stream());
        when(dateUtil.toLocalTime(any(), any())).thenReturn(null);

        List<EmployeeResponseDto> employees = employeeService.getAllEmployees(0, 10, "NG");

        assertEquals(2, employees.size());
    }

    @Test
    void getAllEmployees_ShouldThrowRuntimeException_WhenDatabaseErrorOccurs() {
        when(messageSource.getMessage(anyString(), nullable(Object[].class), anyString(), any(Locale.class)))
                .thenReturn("Unable to fetch employees at this time.");
        when(employeeRepository.findAll(any(Pageable.class))).thenThrow(new DataAccessException("Database error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.getAllEmployees(0, 10, "NG"));

        assertEquals("Unable to fetch employees at this time.", exception.getMessage());
    }

    @Test
    void addEmployee_ShouldThrowIllegalArgumentException_WhenEmailIsNotUnique() {
        EmployeeDto employeeDto = new EmployeeDto("John", "Doe", "123456789", "Developer", "IT", "john.doe@example.com", 5000f, null);
        when(messageSource.getMessage(anyString(), nullable(Object[].class), anyString(), any(Locale.class)))
                .thenReturn("Employee with this email already exists");
        when(employeeRepository.findByEmail(employeeDto.getEmail())).thenReturn(Optional.of(new Employee()));

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> employeeService.addEmployee(employeeDto, "NG"));

        assertEquals("Employee with this email already exists", exception.getMessage());
    }
}
