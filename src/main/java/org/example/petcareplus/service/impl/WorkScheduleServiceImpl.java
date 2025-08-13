package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.WorkSchedule;
import org.example.petcareplus.repository.WorkScheduleRepository;
import org.example.petcareplus.service.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

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
}
