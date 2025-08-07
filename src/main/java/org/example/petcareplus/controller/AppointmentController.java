package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.ProductDTO;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.repository.CategoryRepository;
import org.example.petcareplus.service.AppointmentService;
import org.example.petcareplus.repository.PetProfileRepository;
import org.example.petcareplus.service.HotelBookingService;
import org.example.petcareplus.service.PetProfileService;
import org.example.petcareplus.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointment-booking")
    public String showHotelBookingForm(HttpSession session, Model model) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        return "appointment-booking";
    }

    @Autowired
    private ServiceService serviceService;

    @ModelAttribute("services")
    public List<Service> getServiceOptions() {
        return serviceService.findByServiceCategory(ServiceCategory.APPOINTMENT);
    }

    @Autowired
    private PetProfileService petProfileService;

    @PostMapping("/appointment/create")
    public String addHotelBooking(
            @RequestParam("bookDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookDate,
            @RequestParam("note") String note,
            @RequestParam("serviceId") Long serviceId,
            @RequestParam("petName") String petName,
            @RequestParam("petSpecies") String petSpecies,
            @RequestParam("petBreed") String petBreed,
            HttpSession session,
            Model model
    ) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";
        // Tạo pet profile mới từ form
        PetProfile petProfile = new PetProfile();
        petProfile.setName(petName);
        petProfile.setSpecies(petSpecies);
        petProfile.setBreeds(petBreed);
        petProfile.setProfile(account.getProfile());
        petProfileService.save(petProfile);

        // gán dữ liệu từ form
        AppointmentBooking booking = new AppointmentBooking();
        booking.setBookDate(bookDate);
        booking.setNote(note);

        Optional<Service> service = serviceService.findById(serviceId);
        if (service.isEmpty()) {
            model.addAttribute("error", "Dịch vụ không tồn tại");
            return "error";
        }
        booking.setService(service.get());
        booking.setService(service.get());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setPetProfile(petProfile);

        appointmentService.save(booking);
        return "home";
    }

    @GetMapping("/appointment/approved")
    public String approvedPage(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        if (page < 0) page = 0;

        Page<AppointmentBooking> approvedPage = appointmentService.getApprovedAppointments(PageRequest.of(page, size));

        model.addAttribute("appointments", approvedPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", approvedPage.getTotalPages());
        model.addAttribute("mode", "approved");
        return "appointment.html";
    }

    @GetMapping("/appointment/pending")
    public String pendingPage(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        if (page < 0) page = 0;

        Page<AppointmentBooking> pendingPage = appointmentService.getPendingAppointments(PageRequest.of(page, size));

        model.addAttribute("appointments", pendingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", pendingPage.getTotalPages());
        model.addAttribute("mode", "pending");
        return "appointment.html";
    }

    @GetMapping("/appointment/history")
    public String historyPage(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        if (page < 0) page = 0;

        Page<AppointmentBooking> historyPage = appointmentService.getHistoryAppointments(PageRequest.of(page, size));

        model.addAttribute("appointments", historyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", historyPage.getTotalPages());
        model.addAttribute("mode", "history");
        return "appointment.html";
    }

    @PostMapping("/appointment/approve/{id}")
    public String approveAppointment(@PathVariable("id") Long id) {
        Optional<AppointmentBooking> appointmentBooking = appointmentService.findById(id);
        if (appointmentBooking.isPresent()) {
            AppointmentBooking appointment = appointmentBooking.get();
            appointment.setStatus(BookingStatus.ACCEPTED);
            appointmentService.save(appointment);
            return "redirect:/appointment/approved"; // This tells Spring to redirect
        }
        return "redirect:/appointment/pending?error=true"; // or some error page
    }

    @PostMapping("/appointment/disapprove/{id}")
    public String disapproveAppointment(@PathVariable("id") Long id) {
        Optional<AppointmentBooking> appointmentBooking = appointmentService.findById(id);
        if (appointmentBooking.isPresent()) {
            AppointmentBooking appointment = appointmentBooking.get();
            appointment.setStatus(BookingStatus.REJECTED);
            appointmentService.save(appointment);
            return "redirect:/appointment/pending";
        }
        return "redirect:/appointment/pending?error=true";
    }
}
