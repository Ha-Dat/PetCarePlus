package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.service.AppointmentService;
import org.example.petcareplus.service.BookingService;
import org.example.petcareplus.service.HotelBookingService;
import org.example.petcareplus.service.SpaBookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/my-schedule")
public class MyServiceController {

    private final BookingService bookingService;

    public MyServiceController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public String viewMyService(@RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "status", required = false) String status,
                                Model model, HttpSession session) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Account account = (Account) session.getAttribute("loggedInUser");
        Long id = account.getAccountId();

        List<MyServiceDTO> schedules = bookingService.getMyServices(id);

        // Filter theo service
        if (type != null && !type.equals("all")) {
            schedules = schedules.stream()
                    .filter(e -> e.getServiceCategory().getValue().equalsIgnoreCase(type))
                    .toList();
        }

        // Filter theo status
        if (status != null && !status.isEmpty()) {
            schedules = schedules.stream()
                    .filter(e -> e.getStatus().getValue().equalsIgnoreCase(status))
                    .toList();
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("selectedType", type == null ? "all" : type);
        model.addAttribute("selectedStatus", status == null ? "all" : status);

        return "my-service-schedule";
    }

    @GetMapping("/cancel")
    public String cancelBooking(@RequestParam("id") Long bookingId,
                                @RequestParam("category") String category,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "status", required = false) String status,
                                HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        bookingService.cancelBookingByTypeAndId(category, bookingId);
        return "redirect:/my-schedule?type=" + (type == null ? "all" : type) + "&status=" + (status == null ? "all" : status);
    }
}
