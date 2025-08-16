package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.example.petcareplus.dto.PrescriptionDTO;
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
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;
    private final PetProfileService petProfileService;
    private final ServiceService serviceService;
    private final CategoryService categoryService;

 @Autowired
    public AppointmentController(AppointmentService appointmentService, PetProfileService petProfileService, ServiceService serviceService, CategoryService categoryService, PrescriptionService prescriptionService) {
        this.appointmentService = appointmentService;
        this.prescriptionService = prescriptionService;
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

    @GetMapping("/vet/appointment/approved")
    public String approvedPage(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        if (page < 0) page = 0;

        Page<AppointmentBooking> approvedPage = appointmentService.getApprovedAppointments(PageRequest.of(page, size));

        // ✅ This must return a List
        List<Prescription> AllPrescriptions = prescriptionService.findAll();

        List<Map<String, String>> prescriptions = AllPrescriptions.stream()
                .map(p -> Map.of(
                        "appointmentId", p.getAppointment().getAppointmentBookingId().toString(),
                        "prescriptionId", p.getPrescriptionId().toString(),
                        "prescriptionDrugName", p.getDrugName(),
                        "prescriptionAmount", p.getAmount(),
                        "prescriptionNote", p.getNote()
                ))
                .collect(Collectors.toList());

        model.addAttribute("appointments", approvedPage.getContent());
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", approvedPage.getTotalPages());
        model.addAttribute("mode", "approved");
        return "appointment";
    }

    @GetMapping("/vet/appointment/pending")
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
        return "appointment";
    }

    @GetMapping("/vet/appointment/history")
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
        return "appointment";
    }

    @PostMapping("/vet/appointment/approve/{id}")
    public String approveAppointment(@PathVariable("id") Long id) {
        Optional<AppointmentBooking> appointmentBooking = appointmentService.findById(id);
        if (appointmentBooking.isPresent()) {
            AppointmentBooking appointment = appointmentBooking.get();
            appointment.setStatus(BookingStatus.ACCEPTED);
            appointmentService.save(appointment);
            return "redirect:/vet/appointment/approved"; // This tells Spring to redirect
        }
        return "redirect:/vet/appointment/pending?error=true"; // or some error page
    }

    @GetMapping("/vet/appointment/completed")
    public String completedPage(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        if (page < 0) page = 0;

        Page<AppointmentBooking> completedAppointments = appointmentService.getCompletedAppointments(PageRequest.of(page, size));

        model.addAttribute("appointments", completedAppointments.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", completedAppointments.getTotalPages());
        model.addAttribute("mode", "completed");
        return "appointment"; // Sử dụng cùng template như các trang khác
    }

    @PostMapping("/vet/appointment/complete/{id}")
    public String completeAppointment(@PathVariable("id") Long id) {
        Optional<AppointmentBooking> appointmentBooking = appointmentService.findById(id);
        if (appointmentBooking.isPresent()) {
            AppointmentBooking appointment = appointmentBooking.get();
            appointment.setStatus(BookingStatus.COMPLETED);
            appointmentService.save(appointment);
            return "redirect:/vet/appointment/completed"; // This tells Spring to redirect
        }
        return "redirect:/vet/appointment/pending?error=true"; // or some error page
    }

    @PostMapping("/vet/appointment/disapprove/{id}")
    public String disapproveAppointment(@PathVariable("id") Long id) {
        Optional<AppointmentBooking> appointmentBooking = appointmentService.findById(id);
        if (appointmentBooking.isPresent()) {
            AppointmentBooking appointment = appointmentBooking.get();
            appointment.setStatus(BookingStatus.REJECTED);
            appointmentService.save(appointment);
            return "redirect:/vet/appointment/pending";
        }
        return "redirect:/vet/appointment/pending?error=true";
    }


@GetMapping("/vet/appointment/prescriptions")
public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByAppointment(@RequestParam Long appointmentId) {
    List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByAppointmentId(appointmentId);
    return ResponseEntity.ok(prescriptions);
}

    @PostMapping("/vet/appointment/prescriptions")
    public ResponseEntity<?> createPrescription(
            @Valid @RequestBody PrescriptionDTO prescriptionDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Trả về thông báo lỗi chi tiết
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            PrescriptionDTO createdPrescription = prescriptionService.createPrescription(prescriptionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPrescription);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/vet/appointment/prescriptions/{prescriptionId}")
    public ResponseEntity<PrescriptionDTO> updatePrescription(
            @PathVariable Long prescriptionId,
            @Valid @RequestBody PrescriptionDTO prescriptionDTO) {

        // Đảm bảo ID trong path và DTO khớp nhau
        if (!prescriptionId.equals(prescriptionDTO.getPrescriptionId())) {
            throw new IllegalArgumentException("Path ID and body ID must match");
        }

        PrescriptionDTO updatedPrescription = prescriptionService.updatePrescription(prescriptionDTO);
        return ResponseEntity.ok(updatedPrescription);
    }

    @DeleteMapping("/vet/appointment/prescriptions/{prescriptionId}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long prescriptionId) {
        prescriptionService.deletePrescription(prescriptionId);
        return ResponseEntity.noContent().build();
    }


}
