package org.example.petcareplus.controller;

import org.example.petcareplus.entity.AppointmentBooking;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.service.AppointmentService;
import org.example.petcareplus.repository.PetProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AppointmentController {
    @Autowired
    private AppointmentRepository appointmentRepository;

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
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

}
