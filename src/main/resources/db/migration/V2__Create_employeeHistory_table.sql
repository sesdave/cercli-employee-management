-- Create the employee_history table
CREATE TABLE employee_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id UUID NOT NULL,
    change_type VARCHAR(255) NOT NULL,
    changes TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Adding indexes for optimization
CREATE INDEX idx_employee_history_employee_id ON employee_history (employee_id);