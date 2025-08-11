package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final int DEFAULT_LEAVE_BALANCE = 20; // default per year for MVP

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Employee createEmployee(Employee emp) {
        // duplicate email check
        employeeRepository.findByEmail(emp.getEmail()).ifPresent(e -> {
            throw new BadRequestException("Employee with this email already exists");
        });

        if (emp.getLeaveBalance() == 0) {
            emp.setLeaveBalance(DEFAULT_LEAVE_BALANCE);
        }
        return employeeRepository.save(emp);
    }

    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public int getLeaveBalance(Long id) {
        Employee e = getEmployee(id);
        return e.getLeaveBalance();
    }
}
