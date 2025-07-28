package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.repository.HotelBookingRepository;
import org.example.petcareplus.repository.SpaBookingRepository;
import org.example.petcareplus.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final AppointmentRepository appointmentRepository;
    private final SpaBookingRepository spaBookingRepository;
    private final HotelBookingRepository hotelBookingRepository;

    public BookingServiceImpl(AppointmentRepository appointmentRepository, SpaBookingRepository spaBookingRepository, HotelBookingRepository hotelBookingRepository) {
        this.appointmentRepository = appointmentRepository;
        this.spaBookingRepository = spaBookingRepository;
        this.hotelBookingRepository = hotelBookingRepository;
    }

    @Override
    public List<MyServiceDTO> getMyServices(Long accountId) {

        List<MyServiceDTO> list = new ArrayList<>();

        list.addAll(appointmentRepository.findAppointmentBookingsByAccountId(accountId));
        list.addAll(spaBookingRepository.findSpaBookingsByAccountId(accountId));
        list.addAll(hotelBookingRepository.findHotelBookingsByAccountId(accountId));

        return list;
    }
}
