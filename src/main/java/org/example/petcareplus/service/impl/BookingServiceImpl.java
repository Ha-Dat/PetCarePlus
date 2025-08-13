package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.repository.HotelBookingRepository;
import org.example.petcareplus.repository.SpaBookingRepository;
import org.example.petcareplus.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public void cancelBookingByTypeAndId(String type, Long bookingId) {
        ServiceCategory category = Arrays.stream(ServiceCategory.values())
                .filter(c -> c.getValue().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid service category: " + type));

        switch (category) {
            case APPOINTMENT -> {
                appointmentRepository.findById(bookingId).ifPresent(booking -> {
                    if (booking.getStatus().equals(BookingStatus.PENDING)) {
                        booking.setStatus(BookingStatus.CANCELLED);
                        appointmentRepository.save(booking);
                    }
                });
            }
            case SPA -> {
                spaBookingRepository.findById(bookingId).ifPresent(booking -> {
                    if (booking.getStatus().equals(BookingStatus.PENDING)) {
                        booking.setStatus(BookingStatus.CANCELLED);
                        spaBookingRepository.save(booking);
                    }
                });
            }
            case HOTEL -> {
                hotelBookingRepository.findById(bookingId).ifPresent(booking -> {
                    if (booking.getStatus().equals(BookingStatus.PENDING)) {
                        booking.setStatus(BookingStatus.CANCELLED);
                        hotelBookingRepository.save(booking);
                    }
                });
            }
        }
    }
}
