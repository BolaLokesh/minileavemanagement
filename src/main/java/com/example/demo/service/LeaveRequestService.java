package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.repository.LeaveRequestRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRepo;
    private final EmployeeRepository employeeRepo;

    public LeaveRequestService(LeaveRequestRepository leaveRepo, EmployeeRepository employeeRepo) {
        this.leaveRepo = leaveRepo;
        this.employeeRepo = employeeRepo;
    }

    public LeaveRequest applyLeave(Long employeeId, LocalDate startDate, LocalDate endDate, String reason) {
        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date cannot be before start date");
        }
        if (emp.getJoiningDate() != null && startDate.isBefore(emp.getJoiningDate())) {
            throw new BadRequestException("Cannot apply leave before joining date: " + emp.getJoiningDate());
        }

        int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days <= 0) {
            throw new BadRequestException("Invalid number of days");
        }
        if (days > emp.getLeaveBalance()) {
            throw new BadRequestException("Insufficient leave balance. Requested: " + days + ", Available: " + emp.getLeaveBalance());
        }

        // check overlapping leaves (PENDING or APPROVED)
        List<LeaveRequest.Status> statuses = Arrays.asList(LeaveRequest.Status.PENDING, LeaveRequest.Status.APPROVED);
        List<LeaveRequest> overlapping = leaveRepo.findByEmployeeAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                emp, statuses, endDate, startDate);

        if (!overlapping.isEmpty()) {
            throw new BadRequestException("Overlapping leave exists for the requested period");
        }

        LeaveRequest lr = new LeaveRequest();
        lr.setEmployee(emp);
        lr.setStartDate(startDate);
        lr.setEndDate(endDate);
        lr.setNumberOfDays(days);
        lr.setReason(reason);
        lr.setStatus(LeaveRequest.Status.PENDING);

        return leaveRepo.save(lr);
    }

    @Transactional
    public LeaveRequest approveLeave(Long leaveId) {
        LeaveRequest lr = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        if (lr.getStatus() != LeaveRequest.Status.PENDING) {
            throw new BadRequestException("Only pending leaves can be approved");
        }

        Employee emp = lr.getEmployee();

        if (lr.getNumberOfDays() > emp.getLeaveBalance()) {
            throw new BadRequestException("Insufficient balance at approval time");
        }

        // deduct balance
        emp.setLeaveBalance(emp.getLeaveBalance() - lr.getNumberOfDays());
        employeeRepo.save(emp);

        lr.setStatus(LeaveRequest.Status.APPROVED);
        return leaveRepo.save(lr);
    }

    public LeaveRequest rejectLeave(Long leaveId) {
        LeaveRequest lr = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));
        if (lr.getStatus() != LeaveRequest.Status.PENDING) {
            throw new BadRequestException("Only pending leaves can be rejected");
        }
        lr.setStatus(LeaveRequest.Status.REJECTED);
        return leaveRepo.save(lr);
    }

    public List<LeaveRequest> listByEmployee(Long employeeId) {
        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        return leaveRepo.findByEmployee(emp);
    }
}
