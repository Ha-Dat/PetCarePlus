package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.AppointmentBooking;
import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.repository.HotelBookingRepository;
import org.example.petcareplus.repository.ServiceRepository;
import org.example.petcareplus.repository.SpaBookingRepository;
import org.example.petcareplus.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final AppointmentRepository appointmentRepository;
    private final SpaBookingRepository spaBookingRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final ServiceRepository serviceRepository;

    public BookingServiceImpl(AppointmentRepository appointmentRepository,
                              SpaBookingRepository spaBookingRepository,
                              HotelBookingRepository hotelBookingRepository,
                              ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.spaBookingRepository = spaBookingRepository;
        this.hotelBookingRepository = hotelBookingRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<MyServiceDTO> getMyServices(Long profileId) {
        return filterMyServices(profileId, "all", "all");
    }

    @Override
    public List<MyServiceDTO> filterMyServices(Long profileId, String type, String status) {
        List<MyServiceDTO> result = new ArrayList<>();

        if ("all".equalsIgnoreCase(type) || "APPOINTMENT".equalsIgnoreCase(type)) {
            result.addAll(filterAppointments(profileId, status));
        }
        if ("all".equalsIgnoreCase(type) || "SPA".equalsIgnoreCase(type)) {
            result.addAll(filterSpaBookings(profileId, status));
        }
        if ("all".equalsIgnoreCase(type) || "HOTEL".equalsIgnoreCase(type)) {
            result.addAll(filterHotelBookings(profileId, status));
        }

        result.sort((a, b) -> b.getBookDate().compareTo(a.getBookDate()));
        return result;
    }

    private List<MyServiceDTO> filterAppointments(Long profileId, String status) {
        if ("all".equalsIgnoreCase(status)) {
            return appointmentRepository.findByProfileId(profileId);
        }
        BookingStatus bookingStatus = BookingStatus.fromValue(status);
        return appointmentRepository.findByProfileIdAndStatus(profileId, bookingStatus);
    }

    private List<MyServiceDTO> filterSpaBookings(Long profileId, String status) {
        if ("all".equalsIgnoreCase(status)) {
            return spaBookingRepository.findByProfileId(profileId);
        }
        BookingStatus bookingStatus = BookingStatus.fromValue(status);
        return spaBookingRepository.findByProfileIdAndStatus(profileId, bookingStatus);
    }

    private List<MyServiceDTO> filterHotelBookings(Long profileId, String status) {
        if ("all".equalsIgnoreCase(status)) {
            return hotelBookingRepository.findByProfileId(profileId);
        }
        BookingStatus bookingStatus = BookingStatus.fromValue(status);
        return hotelBookingRepository.findByProfileIdAndStatus(profileId, bookingStatus);
    }

    @Override
    public boolean cancelBooking(String type, Long id, Long profileId) {
        BookingStatus cancelStatus = BookingStatus.CANCELLED;

        switch (type.toUpperCase()) {
            case "SPA":
                return spaBookingRepository.findById(id).map(spa -> {
                    if (spa.getPetProfile().getProfile().getProfileId().equals(profileId) &&
                            spa.getStatus() == BookingStatus.PENDING) {
                        spa.setStatus(cancelStatus);
                        spaBookingRepository.save(spa);
                        return true;
                    }
                    return false;
                }).orElse(false);

            case "HOTEL":
                return hotelBookingRepository.findById(id).map(hotel -> {
                    if (hotel.getPetProfile().getProfile().getProfileId().equals(profileId) &&
                            hotel.getStatus() == BookingStatus.PENDING) {
                        hotel.setStatus(cancelStatus);
                        hotelBookingRepository.save(hotel);
                        return true;
                    }
                    return false;
                }).orElse(false);

            case "APPOINTMENT":
                return appointmentRepository.findById(id).map(apt -> {
                    if (apt.getPetProfile().getProfile().getProfileId().equals(profileId) &&
                            apt.getStatus() == BookingStatus.PENDING) {
                        apt.setStatus(cancelStatus);
                        appointmentRepository.save(apt);
                        return true;
                    }
                    return false;
                }).orElse(false);

            default:
                return false;
        }
    }

    @Override
    public MyServiceDTO getBookingDetail(String type, Long id, Long profileId) {
        // Lấy detail vẫn phải load entity vì cần validate profileId
        switch (type.toUpperCase()) {
            case "SPA":
                return spaBookingRepository.findById(id)
                        .filter(spa -> spa.getPetProfile().getProfile().getProfileId().equals(profileId))
                        .map(s -> new MyServiceDTO(
                                s.getSpaBookingId(),
                                s.getPetProfile().getName(),
                                s.getService().getName(),
                                s.getService().getServiceCategory(),
                                s.getBookDate(),
                                s.getStatus(),
                                s.getNote()
                        ))
                        .orElse(null);

            case "HOTEL":
                return hotelBookingRepository.findById(id)
                        .filter(hotel -> hotel.getPetProfile().getProfile().getProfileId().equals(profileId))
                        .map(h -> new MyServiceDTO(
                                h.getHotelBookingId(),
                                h.getPetProfile().getName(),
                                h.getService().getName(),
                                h.getService().getServiceCategory(),
                                h.getBookDate(),
                                h.getStatus(),
                                h.getNote()
                        ))
                        .orElse(null);

            case "APPOINTMENT":
                return appointmentRepository.findById(id)
                        .filter(apt -> apt.getPetProfile().getProfile().getProfileId().equals(profileId))
                        .map(a -> new MyServiceDTO(
                                a.getAppointmentBookingId(),
                                a.getPetProfile().getName(),
                                a.getService().getName(),
                                a.getService().getServiceCategory(),
                                a.getBookDate(),
                                a.getStatus(),
                                a.getNote()
                        ))
                        .orElse(null);

            default:
                return null;
        }
    }

    @Override
    @Transactional
    public void updateBooking(MyServiceDTO updated) {
        switch (updated.getServiceCategory()) {
            case SPA -> {
                SpaBooking spa = spaBookingRepository.findById(updated.getBookingId())
                        .orElseThrow(() -> new RuntimeException("Spa booking not found"));
                spa.setBookDate(updated.getBookDate());
                spa.setNote(updated.getNote());
                if (updated.getServiceName() != null) {
                    org.example.petcareplus.entity.Service service = serviceRepository.findByName(updated.getServiceName())
                            .orElseThrow(() -> new RuntimeException("Service not found"));
                    spa.setService(service);
                }
                spaBookingRepository.save(spa);
            }
            case HOTEL -> {
                HotelBooking hotel = hotelBookingRepository.findById(updated.getBookingId())
                        .orElseThrow(() -> new RuntimeException("Hotel booking not found"));
                hotel.setBookDate(updated.getBookDate());
                hotel.setNote(updated.getNote());
                if (updated.getServiceName() != null) {
                    org.example.petcareplus.entity.Service service = serviceRepository.findByName(updated.getServiceName())
                            .orElseThrow(() -> new RuntimeException("Service not found"));
                    hotel.setService(service);
                }
                hotelBookingRepository.save(hotel);
            }
            case APPOINTMENT -> {
                AppointmentBooking apt = appointmentRepository.findById(updated.getBookingId())
                        .orElseThrow(() -> new RuntimeException("Appointment booking not found"));
                apt.setBookDate(updated.getBookDate());
                apt.setNote(updated.getNote());
                if (updated.getServiceName() != null) {
                    org.example.petcareplus.entity.Service service = serviceRepository.findByName(updated.getServiceName())
                            .orElseThrow(() -> new RuntimeException("Service not found"));
                    apt.setService(service);
                }
                appointmentRepository.save(apt);
            }
        }
    }

    @Override
    public boolean isPending(ServiceCategory category, Long bookingId) {
        return switch (category) {
            case SPA -> spaBookingRepository.findById(bookingId)
                    .map(b -> b.getStatus() == BookingStatus.PENDING)
                    .orElse(false);
            case HOTEL -> hotelBookingRepository.findById(bookingId)
                    .map(b -> b.getStatus() == BookingStatus.PENDING)
                    .orElse(false);
            case APPOINTMENT -> appointmentRepository.findById(bookingId)
                    .map(b -> b.getStatus() == BookingStatus.PENDING)
                    .orElse(false);
        };
    }
}


