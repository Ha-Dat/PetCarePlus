package org.example.petcareplus.controller;

import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.repository.HotelBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HotelBookingController {
    private HotelBookingRepository hotelBookingRepository;

    @Autowired
    public HotelBookingController(HotelBookingRepository hotelBookingRepository) {
        this.hotelBookingRepository = hotelBookingRepository;
    }

    @GetMapping("/groomer_hotel_page")
    public String GetHotelBookings(Model model) {
        List<HotelBooking> hotelBookings = hotelBookingRepository.findAll();
        model.addAttribute("hotelBookings", hotelBookings);
        return "groomer_hotel_page";
    }
}
