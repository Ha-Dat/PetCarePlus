package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.entity.Service;
import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.service.PetProfileService;
import org.example.petcareplus.service.ServiceService;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.SpaBookingService;
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
@RequestMapping()
public class SpaBookingController {

    private final SpaBookingService spaBookingService;
    private final PetProfileService petProfileService;
    private final ServiceService serviceService;
    private final CategoryService categoryService;

    public SpaBookingController(SpaBookingService spaBookingService, PetProfileService petProfileService, ServiceService serviceService, CategoryService categoryService) {
        this.spaBookingService = spaBookingService;
        this.petProfileService = petProfileService;
        this.serviceService = serviceService;
        this.categoryService = categoryService;
    }

    @GetMapping("/list-spa-booking")
    public String getAllSpaBookings(Model model,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "8") int size) {
        Page<SpaBooking> spaBookings= spaBookingService.findAll(page, size, "bookDate");
        if (page < 0) {
            return "redirect:/list-spa-booking?page=0&size=" + size;
        }

        model.addAttribute("spaBookings", spaBookings);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", spaBookings.getTotalPages());
        return "list-spa-booking";
    }

    @PostMapping("/list-spa-booking/approve-spa/{id}")
    @ResponseBody
    public String approveSpaBooking(@PathVariable("id") Long id){
        Optional<SpaBooking> booking = spaBookingService.findById(id);
        if (booking.isPresent()) {
            SpaBooking spaBooking = booking.get();
            if (BookingStatus.PENDING.equals(spaBooking.getStatus())){
                spaBooking.setStatus(BookingStatus.ACCEPTED);
                spaBookingService.save(spaBooking);
                return "Duyệt lịch thành công";
            }else {
                return "Lịch đặt đã được duyệt";
            }
        }
        return "Không tìm thấy lịch";
    }

    @PostMapping("/list-spa-booking/reject-spa/{id}")
    @ResponseBody
    public String rejectSpaBooking(@PathVariable("id") Long id){
        Optional<SpaBooking> booking = spaBookingService.findById(id);
        if (booking.isPresent()) {
            SpaBooking spaBooking = booking.get();
            if (BookingStatus.PENDING.equals(spaBooking.getStatus())){
                spaBooking.setStatus(BookingStatus.REJECTED);
                spaBookingService.save(spaBooking);
                return "Từ chối lịch thành công";
            }else {
                return "Lịch đã được duyệt!, Không thể từ chối";
            }
        }
        return "Không tìm thấy lịch";
    }

    @GetMapping("/list-spa-booking/{id}")
    @ResponseBody
    public ResponseEntity<?> getSpaBookingDetail(@PathVariable("id") Long id) {
        Optional<SpaBooking> bookingOpt = spaBookingService.findById(id);
        if (bookingOpt.isPresent()) {
            SpaBooking booking = bookingOpt.get();
            Map<String, Object> data = new HashMap<>();

            // data đơn book
            data.put("id", booking.getSpaBookingId());
            data.put("bookDate", booking.getBookDate().toString());
            data.put("status", booking.getStatus());
            data.put("service", booking.getService().getName());
            data.put("note", booking.getNote());
            // data của pet
            data.put("image", booking.getPetProfile().getMedias().get(0).getUrl());
            data.put("petId", booking.getPetProfile().getPetProfileId());
            data.put("petName", booking.getPetProfile().getName());
            data.put("species", booking.getPetProfile().getSpecies());
            data.put("breed", booking.getPetProfile().getBreeds());
            data.put("weight", booking.getPetProfile().getWeight());
            // data chủ nuôi
            data.put("name", booking.getPetProfile().getProfile().getAccount().getName());
            data.put("phone", booking.getPetProfile().getProfile().getAccount().getPhone());
            data.put("city", booking.getPetProfile().getProfile().getCity().getName());
            data.put("district", booking.getPetProfile().getProfile().getDistrict().getName());
            data.put("ward", booking.getPetProfile().getProfile().getWard().getName());

            return ResponseEntity.ok(data);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy booking.");
    }

    @GetMapping("/spa-booking")
    public String showSpaBookingForm(HttpSession session, Model model) {
        List<Category> parentCategories = categoryService.getParentCategory();

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        // TODO: Add Authen
        model.addAttribute("spaBooking", new SpaBooking());
        model.addAttribute("spaServices", serviceService.findByServiceCategory(ServiceCategory.SPA));
        model.addAttribute("categories", parentCategories);
        return "spa-booking";
    }

    @PostMapping("/spa-booking/book")
    public String submitSpaBooking(
            @RequestParam("bookDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookDate,
            @RequestParam("note") String note,
            @RequestParam("serviceId") Long serviceId,
            @RequestParam("ownerInfo") String ownerInfo,
            @RequestParam("petName") String petName,
            @RequestParam("petSpecies") String petSpecies,
            @RequestParam("petBreed") String petBreed,
            @RequestParam("phoneNumber") String phoneNumber,
            Model model){

        try {
            // Tạo pet profile mới từ form
            PetProfile petProfile = new PetProfile();
            petProfile.setName(petName);
            petProfile.setSpecies(petSpecies);
            petProfile.setBreeds(petBreed);
            petProfile.setAge(2);
            petProfileService.save(petProfile);

            // gán dữ liệu từ form
            SpaBooking booking = new SpaBooking();
            booking.setBookDate(bookDate);
            booking.setNote(note);
            booking.setStatus(BookingStatus.PENDING);
            booking.setCreatedAt(LocalDateTime.now());

            booking.setPetProfile(petProfile);

            Optional<Service> service = serviceService.findById(serviceId);
            if (service.isEmpty()) {
                model.addAttribute("error", "Dịch vụ không tồn tại");
                return "error";
            }
            booking.setService(service.get());

            spaBookingService.save(booking);
            return "redirect:/spa-booking";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi đặt lịch: " + e.getMessage());
            return "error";
        }
    }

}
