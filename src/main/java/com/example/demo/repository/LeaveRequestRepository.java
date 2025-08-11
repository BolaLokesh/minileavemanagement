package com.example.demo.repository;


import com.example.demo.entity.LeaveRequest;
import com.example.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(Employee employee);

    // find overlapping leaves for given employee with statuses in given list
    List<LeaveRequest> findByEmployeeAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Employee employee, List<LeaveRequest.Status> statuses, LocalDate endDate, LocalDate startDate);

    List<LeaveRequest> findByEmployeeAndStatus(Employee employee, LeaveRequest.Status status);
}
