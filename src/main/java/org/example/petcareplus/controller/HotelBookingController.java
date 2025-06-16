package org.example.petcareplus.controller;

import org.example.petcareplus.entity.District;
import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.entity.Ward;
import org.example.petcareplus.repository.DistrictRepository;
import org.example.petcareplus.repository.HotelBookingRepository;
import org.example.petcareplus.repository.WardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HotelBookingController {
    private HotelBookingRepository hotelBookingRepository;

    private DistrictRepository districtRepository;
    private WardRepository wardRepository;

    @Autowired
    public HotelBookingController(HotelBookingRepository hotelBookingRepository, DistrictRepository districtRepository, WardRepository wardRepository) {
        this.hotelBookingRepository = hotelBookingRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    @GetMapping("/groomer_hotel_page")
    public String GetHotelBookings(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookDate").descending());
        Page<HotelBooking> hotelBookings = hotelBookingRepository.findAll(pageable);

        model.addAttribute("hotelBookings", hotelBookings.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", hotelBookings.getTotalPages());
        return "groomer_hotel_page";
    }


    @PostMapping("/approve-booking/{id}")
    @ResponseBody
    public String approveBooking(@PathVariable("id") Integer id) {
        Optional<HotelBooking> bookingOpt = hotelBookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            HotelBooking booking = bookingOpt.get();
            if ("chờ duyệt".equalsIgnoreCase(booking.getStatus())) {
                booking.setStatus("đã duyệt");
                hotelBookingRepository.save(booking);
                return "lịch đặt đã được duyệt";
            } else if("đã duyệt".equalsIgnoreCase(booking.getStatus())){
                return "lịch đặt đã được duyệt rồi";
            }else{
                return "lịch đặt chưa bị từ chối rồi";
            }
        }
        return "Not found";
    }

    @GetMapping("/booking-detail/{id}")
    @ResponseBody
    public ResponseEntity<?> getBookingDetail(@PathVariable("id") Integer id) {
        Optional<HotelBooking> bookingOpt = hotelBookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            HotelBooking booking = bookingOpt.get();
            Map<String, Object> data = new HashMap<>();
            //lấy district và ward từ profile
            District district = districtRepository.findById(booking.getPetProfile().getProfile().getDistrictId()).get();
            Ward ward = wardRepository.findById(booking.getPetProfile().getProfile().getWardId()).get();

            // data đơn book
            data.put("id", booking.getHotelBookingId());
            data.put("bookDate", booking.getBookDate().toString());
            data.put("status", booking.getStatus());
            data.put("service", booking.getService().getName());
            data.put("note", booking.getNote());
            // data của pet
            data.put("image", booking.getPetProfile().getImage());
            data.put("petId", booking.getPetProfile().getPetProfileId());
            data.put("petName", booking.getPetProfile().getName());
            data.put("species", booking.getPetProfile().getSpecies());
            data.put("breed", booking.getPetProfile().getBreeds());
            data.put("weight", booking.getPetProfile().getWeight());
            // data chủ nuôi
            data.put("name", booking.getPetProfile().getProfile().getAccount().getName());
            data.put("phone", booking.getPetProfile().getProfile().getAccount().getPhone());
            data.put("city", booking.getPetProfile().getProfile().getCity().getName());
            data.put("district", district.getName());
            data.put("ward", ward.getName());

            return ResponseEntity.ok(data);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy booking.");
    }

    @PostMapping("/reject-booking/{id}")
    @ResponseBody
    public String rejectBooking(@PathVariable("id") Integer id) {
        Optional<HotelBooking> bookingOpt = hotelBookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            HotelBooking booking = bookingOpt.get();
            if ("chờ duyệt".equalsIgnoreCase(booking.getStatus())) {
                booking.setStatus("từ chối");
                hotelBookingRepository.save(booking);
                return "lịch đặt đã được từ chối";
            } else if("từ chối".equalsIgnoreCase(booking.getStatus())){
                return "lịch đặt đã bị từ chối rồi";
            } else {
                return "lịch đặt được duyệt rồi";
            }
        }
        return "Not found";
    }
}
