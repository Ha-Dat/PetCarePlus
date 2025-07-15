package org.example.petcareplus.service;

import org.example.petcareplus.entity.SpaBooking;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface SpaBookingService {

    Page<SpaBooking> findAll(int page, int size, String sortBy);

    Optional<SpaBooking> findById(Long id);

    SpaBooking save(SpaBooking spaBooking);

}
