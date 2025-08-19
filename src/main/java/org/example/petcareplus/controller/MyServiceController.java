package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.dto.PrescriptionDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.service.BookingService;
import org.example.petcareplus.service.PrescriptionService;
import org.example.petcareplus.service.ServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/my-schedule")
public class MyServiceController {

    private final BookingService bookingService;
    private final ServiceService serviceService;
    private final PrescriptionService prescriptionService;

    public MyServiceController(BookingService bookingService, ServiceService serviceService, PrescriptionService prescriptionService) {
        this.bookingService = bookingService;
        this.serviceService = serviceService;
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public String viewMyService(Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        List<MyServiceDTO> myServices = bookingService.getMyServices(account.getProfile().getProfileId());
        model.addAttribute("myServices", myServices);
        
        return "my-service-schedule";
    }

    @PostMapping("/cancel/{type}/{id}")
    @ResponseBody
    public ResponseEntity<?> cancelBooking(@PathVariable String type,
                                           @PathVariable Long id,
                                           HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Vui lòng đăng nhập."));
        }

        boolean success = bookingService.cancelBooking(type, id, account.getProfile().getProfileId());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Hủy lịch thành công."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Không thể hủy lịch này."));
        }
    }

    @GetMapping("/detail/{type}/{id}")
    @ResponseBody
    public ResponseEntity<?> getBookingDetail(@PathVariable String type,
                                              @PathVariable Long id,
                                              HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Vui lòng đăng nhập."));
        }

        MyServiceDTO detail = bookingService.getBookingDetail(type, id, account.getProfile().getProfileId());
        if (detail != null) {
            return ResponseEntity.ok(detail);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Không tìm thấy lịch đặt."));
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateBooking(@RequestBody MyServiceDTO dto) {
        try {
            // Kiểm tra booking có tồn tại và status PENDING
            boolean canUpdate = bookingService.isPending(dto.getServiceCategory(), dto.getBookingId());
            if (!canUpdate) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Chỉ có thể chỉnh sửa khi booking ở trạng thái Pending"
                ));
            }

            bookingService.updateBooking(dto);

            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật booking thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Có lỗi xảy ra khi cập nhật booking",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/services/by-type/{type}")
    @ResponseBody
    public List<Map<String, String>> getServicesByType(@PathVariable String type) {
        // Gọi service lấy danh sách dịch vụ từ DB
        return serviceService.findByServiceCategory(ServiceCategory.valueOf(type))
                .stream()
                .map(s -> Map.of("name", s.getName(),
                        "price", s.getPrice().toString()
                ))
                .toList();
    }

    @GetMapping("/prescriptions/{appointmentId}")
    @ResponseBody
    public ResponseEntity<?> getPrescriptions(@PathVariable Long appointmentId, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Vui lòng đăng nhập."));
        }

        try {
            List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByAppointmentId(appointmentId);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Có lỗi xảy ra khi lấy đơn thuốc: " + e.getMessage()));
        }
    }
}