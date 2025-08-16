package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.WorkScheduleRequest;
import org.example.petcareplus.repository.WorkScheduleRequestRepository;
import org.example.petcareplus.service.WorkScheduleRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkScheduleRequestServiceImpl implements WorkScheduleRequestService {

    @Autowired
    private WorkScheduleRequestRepository workScheduleRequestRepository;

    @Override
    public List<WorkScheduleRequest> findAll() {
        return workScheduleRequestRepository.findAll();
    }

    @Override
    public Page<WorkScheduleRequest> findAll(Pageable pageable) {
        return workScheduleRequestRepository.findAll(pageable);
    }

    @Override
    public WorkScheduleRequest save(WorkScheduleRequest workScheduleRequest) {
        return workScheduleRequestRepository.save(workScheduleRequest);
    }

    @Override
    public Optional<WorkScheduleRequest> findById(Long id) {
        return workScheduleRequestRepository.findById(id);
    }
}
