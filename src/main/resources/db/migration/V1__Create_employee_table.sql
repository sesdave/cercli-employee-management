-- Enable the uuid-ossp extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the 'employee' table
CREATE TABLE IF NOT EXISTS employees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    hire_date DATE NOT NULL,
    job_title VARCHAR(100),
    department VARCHAR(100),
    salary DECIMAL(15, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ensure 'id' column has values (if migrating an existing table)
UPDATE employees SET id = gen_random_uuid() WHERE id IS NULL;
ALTER TABLE employees ALTER COLUMN id SET NOT NULL;

-- Add index on frequently queried columns
CREATE INDEX idx_employees_email ON employees (email);
