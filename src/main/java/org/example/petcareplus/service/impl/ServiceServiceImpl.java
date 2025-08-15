package org.example.petcareplus.service.impl;

import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.repository.HotelBookingRepository;
import org.example.petcareplus.repository.ServiceRepository;
import org.example.petcareplus.repository.SpaBookingRepository;
import org.example.petcareplus.service.ServiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final SpaBookingRepository spaBookingRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final AppointmentRepository appointmentRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository,
                           SpaBookingRepository spaBookingRepository,
                           HotelBookingRepository hotelBookingRepository,
                           AppointmentRepository appointmentRepository) {
        this.serviceRepository = serviceRepository;
        this.spaBookingRepository = spaBookingRepository;
        this.hotelBookingRepository = hotelBookingRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Optional<org.example.petcareplus.entity.Service> findById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    public List<org.example.petcareplus.entity.Service> findByServiceCategory(ServiceCategory serviceCategory) {
        return serviceRepository.findByServiceCategory(serviceCategory);
    }

    @Override
    public Page<org.example.petcareplus.entity.Service> getServicesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("serviceId").ascending());
        return serviceRepository.findAll(pageable);
    }

    @Override
    public org.example.petcareplus.entity.Service saveService(org.example.petcareplus.entity.Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public void deleteService(Long id) {
        // Kiểm tra xem có thể xóa service không
        if (!canDeleteService(id)) {
            throw new RuntimeException("Dịch vụ này đã có người đặt, không thể xóa");
        }
        serviceRepository.deleteById(id);
    }

    @Override
    public boolean canDeleteService(Long id) {
        // Kiểm tra xem có booking nào đang sử dụng service này không
        boolean hasSpaBookings = spaBookingRepository.existsByServiceServiceId(id);
        boolean hasHotelBookings = hotelBookingRepository.existsByServiceServiceId(id);
        boolean hasAppointmentBookings = appointmentRepository.existsByServiceServiceId(id);
        
        // Nếu có bất kỳ booking nào thì không thể xóa
        return !hasSpaBookings && !hasHotelBookings && !hasAppointmentBookings;
    }

    @Override
    public Optional<org.example.petcareplus.entity.Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    public Map<String, Object> getServiceBookingInfo(Long serviceId) {
        Map<String, Object> bookingInfo = new HashMap<>();
        
        // Kiểm tra các loại booking
        boolean hasSpaBookings = spaBookingRepository.existsByServiceServiceId(serviceId);
        boolean hasHotelBookings = hotelBookingRepository.existsByServiceServiceId(serviceId);
        boolean hasAppointmentBookings = appointmentRepository.existsByServiceServiceId(serviceId);
        
        bookingInfo.put("hasSpaBookings", hasSpaBookings);
        bookingInfo.put("hasHotelBookings", hasHotelBookings);
        bookingInfo.put("hasAppointmentBookings", hasAppointmentBookings);
        bookingInfo.put("canDelete", !hasSpaBookings && !hasHotelBookings && !hasAppointmentBookings);
        
        return bookingInfo;
    }
}
