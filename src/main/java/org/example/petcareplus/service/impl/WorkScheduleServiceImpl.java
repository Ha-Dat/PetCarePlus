package org.example.petcareplus.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.petcareplus.entity.WorkSchedule;
import org.example.petcareplus.entity.WorkScheduleRequest;
import org.example.petcareplus.repository.WorkScheduleRepository;
import org.example.petcareplus.repository.WorkScheduleRequestRepository;
import org.example.petcareplus.service.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {

    @Autowired
    private WorkScheduleRepository workScheduleRepository;
    
    @Autowired
    private WorkScheduleRequestRepository workScheduleRequestRepository;

    @Override
    public List<WorkSchedule> findAll() {
        return workScheduleRepository.findAll();
    }

    @Override
    public Page<WorkSchedule> findAll(Pageable pageable) {
        return workScheduleRepository.findAll(pageable);
    }

    @Override
    public WorkSchedule save(WorkSchedule workSchedule) {
        return workScheduleRepository.save(workSchedule);
    }

    @Override
    public Optional<WorkSchedule> findById(Long id) {
        return workScheduleRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // First, find and delete all related WorkScheduleRequests
        Optional<WorkSchedule> workSchedule = workScheduleRepository.findById(id);
        if (workSchedule.isPresent()) {
            // Find all WorkScheduleRequests that reference this WorkSchedule
            List<WorkScheduleRequest> relatedRequests = workScheduleRequestRepository.findByOriginalSchedule_ScheduleId(id);
            
            // Delete all related requests first
            for (WorkScheduleRequest request : relatedRequests) {
                workScheduleRequestRepository.deleteById(request.getRequestId());
            }
            
            // Now delete the WorkSchedule
            workScheduleRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("WorkSchedule not found with id: " + id);
        }
    }
}
