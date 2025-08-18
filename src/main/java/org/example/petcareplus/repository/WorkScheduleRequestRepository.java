package org.example.petcareplus.repository;

import org.example.petcareplus.entity.WorkSchedule;
import org.example.petcareplus.entity.WorkScheduleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkScheduleRequestRepository extends JpaRepository<WorkScheduleRequest, Long> {
    Optional<WorkScheduleRequest> findById(long id);
    
    // Find all WorkScheduleRequests by schedule_id
    List<WorkScheduleRequest> findByOriginalSchedule_ScheduleId(Long scheduleId);
}
