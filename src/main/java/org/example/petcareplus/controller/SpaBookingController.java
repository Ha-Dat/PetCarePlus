package org.example.petcareplus.controller;

import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.repository.SpaBookingRepository;
import org.example.petcareplus.service.SpaBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/list-spa-booking")
public class SpaBookingController {

    private final SpaBookingRepository spaBookingRepository;
    private final SpaBookingService spaBookingService;

    @Autowired
    public SpaBookingController(SpaBookingRepository spaBookingRepository, SpaBookingService spaBookingService) {
        this.spaBookingRepository = spaBookingRepository;
        this.spaBookingService = spaBookingService;
    }

    @GetMapping("")
    public String getAllSpaBookings(Model model) {
        List<SpaBooking> spaBookings= spaBookingService.findAll();
        model.addAttribute("spaBookings", spaBookings);
        return "list-spa-booking";
    }


}
