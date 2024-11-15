# Employee Management Service

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Configuration](#database-configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
    - [Add Employee](#add-employee)
    - [Update Employee](#update-employee)
    - [Get Employee by ID](#get-employee-by-id)
    - [Fetch All Employee](#delete-employee)
- [Validation and Error Handling](#validation-and-error-handling)
- [Model Structure](#model-structure)
- [Unit Testing](#unit-testing)
- [Swagger Documentation](#swagger-documentation)
- [License](#license)

---

## Introduction

The **Employee Management Service** is a RESTful API built with Spring Boot that manages employee records. This includes creating, updating and retrieving employee data, along with validations and error handling.

## Features

- **Create, Update, and Fetch Employees**
- **Validation**: Ensures data integrity using annotations like `@NotNull`, `@Min`, and custom validators.
- **Error Handling**: Centralized exception handling using `@ControllerAdvice`.
- **Logging**: Integrated with SLF4J for logging different levels of information.
- **Transaction Management**: Uses `@Transactional` for ensuring data consistency.
- **DTO and Entity Separation**: Uses Data Transfer Objects (DTO) for input validation and output formatting.

## Technologies Used

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Swagger**- 
- **Hibernate**
- **Lombok**
- **ModelMapper**
- **PostgreSQL Database**
- **Maven**
- **JUnit & Mockito (for testing)**

## Architecture

The project follows a layered architecture:
- **Controller**: Handles HTTP requests.
- **Service**: Contains business logic.
- **Repository**: Manages database interactions.
- **Model/Entity**: Represents database tables.
- **DTO**: Used for data transfer between layers.
- **Exception Handling**: Global exception management.

## Prerequisites

Before you begin, ensure you have the following installed:
- **Java 17**
- **Maven 3.x**
- **Git**
- **Postman** (optional, for API testing)

## Installation

1. **Clone the repository:**
    ```bash
    git clone https://github.com/sesdave/cercli-employee-management.git
    cd employee-management
    ```

2. **Build the project:**
    ```bash
    mvn clean install
    ```

## Database Configuration

The application uses an postgresql database for development. To switch to MySQL or PostgreSQL, update the `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/employee_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## Running the Application

To run the application, make sure you have [Java JDK 17+](https://adoptium.net/temurin/releases/) and [Maven](https://maven.apache.org/) installed. 

1. **Clone the repository**:
    ```bash
    git clone https://github.com/sesdave/cercli-employee-management.git
    cd employee-management
    ```

2. **Build the project**:
    ```bash
    mvn clean install
    ```

3. **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

The application will start at `http://localhost:8080`.

## API Endpoints

### Add Employee
- **Endpoint**: `POST /api/employees`
### Headers
| Header          | Value          | Description                   |
|-----------------|----------------|-------------------------------|
| Content-Type    | application/json | Specifies the media type of the resource |
| X-Entity        | NG             | **Required**. Custom entity identifier    |
- 
- **Request Body**:
    ```json
    {
      "firstName": "John",
      "lastName": "Doe",
      "phoneNumber": "1234567890",
      "position": "Software Engineer",
      "department": "IT",
      "email": "john.doe@example.com",
      "salary": 70000,
      "hireDate": "2024-01-01"
    }
    ```
- **Response**: 
    - `201 Created`
    ```json
    {
      "employeeId": "uuid",
      "firstName": "John",
      "lastName": "Doe",
      "phoneNumber": "1234567890",
      "position": "Software Engineer",
      "department": "IT",
      "email": "john.doe@example.com",
      "salary": 70000,
      "hireDate": "2024-01-01",
      "createdAt": "2024-11-11T06:33:30.691382",
      "modifiedAt": "2024-11-11T06:33:30.691382"
      
    }
    ```

### Update Employee
- **Endpoint**: `PUT /api/employees/{id}`
### Headers
| Header          | Value          | Description                   |
|-----------------|----------------|-------------------------------|
| Content-Type    | application/json | Specifies the media type of the resource |
| X-Entity        | NG             | **Required**. Custom entity identifier    |
- 
- **Request Body**:
    ```json
    {
      "firstName": "Jane",
      "position": "Senior Engineer",
      "salary": 80000
    }
    ```
- **Response**: 
    - `200 OK`
    ```json
    {
      "employeeId": "uuid",
      "firstName": "Jane",
      "position": "Senior Engineer"
    }
    ```

### Get Employee by ID
- **Endpoint**: `GET /api/employees/{id}`
### Headers
| Header          | Value          | Description                   |
|-----------------|----------------|-------------------------------|
| Content-Type    | application/json | Specifies the media type of the resource |
| X-Entity        | NG             | **Required**. Custom entity identifier    |
- 
- **Response**:
    - `200 OK`
    ```json
    {
      "employeeId": "uuid",
      "firstName": "John"
    }
    ```
    - `404 Not Found`
    ```json
    {
      "error": "Employee not found"
    }
    ```

### Get All Employees
- **Endpoint**: `GET /api/employees`
### Query Parameters
| Parameter | Type    | Description                               | Required | Example |
|-----------|---------|-------------------------------------------|----------|---------|
| `page`    | integer | The page number for paginated results     | No       | `1`     |
| `size`    | integer | The number of items per page              | No       | `10`    |
- 
### Headers
| Header          | Value          | Description                   |
|-----------------|----------------|-------------------------------|
| Content-Type    | application/json | Specifies the media type of the resource |
| X-Entity        | NG             | **Required**. Custom entity identifier    |
-
- **Response**:
    - `200 OK`
    ```json
    [{
      "employeeId": "uuid",
      "firstName": "John"
    },{
      "employeeId": "uuid",
      "firstName": "John"
    }]



## Validation and Error Handling

All input data is validated using `jakarta.validation` annotations:
- `@NotNull` for mandatory fields.
- `@Min` for numeric validations.
- Custom annotations like `@ValidEmail` for email validation.

### Example Error Response
- **Status**: `400 Bad Request`
    ```json
    {
      "timestamp": "2024-11-11T12:00:00",
      "error": "Bad Request",
      "message": "first name is required"
    }
    ```

## Model Structure

### Employee Model
```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID employeeId;
    
    @NotNull
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String position;
    private String department;
    private String email;
    private Float salary;
    private LocalDate hireDate;
}
```
## Unit Testing

To run tests, use the following command:
```bash
mvn test
```

## Swagger Documentation

This project uses [Swagger](https://swagger.io/) for API documentation. Swagger provides an interactive API explorer and helps you test the endpoints.

### How to Access Swagger UI
Once the application is running, you can access the Swagger UI at:
http://localhost:8081/swagger-ui/index.html

# MIT License

Copyright (c) 2024 David

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

**THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.**



