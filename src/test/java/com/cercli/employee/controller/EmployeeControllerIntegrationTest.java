package com.cercli.employee.controller;

import com.cercli.employee.dto.ApiResponse;
import com.cercli.employee.dto.EmployeeDto;
import com.cercli.employee.dto.EmployeeResponseDto;
import com.cercli.employee.service.EmployeeServiceImpl;
import com.cercli.employee.util.EntityContextUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerIntegrationTest {

    private static final UUID EMPLOYEE_ID = UUID.fromString("4f4b8a6f-c2c2-4f0f-a787-3b8b264e0d50");

    @Mock
    private EmployeeServiceImpl employeeService;

    @Mock
    private EntityContextUtils entityContextUtils;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addEmployee_ShouldReturnApiResponse_WhenEmployeeAddedSuccessfully() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto("John", "Doe", "123456789", "Developer", "IT", "john.doe@example.com", 5000f, null);
        EmployeeResponseDto responseDto = new EmployeeResponseDto(UUID.randomUUID(), "John", "Doe", "123456789", "Developer", "IT", "john.doe@example.com", 5000f, null, null, null);
        ApiResponse<EmployeeResponseDto> apiResponse = new ApiResponse<>(200, "Employee added successfully", responseDto);
        when(messageSource.getMessage(anyString(), nullable(Object[].class), any(Locale.class)))
                .thenReturn("Employee added successfully");
        when(entityContextUtils.getCountryCode()).thenReturn("US");
        when(employeeService.addEmployee(any(EmployeeDto.class), anyString())).thenReturn(responseDto);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employee added successfully"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));
    }

    @Test
    void addEmployee_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(null, "Doe", "123456789", "Developer", "IT", "john.doe@example.com", 5000f, null); // First name is null

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEmployee_ShouldReturnApiResponse_WhenEmployeeExists() throws Exception {
        EmployeeResponseDto responseDto = new EmployeeResponseDto(UUID.randomUUID(), "John", "Doe", "123456789", "Developer", "IT", "john.doe@example.com", 5000f, null, null, null);
        ApiResponse<EmployeeResponseDto> apiResponse = new ApiResponse<>(200, "Employee retrieved successfully", responseDto);

        when(messageSource.getMessage(anyString(), nullable(Object[].class), any(Locale.class)))
                .thenReturn("Employee retrieved successfully");
        when(entityContextUtils.getCountryCode()).thenReturn("US");
        when(employeeService.getEmployee(eq(EMPLOYEE_ID), anyString())).thenReturn(Optional.of(responseDto));

        mockMvc.perform(get("/api/employees/{id}", EMPLOYEE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employee retrieved successfully"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));
    }
}
