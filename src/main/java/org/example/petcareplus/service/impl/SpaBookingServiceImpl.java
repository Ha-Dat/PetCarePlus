package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.repository.SpaBookingRepository;
import org.example.petcareplus.service.SpaBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpaBookingServiceImpl implements SpaBookingService {

    private final SpaBookingRepository spaBookingRepository;

    @Autowired
    public SpaBookingServiceImpl(SpaBookingRepository spaBookingRepository) {
        this.spaBookingRepository = spaBookingRepository;
    }

    @Override
    public List<SpaBooking> findAll() {
        return spaBookingRepository.findAll();
    }

}
