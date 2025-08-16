package org.example.petcareplus.service;

import org.example.petcareplus.entity.WorkScheduleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface WorkScheduleRequestService {
    // Lấy toàn bộ không phân trang
    List<WorkScheduleRequest> findAll();

    // Lấy toàn bộ có phân trang
    Page<WorkScheduleRequest> findAll(Pageable pageable);

    WorkScheduleRequest save(WorkScheduleRequest workScheduleRequest);

    Optional<WorkScheduleRequest> findById(Long id);
}
