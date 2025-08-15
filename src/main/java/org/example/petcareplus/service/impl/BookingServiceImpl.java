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

    public BookingServiceImpl(AppointmentRepository appointmentRepository, SpaBookingRepository spaBookingRepository, HotelBookingRepository hotelBookingRepository, ServiceRepository serviceRepository) {
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

        // Filter theo loại dịch vụ
        if ("all".equalsIgnoreCase(type) || "APPOINTMENT".equalsIgnoreCase(type)) {
            result.addAll(filterAppointments(profileId, status));
        }
        if ("all".equalsIgnoreCase(type) || "SPA".equalsIgnoreCase(type)) {
            result.addAll(filterSpaBookings(profileId, status));
        }
        if ("all".equalsIgnoreCase(type) || "HOTEL".equalsIgnoreCase(type)) {
            result.addAll(filterHotelBookings(profileId, status));
        }

        // Sắp xếp theo thời gian đặt
        result.sort((a, b) -> b.getBookDate().compareTo(a.getBookDate()));

        return result;
    }

    private List<MyServiceDTO> filterAppointments(Long profileId, String status) {
        List<AppointmentBooking> appointments;

        if ("all".equalsIgnoreCase(status)) {
            appointments = appointmentRepository.findByProfileId(profileId);
        } else {
            BookingStatus bookingStatus = BookingStatus.fromValue(status);
            appointments = appointmentRepository.findByProfileIdAndStatus(profileId, bookingStatus);
        }

        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private List<MyServiceDTO> filterSpaBookings(Long profileId, String status) {
        List<SpaBooking> spaBookings;

        if ("all".equalsIgnoreCase(status)) {
            spaBookings = spaBookingRepository.findByProfileId(profileId);
        } else {
            BookingStatus bookingStatus = BookingStatus.fromValue(status);
            spaBookings = spaBookingRepository.findByProfileIdAndStatus(profileId, bookingStatus);
        }

        return spaBookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private List<MyServiceDTO> filterHotelBookings(Long profileId, String status) {
        List<HotelBooking> hotelBookings;

        if ("all".equalsIgnoreCase(status)) {
            hotelBookings = hotelBookingRepository.findByProfileId(profileId);
        } else {
            BookingStatus bookingStatus = BookingStatus.fromValue(status);
            hotelBookings = hotelBookingRepository.findByProfileIdAndStatus(profileId, bookingStatus);
        }

        return hotelBookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MyServiceDTO convertToDTO(Object booking) {
        if (booking instanceof AppointmentBooking) {
            AppointmentBooking apt = (AppointmentBooking) booking;
            return new MyServiceDTO(
                    apt.getAppointmentBookingId(),
                    apt.getPetProfile().getName(),
                    ServiceCategory.APPOINTMENT.getValue(),
                    ServiceCategory.APPOINTMENT,
                    apt.getBookDate(),
                    apt.getStatus(),
                    apt.getNote()
            );
        } else if (booking instanceof SpaBooking) {
            SpaBooking spa = (SpaBooking) booking;
            return new MyServiceDTO(
                    spa.getSpaBookingId(),
                    spa.getPetProfile().getName(),
                    ServiceCategory.SPA.getValue(),
                    ServiceCategory.SPA,
                    spa.getBookDate(),
                    spa.getStatus(),
                    spa.getNote()
            );
        } else if (booking instanceof HotelBooking) {
            HotelBooking hotel = (HotelBooking) booking;
            return new MyServiceDTO(
                    hotel.getHotelBookingId(),
                    hotel.getPetProfile().getName(),
                    ServiceCategory.HOTEL.getValue(),
                    ServiceCategory.HOTEL,
                    hotel.getBookDate(),
                    hotel.getStatus(),
                    hotel.getNote()
            );
        }
        throw new IllegalArgumentException("Invalid booking type");
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
        switch (type.toUpperCase()) {
            case "SPA":
                return spaBookingRepository.findById(id)
                        .filter(spa -> spa.getPetProfile().getProfile().getProfileId().equals(profileId))
                        .map(this::convertToDTO)
                        .orElse(null);
            case "HOTEL":
                return hotelBookingRepository.findById(id)
                        .filter(hotel -> hotel.getPetProfile().getProfile().getProfileId().equals(profileId))
                        .map(this::convertToDTO)
                        .orElse(null);
            case "APPOINTMENT":
                return appointmentRepository.findById(id)
                        .filter(apt -> apt.getPetProfile().getProfile().getProfileId().equals(profileId))
                        .map(this::convertToDTO)
                        .orElse(null);
            default:
                return null;
        }
    }

    @Override
    @Transactional
    public void updateBooking(MyServiceDTO updated) {
        if (updated.getServiceCategory() == ServiceCategory.SPA) {
            SpaBooking spa = spaBookingRepository.findById(updated.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Spa booking not found"));
            spa.setBookDate(updated.getBookDate());
            spa.setNote(updated.getNote());
            // Không cho đổi petName và status
            // Service chỉ thay đổi nếu cần (thông qua ID hoặc name)
            if (updated.getServiceName() != null) {
                org.example.petcareplus.entity.Service service = serviceRepository.findByName(updated.getServiceName())
                        .orElseThrow(() -> new RuntimeException("Service not found"));
                spa.setService(service);
            }
            spaBookingRepository.save(spa);
        }
        else if (updated.getServiceCategory() == ServiceCategory.HOTEL) {
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
        else if (updated.getServiceCategory() == ServiceCategory.APPOINTMENT) {
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

    @Override
    public boolean isPending(ServiceCategory category, Long bookingId) {
        switch (category) {
            case SPA:
                return spaBookingRepository.findById(bookingId)
                        .map(b -> b.getStatus() == BookingStatus.PENDING)
                        .orElse(false);
            case HOTEL:
                return hotelBookingRepository.findById(bookingId)
                        .map(b -> b.getStatus() == BookingStatus.PENDING)
                        .orElse(false);
            case APPOINTMENT:
                return appointmentRepository.findById(bookingId)
                        .map(b -> b.getStatus() == BookingStatus.PENDING)
                        .orElse(false);
            default:
                return false;
        }
    }
}
