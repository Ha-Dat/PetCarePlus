package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.repository.SpaBookingRepository;
import org.example.petcareplus.service.SpaBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SpaBookingServiceImpl implements SpaBookingService {

    private final SpaBookingRepository spaBookingRepository;

    @Autowired
    public SpaBookingServiceImpl(SpaBookingRepository spaBookingRepository) {
        this.spaBookingRepository = spaBookingRepository;
    }

    @Override
    public Page<SpaBooking> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookDate").descending());
        Page<SpaBooking> spaBookings = spaBookingRepository.findAll(pageable);
        return spaBookings;
    }

}
