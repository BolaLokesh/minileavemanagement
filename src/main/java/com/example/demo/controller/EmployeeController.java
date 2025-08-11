package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.dto.EmployeeDTO;
import com.example.demo.dto.ApiResponse;
import com.example.demo.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService empService;

    public EmployeeController(EmployeeService empService) {
        this.empService = empService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> addEmployee(@Valid @RequestBody EmployeeDTO dto) {
        Employee emp = new Employee();
        emp.setName(dto.getName());
        emp.setEmail(dto.getEmail());
        emp.setDepartment(dto.getDepartment());
        emp.setJoiningDate(dto.getJoiningDate());
        emp.setLeaveBalance(dto.getLeaveBalance() == null ? 0 : dto.getLeaveBalance());

        Employee saved = empService.createEmployee(emp);
        ApiResponse<Employee> resp = ApiResponse.ofSuccess("Employee created", saved);
        return ResponseEntity.created(URI.create("/api/employees/" + saved.getId())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getEmployee(@PathVariable Long id) {
        Employee e = empService.getEmployee(id);
        return ResponseEntity.ok(ApiResponse.ofSuccess(null, e));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> listAll() {
        List<Employee> list = empService.getAll();
        return ResponseEntity.ok(ApiResponse.ofSuccess(null, list));
    }

    @GetMapping("/{id}/leave-balance")
    public ResponseEntity<ApiResponse<Integer>> getBalance(@PathVariable Long id) {
        int bal = empService.getLeaveBalance(id);
        return ResponseEntity.ok(ApiResponse.ofSuccess(null, bal));
    }
}
