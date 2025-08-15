package org.example.petcareplus.service;

import org.example.petcareplus.entity.Service;
import org.example.petcareplus.enums.ServiceCategory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceService {

    Optional<Service> findById(Long id);

    List<Service> findByServiceCategory(ServiceCategory serviceCategory);

    Page<Service> getServicesPaginated(int page, int size);

    Service saveService(Service service);

    void deleteService(Long id);

    // Kiểm tra xem service có thể xóa được không
    boolean canDeleteService(Long id);

    // Lấy thông tin chi tiết về service
    Optional<Service> getServiceById(Long id);

    // Lấy thông tin chi tiết về các booking của service
    Map<String, Object> getServiceBookingInfo(Long serviceId);
}
