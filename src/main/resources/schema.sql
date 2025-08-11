CREATE TABLE employees (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  department VARCHAR(255),
  joining_date DATE,
  leave_balance INTEGER
);

CREATE TABLE leave_requests (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  start_date DATE,
  end_date DATE,
  number_of_days INTEGER,
  status VARCHAR(50),
  reason VARCHAR(500),
  CONSTRAINT fk_emp FOREIGN KEY (employee_id) REFERENCES employees(id)
);
