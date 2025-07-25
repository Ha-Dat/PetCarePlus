package org.example.petcareplus.service;

import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.entity.Service;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface HotelBookingService {

    Page<HotelBooking> findAll(int page, int size, String sortBy);

    Optional<HotelBooking> findById(Long id);

    void save(HotelBooking hotelBooking);

    List<Service> Service_findAll();

    Optional<Service> Service_findById(Long id);
}
