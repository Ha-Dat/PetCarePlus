package org.example.petcareplus.controller;

import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.repository.SpaBookingRepository;
import org.example.petcareplus.service.SpaBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping()
    public String getAllSpaBookings(Model model) {
        List<SpaBooking> spaBookings= spaBookingService.findAll();
        model.addAttribute("spaBookings", spaBookings);
        return "list-spa-booking";
    }

    @PostMapping("/approve-spa/{id}")
    @ResponseBody
    public String approveSpaBooking(@PathVariable("id") Integer id){
        Optional<SpaBooking> booking = spaBookingRepository.findById(id);
        if (booking.isPresent()) {
            SpaBooking spaBooking = booking.get();
            if ("Chờ xác nhận".equalsIgnoreCase(spaBooking.getStatus())){
                spaBooking.setStatus("Đã đặt");
                spaBookingRepository.save(spaBooking);
                return "Duyệt lịch thành công";
            }else {
                return "Lịch đặt đã được duyệt";
            }
        }
        return "Không tìm thấy lịch";
    }

    @PostMapping("/reject-spa/{id}")
    @ResponseBody
    public String rejectSpaBooking(@PathVariable("id") Integer id){
        Optional<SpaBooking> booking = spaBookingRepository.findById(id);
        if (booking.isPresent()) {
            SpaBooking spaBooking = booking.get();
            if ("Chờ xác nhận".equalsIgnoreCase(spaBooking.getStatus())){
                spaBooking.setStatus("Đã từ chối");
                spaBookingRepository.save(spaBooking);
                return "Từ chối lịch thành công";
            }else {
                return "Lịch đặt đã từ chối";
            }
        }
        return "Không tìm thấy lịch";
    }

}
