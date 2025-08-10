package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.repository.SpaBookingRepository;
import org.example.petcareplus.service.SpaBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<SpaBooking> findById(Long id) {
        return spaBookingRepository.findById(id);
    }

    @Override
    public SpaBooking save(SpaBooking spaBooking) {
        return spaBookingRepository.save(spaBooking);
    }

    @Override
    public Page<SpaBooking> findByBookDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable) {
        return spaBookingRepository.findByBookDateBetween(startOfDay, endOfDay, pageable);
    }

    @Override
    public List<Object[]> getBookingCountByMonthInCurrentYear() {
        return spaBookingRepository.getBookingCountByMonthInCurrentYear();
    }

    @Override
    public Long getTotalSpaBookings() {
        return spaBookingRepository.count();
    }

    @Override
    public Long getTotalPendingSpaBooking() {
        return spaBookingRepository.countByStatus(BookingStatus.PENDING);
    }

    @Override
    public Long getTotalAcceptedSpaBookings() {
        return spaBookingRepository.countByStatus(BookingStatus.ACCEPTED);
    }
}
