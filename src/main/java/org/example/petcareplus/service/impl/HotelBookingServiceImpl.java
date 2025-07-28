package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.repository.HotelBookingRepository;
import org.example.petcareplus.repository.PetProfileRepository;
import org.example.petcareplus.repository.ServiceRepository;
import org.example.petcareplus.service.HotelBookingService;
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
public class HotelBookingServiceImpl implements HotelBookingService {
    private final HotelBookingRepository hotelBookingRepository;
    private final ServiceRepository serviceRepository;
    private final PetProfileRepository petProfileRepository;

    @Autowired
    public HotelBookingServiceImpl(HotelBookingRepository hotelBookingRepository,ServiceRepository serviceRepository, PetProfileRepository petProfileRepository) {
        this.hotelBookingRepository = hotelBookingRepository;
        this.serviceRepository = serviceRepository;
        this.petProfileRepository = petProfileRepository;
    }

    @Override
    public Page<HotelBooking> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookDate").descending());
        Page<HotelBooking> hotelBookings = hotelBookingRepository.findAll(pageable);
        return hotelBookings;
    }

    @Override
    public Optional<HotelBooking> findById(Long id) {
        return hotelBookingRepository.findById(id);
    }

    @Override
    public void save(HotelBooking hotelBooking) {
        hotelBookingRepository.save(hotelBooking);
    }

    @Override
    public List<org.example.petcareplus.entity.Service> Service_findAll() {
        return serviceRepository.findAll();
    }

    @Override
    public Optional<org.example.petcareplus.entity.Service> Service_findById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    public Page<HotelBooking> findByBookDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable) {
        return hotelBookingRepository.findByBookDateBetween(startOfDay, endOfDay, pageable);
    }

}
