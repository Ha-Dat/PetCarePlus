package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.service.HotelBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HotelBookingController {
    @Autowired
    private HotelBookingService hotelBookingService;

    @Autowired
    private CategoryService categoryService;

    public HotelBookingController(HotelBookingService hotelBookingService, CategoryService categoryService) {
        this.hotelBookingService = hotelBookingService;
        this.categoryService = categoryService;
    }

    @GetMapping("/list-hotel-booking")
    public String GetHotelBookings(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "8") int size) {
        Page<HotelBooking> hotelBookings = hotelBookingService.findAll(page, size, "bookDate");

        model.addAttribute("hotelBookings", hotelBookings.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", hotelBookings.getTotalPages());
        return "list-hotel-booking";
    }

    @PostMapping("/list-hotel-booking/approve-hotel/{id}")
    @ResponseBody
    public String approveBooking(@PathVariable("id") Long id) {
        Optional<HotelBooking> bookingOpt = hotelBookingService.findById(id);
        if (bookingOpt.isPresent()) {
            HotelBooking booking = bookingOpt.get();
            if (BookingStatus.PENDING.equals(booking.getStatus())) {
                booking.setStatus(BookingStatus.ACCEPTED);
                hotelBookingService.save(booking);
                return "lịch đặt đã được duyệt";
            } else if(BookingStatus.ACCEPTED.equals(booking.getStatus())){
                return "lịch đặt đã được duyệt rồi";
            }else{
                return "lịch đặt chưa bị từ chối rồi";
            }
        }
        return "Not found";
    }

    @GetMapping("/list-hotel-booking/hotel-detail/{id}")
    @ResponseBody
    public ResponseEntity<?> getBookingDetail(@PathVariable("id") Long id) {
        Optional<HotelBooking> bookingOpt = hotelBookingService.findById(id);
        if (bookingOpt.isPresent()) {
            HotelBooking booking = bookingOpt.get();
            Map<String, Object> data = new HashMap<>();

            // data đơn book
            data.put("id", booking.getHotelBookingId());
            data.put("bookDate", booking.getBookDate().toString());
            data.put("status", booking.getStatus());
            data.put("service", booking.getService().getName());
            data.put("note", booking.getNote());
            // data của pet
            data.put("petId", booking.getPetProfile().getPetProfileId());
            data.put("petName", booking.getPetProfile().getName());
            data.put("species", booking.getPetProfile().getSpecies());
            data.put("breed", booking.getPetProfile().getBreeds());
            // data chủ nuôi
            data.put("name", booking.getPetProfile().getProfile().getAccount().getName());
            data.put("phone", booking.getPetProfile().getProfile().getAccount().getPhone());

            return ResponseEntity.ok(data);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy booking.");
    }

    @PostMapping("/list-hotel-booking/reject-hotel/{id}")
    @ResponseBody
    public String rejectBooking(@PathVariable("id") Long id) {
        Optional<HotelBooking> bookingOpt = hotelBookingService.findById(id);
        if (bookingOpt.isPresent()) {
            HotelBooking booking = bookingOpt.get();
            if (BookingStatus.PENDING.equals(booking.getStatus())) {
                booking.setStatus(BookingStatus.REJECTED);
                hotelBookingService.save(booking);
                return "lịch đặt đã được từ chối";
            } else if(BookingStatus.REJECTED.equals(booking.getStatus())){
                return "lịch đặt đã bị từ chối rồi";
            } else {
                return "lịch đặt được duyệt rồi";
            }
        }
        return "Not found";
    }

    @GetMapping("/hotel-booking/form")
    public String showHotelBookingForm(HttpSession session, Model model) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        List<Category> parentCategories = categoryService.getParentCategory();

        model.addAttribute("hotelBooking", new HotelBooking()); // model binding
        model.addAttribute("services", hotelBookingService.Service_findAll()); // list dịch vụ
        model.addAttribute("categories", parentCategories);
        return "hotel-booking";
    }

    @PostMapping("/hotel-booking/book")
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
        try {
            Account account = (Account) session.getAttribute("loggedInUser");
            if (account == null) return "redirect:/login";
            // Tạo pet profile mới từ form
            PetProfile petProfile = new PetProfile();
            petProfile.setName(petName);
            petProfile.setSpecies(petSpecies);
            petProfile.setBreeds(petBreed);
            petProfile.setProfile(account.getProfile());
            hotelBookingService.PetProfile_save(petProfile);

            // gán dữ liệu từ form
            HotelBooking booking = new HotelBooking();
            booking.setBookDate(bookDate);
            booking.setNote(note);
            booking.setStatus(BookingStatus.PENDING);
            booking.setCreatedAt(LocalDateTime.now());

            booking.setPetProfile(petProfile);

            Optional<Service> service = hotelBookingService.Service_findById(serviceId);
            if (service.isEmpty()) {
                model.addAttribute("error", "Dịch vụ không tồn tại");
                return "error";
            }
            booking.setService(service.get());

            hotelBookingService.save(booking);
            return "redirect:/hotel-booking/form";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi đặt lịch: " + e.getMessage());
            return "error";
        }
    }


}
