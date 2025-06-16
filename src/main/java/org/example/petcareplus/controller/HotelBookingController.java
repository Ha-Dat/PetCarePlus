package org.example.petcareplus.controller;

import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.repository.HotelBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @PostMapping("/approve-booking/{id}")
    @ResponseBody
    public String approveBooking(@PathVariable("id") Integer id) {
        Optional<HotelBooking> bookingOpt = hotelBookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            HotelBooking booking = bookingOpt.get();
            if ("pending".equalsIgnoreCase(booking.getStatus())) {
                booking.setStatus("accepted");
                hotelBookingRepository.save(booking);
                return "lịch đặt đã được duyệt";
            } else {
                return "lịch đặt đã được duyệt rồi";
            }
        }
        return "Not found";
    }
}
