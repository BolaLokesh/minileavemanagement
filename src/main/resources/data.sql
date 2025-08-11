INSERT INTO employees (id, name, email, department, joining_date, leave_balance) VALUES
(1, 'Alice', 'alice@example.com', 'Engineering', '2023-06-01', 20),
(2, 'Bob', 'bob@example.com', 'Design', '2024-01-15', 20);

-- Leave requests example (start_date and end_date format accepted by H2)
INSERT INTO leave_requests (id, employee_id, start_date, end_date, number_of_days, status, reason) VALUES
(1, 1, '2025-08-20', '2025-08-22', 3, 'APPROVED', 'Personal'),
(2, 1, '2025-09-10', '2025-09-11', 2, 'PENDING', 'Sick');
