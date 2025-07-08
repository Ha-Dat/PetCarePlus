package org.example.petcareplus.service;

import org.example.petcareplus.entity.SpaBooking;

import java.util.List;
import java.util.Optional;

public interface SpaBookingService {

    List<SpaBooking> findAll();

    Optional<SpaBooking> findById(Long id);

    SpaBooking save(SpaBooking spaBooking);

}
