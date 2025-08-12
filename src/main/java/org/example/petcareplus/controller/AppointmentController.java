package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.ProductDTO;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.repository.CategoryRepository;
import org.example.petcareplus.service.*;
import org.example.petcareplus.repository.PetProfileRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PetProfileService petProfileService;
    private final ServiceService serviceService;
    private final CategoryService categoryService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, PetProfileService petProfileService, ServiceService serviceService, CategoryService categoryService) {
        this.appointmentService = appointmentService;
        this.petProfileService = petProfileService;
        this.serviceService = serviceService;
        this.categoryService = categoryService;
    }

    @GetMapping("/appointment-booking-form")
    public String showAppointmentBookingForm(HttpSession session, Model model) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        List<Category> parentCategories = categoryService.getParentCategory();
        List<PetProfile> petProfiles = petProfileService.findByProfileId(account.getProfile().getProfileId());

        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("appointmentBooking", new AppointmentBooking()); // model binding
        model.addAttribute("appointmentServices", serviceService.findByServiceCategory(ServiceCategory.APPOINTMENT));
        model.addAttribute("categories", parentCategories);
        return "appointment-booking";
    }


    @ModelAttribute("services")
    public List<Service> getServiceOptions() {
        return serviceService.findByServiceCategory(ServiceCategory.APPOINTMENT);
    }


    @PostMapping("/appointment-booking/book/{id}")
    public String addAppointmentBooking(
            @RequestParam("bookDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookDate,
            @RequestParam("note") String note,
            @RequestParam("serviceId") Long serviceId,
            @RequestParam("petName") String petName,
            @RequestParam("petSpecies") String petSpecies,
            @RequestParam("petBreed") String petBreed,
            @RequestParam("petWeight") float petWeight,
            @RequestParam("petAge") Integer petAge,
            @PathVariable("id") Long petProfileId,
            HttpSession session,
            Model model
    ) {
        try {
            Account account = (Account) session.getAttribute("loggedInUser");
            if (account == null) return "redirect:/login";
            PetProfile petProfile = petProfileService.findById(petProfileId);
            petProfile.setName(petName);
            petProfile.setSpecies(petSpecies);
            petProfile.setBreeds(petBreed);
            petProfile.setWeight(petWeight);
            petProfile.setAge(petAge);
            petProfile.setProfile(account.getProfile());
            petProfileService.updatePetProfile(petProfileId, petProfile);

            // gán dữ liệu từ form
            AppointmentBooking booking = new AppointmentBooking();
            booking.setBookDate(bookDate);
            booking.setNote(note);
            booking.setStatus(BookingStatus.PENDING);
            booking.setCreatedAt(LocalDateTime.now());

            booking.setPetProfile(petProfile);

            Optional<Service> service = appointmentService.Service_findById(serviceId);
            if (service.isEmpty()) {
                model.addAttribute("error", "Dịch vụ không tồn tại");
                return "error";
            }
            booking.setService(service.get());

            appointmentService.save(booking);
            return "redirect:/appointment-booking-form";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi đặt lịch: " + e.getMessage());
            return "error";
        }
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
    @ResponseBody
    public String approveAppointment(@PathVariable("id") Long id) {
        Optional<AppointmentBooking> appointmentBooking = appointmentService.findById(id);
        if (appointmentBooking.isPresent()) {
            AppointmentBooking appointment = appointmentBooking.get();
            appointment.setStatus(BookingStatus.ACCEPTED);
            appointmentService.save(appointment);
            return "Đã chấp nhận";
        }
        return "Thao tác không thành công";
    }

    @PostMapping("/appointment/disapprove/{id}")
    @ResponseBody
    public String disapproveAppointment(@PathVariable("id") Long id) {
        Optional<AppointmentBooking> disappointmentBooking = appointmentService.findById(id);
        if (disappointmentBooking.isPresent()) {
            AppointmentBooking appointment = disappointmentBooking.get();
            appointment.setStatus(BookingStatus.REJECTED);
            appointmentService.save(appointment);
            return "Đã từ chối";
        }
        return "Thao tác không thành công";
    }
}
