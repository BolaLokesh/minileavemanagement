package com.example.demo.controller;

import com.example.demo.dto.ApplyLeaveRequestDTO;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.service.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveService;

    public LeaveRequestController(LeaveRequestService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LeaveRequest>> applyLeave(@Valid @RequestBody ApplyLeaveRequestDTO req) {
        LeaveRequest lr = leaveService.applyLeave(req.getEmployeeId(), req.getStartDate(), req.getEndDate(), req.getReason());
        ApiResponse<LeaveRequest> resp = ApiResponse.ofSuccess("Leave applied", lr);
        return ResponseEntity.created(URI.create("/api/leaves/" + lr.getId())).body(resp);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<LeaveRequest>> approve(@PathVariable Long id) {
        LeaveRequest lr = leaveService.approveLeave(id);
        return ResponseEntity.ok(ApiResponse.ofSuccess("Leave approved", lr));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<LeaveRequest>> reject(@PathVariable Long id) {
        LeaveRequest lr = leaveService.rejectLeave(id);
        return ResponseEntity.ok(ApiResponse.ofSuccess("Leave rejected", lr));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> listByEmployee(@PathVariable Long employeeId) {
        List<LeaveRequest> list = leaveService.listByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.ofSuccess(null, list));
    }
}
