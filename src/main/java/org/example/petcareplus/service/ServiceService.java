package org.example.petcareplus.service;

import org.example.petcareplus.entity.Service;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.enums.ServiceStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceService {

    Optional<org.example.petcareplus.entity.Service> findById(Long id);

    List<org.example.petcareplus.entity.Service> findByServiceCategory(ServiceCategory serviceCategory);

    Page<org.example.petcareplus.entity.Service> getServicesPaginated(int page, int size);

    org.example.petcareplus.entity.Service saveService(org.example.petcareplus.entity.Service service);

    void deleteService(Long id);

    // Kiểm tra xem service có thể xóa được không
    boolean canDeleteService(Long id);

    // Lấy thông tin chi tiết về service
    Optional<org.example.petcareplus.entity.Service> getServiceById(Long id);

    // Lấy thông tin chi tiết về các booking của service
    Map<String, Object> getServiceBookingInfo(Long serviceId);
    
    // Lấy service theo status
    List<org.example.petcareplus.entity.Service> findByStatus(ServiceStatus status);
    
    // Lấy service ACTIVE theo category (cho trang đặt lịch)
    List<org.example.petcareplus.entity.Service> findActiveByServiceCategory(ServiceCategory serviceCategory);

    List<Service> findAll();
}
