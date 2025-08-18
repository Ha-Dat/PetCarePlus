package org.example.petcareplus.service;

import org.example.petcareplus.entity.WorkSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface WorkScheduleService {
    // Lấy toàn bộ không phân trang
    List<WorkSchedule> findAll();

    // Lấy toàn bộ có phân trang
    Page<WorkSchedule> findAll(Pageable pageable);

    WorkSchedule save(WorkSchedule workSchedule);

    Optional<WorkSchedule> findById(Long id);
    
    void deleteById(Long id);
}
