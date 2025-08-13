package org.example.petcareplus.service;

import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.entity.Service;
import org.example.petcareplus.entity.SpaBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpaBookingService {

    Page<SpaBooking> findAll(int page, int size, String sortBy);

    Optional<SpaBooking> findById(Long id);

    SpaBooking save(SpaBooking spaBooking);

    Page<SpaBooking> findByBookDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

    List<Object[]> getBookingCountByMonthInCurrentYear();

    Long getTotalSpaBookings();

    Long getTotalPendingSpaBooking();

    Long getTotalAcceptedSpaBookings();

    Optional<Service> Service_findById(Long id);
}
