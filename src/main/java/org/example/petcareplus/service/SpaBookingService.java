package org.example.petcareplus.service;

import org.example.petcareplus.entity.SpaBooking;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SpaBookingService {

    Page<SpaBooking> findAll(int page, int size, String sortBy);

}
