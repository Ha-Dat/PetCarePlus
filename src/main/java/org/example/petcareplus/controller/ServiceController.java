package org.example.petcareplus.controller;

import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.enums.ServiceStatus;
import org.example.petcareplus.service.ServiceService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping()
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping("/manager/service-dashboard")
    public String serviceDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model) {
        
        Page<org.example.petcareplus.entity.Service> servicesPage = serviceService.getServicesPaginated(page, size);
        
        model.addAttribute("services", servicesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", servicesPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("serviceCategories", Arrays.asList(ServiceCategory.values()));
        
        return "service-dashboard";
    }

    @PostMapping("/manager/service-dashboard/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createService(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String name = (String) request.get("name");
            String serviceCategoryStr = (String) request.get("serviceCategory");
            BigDecimal price = new BigDecimal(request.get("price").toString());
            Integer duration = Integer.valueOf(request.get("duration").toString());
            
            ServiceCategory serviceCategory = ServiceCategory.valueOf(serviceCategoryStr);
            
            org.example.petcareplus.entity.Service service = new org.example.petcareplus.entity.Service();
            service.setName(name);
            service.setServiceCategory(serviceCategory);
            service.setPrice(price);
            service.setDuration(duration);
            service.setStatus(ServiceStatus.ACTIVE); // Mặc định là ACTIVE
            
            org.example.petcareplus.entity.Service savedService = serviceService.saveService(service);
            
            response.put("success", true);
            response.put("message", "Dịch vụ đã được tạo thành công");
            response.put("serviceId", savedService.getServiceId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo dịch vụ: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/manager/service-dashboard/update/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateService(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String name = (String) request.get("name");
            String serviceCategoryStr = (String) request.get("serviceCategory");
            BigDecimal price = new BigDecimal(request.get("price").toString());
            Integer duration = Integer.valueOf(request.get("duration").toString());

            ServiceCategory serviceCategory = ServiceCategory.valueOf(serviceCategoryStr);
            
            org.example.petcareplus.entity.Service service = serviceService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + id));
            
            service.setName(name);
            service.setServiceCategory(serviceCategory);
            service.setPrice(price);
            service.setDuration(duration);
            
            org.example.petcareplus.entity.Service updatedService = serviceService.saveService(service);
            
            response.put("success", true);
            response.put("message", "Dịch vụ đã được cập nhật thành công");
            response.put("serviceId", updatedService.getServiceId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật dịch vụ: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/manager/service-dashboard/ban/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> banService(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            org.example.petcareplus.entity.Service service = serviceService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + id));
            
            service.setStatus(ServiceStatus.BANNED);
            serviceService.saveService(service);
            
            response.put("success", true);
            response.put("message", "Dịch vụ đã được khóa thành công");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi khóa dịch vụ: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/manager/service-dashboard/unban/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unbanService(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            org.example.petcareplus.entity.Service service = serviceService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + id));
            
            service.setStatus(ServiceStatus.ACTIVE);
            serviceService.saveService(service);
            
            response.put("success", true);
            response.put("message", "Dịch vụ đã được mở khóa thành công");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi mở khóa dịch vụ: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/manager/service-dashboard/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteService(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra xem có thể xóa service không
            if (!serviceService.canDeleteService(id)) {
                response.put("success", false);
                response.put("message", "Dịch vụ này đã có người đặt, không thể xóa");
                return ResponseEntity.badRequest().body(response);
            }
            
            serviceService.deleteService(id);
            
            response.put("success", true);
            response.put("message", "Dịch vụ đã được xóa thành công");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi xóa dịch vụ: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/manager/service-dashboard/check-delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkServiceDeletable(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> bookingInfo = serviceService.getServiceBookingInfo(id);
            response.put("success", true);
            response.put("data", bookingInfo);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi kiểm tra dịch vụ: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/service-dashboard/service/{id}")
    @ResponseBody
    public ResponseEntity<org.example.petcareplus.entity.Service> getServiceById(@PathVariable Long id) {
        return serviceService.getServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
